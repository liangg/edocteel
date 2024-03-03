#include <cstdio>
#include <iostream>
#include <string>
#include <map>
#include <queue>
#include <unordered_set>
#include <unordered_map>
#include <vector>

using std::cout;
using std::endl;
using std::map; // sorted 
using std::vector;
using std::pair;
using std::string;
using std::queue;
using std::unordered_map;

// timestamp_sec, count
typedef std::tuple<int64_t, int> CountEntry;

/**
 * API request rate limiter restricts each user to N requests per second. E.g. “User X can send 1000 API 
 * requests every second”.
 * 
 * Redis server uses (de)multiplexing mechanism and a single-threaded event loop and therefore is race 
 * condition safe.
 */
class FixedWindowRateLimit {
public:
    FixedWindowRateLimit() {}

    /**
     * A fixed-sized window counter implementation, and the caller need to ensure mutex exclusion.
     * It is subject to the issue of bursty requests at the edges of 2 adjacent time windows.
     */
    bool rateLimit(int64_t userId, int64_t reqId, int64_t timestampMs) {
        int64_t sec = timestampMs / 1000;
        auto iter = userSecCounter.find(userId);
        if (iter == userSecCounter.end()) {
            userSecCounter[userId] = std::make_tuple(sec, 1);
            return true;
        }

        CountEntry& entry = iter->second;
        if (std::get<0>(entry) == sec) {
          if (std::get<1>(entry) < REQUEST_RATE) {
            entry = std::make_tuple(sec, std::get<1>(entry)+1);
            return true;
          }
          return false;
        }
        // starts a new second time unit
        userSecCounter[userId] = std::make_tuple(sec, 1);
        return true;
    }

private:
    const int REQUEST_RATE = 10;
    const int BURST_RATE = 2; // can allow 2x bursty traffic if needed

    // Fixed-sized window counter, {user_id, {curr_ts, count}}
    unordered_map<int64_t, CountEntry> userSecCounter; 
};

/**
 * Token bucket API request rate limiter.
 */
class TokenBucketRateLimit {
  const static int TOKEN = 0;
  const static int LAST_CHECKED_TIME = 1;
public:
  // K tokens per T time_unit
  TokenBucketRateLimit(int tokens, int timeUnitSec) 
    : fillTokens_(tokens), timeUnitMs_(timeUnitSec*1000) 
  {}

  bool rateLimit(int64_t key, int64_t reqId, int64_t timestampMs) {
    auto iter = userTokenBucket_.find(key);
    if (iter == userTokenBucket_.end()) {
      userTokenBucket_[key] = {timestampMs, fillTokens_-1};
      cout << "Ok: " << key << "," << reqId << endl;
      return true;
    }

    auto& tokenData = iter->second;
    // fill tokens based on elapse time since last fill time
    int64_t timePassed = timestampMs - tokenData.first;
    // (timePassed / timeUnit) is the fill rate
    // Note, timePassed * fillTokens / timeUnitMs_ is always 0
    int tokens = tokenData.second + fillTokens_ * timePassed / timeUnitMs_;    
    int fill = tokens > fillTokens_ ? fillTokens_ : tokens;
    cout << "debug: time_passed:" << timePassed << " fill:" << fill << endl;
    if (fill > 0) {   
      tokenData.first = timestampMs; // last_filled_ts
      tokenData.second = fill - 1;
      cout << "Ok: " << key << "," << reqId << ", left tokens " << tokenData.second << endl;
      return true;
    }    
    // track dropped request stats
    cout << "Rate limited: " << key << "," << reqId << endl;
    return false;
  }

private:
  int fillTokens_; // number of tokens per time unit
  int timeUnitMs_; // time unit in seconds

  // {key, (last_filled_ts, tokens)}
  unordered_map<int64_t, pair<int64_t, int>> userTokenBucket_;
};

/**
 * Sliding window rate limit does not have the issue of bursty requests at the adjacent window boundary.
 * 
 * Stripe's concurrent request rate limit uses similar implementation using Redis.
 */
class SlidingWindowRateLimit {
public:
    SlidingWindowRateLimit(int reqsPerWindow, int64_t windowDurationSec) 
      : capacity_(reqsPerWindow), windowDurationMs_(windowDurationSec*1000) 
    {}

    bool rateLimit(int64_t userId, int64_t reqId, int64_t timestampMs) {
      auto iter = userSlidingWindows_.find(userId);
      if (iter == userSlidingWindows_.end()) {
        auto& window = userSlidingWindows_[userId];
        window.push(timestampMs);
        return true;
      }

      auto& window = iter->second;
      // remove expired request timestamps from the window
      while (!window.empty() && (timestampMs - window.front()) >= windowDurationMs_) {
        window.pop();
      }

      if (window.size() >= capacity_) {
        // track dropped request stats
        return false;
      }
      window.push(timestampMs);
      return true;
    }

private:
  int capacity_; // request rate limit
  int windowDurationMs_;

  // {user_id, sliding_window_queue}
  unordered_map<int64_t, queue<int64_t>> userSlidingWindows_; 
};

void unittest1() {
  cout << "Token Bucket Rate Limit" << endl;
  TokenBucketRateLimit rateLimit(5, 1000); // 1 token every 200ms
  rateLimit.rateLimit(100, 1, 1990000); // ok
  rateLimit.rateLimit(100, 2, 1990100); 
  rateLimit.rateLimit(100, 3, 1990200);
  rateLimit.rateLimit(100, 4, 1990300);
  rateLimit.rateLimit(100, 5, 1990400); // ok
  rateLimit.rateLimit(100, 6, 1990500); // limited
  rateLimit.rateLimit(100, 7, 1990550); // limited
  rateLimit.rateLimit(100, 8, 1990600); // ok, 1->0
  rateLimit.rateLimit(100, 9, 1991000); // ok, 2->1
  rateLimit.rateLimit(100, 10, 1992000); // ok, 5->4
}

void unittest2() {
  cout << "Sliding Window Log" << endl;
  SlidingWindowRateLimit rateLimit(5, 1000); 
  rateLimit.rateLimit(100, 1, 1990000); // ok
  rateLimit.rateLimit(100, 2, 1990100); 
  rateLimit.rateLimit(100, 3, 1990200);
  rateLimit.rateLimit(100, 4, 1990300);
  rateLimit.rateLimit(100, 5, 1990400); // ok
  rateLimit.rateLimit(100, 6, 1990500); // no
  rateLimit.rateLimit(100, 7, 1990550); // no
  rateLimit.rateLimit(100, 8, 1990600); // no
  rateLimit.rateLimit(100, 9, 1991000); // ok
  rateLimit.rateLimit(100, 10, 1992000); // ok
}

int main() {
  cout << "Rate limit" << endl;
  unittest1();
  return 0;
}
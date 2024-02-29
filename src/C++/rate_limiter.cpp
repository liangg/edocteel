#include <cstdio>
#include <string>
#include <vector>
#include <map>
#include <queue>
#include <unordered_set>
#include <unordered_map>

using std::map; // sorted 
using std::vector;
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
  TokenBucketRateLimit(int tokens, int timeUnitSec) 
    : fillTokens_(tokens), timeUnitMs_(timeUnitSec*1000) 
  {}

  bool rateLimit(int64_t userId, int64_t reqId, int64_t timestampMs) {
    auto iter = userTokenBucket_.find(userId);
    if (iter == userTokenBucket_.end()) {
      userTokenBucket_[userId] = {fillTokens_-1, timestampMs};
      return true;
    }

    vector<int64_t>& data = iter->second;
    // fill tokens based on elapse time since last check/fill time
    int64_t timePassed = timestampMs - data[LAST_CHECKED_TIME];
    // (fillTokens / timeUnit) is the fill rate
    int tokens = data[TOKEN] + timePassed * (fillTokens_ / timeUnitMs_);
    data[TOKEN] = tokens > fillTokens_ ? fillTokens_ : tokens;
    data[LAST_CHECKED_TIME] = timestampMs;
    if (data[TOKEN] <= 0) {
      // track dropped request stats
      return false;
    }
    data[TOKEN]--;
    return true;
  }

private:
  int fillTokens_; // number of tokens per time unit
  int timeUnitMs_; // time unit in seconds

  // {userId, {tokens, last_checked_ts}}
  unordered_map<int64_t, vector<int64_t>> userTokenBucket_;
};

/**
 * Sliding window rate limit does not have the issue of bursty requests at the adjacent window boundary.
 * 
 * Stripe's concurrent request rate limit uses similar implementation using Redis.
 */
class SlidingWindowRateLimit {
public:
    SlidingWindowRateLimit(int reqsPerWindow, int64_t windowDurationSec) 
      : windowReqs_(reqsPerWindow), windowDurationMs_(windowDurationSec*1000) 
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
      while (!window.empty() && (timestampMs - window.front()) > windowDurationMs_) {
        window.pop();
      }

      if (window.size() >= windowReqs_) {
        // track dropped request stats
        return false;
      }

      window.push(timestampMs);
      return true;
    }

private:
  int windowReqs_; // request rate limit
  int windowDurationMs_;

  // {user_id, sliding_window_queue}
  unordered_map<int64_t, queue<int64_t>> userSlidingWindows_; 
};

#include "stdio.h"
#include <string>
#include <vector>
#include <map>
#include <unordered_set>
#include <unordered_map>

using std::map; // sorted 
using std::vector;
using std::string;
using std::unordered_map;

// timestamp_sec, count
typedef std::tuple<int64_t, int> CountEntry;

/**
 * API request rate limiter restricts each user to N requests per second. E.g. “User X can send 1000 API 
 * requests every second”. Note, it is not a sliding window. 
 * 
 * Redis server uses (de)multiplexing mechanism and a single-threaded event loop and therefore is race 
 * condition safe.
 */
class RequestRateLimit {
public:
    RequestRateLimit() {}

    // A fixed-sized window counter implementation, and the caller need to ensure mutex exclusion.
    // It is subject to the issue of bursty requests at the edges of 2 adjacent time windows.
    bool rateLimit1(int64_t userId, int64_t reqId, int64_t timestampMs) {
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

    // A sliding window counter implementation
    bool rateLimit2(int64_t userId, int64_t reqId, int64_t timestampMs) {
        
    }

private:
    const int REQUEST_RATE = 10;
    const int BURST_RATE = 2; // can allow 2x bursty traffic if needed

    unordered_map<int64_t, CountEntry> userSecCounter; // fixed-sized window counter
    unordered_map<int64_t, vector<CountEntry>> userSecSlidingCounter; // sliding window counter
};



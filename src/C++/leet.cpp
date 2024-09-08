#include "stdio.h"
#include <list> // bidirectional list
#include <map>
#include <queue>
#include <set>
#include <stack>
#include <string>
#include <vector>
#include <unordered_set>
#include <unordered_map>

using std::list;
using std::vector;
using std::queue;
using std::set;
using std::stack;
using std::string;
using std::unordered_map;
using std::unordered_set;

struct ListNode {
    int val;
    ListNode *next;
    ListNode() : val(0), next(nullptr) {}
    ListNode(int x) : val(x), next(nullptr) {}
    ListNode(int x, ListNode *next) : val(x), next(next) {}
 };

/**
 * Q-3: Longest Substring Without Repeating Characters
 */
class LongestSubstrWithoutRepeatingCharacters {
public:
    int lengthOfLongestSubstring(string s) {
        unordered_set<char> window;
        int maxLen = 0, windowLen = 0;
        for (int i = 0; i < s.size(); ++i) {
            char c = s.at(i);
            if (window.count(c) == 0) {
                window.emplace(c);
                windowLen++;
                if (windowLen > maxLen)
                    maxLen = windowLen;
                continue;
            }
            // readjust the window of non-repeating chars
            for (int j = i - windowLen; s.at(j) != c; ++j) {
                window.erase(s.at(j));
                windowLen--;
            }
        }
        return maxLen;
    }
};

// Q-18
class ThreeSum {
public:
    vector<vector<int>> threeSum(vector<int>& nums) {
        vector<vector<int>> result;
        std::sort(nums.begin(), nums.end());
        if (nums.size() < 2 || nums[0] > 0 || nums[nums.size()-1] < 0)
            return result;
        for (int i = 0; i < nums.size()-2; ++i) {
            if (nums[i] > 0) break; // no more candidate
            if (i > 0 && nums[i] == nums[i-1]) continue; // skip duplicate solution
            int target = nums[i];
            for (int l = i+1, r = nums.size()-1; l < r;) {
                if (nums[l] + nums[r] == target) {
                    result.push_back({nums[i], nums[l], nums[r]});
                    while (l < r && nums[l+1] == nums[l]) l++; // skip duplicates
                    while (r > l && nums[r-1] == nums[r]) r--;
                    l++;
                    r--;
                } else if (nums[l] + nums[r] > target)
                    r--;
                else
                    l++;
            }
        }
        return result;
    }
};

// Q-55 Merge intervals
class MergeInterval {
public:
    vector<vector<int>> merge(vector<vector<int>>& intervals) {
        vector<vector<int>> result;
        if (intervals.size() == 0)
            return result;
        std::sort(intervals.begin(), intervals.end());
        result.emplace_back(intervals.at(0));
        for (int i = 1; i < intervals.size(); ++i) {
            vector<int>& intv = intervals.at(i);
            vector<int>& prev = result.back();
            if (intv[0] <= prev[1]) {
                if (intv[1] > prev[1]) 
                    prev[1] = intv[1];
            } else {
                result.emplace_back(intv);
            }
        }
        return result;
    }
};

// Q-80: Remove Duplicates from Sorted Array II
class RemoveDuplicatesSortedArray {
public:
    int removeDuplicates(vector<int>& nums) {
        int wptr, p;
        std::unordered_set<int> seen;
        for (wptr = 0, p = 0; p < nums.size(); ++p) {
            if (seen.count(nums[p]) == 0) {
                seen.insert(nums[p]);
                nums[wptr++] = nums[p];
            }
        }
        return wptr;
    }

    int removeDuplicates2(vector<int>& nums) {
        if (nums.size() <= 2)
            return nums.size();
        int wptr, p;
        for (wptr = 2, p = 2; p < nums.size(); ++p) {
            if (!(nums[p] == nums[wptr-1] && nums[p] == nums[wptr-2]))
                nums[wptr++] = nums[p];
        }
        return wptr;
    }

    static void test() {
        printf("RemoveDuplicatesSortedArray");
        RemoveDuplicatesSortedArray rd;
    }
};

// Q-92 Rotate Linked List 2
class RotateList {
public:
    ListNode* reverseList(ListNode* head, int& len) {
        ListNode* prev = NULL;
        for (ListNode* p = head, *next = NULL; p != NULL; prev = p, p = next, len++) {
            next = p->next;
            p->next = prev;
        }
        return prev;
    }

    ListNode* rotateRight(ListNode* head, int k) {
        int len = 0;
        ListNode* p1 = reverseList(head, len);
        k = k % len;
        ListNode *p2 = p1, *prev = NULL;        
        for (int i = 0; i < k; ++i, prev = p2, p2 = p2->next);
        prev->next = NULL;
        int len1 = 0, len2 = 0;
        ListNode* p3 = reverseList(p1, len1); // new head
        ListNode* p4 = reverseList(p2, len2);
        p1->next = p4;
        p2->next = NULL; // new tail
        return p3;
    }

    ListNode* rotateRight2(ListNode* head, int k) {
        if (head == NULL) return head;
        int len = 0;
        ListNode* prev = NULL;
        for (ListNode* p = head; p != NULL; prev = p, p = p->next, len++);
        if (k == len) return head;
        prev->next = head;
        k = k % len;
        ListNode* p = head;
        for (int i = 0; i < (len - k); ++i, prev = p, p = p->next);
        prev->next = NULL; // new tail
        return p;
    }
};

// Q-128 Longest Consecutive Sequence
class LongestConsecutiveSequence {
public:
    int longestConsecutive(vector<int>& nums) {
        int result = 0;
        std::unordered_set numSet(nums.begin(), nums.end());
        for (int i = 0; i < nums.size(); ++i) {
            if (numSet.count(nums[i]) == 0) continue; // already checked
            numSet.erase(nums[i]);
            int left = nums[i] - 1, right = nums[i] + 1;
            int lsum = 0, rsum = 0;
            while (numSet.count(left) != 0) {
                numSet.erase(left);
                lsum++;
                left--;
            }
            while (numSet.count(right) != 0) {
                numSet.erase(right);
                rsum++;
                right++;
            }
            if ((lsum + rsum + 1) > result)
                result = lsum + rsum + 1;
        }
        return result;
    }

};

/**
 * Q-146 LRU cache
 * 
 * DB buffer cache uses a doubly linked list for LRU and a Hashmap to reference the buffer 
 * cache entry. Use std::pair<key,value> to mimic cache entry, and std::list for doubly 
 * linked list.
 */
class LRUCache {
public:
    LRUCache(int capacity) : cap_(capacity) {}
    
    int get(int key) {
        auto iter = map_.find(key);        
        if (iter == map_.end())
            return -1;
        // move to the head of the LRU cache
        lruCache_.splice(lruCache_.begin(), lruCache_, iter->second);
        return iter->second->second; // return value
    }
    
    void put(int key, int value) {
        auto iter = map_.find(key);
        if (iter != map_.end()) {
            // remove the entry from the list, using the iterator
            lruCache_.erase(iter->second);
        }
        lruCache_.emplace_front(std::make_pair(key, value));
        map_[key] = lruCache_.begin();
        // evict the last item to make space
        if (map_.size() > cap_) {
            auto& evicted = lruCache_.back();
            map_.erase(evicted.first);
            lruCache_.pop_back();
        }
    }

private:
    const int cap_;
    list<std::pair<int, int>> lruCache_; // (key, value)
    unordered_map<int, list<std::pair<int, int>>::iterator> map_;
};

/** Q-148 Sort List */
class SortList {
public:
    ListNode* middleOfList(ListNode* head) {
        ListNode *prev;
        for (ListNode *p1 = head, *p2 = head; 
             p2 != NULL && p2->next != NULL; 
             prev = p1, p1 = p1->next, p2 = p2->next->next);
        return prev;
    }

    ListNode* sortList(ListNode* head) {
        if (head == NULL || head->next == NULL)
            return head;
        ListNode *p1 = head, *mid = middleOfList(head); 
        ListNode *p2 = mid->next;
        mid->next = NULL;
        p1 = sortList(p1);
        p2 = sortList(p2);
        ListNode *p3 = NULL, *tail = NULL;
        for (ListNode* p; p1 != NULL && p2 != NULL; tail = p) {
            if (p1->val < p2->val) {
                p = p1;
                p1 = p1->next;
            } else {
                p = p2;
                p2 = p2->next;
            }
            if (p3 == NULL)
                p3 = tail = p;
            tail->next = p;
        }
        tail->next = p1 != NULL ? p1 : p2;
        return p3;
    }
};

/**
 * Q-269 Alien dictionary
 */
class AlienDictionary {
public:
    string alienOrder(vector<string>& words) {

    }
};

/**
 * Q-251 Flatten 2D vectors
 * 
 * Implement an iterator to flatten a 2d vector. Given 2D vector [[1,2], [3], [4,5,6]], 
 * The iterator returns [1,2,3,4,5,6]. Use an iterator to implement this.
 */
class TwoDVectorIterator {
public:
    TwoDVectorIterator(vector<vector<int>>& vec2d) 
        : iter(vec2d.begin()), end(vec2d.end()) {}

    int next() {
        hasNext(); // make sure iter & innerIdx
        return (*iter)[innerIdx++];
    }

    bool hasNext() {
        while (iter != end && (*iter).size() == innerIdx) {
            iter++;
            innerIdx = 0;
        }
        return iter != end;
    }

private:
    vector<vector<int>>::iterator iter, end;
    int innerIdx = 0;
};

/**
 * Q-281 Zigzag iterator
 * 
 * Given 2 or K 1-d vectors, implement an iterator to return their elements alternately.
 */
class ZigzagIterator {
public:
    ZigzagIterator(vector<int>& v1, vector<int>& v2) {
        if (!v1.empty()) que_.push(make_pair(v1.begin(), v1.end()));
        if (!v2.empty()) que_.push(make_pair(v2.begin(), v2.end()));
    }

    int next() {
        auto iter = que_.front().first;
        auto end = que_.front().second;
        que_.pop();
        if (iter + 1 != end) que_.push(std::make_pair(iter + 1, end));
        return *iter;
    }

    bool hasNext() {
        return !que_.empty();
    }

private:
    // {moving_iterator, vector end_iter}
    queue<std::pair<vector<int>::iterator, vector<int>::iterator>> que_;
};


/**
 * Q-284 Peeking iterator
 *
 * Design an iterator that supports the peek operation on an existing iterator in addition 
 * to the hasNext and the next operations.
 */
class Iterator {
 	struct Data;
 	Data* data;
public:
	Iterator(const vector<int>& nums);
  	Iterator(const Iterator& iter);
 
  	// Returns the next element in the iteration.
 	int next();
 
 	// Returns true if the iteration has more elements.
 	bool hasNext() const;
};

class PeekingIterator : public Iterator {
public:
	PeekingIterator(const vector<int>& nums) : Iterator(nums) {
	    // Initialize any member here.
	    // **DO NOT** save a copy of nums and manipulate it directly.
	    // You should only use the Iterator interface methods.
        _hasSaved = false;
	}
	
    // Returns the next element in the iteration without advancing the iterator.
	int peek() {
        if (!_hasSaved) {
            _saved = Iterator::next();
            _hasSaved = true;
        }
        return _saved;
	}
	
	// hasNext() and next() should behave the same as in the Iterator interface.
	// Override them if needed.
	int next() {
        if (_hasSaved) {
            _hasSaved = false;
            return _saved;
        }
	    return Iterator::next();
	}
	
	bool hasNext() const {
	    return _hasSaved || Iterator::hasNext();
	}

private:
    bool _hasSaved;
    int _saved;
};

class NestedInteger {
public:
    // Return true if this NestedInteger holds a single integer, rather than a nested list.
    bool isInteger() const;
 
    // Return the single integer that this NestedInteger holds, if it holds a single integer
    // The result is undefined if this NestedInteger holds a nested list
    int getInteger() const;
 
    // Return the nested list that this NestedInteger holds, if it holds a nested list
    // The result is undefined if this NestedInteger holds a single integer
    const vector<NestedInteger> &getList() const;
 };

class NestedIterator {
public:
    NestedIterator(vector<NestedInteger> &nestedList) {
        for (int i = nestedList.size()-1; i >= 0; --i) {
            stk_.push(nestedList[i]);
        }
    }
    
    int next() {
        NestedInteger result = stk_.top();
        stk_.pop();
        return result.getInteger();
    }
    
    bool hasNext() {
        while (!stk_.empty()) {
            NestedInteger item = stk_.top();
            if (item.isInteger()) return true;
            stk_.pop();
            const vector<NestedInteger> &nestedList = item.getList();
            for (int i = nestedList.size()-1; i >= 0; --i) {
                stk_.push(nestedList[i]);
            }
        }
        return false;
    }

private:
    stack<NestedInteger> stk_;
};

/**
 * Q-359 Logger rate limiter
 * 
 * Design a logger system that receive stream of messages along with its timestamps, each message should 
 * be printed if and only if it is not printed in the last 10 seconds. Given a message and a timestamp 
 * (in seconds granularity), return true if the message should be printed in the given timestamp, 
 * otherwise returns false. It is possible that several messages arrive roughly at the same time.
 */
class Logger {
public:
    Logger() {}
    
    bool shouldPrintMessage(int timestamp, string message) {
        auto miter = m.find(message);
        if (miter == m.end() || (timestamp - miter->second) >= TTL) {
            m[message] = timestamp;
            return true;
        } 
        return false;
    }

private:
    const int TTL = 10; // 10s
    unordered_map<string, int> m;
};

/**
 * Q-362 Design Hit Counter 
 * Design a hit counter which counts the number of hits received in the past 5 minutes. Each function accepts 
 * a timestamp parameter (in seconds granularity) and you may assume that calls are being made to the system 
 * in chronological order (ie, the timestamp is monotonically increasing). You may assume that the earliest 
 * timestamp starts at 1. It is possible that several hits arrive roughly at the same time.
 */
class HitCounter {
public:
    HitCounter() {}
    
    /** Record a hit.
     * @param timestamp - The current timestamp (in seconds granularity). 
     */
    void hit(int timestamp) {
        window_.push(timestamp);
    }
    
    /** Return the number of hits in the past 5 minutes.
     * @param timestamp - The current timestamp (in seconds granularity). 
     */
    int getHits(int timestamp) {
        // remove timestamps that has expired the 5-min window
        while (!window_.empty() && (timestamp - window_.front()) >= 300) {
            window_.pop();
        }
        return window_.size();
    }

private:
    std::queue<int> window_; // 5-min window
};

/**
 * Q-460 LFU Cache
 * 
 * Design and implement a data structure for a Least Frequently Used (LFU) cache.
 * 
 *  -- void put(int key, int value) Update the value of the key if present, or inserts the key if not already 
 * present. When the cache reaches its capacity, it should invalidate and remove the least frequently used 
 * key before inserting a new item. For this problem, when there is a tie (i.e., two or more keys with the 
 * same frequency), the least recently used key would be invalidated.
 * 
 * To determine the least frequently used key, a use counter is maintained for each key in the cache. 
 * The key with the smallest use counter is the least frequently used key. When a key is first inserted 
 * into the cache, its use counter is set to 1 (due to the put operation). The use counter for a key in 
 * the cache is incremented either a get or put operation is called on it.
 */
class LFUCache {
public:
    LFUCache(int capacity) {
        
    }
    
    int get(int key) {
        return 0;
    }
    
    void put(int key, int value) {
        
    }
};

/**
 * Q-588 In-memory file system
 * 
 * Design an in-memory file system to simulate the following functions:
 */
class InMemFileSystem {
public:
    InMemFileSystem() {
        dirs_["/"] = {};
    }
    
    vector<string> ls(string path) {
    }

    // path starts with '/'
    void mkdir(string path) {

    }

    void addContentToFile(string filePath, string content) {}

    string readContentFromFile(string filePath) {}

private:
    // directory -> ordered set of sub-directories and files
    unordered_map<string, set<string>> dirs_;
    // file path -> file
    unordered_map<string, string> files_;
};

/**
 * Q-981: Time based KV store
 * Design a time-based key-value data structure that can store multiple values for the same 
 * key at different time stamps and retrieve the key's value at a certain timestamp. Implement 
 * the TimeMap class:
 *   -- TimeMap() Initializes the object of the data structure.
 *   -- void set(String key, String value, int timestamp) Stores the key key with the value 
 *      value at the given time timestamp.
 *   -- String get(String key, int timestamp) Returns a value such that set was called previously, 
 *      with timestamp_prev <= timestamp. If there are multiple such values, it returns the value 
 *      associated with the largest timestamp_prev. If there are no values, it returns "".
 */
class TimeBasedKV {
public:
    TimeBasedKV() {        
    }
    
    void set(string key, string value, int timestamp) {
        auto& data = _kvstore[key];
        data.emplace(timestamp, value);
    }
    
    string get(string key, int timestamp) {
        if (_kvstore.count(key) <= 0) return "";
        auto& data = _kvstore[key];
        auto iter = data.upper_bound(timestamp); // first greater than timestamp
        return iter == data.begin() ? "" : std::prev(iter)->second;
    }

private:
    unordered_map<string, std::map<int, string>> _kvstore;
};

int main(int argc, char **argv)
{
    return 0;
}

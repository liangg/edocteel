#include "stdio.h"
#include <map>
#include <set>
#include <string>
#include <vector>
#include <unordered_set>
#include <unordered_map>

using std::vector;
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

/**
 * Q-284 Peeking iterator
 *
 * Design an iterator that supports the peek operation on an existing iterator in addition 
 * to the hasNext and the next operations.
 */
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

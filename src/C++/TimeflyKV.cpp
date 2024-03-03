
#include <cassert>
#include <cstdio>
#include <map>
#include <iostream>
#include <iterator>
#include <limits>
#include <string>
#include <unordered_map>
#include <vector>

using std::cout;
using std::map; // C++ red-black tree
using std::string;
using std::unordered_map;
using std::vector;
using std::endl;

/**
 * Time-fly KV
 */

// key + version_ts, where key is fixed-sized
typedef string PKey; 
typedef string RowVal; 

const int KeySize = 5;
const int64_t MaxVersion = std::numeric_limits<int64_t>::max();
const string NonVal = "";
const string Tombstone = "DEADMARKER";

/**
 * The in-memory KV table uses a red-black tree for ordering row versions of
 * the same key, {key+timestamp, value}. It should be close to serialized 
 * composite key of typical KV storage.
 */
class TimeflyKV {
    typedef map<PKey, RowVal> MemTable;

public:
    TimeflyKV() {}

    bool set(string key, string value, int64_t timestamp) {
        PKey pkey = makeKey(key, timestamp);
        // overwrite or return error
        auto iter = _memtable.find(pkey);
        if (iter != _memtable.end()) {
            std::cout << "Key already exists: " << key << "+" << timestamp;
            return false;
        }
        _memtable[pkey] = value;
        return true;
    }

    /**
     * A tombstone record's key has a deletion marker "DEADMARKER" at the end
     * of its key.
     */
    bool del(string key, int64_t timestamp) {
        PKey tombstoneKey = makeTombstone(key, timestamp);
        _memtable[tombstoneKey] = NonVal;
        return true;
    }

    /**
     * Return the most recent version i.e. key with largest timestamp.
     */
    RowVal get(string key) {
        PKey pkey = makeKey(key, MaxVersion);
        auto iter = _memtable.upper_bound(pkey);
        if (iter != _memtable.begin()) {
            auto prevIter = std::prev(iter);
            const PKey& prevKey = prevIter->first;
            if (!prevKey.compare(0, KeySize, key)) {
                // FIXME: check tombstone record
                return prevIter->second;
            }
        }
        return NonVal;
    }

    /**
     * Return the largest key whose version is <= timestamp, i.e. a row version
     * that has committed prior to the timestamp. 
     * 
     * FIXME: needs to check whether the key has been delete-marked, i.e. tombstone 
     * record exists.
     */
    RowVal get(string key, int64_t timestamp) {
        PKey pkey = makeKey(key, timestamp);
        auto iter = _memtable.lower_bound(pkey); // >= pkey
        if (iter != _memtable.end()) {
            const PKey& fkey = iter->first;
            if (!fkey.compare(pkey)) // exact match
                return iter->second;
            // reach the leftmost range boundary, i.e. not in valid range
            if (!fkey.compare(0, KeySize, key)) { // the main key matches 
                if (iter == _memtable.begin())
                    return NonVal;
            }
        }
        // peek immediately previous element
        auto prevIter = std::prev(iter);
        const PKey& prevKey = prevIter->first;
        if (!prevKey.compare(0, KeySize, key))
            return prevIter->second;
        return NonVal;
    }

private:
    MemTable _memtable;

    PKey makeKey(string key, int64_t timestamp) {
        return key + std::to_string(timestamp);
    }

    PKey makeTombstone(string key, int64_t timestamp) {
        return key + std::to_string(timestamp) + Tombstone;
    }
};

/**
 * A different implementation with {key, {timestamp, value}}
 */
class TimeflyKV2 {
public:
  TimeflyKV2() {}

  // Does KV allow duplicated timestamp? Assume no
  // Does set overwrite deleted records?
  bool set(string key, string value, int64_t timestamp) {
    auto& table = memStore_[key];
    // TODO: check existence of timestamp and tombstone
    table[timestamp] = value;
    return true;
  }

  string get(string key) {
    auto iter = memStore_.find(key);
    if (iter == memStore_.end()) {
      cout << "Key not found: " << key << endl;
      return NonVal;
    }
    auto& table = iter->second;
    auto tableIter = table.rbegin();
    if (tableIter == table.rend() || tableIter->second == Tombstone) {
      cout << "Key no longer exist: " << key << endl;
      return NonVal;
    }
    return tableIter->second;
  }

  string get(string key, int64_t timestamp) {
    auto iter = memStore_.find(key);
    if (iter == memStore_.end()) {
      cout << "Key not found: " << key << endl;
      return NonVal;
    }
    auto& table = iter->second;
    // upper_bound finds the 1st element greater than the version timestamp
    auto tableIter = table.upper_bound(timestamp);
    if (tableIter == table.begin()) {
      cout << "Key version not exist: " << key << "@" << timestamp << endl;
      return NonVal;
    }
    tableIter--;
    cout << tableIter->second << "," << tableIter->first << endl;
    return tableIter->second;
  }

  bool del(string key, int64_t timestamp) {
    auto iter = memStore_.find(key);
    if (iter == memStore_.end()) {
      cout << "Key not found: " << key << endl;
      return false;
    }
    auto& table = iter->second;
    table[timestamp] = Tombstone;
    return true;
  }

private:
  // key -> sorted map
  unordered_map<PKey, map<int64_t /*ts*/, string /*value*/>> memStore_;
};

void check_assert(TimeflyKV& db, string key, int64_t timestamp, string expectedVal) {
    auto res = timestamp != 0 ? db.get(key, timestamp) : db.get(key);
    auto print = [](string& key, int64_t ts, string& val) { 
        std::cout << "get(" << key << "," << ts << ") = " << val << std::endl; };
    print(key, timestamp, res);
    if (res != expectedVal)
        cout << "expected " << expectedVal << ", got " << res << endl;
}

void unittest1() 
{
    std::cout << "unittest" << "\n";
    TimeflyKV db;

    db.set("AAAAA", "val1", 1100);
    db.set("AAAAA", "val2", 1105);
    db.set("AAAAA", "val3", 1110);
    db.set("AAAAA", "val4", 1113);
    db.set("AAAAA", "val5", 1120);
    check_assert(db, "AAAAA", 1099, NonVal); // invalid range
    check_assert(db, "AAAAA", 1100, "val1");
    check_assert(db, "AAAAA", 1105, "val2");
    check_assert(db, "AAAAA", 1110, "val3");
    check_assert(db, "AAAAA", 1108, "val2"); // AAAAA1105
    check_assert(db, "AAAAA", 1112, "val3"); // AAAAA1110
    check_assert(db, "AAAAA", 1116, "val4"); // AAAAA1113
    check_assert(db, "AAAAA", 1120, "val5"); 
    check_assert(db, "AAAAA", 1121, "val5"); // AAAAA11120
    
    db.set("AAAAB", "val6", 1106);
    db.set("AAAAD", "val7", 1111);
    db.set("AAAAD", "val8", 1118);
    check_assert(db, "AAAAB", 1107, "val6"); // AAAAB1106
    check_assert(db, "AAAAD", 1112, "val7"); // AAAAD1111    
    check_assert(db, "AAAAD", 1110, NonVal); // invalid range

    db.set("AAAA9", "val9", 1009);
    db.set("AAAA9", "val10", 1015);
    check_assert(db, "AAAA9", 1010, "val9"); // AAAA91010
    check_assert(db, "AAAAA", 1099, NonVal); // still invalid range
    check_assert(db, "AAAA9", 1014, "val9"); // AAAA91009

    check_assert(db, "AAAAA", 0, "val5");
    check_assert(db, "AAAA9", 0, "val10");
    check_assert(db, "AAAAB", 0, "val6");
    check_assert(db, "AAAAD", 0, "val8");

    db.del("AAAAA", 1200);
    check_assert(db, "AAAAA", 0, NonVal);
}

int main(int argc, char **argv)
{
    unittest1();
}


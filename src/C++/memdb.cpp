
#include <cassert>
#include <map>
#include <iostream>
#include <iterator>
#include <limits>
#include <string>
#include <vector>

using std::map; // C++ red-black tree
using std::string;
using std::vector;

/**
 * In-memory Postgress table
 */

// key + version_ts, where key is fixed-sized
typedef string PKey; 
typedef string RowVal; 

const int KeySize = 5;
const int64_t MaxVersion = std::numeric_limits<int64_t>::max();
const string NonVal = "";

class InMemDB {
    typedef map<PKey, RowVal> MemTable;

public:
    InMemDB() {}

    bool set(string key, string value, int64_t timestamp) {
        PKey pkey = makeKey(key, timestamp);
        auto iter = _memtable.find(pkey);
        if (iter != _memtable.end()) 
            return false;
        _memtable[pkey] = value;
        return true;
    }

    bool del(string key) {
        assert(0 == 1);
        return false;
    }

    // return the most recent version i.e. key with largest timestamp
    RowVal get(string key) {
        PKey pkey = makeKey(key, MaxVersion);
        auto iter = _memtable.upper_bound(pkey);
        if (iter != _memtable.begin()) {
            auto prevIter = std::prev(iter);
            const PKey& prevKey = prevIter->first;
            if (!prevKey.compare(0, KeySize, key))
                return prevIter->second;
        }
        return NonVal;
    }

    /**
     * Return the largest key whose version is <= timestamp, i.e. a row version
     * that has committed prior to the timestamp.
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
};

void unittest() 
{
    std::cout << "unittest" << "\n";
    InMemDB db;

    db.set("AAAAA", "val1", 1100);
    db.set("AAAAA", "val2", 1105);
    db.set("AAAAA", "val3", 1110);
    db.set("AAAAA", "val4", 1113);
    db.set("AAAAA", "val5", 1120);
    assert(db.get("AAAAA", 1099) == NonVal); // invalid range
    assert(db.get("AAAAA", 1100) == "val1");
    assert(db.get("AAAAA", 1105) == "val2");
    assert(db.get("AAAAA", 1110) == "val3");
    assert(db.get("AAAAA", 1108) == "val2"); // AAAAA1105
    assert(db.get("AAAAA", 1112) == "val3"); // AAAAA1110
    assert(db.get("AAAAA", 1116) == "val4"); // AAAAA1113
    assert(db.get("AAAAA", 1120) == "val5"); 
    assert(db.get("AAAAA", 1121) == "val5"); // AAAAA11120
    
    db.set("AAAAB", "val6", 1106);
    db.set("AAAAD", "val7", 1111);
    db.set("AAAAD", "val8", 1118);
    assert(db.get("AAAAB", 1107) == "val6"); // AAAAB1106
    assert(db.get("AAAAD", 1112) == "val7"); // AAAAD1111    
    assert(db.get("AAAAD", 1110) == NonVal); // invalid range

    db.set("AAAA9", "val9", 1009);
    db.set("AAAA9", "val10", 1015);
    assert(db.get("AAAA9", 1010) == "val9"); // AAAA91010
    assert(db.get("AAAAA", 1099) == NonVal); // still invalid range
    assert(db.get("AAAA9", 1014) == "val9"); // AAAA91009

    assert(db.get("AAAAA") == "val5");
    assert(db.get("AAAA9") == "val10");
    assert(db.get("AAAAB") == "val6");
    assert(db.get("AAAAD") == "val8");
}

int main(int argc, char **argv)
{
    unittest();
}


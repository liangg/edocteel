
#include <cassert>
#include <algorithm> // for_each
#include <map>
#include <memory> // shared_ptr
#include <iostream>
#include <iterator>
#include <limits>
#include <mutex> // mutex, unique_lock, scoped_lock, lock_guard
#include <string>
#include <unordered_map>
#include <vector>

using std::string;
using std::map;
using std::unordered_map;
using std::vector;

// If columns have different types (int, long, string), what'd be a right data structure?
// Or, use std::string for all column values, therefore unordered_map<string, vector<string>>

// Query: select table where col1 = XX and col2 = YY, 
//  -- full scan all rows
//  -- index unordered_map<vector<int>, vector<string>>, i.e. {col values} -> {pk1, pk2}

// Update: NO in-place update, insert updated row version instead
// Track all row versions with unordered_map<string, map<int, vector<int>>>, where
// map<int, vector<int>> use a row-level version number.

#define assertm(exp, msg) assert(((void)msg, exp))

std::ostream& operator<< (std::ostream& out, const vector<int>& colVec) {
    out << "(";
    for (auto col : colVec)
        out << col << ",";
    out << ")" << std::endl;
    return out;
}

/**
 * In-memory database table
 * 
 * 1, Table schmea is maintained in unordered_map<string, int>, column name to vector index
 * 2, Table PK should be unique
 */
class InMemTable {
    typedef unordered_map<string, vector<int>> MEM_TABLE;
    // key -> rows with version numbers
    typedef unordered_map<string, map<int, vector<int>>> VER_TABLE;

    const int TOMBSTONE = INT_MAX;

public:
    InMemTable(string pkey, const vector<string>& columns) {
        for (int i = 0; i < columns.size(); ++i) {
            string cn = columns.at(i);
            schema_[cn] = i;
        }
    }

    bool insert(string pkey, const vector<int>& columnVals) {    
        if (table_.count(pkey) > 0) {
            std::cout << "ERROR: pkey exists " << pkey << std::endl;
            return false;
        }
        table_[pkey] = std::move(columnVals);
        return true;
    }

    // update table set col1 = val1, col2 = val2
    bool update(string pkey, const vector<string>& columnNames, 
        const vector<int>& columnVals) {
        auto iter = table_.find(pkey);
        if (iter == table_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return false;
        }
        auto& colVec = iter->second;
        for (int i = 0; i < columnNames.size(); ++i) {
            auto schemaIter = schema_.find(columnNames[i]);
            if (schemaIter == schema_.end()) {
                std::cout << "ERROR: column not found " << columnNames[i] << std::endl;
                return false;
            }
            int vecIdx = schemaIter->second;
            colVec[vecIdx] = columnVals[i];
        }
        return true;
    }

    bool updateWithVersion(string pkey, const vector<string>& columnNames, 
        const vector<int>& columnVals) {
        auto iter = verTable_.find(pkey);
        if (iter == verTable_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return false;
        }
        auto& verRows = iter->second;
        auto verRowsIter = verRows.rbegin(); // latest row version
        auto curRowVec = verRowsIter->second;
        for (int i = 0; i < columnNames.size(); ++i) {
            auto schemaIter = schema_.find(columnNames[i]);
            if (schemaIter == schema_.end()) {
                std::cout << "ERROR: column not found " << columnNames[i] << std::endl;
                return false;
            }
            int vecIdx = schemaIter->second;
            curRowVec[vecIdx] = columnVals[i];
        }
        // append an updated row version
        verRows.emplace(verRowsIter->first + 1, curRowVec);
        return true;
    }

    vector<int> select(string pkey) const {
        auto iter = table_.find(pkey);
        if (iter == table_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return {};
        }
        return iter->second;
    }

    vector<int> selectVersioned(string pkey) const {
        auto iter = verTable_.find(pkey);
        if (iter == verTable_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return {};
        }
        auto& verRows = iter->second;
        auto verRowsIter = verRows.rbegin();
        return verRowsIter->second; // always return latest version
    }

    bool del(string pkey) { 
        auto iter = table_.find(pkey);
        if (iter == table_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return false;
        }
        // hard-delete        
        table_.erase(pkey);
        return false; 
    }

    bool delVersioned(string pkey) { 
        auto iter = verTable_.find(pkey);
        if (iter == verTable_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return {};
        }
        auto& verRows = iter->second;
        auto verRowsIter = verRows.rbegin();
        auto curRowVec = verRowsIter->second;
        // soft-delete with a tombstone marker and last version 
        verRows.emplace(TOMBSTONE, curRowVec);
        return false; 
    }

private:
    // Pkey -> vector of column values
    MEM_TABLE table_;
    // Pkey -> versioned rows
    VER_TABLE verTable_;
    // Table schema: column name -> column's vector index
    unordered_map<string, int> schema_;
    // Table level lock
    std::mutex tableLock_;
};


class InMemDB {
public:
    InMemDB() {}

    /**
     * A table has a PKey (string) and a few columns of type int.
     */
    bool createTable(string tableName, string pkey, const vector<string>& columns) {        
        auto iter = tables_.find(tableName);
        if (iter != tables_.end()) {
            std::cout << "Error: table already exists " << tableName << "\n";
            return false;
        }
        // do not use tables_.emplace(tableName, table) or tables_.insert({tableName, table})
        auto tablePtr = std::make_shared<InMemTable>(pkey, columns);
        tables_.insert({tableName, tablePtr});
        return true;
    }

    std::shared_ptr<InMemTable> findTable(string tableName) {
        auto iter = tables_.find(tableName);
        if (iter == tables_.end()) {
            std::cout << "Error: table not exists " << tableName << "\n";
            return nullptr;
        }
        return iter->second;
    }

private:
    // table_name -> InMemTable instance
    unordered_map<string /*table name*/, std::shared_ptr<InMemTable>> tables_;
};

void check(std::shared_ptr<InMemTable> table, string key) {
    auto res = table->select(key);
    auto printCols = [](int col) { std::cout << col << ","; };
    std::cout << "(";
    std::for_each(res.begin(), res.end(), printCols);
    std::cout << ")" << std::endl;
}

void test1(InMemDB& imdb) {
    imdb.createTable("table1", "pkey", {"col1", "col2"});
    std::shared_ptr<InMemTable> table1 = imdb.findTable("table1");
    assertm(table1 != nullptr, "table1 not found");
    table1->insert("AA", {1,2});
    table1->insert("AB", {3,4});
    check(table1, "AA");
    check(table1, "AB");
    check(table1, "AC"); // not found
    table1->update("AA", {"col2"}, {20});
    check(table1, "AA");
    table1->update("AB", {"col1", "col2"}, {30,40});
    check(table1, "AB");
}

int main(int argc, char  **argv) {
    InMemDB imdb;
    test1(imdb);
    return 0;
}

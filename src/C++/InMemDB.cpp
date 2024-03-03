
#include <cassert>
#include <algorithm>
#include <map>
#include <iostream>
#include <iterator>
#include <limits>
#include <mutex> // mutex, unique_lock, scoped_lock, lock_guard
#include <string>
#include <unordered_map>
#include <vector>

using std::string;
using std::unordered_map;
using std::vector;

// If columns have different types (int, long, string), what'd be a right data structure?
// Or, use std::string for all column values, therefore unordered_map<string, vector<string>>

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
        return true;
    }

    bool del(string pkey) { return false; }

    vector<int> select(string pkey) const {
        auto iter = table_.find(pkey);
        if (iter == table_.end()) {
            std::cout << "ERROR: pkey not found " << pkey << std::endl;
            return {};
        }
        return iter->second;
    }

private:
    // Pkey -> vector of column values
    MEM_TABLE table_;
    // Table schema: column name -> column's vector index
    unordered_map<string, int> schema_;
    // Table level lock
    std::mutex tableLock_;
};


class InMemDB {
    //typedef unordered_map<string /*table name*/, unordered_map<string, vector<int>>> DB_TABLES;

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
        InMemTable table(pkey, columns); // initialize table
        // tables_.insert({tableName, table});
        auto tablePtr = std::make_shared<InMemTable>(pkey, columns);
        allTables_.insert({tableName, tablePtr});
        return true;
    }

    std::shared_ptr<InMemTable> findTable(string tableName) {
        auto iter = allTables_.find(tableName);
        if (iter == allTables_.end()) {
            std::cout << "Error: table not exists " << tableName << "\n";
            return nullptr;
        }
        return iter->second;
    }

private:
    unordered_map<string /*table name*/, std::shared_ptr<InMemTable>> allTables_;
    unordered_map<string /*table name*/, InMemTable> tables_;
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
}

int main(int argc, char  **argv) {
    InMemDB imdb;
    test1(imdb);
    return 0;
}

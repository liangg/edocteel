
#include <cassert>
#include <map>
#include <iostream>
#include <iterator>
#include <limits>
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

    vector<int> select(string pkey) {
        MEM_TABLE::iterator iter = table_.find(pkey);
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
};


class InMemDB {
    typedef unordered_map<string /*table name*/, InMemTable> DB_TABLES;

public:
    InMemDB() {}

    /**
     * A table has a PKey (string) and a few columns of type int.
     */
    bool createTable(string tableName, string pkey, const vector<string>& columns) {        
        DB_TABLES::iterator iter = tables_.find(tableName);
        if (iter != tables_.end()) {
            std::cout << "Error: table already exists " << tableName << "\n";
            return false;
        }
        InMemTable table(pkey, columns); // initialize table
        tables_.insert({tableName, std::move(table)});
        return true;
    }

    InMemTable* findTable(string tableName) {
        DB_TABLES::iterator iter = tables_.find(tableName);
        if (iter == tables_.end()) {
            std::cout << "Error: table not exists " << tableName << "\n";
            return nullptr;
        }
        return &iter->second;
    }

private:
    DB_TABLES tables_;
};

void test1(InMemDB& imdb) {
    imdb.createTable("table1", "pkey", {"col1", "col2"});
    InMemTable* table1 = imdb.findTable("table1");
    assertm(table1 != nullptr, "table1 not found");
    table1->insert("AA", {1,2});
    table1->insert("AB", {3,4});
    std::cout << table1->select("AA");
    std::cout << table1->select("AB");
    std::cout << table1->select("AC");
}

int main(int argc, char  **argv) {
    InMemDB imdb;
    test1(imdb);
    return 0;
}

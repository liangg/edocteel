
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

class InMemTable {
public:
    InMemTable(string pkey, vector<string>& columns) {
        for (int i = 0; i < columns.size(); ++i) {
            string& cn = columns.at(i);
            schema_[cn] = i;
        }
    }

    bool insert(string pkey, vector<int>& columnVals) {}

    // update table set col1 = val1, col2 = val2
    bool update(string pkey, vector<string>& columnNames, vector<int>& columnVals) {}

    bool del(string pkey) { return false; }

    vector<int> select(string key) {}

private:
    // pkey -> column vector
    unordered_map<string, vector<int>> table_;
    // column name to column vector index
    unordered_map<string, int> schema_;
};


class InMemDB {
    typedef unordered_map<string, InMemTable> DB_TABLES;

public:
    InMemDB() {}

    bool createTable(string tableName, string pkey, vector<string>& columns) {        
        DB_TABLES::iterator iter = tables_.find(tableName);
        if (iter != tables_.end()) {
            std::cout << "Error: table already exists " << tableName << "\n";
            return false;
        }
        InMemTable table(pkey, columns);
        tables_[tableName] = std::move(table);
        return true;
    }

private:
    DB_TABLES tables_;
};
#include "RoomieMatcher.h"
#include <iostream>
#include <vector>
#include <queue>
#include <string>
#include <unordered_map>

// Simple bipartite graph: students and rooms
std::vector<std::string> students = {"Alice", "Bob", "Charlie", "Daisy"};
std::vector<std::string> rooms = {"A1", "A2"};
std::unordered_map<std::string, std::vector<std::string>> preferences = {
    {"Alice", {"A1"}},
    {"Bob", {"A2"}},
    {"Charlie", {"A1", "A2"}},
    {"Daisy", {"A2"}}
};

std::unordered_map<std::string, std::string> matches;

bool bfsMatch(const std::string& student) {
    for (const auto& room : preferences[student]) {
        if (matches.count(room) == 0) {
            matches[room] = student;
            return true;
        }
    }
    return false;
}

void RoomieMatcher::run() {
    std::cout << "\n[RoomieMatcher] Welcome to the RoomieMatcher module!\n";
    matches.clear();
    for (const auto& student : students) {
        bfsMatch(student);
    }
    std::cout << "Room Assignments:\n";
    for (const auto& room : rooms) {
        if (matches.count(room)) {
            std::cout << " - " << room << ": " << matches[room] << "\n";
        } else {
            std::cout << " - " << room << ": [Unassigned]\n";
        }
    }
}

#include "MessyMess.h"
#include <iostream>
#include <queue>
#include <vector>
#include <string>
#include <map>

struct Dish {
    std::string name;
    int rating;
};

std::vector<Dish> dishes = {
    {"Paneer", 5}, {"Dal", 3}, {"Rice", 4}, {"Aloo", 2}, {"Chole", 4}
};

void MessyMess::run() {
    std::cout << "\n[MessyMess] Welcome to the MessyMess module!\n";
    std::priority_queue<std::pair<int, std::string>> heap;
    for (auto& d : dishes) heap.push({d.rating, d.name});
    std::cout << "Top Dishes (by rating):\n";
    int count = 0;
    while (!heap.empty() && count < 3) {
        auto top = heap.top(); heap.pop();
        std::cout << " - " << top.second << " (" << top.first << "/5)\n";
        count++;
    }

    // Simple graph coloring for week planner
    std::map<std::string, std::string> dayDish = {
        {"Mon", "Paneer"}, {"Tue", "Dal"}, {"Wed", "Rice"}, {"Thu", "Aloo"}, {"Fri", "Chole"}
    };
    std::cout << "\nWeek Planner:\n";
    for (auto& dd : dayDish) {
        std::cout << " - " << dd.first << ": " << dd.second << "\n";
    }
}

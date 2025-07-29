#include "LaundryLoad.h"
#include <iostream>
#include <vector>
#include <algorithm>

std::vector<std::pair<int, int>> slots = {{9, 10}, {10, 11}, {11, 12}}; // hour intervals

void LaundryLoad::run() {
    std::cout << "\n[LaundryLoad] Welcome to the LaundryLoad module!\n";
    std::cout << "Available slots:\n";
    for (auto& s : slots) {
        std::cout << " - " << s.first << ":00 to " << s.second << ":00\n";
    }
    int start, end;
    std::cout << "Book a slot (start hour): "; std::cin >> start;
    std::cout << "End hour: "; std::cin >> end;
    bool clash = false;
    for (auto& s : slots) {
        if (!(end <= s.first || start >= s.second)) {
            clash = true;
            break;
        }
    }
    if (!clash) {
        slots.push_back({start, end});
        std::cout << "Slot booked!\n";
    } else {
        std::cout << "Clash detected! Choose another slot.\n";
    }
}

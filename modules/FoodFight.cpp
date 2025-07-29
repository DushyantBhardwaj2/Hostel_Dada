#include "FoodFight.h"
#include <iostream>
#include <vector>
#include <algorithm>

std::vector<int> entryTimes = {1, 2, 2, 3, 4, 4, 5}; // hours

void FoodFight::run() {
    std::cout << "\n[FoodFight] Welcome to the FoodFight module!\n";
    int window = 2;
    std::vector<int> prefix(entryTimes.size() + 1, 0);
    for (size_t i = 0; i < entryTimes.size(); ++i) prefix[i+1] = prefix[i] + 1;
    std::cout << "Queue prediction (sliding window " << window << " hours):\n";
    for (size_t i = 0; i + window <= entryTimes.size(); ++i) {
        int cnt = prefix[i+window] - prefix[i];
        std::cout << " - Time " << entryTimes[i] << " to " << entryTimes[i+window-1] << ": " << cnt << " people\n";
    }
    int bestTime = entryTimes[0], minQueue = entryTimes.size();
    for (size_t i = 0; i + window <= entryTimes.size(); ++i) {
        int cnt = prefix[i+window] - prefix[i];
        if (cnt < minQueue) {
            minQueue = cnt;
            bestTime = entryTimes[i];
        }
    }
    std::cout << "\nBest time to enter: " << bestTime << " (" << minQueue << " people in queue)\n";
}

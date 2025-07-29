#include "SnackCart.h"
#include <iostream>
#include <unordered_map>
#include <queue>
#include <vector>
#include <string>
#include <algorithm>

struct Snack {
    std::string name;
    int quantity;
    int price;
    std::string expiry;
};

struct ExpiryCompare {
    bool operator()(const Snack& a, const Snack& b) {
        return a.expiry > b.expiry;
    }
};

std::unordered_map<std::string, Snack> stock = {
    {"Kurkure", {"Kurkure", 10, 15, "2025-09-01"}},
    {"Lays", {"Lays", 5, 20, "2025-08-10"}},
    {"Oreo", {"Oreo", 8, 25, "2025-10-05"}}
};

std::vector<std::pair<std::string, int>> salesHistory; // name, profit

void SnackCart::run() {
    std::cout << "\n[SnackCart] Welcome to the SnackCart module!\n";
    while (true) {
        std::cout << "\n1. View Stock\n2. Buy Snack\n3. View Profit\n0. Back\n> ";
        int opt; std::cin >> opt;
        if (opt == 0) break;
        if (opt == 1) {
            std::cout << "> Stock:\n";
            std::priority_queue<Snack, std::vector<Snack>, ExpiryCompare> pq;
            for (auto& kv : stock) pq.push(kv.second);
            while (!pq.empty()) {
                Snack s = pq.top(); pq.pop();
                std::cout << " - " << s.name << " x" << s.quantity << " \u20B9" << s.price << " | Expires: " << s.expiry << "\n";
            }
        } else if (opt == 2) {
            std::string name; int qty;
            std::cout << "Enter snack name: "; std::cin >> name;
            std::cout << "Enter quantity: "; std::cin >> qty;
            if (stock.count(name) && stock[name].quantity >= qty) {
                stock[name].quantity -= qty;
                int profit = qty * stock[name].price;
                salesHistory.push_back({name, profit});
                std::cout << "Purchased!\n";
            } else {
                std::cout << "Not enough stock or invalid snack.\n";
            }
        } else if (opt == 3) {
            int total = 0;
            std::sort(salesHistory.begin(), salesHistory.end(), [](auto& a, auto& b){ return a.second > b.second; });
            std::cout << "Profit History:\n";
            for (auto& p : salesHistory) {
                std::cout << " - " << p.first << ": \u20B9" << p.second << "\n";
                total += p.second;
            }
            std::cout << "Total Profit: \u20B9" << total << "\n";
        } else {
            std::cout << "Invalid option.\n";
        }
    }
}

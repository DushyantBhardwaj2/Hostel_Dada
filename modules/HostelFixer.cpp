#include "HostelFixer.h"
#include <iostream>
#include <queue>
#include <vector>
#include <string>
#include <map>

struct Task {
    std::string desc;
    int urgency;
    bool operator>(const Task& other) const { return urgency > other.urgency; }
};

std::priority_queue<Task, std::vector<Task>, std::greater<Task>> tasks;

std::map<std::string, std::vector<std::pair<std::string, int>>> graph = {
    {"Gate", {{"Mess", 2}, {"Laundry", 4}}},
    {"Mess", {{"Gate", 2}, {"Laundry", 1}}},
    {"Laundry", {{"Gate", 4}, {"Mess", 1}}}
};

void HostelFixer::run() {
    std::cout << "\n[HostelFixer] Welcome to the HostelFixer module!\n";
    std::cout << "Add a maintenance task (desc): ";
    std::string desc; std::cin >> desc;
    std::cout << "Urgency (1-10): ";
    int urg; std::cin >> urg;
    tasks.push({desc, urg});
    std::cout << "\nUrgent Tasks:\n";
    auto temp = tasks;
    while (!temp.empty()) {
        auto t = temp.top(); temp.pop();
        std::cout << " - " << t.desc << " (Urgency: " << t.urgency << ")\n";
    }

    // Simple Dijkstra for shortest path
    std::string src = "Gate", dest = "Laundry";
    std::map<std::string, int> dist;
    for (auto& kv : graph) dist[kv.first] = 1e9;
    dist[src] = 0;
    std::priority_queue<std::pair<int, std::string>, std::vector<std::pair<int, std::string>>, std::greater<>> pq;
    pq.push({0, src});
    while (!pq.empty()) {
        auto [d, u] = pq.top(); pq.pop();
        if (d > dist[u]) continue;
        for (auto& [v, w] : graph[u]) {
            if (dist[v] > dist[u] + w) {
                dist[v] = dist[u] + w;
                pq.push({dist[v], v});
            }
        }
    }
    std::cout << "\nShortest path from " << src << " to " << dest << ": " << dist[dest] << " units\n";
}

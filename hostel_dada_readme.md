# 📦 Hostel Dada – DSA-Centric Terminal-Based Hostel Life Manager

Welcome to **Hostel Dada**, a powerful, data-structure-focused, terminal-based project built in **C++**, designed to solve everyday hostel life problems. This project is engineered to demonstrate real-world applications of advanced **DSA concepts** while maintaining a clean, testable structure that can be extended by tools like **GitHub Copilot**.

---

## 📌 Project Summary

- **Type:** Terminal-based C++ application
- **UI:** Text-based CLI (no GUI)
- **Backend:** Optional online sync using **Firebase REST API**
- **Focus:** DSA-first, minimal UI
- **Tools:** VS Code, libcurl, nlohmann/json, CMake
- **Author Style:** C++17, modular structure, class-based design, inline helper methods, consistent naming, minimal STL abstraction leakage

---

## 🔄 What You Can Change / Extend

### 🔧 Extensible Components:

- Replace Firebase with another REST API or local database.
- Add file-based persistence (JSON storage locally).
- Add multi-threading for parallel request handling.
- Integrate a minimal GUI with Qt or Compose Desktop (optional).
- Implement analytics modules (top 5 snacks sold, busiest mess hour, etc.).
- Unit test integration using GoogleTest.

---

## 📁 Project Structure & Files

```
/HostelDada
├── main.cpp                  # Entry point, menu dispatcher
├── CMakeLists.txt            # CMake config
├── /modules
│   ├── SnackCart.cpp/.h
│   ├── RoomieMatcher.cpp/.h
│   ├── MessyMess.cpp/.h
│   ├── LaundryLoad.cpp/.h
│   ├── HostelFixer.cpp/.h
│   └── FoodFight.cpp/.h
├── /firebase
│   └── FirebaseAPI.cpp/.h    # libcurl-based HTTP handlers
├── /utils
│   ├── JSONHelper.cpp/.h     # nlohmann/json usage helpers
│   └── Logger.cpp/.h         # Terminal logging tools
├── /data
│   └── config.json           # Firebase API keys or dummy data
└── README.md
```

---

## 🧩 Modules & Working

### 1. 🛒 SnackCart – Inventory Manager

- Store snacks using `unordered_map`
- Maintain expiry order via `priority_queue`
- Calculate profit with sorted history
- Syncs with Firebase stock list

### 2. 👯 RoomieMatcher – Smart Roommate Matcher

- Collect student preferences
- Build weighted bipartite graph
- Use BFS/DFS for maximum matching
- Outputs best compatible pairings

### 3. 🍛 MessyMess – Menu Optimization System

- Store ratings per dish
- Top dishes from heap
- Week planner via graph coloring
- Ensures diversity + student vote impact

### 4. 🧺 LaundryLoad Balancer – Slot Booking

- Store time intervals in `vector<pair<int, int>>`
- Use Interval Tree for overlap detection
- Binary Search for slot fitting
- Avoids booking clashes, suggests best slots

### 5. 🧰 HostelFixer – Maintenance Prioritizer

- Each task: priority + location
- Min-heap to get urgent task
- Hostel layout as graph (adjacency list)
- Dijkstra for routing multiple tasks

### 6. 🍽️ FoodFight – Queue Predictor

- Collect entry times per day
- Use sliding window + prefix sum array
- Predict queue length at given hour
- Show suggestion: "Best Time to Visit"

---

## 🧠 DSA Concepts Covered

| Concept                       | Modules                    | Real-World Application                |
| ----------------------------- | -------------------------- | ------------------------------------- |
| HashMap                       | SnackCart, MessyMess       | Quick lookups for stock, rating data  |
| Priority Queue (Min/Max Heap) | SnackCart, HostelFixer     | Sorting by expiry/profit or urgency   |
| Graphs & BFS/DFS              | RoomieMatcher, HostelFixer | Matching algorithm, path finding      |
| Bipartite Matching            | RoomieMatcher              | Compatibility-based roommate matching |
| Interval Tree                 | LaundryLoad                | Conflict-free time slot management    |
| Sliding Window                | FoodFight                  | Queue prediction from past windows    |
| Graph Coloring                | MessyMess                  | Weekly menu diversification           |
| Dijkstra                      | HostelFixer                | Optimize task travel time             |

---

## 🖥️ UI + Terminal Working

### Interface:

- Simple CLI (Command Line Interface) using `cin/cout`
- Option-based navigation (e.g., 1 to 6)
- Output formatted with indentations, symbols, and status bars
- Example:

```bash
Welcome to Hostel Dada 👋
Choose an Option:
1. SnackCart 🍫
2. RoomieMatcher 👯
3. MessyMess 🍛
...
> 1

> Stock:
 - Kurkure x10 ₹15 | Expires: 2025-09-01
 - Lays x5 ₹20 | Expires: 2025-08-10
```

---

## 🧑‍💻 Code Style Guide (from Author Patterns)

From previously stored project data, the author typically:

- Uses `class` for modules with `.h/.cpp` split
- Applies `private:` members and `public:` methods structure
- Follows naming: `camelCase` for variables, `PascalCase` for classes
- Always uses inline comments (`//`) for non-obvious logic
- Breaks long logic into helper methods (within private scope)
- Avoids magic numbers → uses `const` or `#define`
- Consistent use of STL: prefers `unordered_map` over `map`, `vector` over arrays
- Practices modularity and separation of concerns

---

## 🔄 Firebase Online Sync (Optional)

- Use `libcurl` to make REST calls to Firebase Realtime DB
- JSON handled using `nlohmann/json.hpp`
- CRUD support for snacks, bookings, tasks
- `/firebase/FirebaseAPI.cpp` contains generic `GET`, `POST`, `PATCH`, `DELETE` methods

---

## 🧪 Testing & Debugging

- Each module has CLI test stubs
- Firebase interaction can be toggled via a flag
- `Logger.h` used for debug prints with timestamps
- Potential for GoogleTest or manual CLI scripts

---

## 💡 Future Enhancements

- Add login/auth for students/admin
- Expand Firebase usage to enable real-time updates
- Create analytics dashboard from CLI outputs
- Add file export (e.g., daily logs to JSON/CSV)
- Convert CLI app to REST API microservice

---

## 🚀 Getting Started

### Requirements:

- C++17
- VS Code
- CMake
- libcurl
- nlohmann/json

### Compile:

```bash
mkdir build && cd build
cmake ..
make
./HostelDada
```

---

> **Made by humans, with human logic.** Focused on solving real-world problems, not just running code.

---

## 🤝 Contributions

Pull requests welcome. If you're using GitHub Copilot, let this README guide you on logic, structure, and style.


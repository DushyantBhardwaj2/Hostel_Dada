
#include <iostream>
#include <vector>
#include <string>


#include "modules/SnackCart.h"
#include "modules/RoomieMatcher.h"
#include "modules/MessyMess.h"
#include "modules/LaundryLoad.h"
#include "modules/HostelFixer.h"
#include "modules/FoodFight.h"

int main() {
    std::vector<std::string> modules = {
        "SnackCart \uD83C\uDF6B",
        "RoomieMatcher \uD83D\uDC6F",
        "MessyMess \uD83C\uDF5B",
        "LaundryLoad \uD83D\uDC5A",
        "HostelFixer \uD83D\uDEE0",
        "FoodFight \uD83C\uDF57"
    };

    std::cout << "\nWelcome to Hostel Dada \uD83D\uDC4B\n";
    while (true) {
        std::cout << "\nChoose an Option:\n";
        for (size_t i = 0; i < modules.size(); ++i) {
            std::cout << "  " << (i + 1) << ". " << modules[i] << "\n";
        }
        std::cout << "  0. Exit\n> ";
        int choice;
        std::cin >> choice;
        if (choice == 0) break;
        switch (choice) {
            case 1: SnackCart::run(); break;
            case 2: RoomieMatcher::run(); break;
            case 3: MessyMess::run(); break;
            case 4: LaundryLoad::run(); break;
            case 5: HostelFixer::run(); break;
            case 6: FoodFight::run(); break;
            default:
                std::cout << "Invalid option. Please try again.\n";
        }
    }
    std::cout << "\nThank you for using Hostel Dada!\n";
    return 0;
}

// ...existing code...

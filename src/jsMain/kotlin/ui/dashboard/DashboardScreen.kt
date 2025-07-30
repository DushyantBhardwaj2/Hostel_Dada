package ui.dashboard

import androidx.compose.runtime.*
import models.User
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import ui.theme.AppColors
import ui.theme.AppSpacing
import ui.theme.ModuleColors
import auth.Auth
import kotlinx.coroutines.launch

/**
 * 🏠 Dashboard Screen Component
 * 
 * Main dashboard showing all 5 hostel management modules
 * with responsive grid layout and quick actions
 */
@Composable
fun DashboardScreen(user: User) {
    var selectedModule by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    Div({
        style {
            minHeight(100.vh)
            backgroundColor(AppColors.background)
        }
    }) {
        // Top Navigation Bar
        TopNavigationBar(user) {
            scope.launch {
                Auth.manager.signOut()
            }
        }
        
        // Dashboard Content
        Div({
            style {
                maxWidth(1200.px)
                margin(0.px, "auto")
                padding(AppSpacing.large.px)
            }
        }) {
            // Welcome Section
            WelcomeSection(user)
            
            // Modules Grid
            ModulesGrid {
                selectedModule = it
            }
            
            // Quick Stats
            QuickStatsSection()
        }
    }
}

/**
 * 🧭 Top Navigation Bar
 */
@Composable
private fun TopNavigationBar(user: User, onSignOut: () -> Unit) {
    Div({
        style {
            backgroundColor(AppColors.surface)
            borderBottom(1.px, LineStyle.Solid, AppColors.border)
            padding(AppSpacing.medium.px, AppSpacing.large.px)
        }
    }) {
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                maxWidth(1200.px)
                margin(0.px, "auto")
            }
        }) {
            // Logo & Title
            Div({
                style {
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                    gap(AppSpacing.medium.px)
                }
            }) {
                H1({
                    style {
                        fontSize(24.px)
                        fontWeight("bold")
                        color(AppColors.primary)
                        margin(0.px)
                    }
                }) {
                    Text("🏠 Hostel Dada")
                }
                
                Span({
                    style {
                        backgroundColor(AppColors.primaryLight)
                        color(Color.white)
                        padding(4.px, 8.px)
                        borderRadius(12.px)
                        fontSize(12.px)
                        fontWeight("500")
                    }
                }) {
                    Text(user.hostelId)
                }
            }
            
            // User Menu
            Div({
                style {
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                    gap(AppSpacing.medium.px)
                }
            }) {
                Span({
                    style {
                        color(AppColors.textSecondary)
                        fontSize(14.px)
                    }
                }) {
                    Text("Welcome, ${user.getDisplayName()}")
                }
                
                Button({
                    style {
                        backgroundColor(AppColors.error)
                        color(Color.white)
                        border(0.px)
                        padding(8.px, 16.px)
                        borderRadius(6.px)
                        fontSize(14.px)
                        cursor("pointer")
                    }
                    onClick { onSignOut() }
                }) {
                    Text("Sign Out")
                }
            }
        }
    }
}

/**
 * 👋 Welcome Section
 */
@Composable
private fun WelcomeSection(user: User) {
    Div({
        style {
            backgroundColor(AppColors.surface)
            borderRadius(12.px)
            padding(AppSpacing.large.px)
            marginBottom(AppSpacing.large.px)
            boxShadow("0 2px 8px rgba(0,0,0,0.1)")
        }
    }) {
        H2({
            style {
                fontSize(28.px)
                fontWeight("bold")
                color(AppColors.textPrimary)
                marginBottom(AppSpacing.small.px)
            }
        }) {
            Text("Welcome back, ${user.getDisplayName()}! 👋")
        }
        
        P({
            style {
                fontSize(16.px)
                color(AppColors.textSecondary)
                lineHeight(1.5)
                margin(0.px)
            }
        }) {
            Text("Manage your hostel life with our comprehensive suite of tools. Choose a module below to get started.")
        }
        
        // User Info Chips
        Div({
            style {
                display(DisplayStyle.Flex)
                gap(AppSpacing.small.px)
                marginTop(AppSpacing.medium.px)
                flexWrap(FlexWrap.Wrap)
            }
        }) {
            InfoChip("📍", "Room ${user.roomNumber}")
            InfoChip("🏠", user.hostelId)
            InfoChip("👤", user.role.name.lowercase().replaceFirstChar { it.uppercase() })
        }
    }
}

/**
 * 🏷️ Info Chip Component
 */
@Composable
private fun InfoChip(icon: String, text: String) {
    Div({
        style {
            display(DisplayStyle.InlineBlock)
            backgroundColor(AppColors.surfaceVariant)
            padding(6.px, 12.px)
            borderRadius(16.px)
            fontSize(14.px)
            color(AppColors.textSecondary)
        }
    }) {
        Text("$icon $text")
    }
}

/**
 * 🎯 Modules Grid
 */
@Composable
private fun ModulesGrid(onModuleClick: (String) -> Unit) {
    Div({
        style {
            marginBottom(AppSpacing.large.px)
        }
    }) {
        H3({
            style {
                fontSize(20.px)
                fontWeight("600")
                color(AppColors.textPrimary)
                marginBottom(AppSpacing.medium.px)
            }
        }) {
            Text("🎯 Hostel Management Modules")
        }
        
        Div({
            style {
                display(DisplayStyle.Grid)
                property("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))")
                gap(AppSpacing.medium.px)
            }
        }) {
            // Module Cards
            ModuleCard(
                title = "SnackCart",
                description = "Order snacks and beverages with smart inventory management",
                icon = "🍿",
                color = AppColors.snackCart,
                features = listOf("Real-time inventory", "Order tracking", "Payment integration")
            ) { onModuleClick("snackcart") }
            
            ModuleCard(
                title = "RoomieMatcher",
                description = "Find compatible roommates using advanced matching algorithms",
                icon = "🤝",
                color = AppColors.roomieMatcher,
                features = listOf("Compatibility scoring", "Preference matching", "Room optimization")
            ) { onModuleClick("roomiematcher") }
            
            ModuleCard(
                title = "LaundryBalancer",
                description = "Optimize laundry scheduling with load balancing algorithms",
                icon = "👕",
                color = AppColors.laundryBalancer,
                features = listOf("Schedule optimization", "Load balancing", "Time tracking")
            ) { onModuleClick("laundrybalancer") }
            
            ModuleCard(
                title = "MessyMess",
                description = "Manage mess operations with feedback and meal planning",
                icon = "🍽️",
                color = AppColors.messyMess,
                features = listOf("Meal feedback", "Menu planning", "Nutritional data")
            ) { onModuleClick("messymess") }
            
            ModuleCard(
                title = "HostelFixer",
                description = "Report and track maintenance issues efficiently",
                icon = "🔧",
                color = AppColors.hostelFixer,
                features = listOf("Issue reporting", "Priority system", "Status tracking")
            ) { onModuleClick("hostelfixer") }
        }
    }
}

/**
 * 🎴 Module Card Component
 */
@Composable
private fun ModuleCard(
    title: String,
    description: String,
    icon: String,
    color: Color,
    features: List<String>,
    onClick: () -> Unit
) {
    Div({
        style {
            backgroundColor(AppColors.surface)
            borderRadius(12.px)
            padding(AppSpacing.large.px)
            boxShadow("0 2px 8px rgba(0,0,0,0.1)")
            cursor("pointer")
            transition("all 0.2s ease")
            border(2.px, LineStyle.Solid, Color.transparent)
            
            hover {
                boxShadow("0 8px 24px rgba(0,0,0,0.15)")
                transform { translateY((-4).px) }
                borderColor(color)
            }
        }
        onClick { onClick() }
    }) {
        // Header
        Div({
            style {
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                marginBottom(AppSpacing.medium.px)
            }
        }) {
            Div({
                style {
                    fontSize(32.px)
                    marginRight(AppSpacing.medium.px)
                }
            }) {
                Text(icon)
            }
            
            H4({
                style {
                    fontSize(20.px)
                    fontWeight("600")
                    color(color)
                    margin(0.px)
                }
            }) {
                Text(title)
            }
        }
        
        // Description
        P({
            style {
                fontSize(14.px)
                color(AppColors.textSecondary)
                lineHeight(1.5)
                marginBottom(AppSpacing.medium.px)
            }
        }) {
            Text(description)
        }
        
        // Features List
        Div({
            style {
                marginBottom(AppSpacing.medium.px)
            }
        }) {
            features.forEach { feature ->
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        marginBottom(AppSpacing.small.px)
                        fontSize(12.px)
                        color(AppColors.textSecondary)
                    }
                }) {
                    Span({
                        style {
                            marginRight(AppSpacing.small.px)
                            color(color)
                        }
                    }) {
                        Text("✓")
                    }
                    Text(feature)
                }
            }
        }
        
        // Action Button
        Button({
            style {
                backgroundColor(color)
                color(Color.white)
                border(0.px)
                padding(10.px, 16.px)
                borderRadius(6.px)
                fontSize(14.px)
                fontWeight("500")
                cursor("pointer")
                width(100.percent)
                
                hover {
                    opacity(0.9)
                }
            }
        }) {
            Text("Open $title")
        }
    }
}

/**
 * 📊 Quick Stats Section
 */
@Composable
private fun QuickStatsSection() {
    Div({
        style {
            backgroundColor(AppColors.surface)
            borderRadius(12.px)
            padding(AppSpacing.large.px)
            boxShadow("0 2px 8px rgba(0,0,0,0.1)")
        }
    }) {
        H3({
            style {
                fontSize(20.px)
                fontWeight("600")
                color(AppColors.textPrimary)
                marginBottom(AppSpacing.medium.px)
            }
        }) {
            Text("📊 Quick Stats")
        }
        
        Div({
            style {
                display(DisplayStyle.Grid)
                property("grid-template-columns", "repeat(auto-fit, minmax(150px, 1fr))")
                gap(AppSpacing.medium.px)
            }
        }) {
            StatCard("🍿", "12", "Active Orders")
            StatCard("🤝", "3", "Roommate Matches")
            StatCard("👕", "5", "Laundry Slots")
            StatCard("🔧", "2", "Open Issues")
        }
    }
}

/**
 * 📈 Stat Card Component
 */
@Composable
private fun StatCard(icon: String, value: String, label: String) {
    Div({
        style {
            textAlign("center")
            padding(AppSpacing.medium.px)
            backgroundColor(AppColors.surfaceVariant)
            borderRadius(8.px)
        }
    }) {
        Div({
            style {
                fontSize(24.px)
                marginBottom(AppSpacing.small.px)
            }
        }) {
            Text(icon)
        }
        
        Div({
            style {
                fontSize(24.px)
                fontWeight("bold")
                color(AppColors.primary)
                marginBottom(4.px)
            }
        }) {
            Text(value)
        }
        
        Div({
            style {
                fontSize(12.px)
                color(AppColors.textSecondary)
            }
        }) {
            Text(label)
        }
    }
}

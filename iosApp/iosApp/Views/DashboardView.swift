import SwiftUI

/**
 * Dashboard View
 *
 * Main home screen showing user stats and quick access to features
 */
struct DashboardView: View {
    @EnvironmentObject var appState: AppState
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Welcome Header
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Welcome back!")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        Text(appState.currentUser?.displayName ?? "User")
                            .font(.title)
                            .fontWeight(.bold)
                    }
                    Spacer()
                    
                    // Profile Avatar
                    Circle()
                        .fill(Color.orange.opacity(0.2))
                        .frame(width: 50, height: 50)
                        .overlay(
                            Text(String((appState.currentUser?.displayName ?? "U").prefix(1)))
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.orange)
                        )
                }
                .padding(.horizontal)
                
                // Quick Stats Card
                HStack(spacing: 0) {
                    StatItem(icon: "cart.fill", value: "5", label: "Orders")
                    Divider()
                        .frame(height: 40)
                    StatItem(icon: "person.2.fill", value: "12", label: "Matches")
                    Divider()
                        .frame(height: 40)
                    StatItem(icon: "star.fill", value: "4.8", label: "Rating")
                }
                .padding()
                .background(Color.orange.opacity(0.1))
                .cornerRadius(16)
                .padding(.horizontal)
                
                // Features Section
                VStack(alignment: .leading, spacing: 16) {
                    Text("Features")
                        .font(.title2)
                        .fontWeight(.bold)
                        .padding(.horizontal)
                    
                    HStack(spacing: 16) {
                        FeatureCard(
                            title: "SnackCart",
                            description: "Order snacks from hostel shops",
                            icon: "cart.fill",
                            color: .orange
                        )
                        
                        FeatureCard(
                            title: "Roomie",
                            description: "Find your perfect roommate",
                            icon: "person.2.fill",
                            color: .purple
                        )
                    }
                    .padding(.horizontal)
                }
                
                // Recent Activity Section
                VStack(alignment: .leading, spacing: 16) {
                    Text("Recent Activity")
                        .font(.title2)
                        .fontWeight(.bold)
                        .padding(.horizontal)
                    
                    VStack(spacing: 0) {
                        ActivityItem(
                            title: "Order #1234 delivered",
                            subtitle: "2 hours ago",
                            icon: "checkmark.circle.fill"
                        )
                        Divider()
                            .padding(.leading, 44)
                        ActivityItem(
                            title: "New roomie match: Rahul",
                            subtitle: "5 hours ago",
                            icon: "person.badge.plus"
                        )
                        Divider()
                            .padding(.leading, 44)
                        ActivityItem(
                            title: "Profile updated",
                            subtitle: "1 day ago",
                            icon: "pencil.circle.fill"
                        )
                    }
                    .background(Color(.systemBackground))
                    .cornerRadius(16)
                    .shadow(color: .black.opacity(0.05), radius: 10)
                    .padding(.horizontal)
                }
            }
            .padding(.vertical)
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Home")
    }
}

// MARK: - Stat Item
struct StatItem: View {
    let icon: String
    let value: String
    let label: String
    
    var body: some View {
        VStack(spacing: 4) {
            Image(systemName: icon)
                .foregroundColor(.orange)
            Text(value)
                .font(.title2)
                .fontWeight(.bold)
            Text(label)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
    }
}

// MARK: - Feature Card
struct FeatureCard: View {
    let title: String
    let description: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Circle()
                .fill(color.opacity(0.2))
                .frame(width: 48, height: 48)
                .overlay(
                    Image(systemName: icon)
                        .foregroundColor(color)
                )
            
            Spacer()
            
            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                    .fontWeight(.bold)
                Text(description)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
            }
        }
        .padding()
        .frame(maxWidth: .infinity, minHeight: 160, alignment: .leading)
        .background(color.opacity(0.1))
        .cornerRadius(16)
    }
}

// MARK: - Activity Item
struct ActivityItem: View {
    let title: String
    let subtitle: String
    let icon: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(.orange)
                .frame(width: 32)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.subheadline)
                    .fontWeight(.medium)
                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
        .padding()
    }
}

#Preview {
    NavigationStack {
        DashboardView()
            .environmentObject(AppState())
    }
}

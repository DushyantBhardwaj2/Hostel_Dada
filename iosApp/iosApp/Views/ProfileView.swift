import SwiftUI

/**
 * Profile View
 *
 * User profile settings and app configuration
 */
struct ProfileView: View {
    @EnvironmentObject var appState: AppState
    @State private var showLogoutAlert = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Profile Header
                VStack(spacing: 16) {
                    Circle()
                        .fill(Color.orange.opacity(0.2))
                        .frame(width: 120, height: 120)
                        .overlay(
                            Text(String((appState.currentUser?.displayName ?? "U").prefix(1)))
                                .font(.system(size: 48))
                                .fontWeight(.bold)
                                .foregroundColor(.orange)
                        )
                    
                    Text(appState.currentUser?.displayName ?? "User")
                        .font(.title)
                        .fontWeight(.bold)
                    
                    Text(appState.currentUser?.email ?? "user@hosteldada.com")
                        .foregroundColor(.secondary)
                }
                .padding(.top, 20)
                
                // Profile Options
                VStack(spacing: 2) {
                    ProfileOption(icon: "pencil", title: "Edit Profile", color: .orange)
                    ProfileOption(icon: "gearshape.fill", title: "Settings", color: .gray)
                    ProfileOption(icon: "bell.fill", title: "Notifications", color: .blue)
                    ProfileOption(icon: "questionmark.circle.fill", title: "Help & Support", color: .green)
                    ProfileOption(icon: "info.circle.fill", title: "About", color: .purple)
                }
                .background(Color(.systemBackground))
                .cornerRadius(16)
                .padding(.horizontal)
                
                // App Info
                VStack(spacing: 8) {
                    Text("Hostel Dada")
                        .font(.headline)
                    Text("Version 1.0.0")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text("Built with Kotlin Multiplatform")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 20)
                
                // Logout Button
                Button(action: { showLogoutAlert = true }) {
                    HStack {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                        Text("Logout")
                    }
                    .foregroundColor(.red)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.red.opacity(0.1))
                    .cornerRadius(16)
                }
                .padding(.horizontal)
                .padding(.top, 20)
            }
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Profile")
        .alert("Logout", isPresented: $showLogoutAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Logout", role: .destructive) {
                logout()
            }
        } message: {
            Text("Are you sure you want to logout?")
        }
    }
    
    private func logout() {
        // Firebase logout
        appState.isLoggedIn = false
        appState.currentUser = nil
    }
}

// MARK: - Profile Option
struct ProfileOption: View {
    let icon: String
    let title: String
    let color: Color
    
    var body: some View {
        Button(action: {}) {
            HStack(spacing: 16) {
                Image(systemName: icon)
                    .foregroundColor(color)
                    .frame(width: 24)
                
                Text(title)
                    .foregroundColor(.primary)
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding()
        }
    }
}

#Preview {
    NavigationStack {
        ProfileView()
            .environmentObject(AppState())
    }
}

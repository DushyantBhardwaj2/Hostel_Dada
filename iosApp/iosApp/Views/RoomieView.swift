import SwiftUI

/**
 * Roomie Matcher View
 *
 * Find compatible roommates using weighted graph algorithm
 * Calculates compatibility based on lifestyle preferences
 */
struct RoomieView: View {
    @State private var selectedTab = 0
    
    var body: some View {
        VStack(spacing: 0) {
            // Custom Tab Bar
            HStack(spacing: 0) {
                TabButton(title: "Profile", isSelected: selectedTab == 0) {
                    selectedTab = 0
                }
                TabButton(title: "Matches", isSelected: selectedTab == 1) {
                    selectedTab = 1
                }
                TabButton(title: "Requests", isSelected: selectedTab == 2) {
                    selectedTab = 2
                }
            }
            .padding(.horizontal)
            .padding(.top, 8)
            
            // Tab Content
            TabView(selection: $selectedTab) {
                RoomieProfileTab()
                    .tag(0)
                
                RoomieMatchesTab()
                    .tag(1)
                
                RoomieRequestsTab()
                    .tag(2)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
        }
        .navigationTitle("Roomie Matcher")
    }
}

// MARK: - Tab Button
struct TabButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Text(title)
                    .font(.subheadline)
                    .fontWeight(isSelected ? .semibold : .regular)
                    .foregroundColor(isSelected ? .purple : .secondary)
                
                Rectangle()
                    .fill(isSelected ? Color.purple : Color.clear)
                    .frame(height: 2)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

// MARK: - Profile Tab
struct RoomieProfileTab: View {
    @State private var bio = ""
    @State private var sleepSchedule = "Flexible"
    @State private var cleanlinessLevel: Double = 3
    @State private var studyHabits = "Moderate"
    @State private var guestPolicy = "Occasional"
    @State private var noiseTolerance: Double = 3
    @State private var isSaving = false
    
    let sleepOptions = ["Early Bird", "Night Owl", "Flexible"]
    let studyOptions = ["Intensive", "Moderate", "Casual"]
    let guestOptions = ["No Guests", "Occasional", "Frequent", "Open"]
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                // Bio Section
                VStack(alignment: .leading, spacing: 8) {
                    Text("About You")
                        .font(.headline)
                    
                    TextEditor(text: $bio)
                        .frame(height: 100)
                        .padding(8)
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                        .overlay(
                            Group {
                                if bio.isEmpty {
                                    Text("Tell potential roommates about yourself...")
                                        .foregroundColor(.gray)
                                        .padding(12)
                                }
                            },
                            alignment: .topLeading
                        )
                }
                
                // Sleep Schedule
                VStack(alignment: .leading, spacing: 8) {
                    Text("Sleep Schedule")
                        .font(.headline)
                    
                    HStack(spacing: 8) {
                        ForEach(sleepOptions, id: \.self) { option in
                            PreferenceChip(
                                title: option,
                                isSelected: sleepSchedule == option,
                                color: .purple
                            ) {
                                sleepSchedule = option
                            }
                        }
                    }
                }
                
                // Cleanliness Level
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("Cleanliness Level")
                            .font(.headline)
                        Spacer()
                        Text("\(Int(cleanlinessLevel))/5")
                            .foregroundColor(.secondary)
                    }
                    
                    Slider(value: $cleanlinessLevel, in: 1...5, step: 1)
                        .tint(.purple)
                    
                    HStack {
                        Text("Relaxed")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Spacer()
                        Text("Very Tidy")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                
                // Study Habits
                VStack(alignment: .leading, spacing: 8) {
                    Text("Study Habits")
                        .font(.headline)
                    
                    HStack(spacing: 8) {
                        ForEach(studyOptions, id: \.self) { option in
                            PreferenceChip(
                                title: option,
                                isSelected: studyHabits == option,
                                color: .purple
                            ) {
                                studyHabits = option
                            }
                        }
                    }
                }
                
                // Guest Policy
                VStack(alignment: .leading, spacing: 8) {
                    Text("Guest Policy")
                        .font(.headline)
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 8) {
                        ForEach(guestOptions, id: \.self) { option in
                            PreferenceChip(
                                title: option,
                                isSelected: guestPolicy == option,
                                color: .purple
                            ) {
                                guestPolicy = option
                            }
                        }
                    }
                }
                
                // Noise Tolerance
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("Noise Tolerance")
                            .font(.headline)
                        Spacer()
                        Text("\(Int(noiseTolerance))/5")
                            .foregroundColor(.secondary)
                    }
                    
                    Slider(value: $noiseTolerance, in: 1...5, step: 1)
                        .tint(.purple)
                    
                    HStack {
                        Text("Quiet")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Spacer()
                        Text("Loud OK")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                
                // Save Button
                Button(action: saveProfile) {
                    HStack {
                        if isSaving {
                            ProgressView()
                                .tint(.white)
                        } else {
                            Text("Save Profile")
                                .fontWeight(.bold)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.purple)
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }
            }
            .padding()
        }
    }
    
    private func saveProfile() {
        isSaving = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isSaving = false
        }
    }
}

// MARK: - Preference Chip
struct PreferenceChip: View {
    let title: String
    let isSelected: Bool
    let color: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.caption)
                .fontWeight(isSelected ? .semibold : .regular)
                .padding(.horizontal, 12)
                .padding(.vertical, 8)
                .background(isSelected ? color : Color(.systemGray6))
                .foregroundColor(isSelected ? .white : .primary)
                .cornerRadius(16)
        }
    }
}

// MARK: - Matches Tab
struct RoomieMatchesTab: View {
    let matches: [RoomieMatch] = [
        RoomieMatch(id: "1", name: "Rahul Sharma", bio: "CS student, night owl, loves gaming", compatibilityScore: 92),
        RoomieMatch(id: "2", name: "Amit Patel", bio: "ECE student, early riser, fitness enthusiast", compatibilityScore: 85),
        RoomieMatch(id: "3", name: "Vikram Singh", bio: "ME student, moderate sleeper, movie buff", compatibilityScore: 78),
        RoomieMatch(id: "4", name: "Priya Gupta", bio: "IT student, flexible schedule, bookworm", compatibilityScore: 71)
    ]
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(matches) { match in
                    MatchCard(match: match)
                }
            }
            .padding()
        }
    }
}

// MARK: - Match Model
struct RoomieMatch: Identifiable {
    let id: String
    let name: String
    let bio: String
    let compatibilityScore: Int
}

// MARK: - Match Card
struct MatchCard: View {
    let match: RoomieMatch
    @State private var showRequestSheet = false
    
    var body: some View {
        HStack(spacing: 16) {
            // Avatar
            Circle()
                .fill(Color.purple.opacity(0.2))
                .frame(width: 56, height: 56)
                .overlay(
                    Text(String(match.name.prefix(1)))
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.purple)
                )
            
            // Details
            VStack(alignment: .leading, spacing: 4) {
                Text(match.name)
                    .font(.headline)
                Text(match.bio)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
                
                // Compatibility Bar
                HStack(spacing: 8) {
                    ProgressView(value: Double(match.compatibilityScore) / 100)
                        .tint(match.compatibilityScore > 80 ? .green : .purple)
                    
                    Text("\(match.compatibilityScore)%")
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(.purple)
                }
            }
            
            Spacer()
            
            // Request Button
            Button(action: { showRequestSheet = true }) {
                Image(systemName: "person.badge.plus")
                    .font(.title2)
                    .foregroundColor(.purple)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: .black.opacity(0.05), radius: 5)
        .sheet(isPresented: $showRequestSheet) {
            SendRequestSheet(matchName: match.name)
        }
    }
}

// MARK: - Send Request Sheet
struct SendRequestSheet: View {
    @Environment(\.dismiss) var dismiss
    let matchName: String
    @State private var message = "Hi! I'd like to be your roommate."
    @State private var isSending = false
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Text("Send Request to \(matchName)")
                    .font(.headline)
                
                TextEditor(text: $message)
                    .frame(height: 150)
                    .padding(8)
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                
                Button(action: sendRequest) {
                    HStack {
                        if isSending {
                            ProgressView()
                                .tint(.white)
                        } else {
                            Text("Send Request")
                                .fontWeight(.bold)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.purple)
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("Match Request")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
    
    private func sendRequest() {
        isSending = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            isSending = false
            dismiss()
        }
    }
}

// MARK: - Requests Tab
struct RoomieRequestsTab: View {
    var body: some View {
        ContentUnavailableView(
            "No Requests",
            systemImage: "envelope",
            description: Text("Match requests will appear here")
        )
    }
}

#Preview {
    NavigationStack {
        RoomieView()
    }
}

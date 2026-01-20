import SwiftUI
import FirebaseCore

/**
 * Hostel Dada iOS App Entry Point
 *
 * Initializes Firebase and sets up the main app structure
 * Uses Kotlin Multiplatform shared code for business logic
 */

// AppDelegate for Firebase initialization
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        // Initialize Firebase
        FirebaseApp.configure()
        print("🔥 Firebase initialized successfully")
        return true
    }
}

@main
struct HostelDadaApp: App {
    // Register app delegate for Firebase setup
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    // App state
    @StateObject private var appState = AppState()
    
    var body: some Scene {
        WindowGroup {
            NavigationStack {
                ContentView()
            }
            .environmentObject(appState)
        }
    }
}

// App-wide state management
class AppState: ObservableObject {
    @Published var isLoggedIn: Bool = false
    @Published var currentUser: UserModel? = nil
    
    init() {
        checkAuthState()
    }
    
    func checkAuthState() {
        // Check Firebase auth state
        // This will be connected to shared KMP code
    }
}

// User model
struct UserModel: Identifiable {
    let id: String
    let email: String
    let displayName: String
    var photoURL: String?
}

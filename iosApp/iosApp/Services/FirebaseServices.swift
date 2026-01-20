import Foundation
import FirebaseAuth
import FirebaseFirestore
import FirebaseDatabase

/**
 * Firebase Service Layer for iOS
 *
 * Provides Firebase integration matching the shared KMP interfaces
 * Implements repository pattern for clean architecture
 */

// MARK: - Auth Service
class FirebaseAuthService: ObservableObject {
    static let shared = FirebaseAuthService()
    
    @Published var currentUser: User?
    @Published var isAuthenticated = false
    
    private let auth = Auth.auth()
    
    init() {
        setupAuthStateListener()
    }
    
    private func setupAuthStateListener() {
        auth.addStateDidChangeListener { [weak self] _, user in
            self?.currentUser = user
            self?.isAuthenticated = user != nil
        }
    }
    
    // Email/Password Sign In
    func signIn(email: String, password: String) async throws -> User {
        let result = try await auth.signIn(withEmail: email, password: password)
        return result.user
    }
    
    // Email/Password Registration
    func register(email: String, password: String, displayName: String) async throws -> User {
        let result = try await auth.createUser(withEmail: email, password: password)
        
        // Update display name
        let changeRequest = result.user.createProfileChangeRequest()
        changeRequest.displayName = displayName
        try await changeRequest.commitChanges()
        
        return result.user
    }
    
    // Google Sign In (requires GoogleSignIn SDK setup)
    func signInWithGoogle(idToken: String, accessToken: String) async throws -> User {
        let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)
        let result = try await auth.signIn(with: credential)
        return result.user
    }
    
    // Sign Out
    func signOut() throws {
        try auth.signOut()
    }
    
    // Password Reset
    func resetPassword(email: String) async throws {
        try await auth.sendPasswordReset(withEmail: email)
    }
}

// MARK: - Firestore Service
class FirestoreService {
    static let shared = FirestoreService()
    
    private let db = Firestore.firestore()
    
    // MARK: - User Profile
    func getUserProfile(userId: String) async throws -> [String: Any]? {
        let doc = try await db.collection("users").document(userId).getDocument()
        return doc.data()
    }
    
    func saveUserProfile(userId: String, data: [String: Any]) async throws {
        try await db.collection("users").document(userId).setData(data, merge: true)
    }
    
    // MARK: - Snacks
    func getSnacks() async throws -> [[String: Any]] {
        let snapshot = try await db.collection("snacks")
            .whereField("available", isEqualTo: true)
            .getDocuments()
        
        return snapshot.documents.map { doc in
            var data = doc.data()
            data["id"] = doc.documentID
            return data
        }
    }
    
    func searchSnacks(query: String) async throws -> [[String: Any]] {
        // Firestore doesn't support full-text search natively
        // Filter client-side or use Algolia/Elasticsearch
        let allSnacks = try await getSnacks()
        return allSnacks.filter { snack in
            let name = snack["name"] as? String ?? ""
            return name.localizedCaseInsensitiveContains(query)
        }
    }
    
    // MARK: - Orders
    func placeOrder(order: [String: Any]) async throws -> String {
        let docRef = try await db.collection("orders").addDocument(data: order)
        return docRef.documentID
    }
    
    func getOrders(userId: String) async throws -> [[String: Any]] {
        let snapshot = try await db.collection("orders")
            .whereField("userId", isEqualTo: userId)
            .order(by: "createdAt", descending: true)
            .getDocuments()
        
        return snapshot.documents.map { doc in
            var data = doc.data()
            data["id"] = doc.documentID
            return data
        }
    }
    
    // MARK: - Roomie Profiles
    func getRoomieProfile(userId: String) async throws -> [String: Any]? {
        let doc = try await db.collection("roomie_profiles").document(userId).getDocument()
        return doc.data()
    }
    
    func saveRoomieProfile(userId: String, data: [String: Any]) async throws {
        try await db.collection("roomie_profiles").document(userId).setData(data, merge: true)
    }
    
    func findRoomieMatches(userId: String) async throws -> [[String: Any]] {
        let snapshot = try await db.collection("roomie_profiles")
            .whereField("userId", isNotEqualTo: userId)
            .getDocuments()
        
        return snapshot.documents.map { doc in
            var data = doc.data()
            data["id"] = doc.documentID
            return data
        }
    }
}

// MARK: - Realtime Database Service
class RealtimeDatabaseService {
    static let shared = RealtimeDatabaseService()
    
    private let database = Database.database()
    
    // MARK: - Cart Operations
    func getCart(userId: String) async throws -> [String: Any]? {
        let snapshot = try await database.reference()
            .child("carts")
            .child(userId)
            .getData()
        
        return snapshot.value as? [String: Any]
    }
    
    func addToCart(userId: String, snackId: String, quantity: Int) async throws {
        try await database.reference()
            .child("carts")
            .child(userId)
            .child("items")
            .child(snackId)
            .setValue(["snackId": snackId, "quantity": quantity])
    }
    
    func clearCart(userId: String) async throws {
        try await database.reference()
            .child("carts")
            .child(userId)
            .removeValue()
    }
    
    // MARK: - Match Requests
    func sendMatchRequest(fromUserId: String, toUserId: String, message: String) async throws {
        let requestData: [String: Any] = [
            "fromUserId": fromUserId,
            "toUserId": toUserId,
            "message": message,
            "status": "pending",
            "createdAt": ServerValue.timestamp()
        ]
        
        try await database.reference()
            .child("match_requests")
            .child(toUserId)
            .child(fromUserId)
            .setValue(requestData)
    }
    
    func getMatchRequests(userId: String) async throws -> [[String: Any]] {
        let snapshot = try await database.reference()
            .child("match_requests")
            .child(userId)
            .getData()
        
        guard let value = snapshot.value as? [String: [String: Any]] else {
            return []
        }
        
        return Array(value.values)
    }
}

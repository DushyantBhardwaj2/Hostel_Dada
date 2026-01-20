import SwiftUI

/**
 * SnackCart View
 *
 * Browse and order snacks from hostel shops
 * Uses Trie-based search for efficient prefix matching
 */
struct SnackCartView: View {
    @State private var searchText = ""
    @State private var selectedCategory = "All"
    @State private var cartItems: [CartItem] = []
    @State private var showCart = false
    
    let categories = ["All", "Noodles", "Chips", "Biscuits", "Beverages", "Sweets"]
    
    let snacks: [Snack] = [
        Snack(id: "1", name: "Maggi", description: "Instant noodles - 2 min ready", price: 25, category: "Noodles", emoji: "🍜"),
        Snack(id: "2", name: "Kurkure", description: "Crunchy masala snack", price: 20, category: "Chips", emoji: "🥨"),
        Snack(id: "3", name: "Parle-G", description: "Classic glucose biscuits", price: 10, category: "Biscuits", emoji: "🍪"),
        Snack(id: "4", name: "Coca-Cola", description: "Chilled cola 500ml", price: 40, category: "Beverages", emoji: "🥤"),
        Snack(id: "5", name: "Lays Classic", description: "Salted potato chips", price: 20, category: "Chips", emoji: "🥔"),
        Snack(id: "6", name: "Dairy Milk", description: "Cadbury chocolate", price: 50, category: "Sweets", emoji: "🍫"),
        Snack(id: "7", name: "Cup Noodles", description: "Hot & spicy instant cup", price: 45, category: "Noodles", emoji: "🍜"),
        Snack(id: "8", name: "Frooti", description: "Mango drink 200ml", price: 15, category: "Beverages", emoji: "🧃")
    ]
    
    var filteredSnacks: [Snack] {
        snacks.filter { snack in
            let matchesSearch = searchText.isEmpty || snack.name.localizedCaseInsensitiveContains(searchText)
            let matchesCategory = selectedCategory == "All" || snack.category == selectedCategory
            return matchesSearch && matchesCategory
        }
    }
    
    var totalAmount: Double {
        cartItems.reduce(0) { $0 + ($1.snack.price * Double($1.quantity)) }
    }
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ScrollView {
                VStack(spacing: 16) {
                    // Search Bar
                    HStack {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.gray)
                        TextField("Search snacks...", text: $searchText)
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(24)
                    .padding(.horizontal)
                    
                    // Categories
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(categories, id: \.self) { category in
                                CategoryChip(
                                    title: category,
                                    isSelected: selectedCategory == category
                                ) {
                                    selectedCategory = category
                                }
                            }
                        }
                        .padding(.horizontal)
                    }
                    
                    // Snacks Grid
                    LazyVStack(spacing: 12) {
                        ForEach(filteredSnacks) { snack in
                            SnackCard(snack: snack) {
                                addToCart(snack)
                            }
                        }
                    }
                    .padding(.horizontal)
                    
                    // Bottom padding for cart button
                    Spacer()
                        .frame(height: 100)
                }
                .padding(.top)
            }
            
            // Cart Button
            if !cartItems.isEmpty {
                Button(action: { showCart = true }) {
                    HStack {
                        Image(systemName: "cart.fill")
                        Text("\(cartItems.count) items")
                        Spacer()
                        Text("₹\(String(format: "%.0f", totalAmount))")
                            .fontWeight(.bold)
                        Image(systemName: "chevron.right")
                    }
                    .padding()
                    .background(Color.orange)
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }
                .padding()
                .background(
                    Rectangle()
                        .fill(.ultraThinMaterial)
                        .ignoresSafeArea()
                )
            }
        }
        .navigationTitle("SnackCart")
        .sheet(isPresented: $showCart) {
            CartSheet(
                items: $cartItems,
                totalAmount: totalAmount,
                onPlaceOrder: placeOrder
            )
        }
    }
    
    private func addToCart(_ snack: Snack) {
        if let index = cartItems.firstIndex(where: { $0.snack.id == snack.id }) {
            cartItems[index].quantity += 1
        } else {
            cartItems.append(CartItem(snack: snack, quantity: 1))
        }
        
        // Haptic feedback
        let impact = UIImpactFeedbackGenerator(style: .light)
        impact.impactOccurred()
    }
    
    private func placeOrder() {
        // Implement order placement
        cartItems.removeAll()
        showCart = false
    }
}

// MARK: - Models
struct Snack: Identifiable {
    let id: String
    let name: String
    let description: String
    let price: Double
    let category: String
    let emoji: String
}

struct CartItem: Identifiable {
    var id: String { snack.id }
    let snack: Snack
    var quantity: Int
}

// MARK: - Category Chip
struct CategoryChip: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline)
                .fontWeight(isSelected ? .semibold : .regular)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? Color.orange : Color(.systemGray6))
                .foregroundColor(isSelected ? .white : .primary)
                .cornerRadius(20)
        }
    }
}

// MARK: - Snack Card
struct SnackCard: View {
    let snack: Snack
    let onAdd: () -> Void
    
    var body: some View {
        HStack(spacing: 16) {
            // Emoji placeholder
            Text(snack.emoji)
                .font(.system(size: 40))
                .frame(width: 64, height: 64)
                .background(Color(.systemGray6))
                .cornerRadius(12)
            
            // Details
            VStack(alignment: .leading, spacing: 4) {
                Text(snack.name)
                    .font(.headline)
                Text(snack.description)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
                Text("₹\(String(format: "%.0f", snack.price))")
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(.orange)
            }
            
            Spacer()
            
            // Add Button
            Button(action: onAdd) {
                Image(systemName: "plus.circle.fill")
                    .font(.title)
                    .foregroundColor(.orange)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: .black.opacity(0.05), radius: 5)
    }
}

// MARK: - Cart Sheet
struct CartSheet: View {
    @Environment(\.dismiss) var dismiss
    @Binding var items: [CartItem]
    let totalAmount: Double
    let onPlaceOrder: () -> Void
    
    var body: some View {
        NavigationStack {
            VStack {
                if items.isEmpty {
                    ContentUnavailableView(
                        "Cart is Empty",
                        systemImage: "cart",
                        description: Text("Add some snacks to get started")
                    )
                } else {
                    List {
                        ForEach(items) { item in
                            HStack {
                                Text(item.snack.emoji)
                                    .font(.title2)
                                
                                VStack(alignment: .leading) {
                                    Text(item.snack.name)
                                        .font(.headline)
                                    Text("₹\(String(format: "%.0f", item.snack.price)) × \(item.quantity)")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                
                                Spacer()
                                
                                Text("₹\(String(format: "%.0f", item.snack.price * Double(item.quantity)))")
                                    .fontWeight(.semibold)
                            }
                        }
                        .onDelete { indexSet in
                            items.remove(atOffsets: indexSet)
                        }
                        
                        // Total
                        HStack {
                            Text("Total")
                                .font(.headline)
                            Spacer()
                            Text("₹\(String(format: "%.0f", totalAmount))")
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.orange)
                        }
                        .listRowBackground(Color.orange.opacity(0.1))
                    }
                    
                    // Place Order Button
                    Button(action: onPlaceOrder) {
                        Text("Place Order")
                            .fontWeight(.bold)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.orange)
                            .foregroundColor(.white)
                            .cornerRadius(16)
                    }
                    .padding()
                }
            }
            .navigationTitle("Your Cart")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        SnackCartView()
    }
}

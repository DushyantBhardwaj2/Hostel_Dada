package com.hosteldada.feature.snackcart.presentation

import com.hosteldada.core.common.DispatcherProvider
import com.hosteldada.core.common.result.Result
import com.hosteldada.core.domain.model.*
import com.hosteldada.feature.snackcart.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ============================================
 * SNACKCART USER VIEWMODEL
 * ============================================
 */

class SnackCartViewModel(
    // Snacks
    private val getAllSnacks: GetAllSnacksUseCase,
    private val searchSnacks: SearchSnacksUseCase,
    private val getSnacksByCategory: GetSnacksByCategoryUseCase,
    private val observeSnacks: ObserveSnacksUseCase,
    // Cart
    private val getCart: GetCartUseCase,
    private val addToCart: AddToCartUseCase,
    private val updateCartQuantity: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val clearCart: ClearCartUseCase,
    private val observeCart: ObserveCartUseCase,
    // Orders
    private val placeOrder: PlaceOrderUseCase,
    private val getUserOrders: GetUserOrdersUseCase,
    private val cancelOrder: CancelOrderUseCase,
    private val observeOrders: ObserveUserOrdersUseCase,
    private val dispatcher: DispatcherProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher.main)
    
    private val _uiState = MutableStateFlow(SnackCartUiState())
    val uiState: StateFlow<SnackCartUiState> = _uiState.asStateFlow()
    
    // Current user ID (would come from auth)
    private var currentUserId: String = ""
    private var currentUserEmail: String = ""
    private var currentUserName: String = ""
    
    init {
        loadInitialData()
    }
    
    fun setCurrentUser(userId: String, email: String, name: String) {
        currentUserId = userId
        currentUserEmail = email
        currentUserName = name
        observeUserData()
    }
    
    fun onIntent(intent: SnackCartIntent) {
        when (intent) {
            // Navigation
            is SnackCartIntent.SelectTab -> selectTab(intent.tab)
            
            // Menu
            is SnackCartIntent.SelectCategory -> selectCategory(intent.category)
            is SnackCartIntent.SearchSnacks -> searchSnacksAction(intent.query)
            is SnackCartIntent.ClearSearch -> clearSearch()
            
            // Cart
            is SnackCartIntent.AddToCart -> addToCartAction(intent.snack)
            is SnackCartIntent.UpdateQuantity -> updateQuantityAction(intent.snackId, intent.quantity)
            is SnackCartIntent.RemoveFromCart -> removeFromCartAction(intent.snackId)
            is SnackCartIntent.ClearCart -> clearCartAction()
            
            // Checkout
            is SnackCartIntent.ShowCheckout -> showCheckout()
            is SnackCartIntent.HideCheckout -> hideCheckout()
            is SnackCartIntent.UpdateDeliveryLocation -> updateDelivery(intent.location)
            is SnackCartIntent.UpdatePaymentMethod -> updatePayment(intent.method)
            is SnackCartIntent.UpdateOrderNotes -> updateNotes(intent.notes)
            is SnackCartIntent.PlaceOrder -> placeOrderAction()
            
            // Orders
            is SnackCartIntent.CancelOrder -> cancelOrderAction(intent.orderId)
            is SnackCartIntent.ViewOrderDetails -> viewOrderDetails(intent.order)
            
            // Error handling
            is SnackCartIntent.ClearError -> _uiState.update { it.copy(error = null) }
            is SnackCartIntent.ClearSuccess -> _uiState.update { it.copy(successMessage = null) }
        }
    }
    
    // ==========================================
    // INITIALIZATION
    // ==========================================
    
    private fun loadInitialData() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = getAllSnacks()) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        snacks = result.data,
                        filteredSnacks = result.data
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.exception.message
                    )}
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun observeUserData() {
        // Observe cart
        scope.launch {
            observeCart(currentUserId).collect { cart ->
                _uiState.update { it.copy(cart = cart) }
            }
        }
        
        // Observe orders
        scope.launch {
            observeOrders(currentUserId).collect { orders ->
                _uiState.update { it.copy(
                    orders = orders,
                    activeOrder = orders.firstOrNull { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
                )}
            }
        }
        
        // Observe snacks
        scope.launch {
            observeSnacks().collect { snacks ->
                _uiState.update { state ->
                    val filtered = if (state.selectedCategory != null) {
                        snacks.filter { it.category == state.selectedCategory }
                    } else snacks
                    
                    state.copy(snacks = snacks, filteredSnacks = filtered)
                }
            }
        }
    }
    
    // ==========================================
    // MENU ACTIONS
    // ==========================================
    
    private fun selectTab(tab: SnackCartTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    private fun selectCategory(category: SnackCategory?) {
        scope.launch {
            _uiState.update { it.copy(selectedCategory = category, isLoading = true) }
            
            val result = if (category != null) {
                getSnacksByCategory(category)
            } else {
                getAllSnacks()
            }
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        filteredSnacks = result.data
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.exception.message
                    )}
                }
                is Result.Loading -> {}
            }
        }
    }
    
    /**
     * Search using Trie-based search for O(k) performance
     */
    private fun searchSnacksAction(query: String) {
        scope.launch {
            _uiState.update { it.copy(searchQuery = query) }
            
            if (query.isBlank()) {
                _uiState.update { it.copy(searchResults = emptyList()) }
                return@launch
            }
            
            when (val result = searchSnacks(query)) {
                is Result.Success -> {
                    _uiState.update { it.copy(searchResults = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }
    
    // ==========================================
    // CART ACTIONS
    // ==========================================
    
    private fun addToCartAction(snack: Snack) {
        scope.launch {
            when (val result = addToCart(currentUserId, snack, 1)) {
                is Result.Success -> {
                    _uiState.update { it.copy(successMessage = "${snack.name} added to cart") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun updateQuantityAction(snackId: String, quantity: Int) {
        scope.launch {
            updateCartQuantity(currentUserId, snackId, quantity)
        }
    }
    
    private fun removeFromCartAction(snackId: String) {
        scope.launch {
            removeFromCart(currentUserId, snackId)
        }
    }
    
    private fun clearCartAction() {
        scope.launch {
            clearCart(currentUserId)
        }
    }
    
    // ==========================================
    // CHECKOUT ACTIONS
    // ==========================================
    
    private fun showCheckout() {
        _uiState.update { it.copy(showCheckout = true) }
    }
    
    private fun hideCheckout() {
        _uiState.update { it.copy(showCheckout = false) }
    }
    
    private fun updateDelivery(location: String) {
        _uiState.update { it.copy(deliveryLocation = location) }
    }
    
    private fun updatePayment(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method) }
    }
    
    private fun updateNotes(notes: String) {
        _uiState.update { it.copy(orderNotes = notes) }
    }
    
    private fun placeOrderAction() {
        val state = _uiState.value
        
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = placeOrder(
                userId = currentUserId,
                userEmail = currentUserEmail,
                userName = currentUserName,
                deliveryLocation = state.deliveryLocation,
                paymentMethod = state.paymentMethod,
                notes = state.orderNotes
            )
            
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        showCheckout = false,
                        selectedTab = SnackCartTab.ORDERS,
                        successMessage = "Order placed successfully!",
                        deliveryLocation = "",
                        orderNotes = ""
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.exception.message
                    )}
                }
                is Result.Loading -> {}
            }
        }
    }
    
    // ==========================================
    // ORDER ACTIONS
    // ==========================================
    
    private fun cancelOrderAction(orderId: String) {
        scope.launch {
            when (val result = cancelOrder(orderId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(successMessage = "Order cancelled") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.exception.message) }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun viewOrderDetails(order: SnackOrder) {
        _uiState.update { it.copy(activeOrder = order) }
    }
}

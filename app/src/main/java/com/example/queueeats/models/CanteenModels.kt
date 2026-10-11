package com.example.queueeats.models

/**
 * Represents a food item on the canteen branch menu.
 */
data class MenuItem(
    val id: String,
    val branchId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int,
    val category: String,
    var stockQuantity: Int,
    var lowStockThreshold: Int = 5,
    var isEnabled: Boolean = true
) {
    val isAvailable: Boolean
        get() = isEnabled && stockQuantity > 0

    val isLowStock: Boolean
        get() = isEnabled && stockQuantity in 1..lowStockThreshold
}

/**
 * Represents an item in the shopping cart.
 */
data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int
) {
    val subtotal: Double
        get() = menuItem.price * quantity
}

/**
 * Represents the status of a canteen pickup order.
 */
enum class OrderStatus {
    WAITING,
    CALLED,
    COLLECTED,
    CANCELLED
}

/**
 * Represents a confirmed canteen order with an assigned queue number.
 */
data class Order(
    val orderId: String,
    val branchId: String,
    val branchName: String,
    val queueNumber: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val paymentMethod: String = "Cash upon pickup",
    val orderTimestamp: Long = System.currentTimeMillis(),
    var status: OrderStatus = OrderStatus.WAITING,
    val customerEmail: String
)

/**
 * Represents an inventory stock adjustment log entry for auditing.
 */
data class StockAdjustmentRecord(
    val recordId: String,
    val itemId: String,
    val itemName: String,
    val branchId: String,
    val previousQuantity: Int,
    val quantityChange: Int,
    val newQuantity: Int,
    val reason: String,
    val staffEmail: String,
    val timestamp: Long = System.currentTimeMillis()
)

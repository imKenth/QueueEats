package com.example.queueeats.data

import com.example.queueeats.DashboardActivity
import com.example.queueeats.R
import com.example.queueeats.models.CartItem
import com.example.queueeats.models.MenuItem
import com.example.queueeats.models.Order
import com.example.queueeats.models.OrderStatus
import com.example.queueeats.models.StockAdjustmentRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Thread-safe singleton repository for managing QueueEats canteen operations,
 * including menu inventory, shopping cart, atomic queue generation, order processing,
 * live queue tracking, and stock audit logging.
 */
object CanteenRepository {

    const val BRANCH_1_ID = "branch_1"
    const val BRANCH_2_ID = "branch_2"

    private val menuItems = mutableListOf<MenuItem>()
    private val carts = mutableMapOf<String, MutableList<CartItem>>()
    private val orders = mutableListOf<Order>()
    private val stockAuditLogs = mutableListOf<StockAdjustmentRecord>()

    // Sequential queue counters per branch
    private val queueCounters = mutableMapOf(
        BRANCH_1_ID to 1,
        BRANCH_2_ID to 1
    )

    // Currently calling queue numbers per branch
    private val callingQueueNumbers = mutableMapOf<String, String?>(
        BRANCH_1_ID to null,
        BRANCH_2_ID to null
    )

    init {
        seedSampleMenuData()
    }

    private fun seedSampleMenuData() {
        // Branch 1: JanPol Canteen 1 • Orange Stall
        menuItems.addAll(
            listOf(
                MenuItem(
                    id = "item_101",
                    branchId = BRANCH_1_ID,
                    name = "Chicken Adobo",
                    description = "Tender chicken cooked in soy sauce, vinegar, and aromatic garlic.",
                    price = 85.0,
                    imageResId = R.drawable.img_chicken_adobo,
                    category = "Meals",
                    stockQuantity = 15,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_102",
                    branchId = BRANCH_1_ID,
                    name = "Sinigang na Baboy",
                    description = "Savory and sour pork soup with fresh native vegetables.",
                    price = 95.0,
                    imageResId = R.drawable.img_sinigang_na_baboy,
                    category = "Meals",
                    stockQuantity = 12,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_103",
                    branchId = BRANCH_1_ID,
                    name = "Lechon Kawali",
                    description = "Crispy deep-fried pork belly served with liver sauce.",
                    price = 110.0,
                    imageResId = R.drawable.img_lechon_kawali,
                    category = "Meals",
                    stockQuantity = 8,
                    lowStockThreshold = 4
                ),
                MenuItem(
                    id = "item_104",
                    branchId = BRANCH_1_ID,
                    name = "Grilled Chicken",
                    description = "Inasal style marinated grilled chicken leg quarter.",
                    price = 105.0,
                    imageResId = R.drawable.grilled_chicken,
                    category = "Meals",
                    stockQuantity = 10,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_105",
                    branchId = BRANCH_1_ID,
                    name = "Mango Float",
                    description = "Chilled Graham float with sweet sweet ripe mangoes and cream.",
                    price = 45.0,
                    imageResId = R.drawable.ic_dessert,
                    category = "Dessert",
                    stockQuantity = 20,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_106",
                    branchId = BRANCH_1_ID,
                    name = "House Iced Tea",
                    description = "Refreshing chilled house-blend lemon tea (16oz).",
                    price = 25.0,
                    imageResId = R.drawable.ic_drinks,
                    category = "Drinks",
                    stockQuantity = 30,
                    lowStockThreshold = 10
                )
            )
        )

        // Branch 2: JanPol Canteen 2 • Red Stall
        menuItems.addAll(
            listOf(
                MenuItem(
                    id = "item_201",
                    branchId = BRANCH_2_ID,
                    name = "Sizzling Pork Sisig",
                    description = "Crispy pork sisig topped with chili and fresh egg.",
                    price = 90.0,
                    imageResId = R.drawable.img_lechon_kawali,
                    category = "Meals",
                    stockQuantity = 15,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_202",
                    branchId = BRANCH_2_ID,
                    name = "Beef Pares",
                    description = "Braised beef stew served with garlic rice and clear soup.",
                    price = 85.0,
                    imageResId = R.drawable.img_chicken_adobo,
                    category = "Meals",
                    stockQuantity = 10,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_203",
                    branchId = BRANCH_2_ID,
                    name = "Chicken Inasal",
                    description = "Grilled chicken marinated in annatto, lemongrass, and citrus.",
                    price = 100.0,
                    imageResId = R.drawable.grilled_chicken,
                    category = "Meals",
                    stockQuantity = 14,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_204",
                    branchId = BRANCH_2_ID,
                    name = "Special Halo-Halo",
                    description = "Shaved ice with sweetened fruits, beans, leche flan, and ube.",
                    price = 55.0,
                    imageResId = R.drawable.ic_dessert,
                    category = "Dessert",
                    stockQuantity = 18,
                    lowStockThreshold = 5
                ),
                MenuItem(
                    id = "item_205",
                    branchId = BRANCH_2_ID,
                    name = "Cold Buko Juice",
                    description = "Fresh young coconut water with coconut shreds (16oz).",
                    price = 30.0,
                    imageResId = R.drawable.ic_drinks,
                    category = "Drinks",
                    stockQuantity = 25,
                    lowStockThreshold = 8
                )
            )
        )
    }

    // =========================================================================
    // MENU & INVENTORY READ METHODS
    // =========================================================================

    @Synchronized
    fun getMenuItems(branchId: String): List<MenuItem> {
        return menuItems.filter { it.branchId == branchId }
    }

    @Synchronized
    fun getMenuItemById(itemId: String): MenuItem? {
        return menuItems.find { it.id == itemId }
    }

    // =========================================================================
    // SHOPPING CART OPERATIONS
    // =========================================================================

    @Synchronized
    fun getCart(branchId: String): List<CartItem> {
        return carts.getOrPut(branchId) { mutableListOf() }
    }

    @Synchronized
    fun addToCart(branchId: String, item: MenuItem, quantity: Int = 1): Boolean {
        if (!item.isAvailable || quantity <= 0) return false

        val cart = carts.getOrPut(branchId) { mutableListOf() }
        val existing = cart.find { it.menuItem.id == item.id }

        val currentInCart = existing?.quantity ?: 0
        if (currentInCart + quantity > item.stockQuantity) {
            return false // Cannot exceed available stock
        }

        if (existing != null) {
            existing.quantity += quantity
        } else {
            cart.add(CartItem(menuItem = item, quantity = quantity))
        }
        return true
    }

    @Synchronized
    fun updateCartQuantity(branchId: String, itemId: String, newQuantity: Int): Boolean {
        val cart = carts[branchId] ?: return false
        val cartItem = cart.find { it.menuItem.id == itemId } ?: return false

        if (newQuantity <= 0) {
            cart.remove(cartItem)
            return true
        }

        if (newQuantity > cartItem.menuItem.stockQuantity) {
            return false // Exceeds available stock
        }

        cartItem.quantity = newQuantity
        return true
    }

    @Synchronized
    fun removeFromCart(branchId: String, itemId: String) {
        val cart = carts[branchId] ?: return
        cart.removeAll { it.menuItem.id == itemId }
    }

    @Synchronized
    fun clearCart(branchId: String) {
        carts[branchId]?.clear()
    }

    @Synchronized
    fun getCartTotal(branchId: String): Double {
        return carts[branchId]?.sumOf { it.subtotal } ?: 0.0
    }

    @Synchronized
    fun getCartItemCount(branchId: String): Int {
        return carts[branchId]?.sumOf { it.quantity } ?: 0
    }

    // =========================================================================
    // ATOMIC CHECKOUT & QUEUE GENERATION
    // =========================================================================

    /**
     * Executes atomic checkout:
     * 1. Validates non-empty cart.
     * 2. Validates item availability & stock sufficiency.
     * 3. Generates unique sequential queue number (Branch 1 -> A001..., Branch 2 -> B001...).
     * 4. Deducts stock quantity atomically.
     * 5. Saves confirmed order and logs stock deduction.
     * 6. Clears branch cart.
     */
    @Synchronized
    fun checkout(branchId: String, customerEmail: String): Result<Order> {
        val cart = carts[branchId]
        if (cart.isNullOrEmpty()) {
            return Result.failure(IllegalStateException("Shopping cart is empty."))
        }

        // Validate stock sufficiency for all cart items
        for (cartItem in cart) {
            val currentItem = menuItems.find { it.id == cartItem.menuItem.id }
            if (currentItem == null || !currentItem.isEnabled || currentItem.stockQuantity < cartItem.quantity) {
                return Result.failure(
                    IllegalStateException("Stock insufficient for ${cartItem.menuItem.name}.")
                )
            }
        }

        // Deduct stock atomically
        for (cartItem in cart) {
            val item = menuItems.first { it.id == cartItem.menuItem.id }
            val prevStock = item.stockQuantity
            item.stockQuantity -= cartItem.quantity

            stockAuditLogs.add(
                StockAdjustmentRecord(
                    recordId = "AUDIT_${System.currentTimeMillis()}_${item.id}",
                    itemId = item.id,
                    itemName = item.name,
                    branchId = branchId,
                    previousQuantity = prevStock,
                    quantityChange = -cartItem.quantity,
                    newQuantity = item.stockQuantity,
                    reason = "Order Deduction",
                    staffEmail = "System Checkout"
                )
            )
        }

        // Generate branch queue number
        val prefix = if (branchId == BRANCH_1_ID) "A" else "B"
        val counter = queueCounters.getOrDefault(branchId, 1)
        val queueNumber = String.format(Locale.US, "%s%03d", prefix, counter)
        queueCounters[branchId] = counter + 1

        val branchName = if (branchId == BRANCH_1_ID) {
            DashboardActivity.BRANCH_1_NAME
        } else {
            DashboardActivity.BRANCH_2_NAME
        }

        val orderId = "QE-" + SimpleDateFormat("yyyyMMdd", Locale.US).format(Date()) + "-" + queueNumber
        val cartSnapshot = cart.map { it.copy(menuItem = it.menuItem.copy()) }

        val order = Order(
            orderId = orderId,
            branchId = branchId,
            branchName = branchName,
            queueNumber = queueNumber,
            items = cartSnapshot,
            totalAmount = cartSnapshot.sumOf { it.subtotal },
            paymentMethod = "Cash upon pickup",
            orderTimestamp = System.currentTimeMillis(),
            status = OrderStatus.WAITING,
            customerEmail = customerEmail
        )

        orders.add(order)
        cart.clear()

        return Result.success(order)
    }

    // =========================================================================
    // LIVE QUEUE & CUSTOMER ORDER LOOKUP
    // =========================================================================

    @Synchronized
    fun getOrdersForCustomer(email: String): List<Order> {
        return orders.filter { it.customerEmail.equals(email, ignoreCase = true) }
            .sortedByDescending { it.orderTimestamp }
    }

    @Synchronized
    fun getOrderById(orderId: String): Order? {
        return orders.find { it.orderId == orderId }
    }

    @Synchronized
    fun getCallingQueueNumber(branchId: String): String? {
        return callingQueueNumbers[branchId]
    }

    @Synchronized
    fun getActiveWaitingOrdersCount(branchId: String, customerQueueNumber: String): Int {
        val waitingOrders = orders.filter { it.branchId == branchId && it.status == OrderStatus.WAITING }
        val targetIndex = waitingOrders.indexOfFirst { it.queueNumber == customerQueueNumber }
        return if (targetIndex >= 0) targetIndex else 0
    }

    @Synchronized
    fun getActiveOrderForBranch(customerEmail: String, branchId: String): Order? {
        val branchOrders = orders.filter { it.customerEmail.equals(customerEmail, ignoreCase = true) && it.branchId == branchId }
        return branchOrders.find { it.status == OrderStatus.WAITING || it.status == OrderStatus.CALLED }
            ?: branchOrders.maxByOrNull { it.orderTimestamp }
    }

    @Synchronized
    fun getActiveOrdersForBranch(customerEmail: String, branchId: String): List<Order> {
        return orders.filter {
            it.customerEmail.equals(customerEmail, ignoreCase = true) &&
                    it.branchId == branchId &&
                    (it.status == OrderStatus.WAITING || it.status == OrderStatus.CALLED)
        }.sortedBy { it.orderTimestamp }
    }

    // =========================================================================
    // STAFF QUEUE MANAGEMENT OPERATIONS
    // =========================================================================

    @Synchronized
    fun getOrdersForBranch(branchId: String): List<Order> {
        return orders.filter { it.branchId == branchId }
            .sortedByDescending { it.orderTimestamp }
    }

    @Synchronized
    fun callNextOrder(branchId: String): Order? {
        val nextOrder = orders.firstOrNull { it.branchId == branchId && it.status == OrderStatus.WAITING }
        if (nextOrder != null) {
            nextOrder.status = OrderStatus.CALLED
            callingQueueNumbers[branchId] = nextOrder.queueNumber
        }
        return nextOrder
    }

    @Synchronized
    fun callOrderAgain(branchId: String, orderId: String): Boolean {
        val order = orders.find { it.branchId == branchId && it.orderId == orderId } ?: return false
        if (order.status == OrderStatus.WAITING) {
            order.status = OrderStatus.CALLED
        }
        callingQueueNumbers[branchId] = order.queueNumber
        return true
    }

    @Synchronized
    fun markOrderCollected(branchId: String, orderId: String): Boolean {
        val order = orders.find { it.branchId == branchId && it.orderId == orderId } ?: return false
        order.status = OrderStatus.COLLECTED
        if (callingQueueNumbers[branchId] == order.queueNumber) {
            callingQueueNumbers[branchId] = null
        }
        return true
    }

    @Synchronized
    fun cancelOrder(branchId: String, orderId: String, restoreStock: Boolean = true, staffEmail: String = "Staff"): Boolean {
        val order = orders.find { it.branchId == branchId && it.orderId == orderId } ?: return false
        if (order.status == OrderStatus.COLLECTED || order.status == OrderStatus.CANCELLED) {
            return false // Already finalized
        }

        order.status = OrderStatus.CANCELLED
        if (callingQueueNumbers[branchId] == order.queueNumber) {
            callingQueueNumbers[branchId] = null
        }

        if (restoreStock) {
            for (cartItem in order.items) {
                val item = menuItems.find { it.id == cartItem.menuItem.id }
                if (item != null) {
                    val prevStock = item.stockQuantity
                    item.stockQuantity += cartItem.quantity
                    stockAuditLogs.add(
                        StockAdjustmentRecord(
                            recordId = "AUDIT_CANCEL_${System.currentTimeMillis()}_${item.id}",
                            itemId = item.id,
                            itemName = item.name,
                            branchId = branchId,
                            previousQuantity = prevStock,
                            quantityChange = cartItem.quantity,
                            newQuantity = item.stockQuantity,
                            reason = "Order Cancellation #${order.queueNumber}",
                            staffEmail = staffEmail
                        )
                    )
                }
            }
        }
        return true
    }

    // =========================================================================
    // STAFF INVENTORY CONTROL
    // =========================================================================

    @Synchronized
    fun updateStock(
        branchId: String,
        itemId: String,
        newStockQuantity: Int,
        reason: String,
        staffEmail: String
    ): Boolean {
        val item = menuItems.find { it.branchId == branchId && it.id == itemId } ?: return false
        val validatedStock = maxOf(0, newStockQuantity)
        val prevStock = item.stockQuantity
        val diff = validatedStock - prevStock

        item.stockQuantity = validatedStock

        stockAuditLogs.add(
            StockAdjustmentRecord(
                recordId = "AUDIT_${System.currentTimeMillis()}_${item.id}",
                itemId = item.id,
                itemName = item.name,
                branchId = branchId,
                previousQuantity = prevStock,
                quantityChange = diff,
                newQuantity = validatedStock,
                reason = reason,
                staffEmail = staffEmail
            )
        )
        return true
    }

    @Synchronized
    fun toggleItemEnabled(branchId: String, itemId: String, isEnabled: Boolean): Boolean {
        val item = menuItems.find { it.branchId == branchId && it.id == itemId } ?: return false
        item.isEnabled = isEnabled
        return true
    }

    @Synchronized
    fun getStockAuditLogs(branchId: String): List<StockAdjustmentRecord> {
        return stockAuditLogs.filter { it.branchId == branchId }
            .sortedByDescending { it.timestamp }
    }
}

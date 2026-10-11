package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.adapter.MenuAdapter
import com.example.queueeats.data.CanteenRepository
import com.example.queueeats.data.UserRepository
import com.example.queueeats.network.OpenFoodFactsClient
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Branch1Activity displays the menu and inventory items for a selected canteen branch,
 * allowing customers to browse items, view nutritional details via Open Food Facts API,
 * and add available dishes to their shopping cart.
 */
class Branch1Activity : AppCompatActivity() {

    companion object {
        const val EXTRA_BRANCH_ID = "EXTRA_BRANCH_ID"
    }

    private var currentBranchId: String = CanteenRepository.BRANCH_1_ID
    private lateinit var menuAdapter: MenuAdapter

    private lateinit var cardBottomCart: CardView
    private lateinit var tvCartSummary: TextView
    private lateinit var btnViewCart: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_branch_details)

        // Determine branch ID from intent or default to Branch 1
        currentBranchId = intent.getStringExtra(EXTRA_BRANCH_ID) ?: CanteenRepository.BRANCH_1_ID

        // Handle edge-to-edge window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.branch1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI references
        cardBottomCart = findViewById(R.id.card_bottom_cart)
        tvCartSummary = findViewById(R.id.tv_cart_count_summary)
        btnViewCart = findViewById(R.id.btn_view_cart)

        // Back button listener
        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        // Location row click listener -> opens map screen centered on branch coordinates
        findViewById<View>(R.id.location_row)?.setOnClickListener {
            val branchName = if (currentBranchId == CanteenRepository.BRANCH_1_ID) {
                DashboardActivity.BRANCH_1_NAME
            } else {
                DashboardActivity.BRANCH_2_NAME
            }
            val lat = if (currentBranchId == CanteenRepository.BRANCH_1_ID) {
                DashboardActivity.CANTEEN_1_LATITUDE
            } else {
                DashboardActivity.CANTEEN_2_LATITUDE
            }
            val lng = if (currentBranchId == CanteenRepository.BRANCH_1_ID) {
                DashboardActivity.CANTEEN_1_LONGITUDE
            } else {
                DashboardActivity.CANTEEN_2_LONGITUDE
            }

            val intent = Intent(this, BranchMapActivity::class.java).apply {
                putExtra(BranchMapActivity.EXTRA_BRANCH_NAME, branchName)
                putExtra(BranchMapActivity.EXTRA_LATITUDE, lat)
                putExtra(BranchMapActivity.EXTRA_LONGITUDE, lng)
            }
            startActivity(intent)
        }

        updateOrderNowButtonUI()

        // Setup RecyclerView Menu
        val rvMenu = findViewById<RecyclerView>(R.id.rv_branch_menu)
        rvMenu.layoutManager = LinearLayoutManager(this)

        val branchMenuItems = CanteenRepository.getMenuItems(currentBranchId)
        menuAdapter = MenuAdapter(
            items = branchMenuItems,
            onAddToCartClicked = { menuItem ->
                val success = CanteenRepository.addToCart(currentBranchId, menuItem, 1)
                if (success) {
                    Toast.makeText(this, "Added ${menuItem.name} to cart", Toast.LENGTH_SHORT).show()
                    updateCartBarUI()
                } else {
                    Toast.makeText(this, "Cannot add more. Stock limit reached.", Toast.LENGTH_SHORT).show()
                }
            },
            onItemClicked = { menuItem ->
                // Fetch nutritional data from Open Food Facts API
                lifecycleScope.launch {
                    Toast.makeText(this@Branch1Activity, "Fetching nutritional facts for ${menuItem.name}...", Toast.LENGTH_SHORT).show()
                    val info = OpenFoodFactsClient.searchFood(menuItem.name)

                    val message = if (info != null) {
                        "Product: ${info.productName}\n" +
                                "Brand / Source: ${info.brands}\n" +
                                "Calories: ${info.caloriesPer100g}\n" +
                                "${info.nutriscore}\n\n" +
                                "Ingredients:\n${info.ingredients}"
                    } else {
                        "Description: ${menuItem.description}\nPrice: ₱${String.format(Locale.US, "%.2f", menuItem.price)}"
                    }

                    AlertDialog.Builder(this@Branch1Activity)
                        .setTitle("Nutritional Facts: ${menuItem.name}")
                        .setMessage(message)
                        .setPositiveButton("Close", null)
                        .show()
                }
            }
        )
        rvMenu.adapter = menuAdapter

        // View Cart button click
        btnViewCart.setOnClickListener {
            val intent = Intent(this, CartCheckoutActivity::class.java).apply {
                putExtra(CartCheckoutActivity.EXTRA_BRANCH_ID, currentBranchId)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh menu items, cart bar, and order now/queue button in case state changed
        menuAdapter.updateData(CanteenRepository.getMenuItems(currentBranchId))
        updateCartBarUI()
        updateOrderNowButtonUI()
    }

    private fun updateOrderNowButtonUI() {
        val email = UserRepository.currentUser?.email ?: "guest@queueeats.app"
        val activeOrders = CanteenRepository.getActiveOrdersForBranch(email, currentBranchId)
        val btnOrderNow = findViewById<AppCompatButton>(R.id.btn_order_now)
        val btnViewQueue = findViewById<AppCompatButton>(R.id.btn_view_queue)
        val tvBranchStatus = findViewById<TextView>(R.id.tv_branch_status)
        val layoutStatusBadge = findViewById<View>(R.id.layout_status_badge)

        if (activeOrders.isNotEmpty()) {
            if (activeOrders.size == 1) {
                val order = activeOrders[0]
                btnOrderNow?.text = "Queue: ${order.queueNumber}"
                tvBranchStatus?.text = "Queue: ${order.queueNumber}"
                btnOrderNow?.setOnClickListener {
                    val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                        putExtra(OrderConfirmationActivity.EXTRA_ORDER_ID, order.orderId)
                    }
                    startActivity(intent)
                }
                layoutStatusBadge?.setOnClickListener {
                    val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                        putExtra(OrderConfirmationActivity.EXTRA_ORDER_ID, order.orderId)
                    }
                    startActivity(intent)
                }
            } else {
                val queueStrs = activeOrders.joinToString(", ") { it.queueNumber }
                btnOrderNow?.text = "${activeOrders.size} Queues ($queueStrs)"
                tvBranchStatus?.text = "${activeOrders.size} Active Queues"
                btnOrderNow?.setOnClickListener {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    startActivity(intent)
                }
                layoutStatusBadge?.setOnClickListener {
                    val intent = Intent(this, OrderHistoryActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            btnOrderNow?.text = "Order Now"
            tvBranchStatus?.text = "Open"
            btnOrderNow?.setOnClickListener {
                findViewById<View>(R.id.form_scroll)?.scrollTo(0, findViewById<View>(R.id.tv_section_title).top)
            }
            layoutStatusBadge?.setOnClickListener {
                // Do nothing when status is Open
            }
        }

        btnViewQueue?.setOnClickListener {
            val intent = Intent(this, LiveQueueActivity::class.java).apply {
                putExtra(LiveQueueActivity.EXTRA_BRANCH_ID, currentBranchId)
                if (activeOrders.isNotEmpty()) {
                    putExtra(LiveQueueActivity.EXTRA_QUEUE_NUMBER, activeOrders[0].queueNumber)
                }
            }
            startActivity(intent)
        }
    }

    private fun updateCartBarUI() {
        val itemCount = CanteenRepository.getCartItemCount(currentBranchId)
        val totalAmount = CanteenRepository.getCartTotal(currentBranchId)

        if (itemCount > 0) {
            cardBottomCart.visibility = View.VISIBLE
            tvCartSummary.text = String.format(Locale.US, "%d items • ₱%.2f", itemCount, totalAmount)
        } else {
            cardBottomCart.visibility = View.GONE
        }
    }
}

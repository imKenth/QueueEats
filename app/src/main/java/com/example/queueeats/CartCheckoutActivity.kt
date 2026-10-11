package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.adapter.CartAdapter
import com.example.queueeats.data.CanteenRepository
import com.example.queueeats.data.UserRepository
import java.util.Locale

/**
 * CartCheckoutActivity allows customers to review their shopping cart items,
 * adjust quantities, view subtotals and total, and confirm their order
 * to generate a unique branch queue number.
 */
class CartCheckoutActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BRANCH_ID = "EXTRA_BRANCH_ID"
    }

    private var currentBranchId: String = CanteenRepository.BRANCH_1_ID
    private lateinit var cartAdapter: CartAdapter

    private lateinit var tvBranchName: TextView
    private lateinit var tvCheckoutTotal: TextView
    private lateinit var btnConfirmOrder: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_checkout)

        currentBranchId = intent.getStringExtra(EXTRA_BRANCH_ID) ?: CanteenRepository.BRANCH_1_ID

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI references
        tvBranchName = findViewById(R.id.tv_cart_branch_name)
        tvCheckoutTotal = findViewById(R.id.tv_checkout_total)
        btnConfirmOrder = findViewById(R.id.btn_confirm_order)

        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        val branchName = if (currentBranchId == CanteenRepository.BRANCH_1_ID) {
            DashboardActivity.BRANCH_1_NAME
        } else {
            DashboardActivity.BRANCH_2_NAME
        }
        tvBranchName.text = branchName

        // Setup Cart RecyclerView
        val rvCart = findViewById<RecyclerView>(R.id.rv_cart_items)
        rvCart.layoutManager = LinearLayoutManager(this)

        val initialCart = CanteenRepository.getCart(currentBranchId)
        cartAdapter = CartAdapter(
            cartItems = initialCart,
            onQuantityChanged = { cartItem, newQty ->
                val success = CanteenRepository.updateCartQuantity(currentBranchId, cartItem.menuItem.id, newQty)
                if (success) {
                    refreshCartData()
                } else {
                    Toast.makeText(this, "Stock limit reached for ${cartItem.menuItem.name}", Toast.LENGTH_SHORT).show()
                }
            },
            onRemoveClicked = { cartItem ->
                CanteenRepository.removeFromCart(currentBranchId, cartItem.menuItem.id)
                refreshCartData()
                Toast.makeText(this, "Removed ${cartItem.menuItem.name}", Toast.LENGTH_SHORT).show()
            }
        )
        rvCart.adapter = cartAdapter

        refreshCartData()

        // Confirm Order Button click
        btnConfirmOrder.setOnClickListener {
            if (CanteenRepository.getCart(currentBranchId).isEmpty()) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnConfirmOrder.isEnabled = false // Prevent duplicate taps / double checkout
            val customerEmail = UserRepository.currentUser?.email ?: "guest@queueeats.app"

            val result = CanteenRepository.checkout(currentBranchId, customerEmail)
            if (result.isSuccess) {
                val order = result.getOrNull()!!
                Toast.makeText(this, "Order Confirmed! Queue #${order.queueNumber}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                    putExtra(OrderConfirmationActivity.EXTRA_ORDER_ID, order.orderId)
                }
                startActivity(intent)
                finish()
            } else {
                btnConfirmOrder.isEnabled = true
                val errorMsg = result.exceptionOrNull()?.message ?: "Checkout failed. Please try again."
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshCartData() {
        val cartItems = CanteenRepository.getCart(currentBranchId)
        cartAdapter.updateData(cartItems)

        val total = CanteenRepository.getCartTotal(currentBranchId)
        tvCheckoutTotal.text = String.format(Locale.US, "₱%.2f", total)

        if (cartItems.isEmpty()) {
            btnConfirmOrder.isEnabled = false
            btnConfirmOrder.alpha = 0.5f
        } else {
            btnConfirmOrder.isEnabled = true
            btnConfirmOrder.alpha = 1.0f
        }
    }
}

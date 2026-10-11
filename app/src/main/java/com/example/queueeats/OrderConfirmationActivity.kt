package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.queueeats.data.CanteenRepository
import java.util.Locale

/**
 * OrderConfirmationActivity displays the successful order receipt and prominent queue number.
 */
class OrderConfirmationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ORDER_ID = "EXTRA_ORDER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_confirmation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confirm_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val orderId = intent.getStringExtra(EXTRA_ORDER_ID)
        val order = orderId?.let { CanteenRepository.getOrderById(it) }

        val tvQueueNumber = findViewById<TextView>(R.id.tv_confirm_queue_number)
        val tvRefNo = findViewById<TextView>(R.id.tv_confirm_ref_no)
        val tvBranch = findViewById<TextView>(R.id.tv_confirm_branch)
        val tvPayment = findViewById<TextView>(R.id.tv_confirm_payment)
        val tvTotal = findViewById<TextView>(R.id.tv_confirm_total)
        val layoutItems = findViewById<LinearLayout>(R.id.layout_confirm_items)
        val btnViewLiveQueue = findViewById<AppCompatButton>(R.id.btn_view_live_queue)
        val btnDashboard = findViewById<AppCompatButton>(R.id.btn_back_dashboard)

        if (order != null) {
            tvQueueNumber.text = order.queueNumber
            tvRefNo.text = order.orderId
            tvBranch.text = order.branchName
            tvPayment.text = order.paymentMethod
            tvTotal.text = String.format(Locale.US, "₱%.2f", order.totalAmount)

            // Populate itemized summary
            layoutItems.removeAllViews()
            for (cartItem in order.items) {
                val itemView = TextView(this).apply {
                    text = "${cartItem.quantity}x ${cartItem.menuItem.name} — ₱${String.format(Locale.US, "%.2f", cartItem.subtotal)}"
                    setTextColor(resources.getColor(android.R.color.black, null))
                    textSize = 13f
                    setPadding(0, 4, 0, 4)
                }
                layoutItems.addView(itemView)
            }

            btnViewLiveQueue.setOnClickListener {
                val intent = Intent(this, LiveQueueActivity::class.java).apply {
                    putExtra(LiveQueueActivity.EXTRA_BRANCH_ID, order.branchId)
                    putExtra(LiveQueueActivity.EXTRA_QUEUE_NUMBER, order.queueNumber)
                }
                startActivity(intent)
                finish()
            }
        } else {
            tvQueueNumber.text = "---"
            tvRefNo.text = "Order not found"
        }

        btnDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}

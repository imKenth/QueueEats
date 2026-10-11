package com.example.queueeats

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.queueeats.data.CanteenRepository
import com.example.queueeats.data.UserRepository
import com.example.queueeats.models.OrderStatus

/**
 * LiveQueueActivity displays the current queue calling status for a canteen branch
 * and tracks the customer's order position and orders ahead in line.
 */
class LiveQueueActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BRANCH_ID = "EXTRA_BRANCH_ID"
        const val EXTRA_QUEUE_NUMBER = "EXTRA_QUEUE_NUMBER"
    }

    private var branchId: String = CanteenRepository.BRANCH_1_ID
    private var customerQueueNumber: String = ""

    private lateinit var tvNowCalling: TextView
    private lateinit var tvCustomerQueue: TextView
    private lateinit var tvStatusBadge: TextView
    private lateinit var tvOrdersAhead: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_live_queue)

        branchId = intent.getStringExtra(EXTRA_BRANCH_ID) ?: CanteenRepository.BRANCH_1_ID
        customerQueueNumber = intent.getStringExtra(EXTRA_QUEUE_NUMBER) ?: ""

        if (customerQueueNumber.isEmpty()) {
            val email = UserRepository.currentUser?.email ?: "guest@queueeats.app"
            val activeOrder = CanteenRepository.getActiveOrderForBranch(email, branchId)
            if (activeOrder != null) {
                customerQueueNumber = activeOrder.queueNumber
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.live_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        tvNowCalling = findViewById(R.id.tv_live_now_calling)
        tvCustomerQueue = findViewById(R.id.tv_live_customer_queue)
        tvStatusBadge = findViewById(R.id.tv_live_status_badge)
        tvOrdersAhead = findViewById(R.id.tv_live_orders_ahead)

        findViewById<AppCompatButton>(R.id.btn_refresh_queue).setOnClickListener {
            refreshLiveQueueData()
        }

        refreshLiveQueueData()
    }

    override fun onResume() {
        super.onResume()
        refreshLiveQueueData()
    }

    private fun refreshLiveQueueData() {
        // 1. Now Calling
        val callingNumber = CanteenRepository.getCallingQueueNumber(branchId)
        if (!callingNumber.isNullOrEmpty()) {
            tvNowCalling.text = callingNumber
        } else {
            tvNowCalling.text = "---"
        }

        // 2. Customer Queue & Status
        tvCustomerQueue.text = if (customerQueueNumber.isNotEmpty()) customerQueueNumber else "---"

        // Find customer order
        val allBranchOrders = CanteenRepository.getOrdersForBranch(branchId)
        val customerOrder = allBranchOrders.find { it.queueNumber.equals(customerQueueNumber, ignoreCase = true) }

        if (customerOrder != null) {
            tvStatusBadge.text = customerOrder.status.name

            when (customerOrder.status) {
                OrderStatus.WAITING -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF8C38"))
                    val aheadCount = CanteenRepository.getActiveWaitingOrdersCount(branchId, customerQueueNumber)
                    if (aheadCount > 0) {
                        tvOrdersAhead.text = "$aheadCount order(s) ahead of you in line"
                    } else {
                        tvOrdersAhead.text = "You are next in line!"
                    }
                }
                OrderStatus.CALLED -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27AE60"))
                    tvOrdersAhead.text = "Your number is currently being called! Please proceed to counter."
                }
                OrderStatus.COLLECTED -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2980B9"))
                    tvOrdersAhead.text = "Order has been collected. Thank you!"
                }
                OrderStatus.CANCELLED -> {
                    tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E74C3C"))
                    tvOrdersAhead.text = "This order was cancelled."
                }
            }
        } else {
            tvStatusBadge.text = "UNKNOWN"
            tvOrdersAhead.text = "No active order found for this branch."
        }
    }
}

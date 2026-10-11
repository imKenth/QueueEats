package com.example.queueeats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.adapter.OrderHistoryAdapter
import com.example.queueeats.data.CanteenRepository
import com.example.queueeats.data.UserRepository

/**
 * OrderHistoryActivity allows customers to view their previous canteen orders,
 * separate active waiting orders from collected ones, and check their queue numbers.
 */
class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: OrderHistoryAdapter
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        tvEmpty = findViewById(R.id.tv_empty_history)
        val rvOrders = findViewById<RecyclerView>(R.id.rv_orders_history)
        rvOrders.layoutManager = LinearLayoutManager(this)

        val email = UserRepository.currentUser?.email ?: "guest@queueeats.app"
        val customerOrders = CanteenRepository.getOrdersForCustomer(email)

        historyAdapter = OrderHistoryAdapter(customerOrders) { order ->
            // Clicking an order opens OrderConfirmationActivity
            val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                putExtra(OrderConfirmationActivity.EXTRA_ORDER_ID, order.orderId)
            }
            startActivity(intent)
        }
        rvOrders.adapter = historyAdapter

        if (customerOrders.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        val email = UserRepository.currentUser?.email ?: "guest@queueeats.app"
        val customerOrders = CanteenRepository.getOrdersForCustomer(email)
        historyAdapter.updateData(customerOrders)
        if (customerOrders.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
        }
    }
}

package com.example.queueeats

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.adapter.StaffInventoryAdapter
import com.example.queueeats.adapter.StaffOrderAdapter
import com.example.queueeats.data.CanteenRepository
import com.example.queueeats.data.UserRepository
import com.example.queueeats.models.MenuItem
import com.example.queueeats.models.Order

/**
 * StaffPortalActivity provides the canteen staff interface for managing branch queues
 * (calling numbers, marking orders collected, cancelling) and controlling food inventory stock.
 */
class StaffPortalActivity : AppCompatActivity() {

    private var selectedBranchId: String = CanteenRepository.BRANCH_1_ID

    private lateinit var tvNowCalling: TextView
    private lateinit var layoutQueueTab: LinearLayout
    private lateinit var layoutInventoryTab: LinearLayout
    private lateinit var tabQueue: TextView
    private lateinit var tabInventory: TextView

    private lateinit var orderAdapter: StaffOrderAdapter
    private lateinit var inventoryAdapter: StaffInventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_portal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.staff_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            finish()
        }

        tvNowCalling = findViewById(R.id.tv_staff_now_calling)
        layoutQueueTab = findViewById(R.id.layout_queue_tab)
        layoutInventoryTab = findViewById(R.id.layout_inventory_tab)
        tabQueue = findViewById(R.id.tab_queue)
        tabInventory = findViewById(R.id.tab_inventory)

        // Branch Spinner
        val spinnerBranch = findViewById<Spinner>(R.id.spinner_staff_branch)
        val branches = listOf(DashboardActivity.BRANCH_1_NAME, DashboardActivity.BRANCH_2_NAME)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, branches)
        spinnerBranch.adapter = spinnerAdapter
        spinnerBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBranchId = if (position == 0) CanteenRepository.BRANCH_1_ID else CanteenRepository.BRANCH_2_ID
                refreshStaffData()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Tab Switching
        tabQueue.setOnClickListener {
            tabQueue.setBackgroundColor(getColor(android.R.color.white))
            tabQueue.setTextColor(Color.parseColor("#FF8C38"))
            tabInventory.setBackgroundColor(getColor(android.R.color.transparent))
            tabInventory.setTextColor(Color.parseColor("#666666"))
            layoutQueueTab.visibility = View.VISIBLE
            layoutInventoryTab.visibility = View.GONE
        }

        tabInventory.setOnClickListener {
            tabInventory.setBackgroundColor(getColor(android.R.color.white))
            tabInventory.setTextColor(Color.parseColor("#FF8C38"))
            tabQueue.setBackgroundColor(getColor(android.R.color.transparent))
            tabQueue.setTextColor(Color.parseColor("#666666"))
            layoutInventoryTab.visibility = View.VISIBLE
            layoutQueueTab.visibility = View.GONE
        }

        // Setup Queue RecyclerView
        val rvOrders = findViewById<RecyclerView>(R.id.rv_staff_orders)
        rvOrders.layoutManager = LinearLayoutManager(this)
        orderAdapter = StaffOrderAdapter(
            orders = emptyList(),
            onCallClicked = { order ->
                CanteenRepository.callOrderAgain(selectedBranchId, order.orderId)
                Toast.makeText(this, "Announced Queue #${order.queueNumber}", Toast.LENGTH_SHORT).show()
                refreshStaffData()
            },
            onCollectClicked = { order ->
                CanteenRepository.markOrderCollected(selectedBranchId, order.orderId)
                Toast.makeText(this, "Order #${order.queueNumber} marked as Collected", Toast.LENGTH_SHORT).show()
                refreshStaffData()
            },
            onCancelClicked = { order ->
                val staffEmail = UserRepository.currentUser?.email ?: "Staff"
                CanteenRepository.cancelOrder(selectedBranchId, order.orderId, true, staffEmail)
                Toast.makeText(this, "Order #${order.queueNumber} cancelled & stock restored", Toast.LENGTH_SHORT).show()
                refreshStaffData()
            }
        )
        rvOrders.adapter = orderAdapter

        // Call Next Button
        findViewById<View>(R.id.btn_call_next).setOnClickListener {
            val calledOrder = CanteenRepository.callNextOrder(selectedBranchId)
            if (calledOrder != null) {
                Toast.makeText(this, "Now Calling Queue #${calledOrder.queueNumber}", Toast.LENGTH_SHORT).show()
                refreshStaffData()
            } else {
                Toast.makeText(this, "No waiting orders in queue", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup Inventory RecyclerView
        val rvInventory = findViewById<RecyclerView>(R.id.rv_staff_inventory)
        rvInventory.layoutManager = LinearLayoutManager(this)
        inventoryAdapter = StaffInventoryAdapter(
            items = emptyList(),
            onEditStockClicked = { menuItem ->
                showEditStockDialog(menuItem)
            },
            onToggleEnabled = { menuItem, isEnabled ->
                CanteenRepository.toggleItemEnabled(selectedBranchId, menuItem.id, isEnabled)
                Toast.makeText(this, "${menuItem.name} for sale: $isEnabled", Toast.LENGTH_SHORT).show()
                refreshStaffData()
            }
        )
        rvInventory.adapter = inventoryAdapter

        refreshStaffData()
    }

    override fun onResume() {
        super.onResume()
        refreshStaffData()
    }

    private fun refreshStaffData() {
        // Calling number banner
        val callingNum = CanteenRepository.getCallingQueueNumber(selectedBranchId)
        tvNowCalling.text = if (!callingNum.isNullOrEmpty()) callingNum else "---"

        // Orders list
        val branchOrders = CanteenRepository.getOrdersForBranch(selectedBranchId)
        orderAdapter.updateData(branchOrders)

        // Inventory list
        val branchMenu = CanteenRepository.getMenuItems(selectedBranchId)
        inventoryAdapter.updateData(branchMenu)
    }

    private fun showEditStockDialog(menuItem: MenuItem) {
        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(menuItem.stockQuantity.toString())
            setSelection(text.length)
        }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 24)
            addView(TextView(this@StaffPortalActivity).apply { text = "Enter new stock quantity for ${menuItem.name}:" })
            addView(input)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Stock Quantity")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val newStockText = input.text.toString()
                val newStock = newStockText.toIntOrNull()
                if (newStock != null && newStock >= 0) {
                    val staffEmail = UserRepository.currentUser?.email ?: "Staff"
                    CanteenRepository.updateStock(selectedBranchId, menuItem.id, newStock, "Manual Staff Adjustment", staffEmail)
                    Toast.makeText(this, "Stock updated successfully", Toast.LENGTH_SHORT).show()
                    refreshStaffData()
                } else {
                    Toast.makeText(this, "Please enter a valid non-negative number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

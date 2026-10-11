package com.example.queueeats.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.R
import com.example.queueeats.models.Order
import com.example.queueeats.models.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryAdapter(
    private var orders: List<Order>,
    private val onOrderClicked: (Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBranch: TextView = itemView.findViewById(R.id.tv_history_branch)
        val tvDate: TextView = itemView.findViewById(R.id.tv_history_date)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_history_status)
        val tvQueueNum: TextView = itemView.findViewById(R.id.tv_history_queue_num)
        val tvTotal: TextView = itemView.findViewById(R.id.tv_history_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = orders[position]
        holder.tvBranch.text = order.branchName
        holder.tvQueueNum.text = order.queueNumber
        holder.tvTotal.text = String.format(Locale.US, "₱%.2f", order.totalAmount)

        val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.US)
        holder.tvDate.text = dateFormat.format(Date(order.orderTimestamp))

        holder.tvStatus.text = order.status.name
        when (order.status) {
            OrderStatus.WAITING -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF8C38"))
            OrderStatus.CALLED -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27AE60"))
            OrderStatus.COLLECTED -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2980B9"))
            OrderStatus.CANCELLED -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E74C3C"))
        }

        holder.itemView.setOnClickListener {
            onOrderClicked(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}

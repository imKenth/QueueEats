package com.example.queueeats.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.R
import com.example.queueeats.models.Order
import com.example.queueeats.models.OrderStatus
import java.util.Locale

class StaffOrderAdapter(
    private var orders: List<Order>,
    private val onCallClicked: (Order) -> Unit,
    private val onCollectClicked: (Order) -> Unit,
    private val onCancelClicked: (Order) -> Unit
) : RecyclerView.Adapter<StaffOrderAdapter.StaffOrderViewHolder>() {

    class StaffOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQueueNum: TextView = itemView.findViewById(R.id.tv_staff_queue_num)
        val tvOrderId: TextView = itemView.findViewById(R.id.tv_staff_order_id)
        val tvItems: TextView = itemView.findViewById(R.id.tv_staff_items_summary)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_staff_status)
        val tvTotal: TextView = itemView.findViewById(R.id.tv_staff_total)
        val btnCall: Button = itemView.findViewById(R.id.btn_staff_call)
        val btnCollect: Button = itemView.findViewById(R.id.btn_staff_collect)
        val btnCancel: Button = itemView.findViewById(R.id.btn_staff_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_order, parent, false)
        return StaffOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: StaffOrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvQueueNum.text = order.queueNumber
        holder.tvOrderId.text = order.orderId
        holder.tvTotal.text = String.format(Locale.US, "Total: ₱%.2f", order.totalAmount)

        val itemsSummary = order.items.joinToString(", ") { "${it.quantity}x ${it.menuItem.name}" }
        holder.tvItems.text = itemsSummary

        holder.tvStatus.text = order.status.name
        when (order.status) {
            OrderStatus.WAITING -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF8C38"))
            OrderStatus.CALLED -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#27AE60"))
            OrderStatus.COLLECTED -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2980B9"))
            OrderStatus.CANCELLED -> holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E74C3C"))
        }

        holder.btnCall.setOnClickListener { onCallClicked(order) }
        holder.btnCollect.setOnClickListener { onCollectClicked(order) }
        holder.btnCancel.setOnClickListener { onCancelClicked(order) }

        if (order.status == OrderStatus.COLLECTED || order.status == OrderStatus.CANCELLED) {
            holder.btnCall.isEnabled = false
            holder.btnCollect.isEnabled = false
            holder.btnCancel.isEnabled = false
            holder.btnCall.alpha = 0.4f
            holder.btnCollect.alpha = 0.4f
            holder.btnCancel.alpha = 0.4f
        } else {
            holder.btnCall.isEnabled = true
            holder.btnCollect.isEnabled = true
            holder.btnCancel.isEnabled = true
            holder.btnCall.alpha = 1.0f
            holder.btnCollect.alpha = 1.0f
            holder.btnCancel.alpha = 1.0f
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }
}

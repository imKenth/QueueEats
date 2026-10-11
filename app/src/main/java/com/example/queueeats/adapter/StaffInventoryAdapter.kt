package com.example.queueeats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.R
import com.example.queueeats.models.MenuItem
import java.util.Locale

class StaffInventoryAdapter(
    private var items: List<MenuItem>,
    private val onEditStockClicked: (MenuItem) -> Unit,
    private val onToggleEnabled: (MenuItem, Boolean) -> Unit
) : RecyclerView.Adapter<StaffInventoryAdapter.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_inv_item_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_inv_item_price)
        val tvStockQty: TextView = itemView.findViewById(R.id.tv_inv_stock_qty)
        val switchEnabled: Switch = itemView.findViewById(R.id.switch_item_enabled)
        val btnEditStock: Button = itemView.findViewById(R.id.btn_edit_stock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = String.format(Locale.US, "₱%.2f", item.price)
        holder.tvStockQty.text = "${item.stockQuantity} servings"

        holder.switchEnabled.setOnCheckedChangeListener(null)
        holder.switchEnabled.isChecked = item.isEnabled

        holder.switchEnabled.setOnCheckedChangeListener { _, isChecked ->
            onToggleEnabled(item, isChecked)
        }

        holder.btnEditStock.setOnClickListener {
            onEditStockClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MenuItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

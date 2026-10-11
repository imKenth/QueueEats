package com.example.queueeats.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.R
import com.example.queueeats.models.MenuItem
import java.util.Locale

class MenuAdapter(
    private var items: List<MenuItem>,
    private val onAddToCartClicked: (MenuItem) -> Unit,
    private val onItemClicked: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDish: ImageView = itemView.findViewById(R.id.iv_dish)
        val tvName: TextView = itemView.findViewById(R.id.tv_dish_name)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_dish_desc)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_dish_price)
        val tvStockBadge: TextView = itemView.findViewById(R.id.tv_stock_badge)
        val btnAdd: ImageButton = itemView.findViewById(R.id.btn_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_food, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvDesc.text = item.description
        holder.tvPrice.text = String.format(Locale.US, "₱%.2f", item.price)
        holder.ivDish.setImageResource(item.imageResId)

        // Availability and stock status badge handling
        if (!item.isAvailable) {
            holder.tvStockBadge.text = "SOLD OUT"
            holder.tvStockBadge.setBackgroundResource(R.drawable.bg_chip_selected)
            holder.tvStockBadge.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#E74C3C")
            )
            holder.btnAdd.isEnabled = false
            holder.btnAdd.alpha = 0.4f
        } else if (item.isLowStock) {
            holder.tvStockBadge.text = "Only ${item.stockQuantity} left!"
            holder.tvStockBadge.setBackgroundResource(R.drawable.bg_chip_selected)
            holder.tvStockBadge.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#E67E22")
            )
            holder.btnAdd.isEnabled = true
            holder.btnAdd.alpha = 1.0f
        } else {
            holder.tvStockBadge.text = "Stock: ${item.stockQuantity}"
            holder.tvStockBadge.setBackgroundResource(R.drawable.bg_chip_selected)
            holder.tvStockBadge.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#27AE60")
            )
            holder.btnAdd.isEnabled = true
            holder.btnAdd.alpha = 1.0f
        }

        holder.btnAdd.setOnClickListener {
            if (item.isAvailable) {
                onAddToCartClicked(item)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MenuItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

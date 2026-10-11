package com.example.queueeats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.queueeats.R
import com.example.queueeats.models.CartItem
import java.util.Locale

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onRemoveClicked: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_cart_item_name)
        val tvPrice: TextView = itemView.findViewById(R.id.tv_cart_item_price)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tv_cart_item_subtotal)
        val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        val btnPlus: Button = itemView.findViewById(R.id.btn_plus)
        val btnMinus: Button = itemView.findViewById(R.id.btn_minus)
        val btnRemove: TextView = itemView.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.tvName.text = cartItem.menuItem.name
        holder.tvPrice.text = String.format(Locale.US, "₱%.2f each", cartItem.menuItem.price)
        holder.tvSubtotal.text = String.format(Locale.US, "₱%.2f", cartItem.subtotal)
        holder.tvQuantity.text = cartItem.quantity.toString()

        holder.btnPlus.setOnClickListener {
            onQuantityChanged(cartItem, cartItem.quantity + 1)
        }

        holder.btnMinus.setOnClickListener {
            onQuantityChanged(cartItem, cartItem.quantity - 1)
        }

        holder.btnRemove.setOnClickListener {
            onRemoveClicked(cartItem)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newItems: List<CartItem>) {
        this.cartItems = newItems
        notifyDataSetChanged()
    }
}

package com.example.sampleshoppingcart.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sampleshoppingcart.R
import com.example.sampleshoppingcart.data.CartItem
import com.example.sampleshoppingcart.data.OrderProduct
import com.example.sampleshoppingcart.data.SummaryItem
import com.example.sampleshoppingcart.mapToDrawableId
import com.google.android.material.button.MaterialButton

private const val PRODUCT_ITEM_VIEWTYPE = 0
private const val SUMMARY_VIEWTYPE = 1

class CartCarouselAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onProductOrderListener: OnProductClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnProductClickListener {
        fun onRemove(position: Int)
        fun onChangeItemQuantity(position: Int, size: Int)
    }

    class ProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImg: ImageView = itemView.findViewById(R.id.product_img)
        val productTitle: TextView = itemView.findViewById(R.id.product_title)
        val productDesc: TextView = itemView.findViewById(R.id.product_desc)
        val price: TextView = itemView.findViewById(R.id.product_price)
        val ratingBar: AppCompatRatingBar = itemView.findViewById(R.id.product_rating)
        val removeButton: MaterialButton = itemView.findViewById(R.id.remove)
        val autoCompleteTextView: AutoCompleteTextView =
            itemView.findViewById(R.id.autoCompleteTextView)

        init {
            val quantity = itemView.context.resources.getStringArray(R.array.quantity)
            val arrayAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item, quantity)
            autoCompleteTextView.setAdapter(arrayAdapter)
            autoCompleteTextView.threshold = 5
        }
    }

    class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val totalPrice: TextView = itemView.findViewById(R.id.total_price)
        val discountPrice: TextView = itemView.findViewById(R.id.discount_price)
        val taxFee: TextView = itemView.findViewById(R.id.tax_fee)
        val estimatedTotalPrice: TextView = itemView.findViewById(R.id.estimated_total_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PRODUCT_ITEM_VIEWTYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)

                return ProductItemViewHolder(itemView)
            }
            SUMMARY_VIEWTYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.summary_item, parent, false)
                return SummaryViewHolder(itemView)
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            PRODUCT_ITEM_VIEWTYPE -> {
                val orderProduct = cartItems[position] as OrderProduct
                bindProductViewHolder(holder as ProductItemViewHolder, orderProduct, position)
            }
            SUMMARY_VIEWTYPE -> {
                val summaryItem = cartItems[position] as SummaryItem
                bindSummaryViewHolder(holder as SummaryViewHolder, summaryItem)
            }
        }

    }

    private fun bindProductViewHolder(
        holder: ProductItemViewHolder,
        orderProduct: OrderProduct,
        position: Int
    ) {
        holder.productTitle.text = orderProduct.product.productName
        holder.productDesc.text = orderProduct.product.desc
        holder.price.text = buildString {
            append("$")
            append(orderProduct.product.price)
        }
        Glide.with(holder.itemView.context).load(orderProduct.product.imageUrl.mapToDrawableId())
            .into(holder.productImg)
        holder.ratingBar.rating = 5f

        if (holder.autoCompleteTextView.text.toString() == "") {
            holder.autoCompleteTextView.setText(orderProduct.quantity.toString())
        }
        holder.autoCompleteTextView.setOnItemClickListener { parent, view, pos, id ->
            val selectedItem = parent.getItemAtPosition(pos) as String
            Log.d("TAG", "onBindViewHolder: $selectedItem")
            onProductOrderListener.onChangeItemQuantity(
                position = position,
                size = selectedItem.toInt()
            )
        }
        holder.removeButton.setOnClickListener {
            onProductOrderListener.onRemove(position)
        }
    }

    private fun bindSummaryViewHolder(
        holder: SummaryViewHolder,
        summaryItem: SummaryItem
    ) {
        holder.totalPrice.text = summaryItem.totalPrice.toString()
        holder.discountPrice.text = summaryItem.discountPrice.toString()
        holder.taxFee.text = summaryItem.discountPrice.toString()
        holder.estimatedTotalPrice.text = summaryItem.estimatedTotalPrice.toString()
    }

    override fun getItemViewType(position: Int): Int {
        return when (cartItems[position]) {
            is OrderProduct -> PRODUCT_ITEM_VIEWTYPE
            is SummaryItem -> SUMMARY_VIEWTYPE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(cartItems: List<CartItem>) {
        this.cartItems.clear()
        this.cartItems.addAll(cartItems)
        notifyDataSetChanged()
    }

    override fun getItemCount() = cartItems.size
}

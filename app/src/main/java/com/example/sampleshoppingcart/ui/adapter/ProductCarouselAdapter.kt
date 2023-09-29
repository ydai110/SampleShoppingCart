package com.example.sampleshoppingcart.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sampleshoppingcart.R
import com.example.sampleshoppingcart.data.Product
import com.example.sampleshoppingcart.mapToDrawableId
import com.google.android.material.button.MaterialButton

class ProductCarouselAdapter(
    val productsList: MutableList<Product>,
    val onProductOrderListener: OnProductOrderListener,
) :
    RecyclerView.Adapter<ProductCarouselAdapter.ProductItemViewHolder>() {

    interface OnProductOrderListener {
        fun onClick(product: Product)
    }

    class ProductItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImg: ImageView = itemView.findViewById(R.id.product_img)
        val productTitle: TextView = itemView.findViewById(R.id.product_title)
        val productDesc: TextView = itemView.findViewById(R.id.product_desc)
        val price: TextView = itemView.findViewById(R.id.product_price)
        val ratingBar: AppCompatRatingBar = itemView.findViewById(R.id.product_rating)
        val orderButton: MaterialButton = itemView.findViewById(R.id.order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)

        return ProductItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductItemViewHolder, position: Int) {
        val product = productsList[position]
        holder.productTitle.text = product.productName
        holder.productDesc.text = product.desc
        holder.price.text = buildString {
            append("$")
            append(product.price)
        }
        Glide.with(holder.itemView.context).load(product.imageUrl.mapToDrawableId())
            .into(holder.productImg)
        holder.ratingBar.rating = 5f
        holder.orderButton.setOnClickListener {
            onProductOrderListener.onClick(product)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(productsList: List<Product>) {
        this.productsList.clear()
        this.productsList.addAll(productsList)
    }

    override fun getItemCount() = productsList.size
}

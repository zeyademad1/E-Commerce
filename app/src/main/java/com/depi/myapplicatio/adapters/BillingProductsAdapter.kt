package com.depi.myapplicatio.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.depi.myapplicatio.R
import com.depi.myapplicatio.data.Address
import com.depi.myapplicatio.data.CartProduct
import com.depi.myapplicatio.databinding.BillingProductsRvItemBinding

class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsViewHolder>() {

    // using DiffUtil for RV operations
    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.billing_products_rv_item, parent, false)
        return BillingProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)

    }

    override fun getItemCount() = differ.currentList.size

}

class BillingProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = BillingProductsRvItemBinding.bind(itemView)

    fun bind(cartProduct: CartProduct) {
        binding.apply {
            Glide.with(itemView).load(cartProduct.product.images?.get(0))
                .into(binding.imageCartProduct)
            tvProductCartName.text = cartProduct.product.name
            tvBillingProductQuantity.text = cartProduct.quantity.toString()
            tvCartProductSize.text = cartProduct.selectedSize ?: "".also {
                imageCartProductColor.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            imageCartProductColor.setImageDrawable(
                ColorDrawable(
                    cartProduct.selectedColor ?: Color.TRANSPARENT
                )
            )

        }
    }

}
package com.hammadirfan.smdproject

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage

class ProductAdapter(private var products: MutableList<Item_Prod>, private val onProductClick: (Item_Prod) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var fullProductList: MutableList<Item_Prod> = products.toMutableList()

// Make sure to update fullProductList wherever products are modified.

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productName)
        val productDescription: TextView = view.findViewById(R.id.productDescription)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productUnits: TextView = view.findViewById(R.id.productUnits)
        val productStatus: TextView = view.findViewById(R.id.productStatus)
        val productImage: ImageView = view.findViewById(R.id.productImage)
    }
    // Keep a full copy of the products to use for filtering

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = "Rs. ${String.format("%.2f", product.price)}"
        holder.productUnits.text = "Units Remaining: ${product.unitsRemaining}"
        holder.productStatus.text = product.status ?: "" // Show status if non-null

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(product.imageUrl)

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .into(holder.productImage)






        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount() = products.size
    fun updateProduct(updatedProduct: Item_Prod) {
        val index = fullProductList.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            fullProductList [index] = updatedProduct
            val displayIndex = products.indexOfFirst { it.id == updatedProduct.id }
            if (displayIndex != -1) {
                products[displayIndex] = updatedProduct
                notifyItemChanged(displayIndex)
            }
        }
    }

    fun filter(query: String) {
        products.clear()
        if (query.isEmpty()) {
            products.addAll(fullProductList)
        } else {
            products.addAll(fullProductList.filter { it.name.contains(query, ignoreCase = true) })
        }
        notifyDataSetChanged()
    }

}

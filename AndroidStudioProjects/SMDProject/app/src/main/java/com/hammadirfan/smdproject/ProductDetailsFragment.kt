package com.hammadirfan.smdproject

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.android.car.ui.toolbar.MenuItem
import com.bumptech.glide.Glide

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
interface OnProductUpdatedListener {
    fun onProductUpdated(updatedProduct: Item_Prod)
}
/**
 * A simple [Fragment] subclass.
 * Use the [ProductDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var product: Item_Prod? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            product = it.getSerializable(ARG_PRODUCT) as Item_Prod?
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
        val product = arguments?.getSerializable(ARG_PRODUCT) as Item_Prod
        // Initialize views with product data...
        view.findViewById<TextView>(R.id.productName).text = "Product Name: ${product?.name}"
        view.findViewById<TextView>(R.id.productDescription).text = "Description: ${product?.description}"
        view.findViewById<TextView>(R.id.productPrice).text = "Price: Rs. ${product?.price}"
        view.findViewById<TextView>(R.id.productUnits).text = "Units Remaining: ${product?.unitsRemaining}"
        Glide.with(this)
            .load(product.imageUrl)
            .into(view.findViewById<ImageView>(R.id.productImage))
        updateProductDetails()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()  // Clear any existing menu items
        inflater.inflate(R.menu.edit_menu, menu)  // Inflate the new menu; this menu should contain your edit button
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                showEditDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showEditDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_product_dialog, null)
        val productNameEditText = dialogView.findViewById<EditText>(R.id.editProductName)
        val productPriceEditText = dialogView.findViewById<EditText>(R.id.editProductPrice)

        // Initialize dialog fields with current product details
        product?.let {
            productNameEditText.setText(it.name)
            productPriceEditText.setText(it.price.toString())
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Save") { _, _ ->
                // Update product details with new inputs from dialog
                product?.name = productNameEditText.text.toString()
                product?.price = productPriceEditText.text.toString().toDouble()
                updateProductDetails() // Update the UI with new product details
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProductDetails() {
        view?.findViewById<TextView>(R.id.productName)?.text = product?.name
        view?.findViewById<TextView>(R.id.productDescription)?.text = product?.description
        view?.findViewById<TextView>(R.id.productPrice)?.text = "Price: Rs. ${product?.price}"
        view?.findViewById<TextView>(R.id.productUnits)?.text = "Units Remaining: ${product?.unitsRemaining}"

        // Notify the MainActivity (or ManagerDashboard)
        (activity as? OnProductUpdatedListener)?.onProductUpdated(product!!)
    }



    var listener: OnProductUpdatedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnProductUpdatedListener
    }

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Item_Prod): ProductDetailsFragment {
            val fragment = ProductDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_PRODUCT, product)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()  // Forces the activity to redraw the menu, thereby calling onCreateOptionsMenu
    }
}
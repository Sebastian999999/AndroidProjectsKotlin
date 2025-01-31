package com.hammadirfan.smdproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class ManagerDashboard : AppCompatActivity(),OnProductUpdatedListener {
    private var isMenuVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_dashboard)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load DashboardFragment initially
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment(), "Dashboard")
        }

        // Listen for changes in the back stack
        supportFragmentManager.addOnBackStackChangedListener {
            updateTitle()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        if (!isMenuVisible) {
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = false
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle back action
                onBackPressed()
                //supportActionBar?.title = "Manager Dashboard"
                return true
            }
            R.id.action_dashboard -> {
                loadFragment(DashboardFragment() , "Dashboard")
                supportActionBar?.title = "Dashboard"
                return true
            }
            R.id.action_orders -> {
                loadFragment(OrdersFragment(), "Orders")
                supportActionBar?.title = "Orders"
                return true
            }
            R.id.action_inventory -> {
                loadFragment(InventoryFragment(), "Products")
                return true
            }
            R.id.action_reorder -> {
                loadFragment(ReorderStockFragment(), "Reorder Products")
                return true
            }
            R.id.action_add_product -> {
                loadFragment(AddProductFragment(), "Add Product")
                return true
            }
            R.id.action_sign_out -> {
                finish()
                startActivity(Intent(this, SignIn::class.java))
                return true
            }
            // Add other cases for different menu items
        }
        return super.onOptionsItemSelected(item)
    }
    fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag) // Tag the fragment for easy retrieval
            .addToBackStack(tag) // Use tag for back stack as well
            .commit()
    }

    fun ShowProductDetails(product: Item_Prod) {
        val fragment = ProductDetailsFragment.newInstance(product)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, "ProductDetailsFragment") // Tag this fragment
            .addToBackStack("ProductDetails") // Consistent back stack management
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Product Details"
        hideMenuItems()
    }


    fun showOrderDetails(order: Order_s) {
        val fragment = OrderDetails.newInstance(order)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Order Details"
        hideMenuItems()
    }

    fun showReorderProductDetails(product: Item_Prod) {
        val fragment = ProductDetailsFragment.newInstance(product)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment , "ProductDetailsFragment")
            .addToBackStack("Product Details")
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reorder Product"
        hideMenuItems()
    }
    fun hideMenuItems() {
        // Assuming you have a boolean to track this
        isMenuVisible = false
        invalidateOptionsMenu() // Causes onCreateOptionsMenu to be called again
    }

    fun replaceFragment(fragment: Fragment , tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(tag)
            .commit()
    }

    private fun updateTitle() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        supportActionBar?.title = when (fragment) {
            is DashboardFragment -> "Dashboard"
            is OrdersFragment -> "Orders"
            is InventoryFragment -> "Products"
            is ProductDetailsFragment -> "Product Details"
            is OrderDetails -> "Order Details"
            is ReorderStockFragment -> "Reorder Products"
            is AddProductFragment -> "Add Product"
            else -> "Manager Dashboard"
        }
    }
    override fun onProductUpdated(updatedProduct: Item_Prod) {
        // Retrieve the InventoryFragment using its tag
        val inventoryFragment = supportFragmentManager.findFragmentByTag("Products") as? InventoryFragment
        inventoryFragment?.onProductUpdated(updatedProduct)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        isMenuVisible = true  // Re-show the menu items when going back
        invalidateOptionsMenu()  // Refresh the menu state
    }


}

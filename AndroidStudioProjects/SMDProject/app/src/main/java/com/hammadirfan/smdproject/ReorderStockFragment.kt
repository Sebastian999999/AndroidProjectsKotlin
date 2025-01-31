package com.hammadirfan.smdproject

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.apache.http.client.methods.RequestBuilder.put
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReorderStockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReorderStockFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var sampleProducts: MutableList<Item_Prod> = mutableListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reorder_stock, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewInventory)
        recyclerView.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.IO).launch {
            fetchProducts()
        }

        return view
    }

    private suspend fun fetchProducts() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://172.17.67.216/myapp/fetch_products.php")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val responseData = response.body?.string()
            val jsonArray = JSONArray(responseData)

            val databaseHelper = DatabaseHelper(context?.applicationContext!!)
            val db = databaseHelper.writableDatabase

            // Clear the table before inserting new data
            db.delete("products", null, null)

            val products = mutableListOf<Item_Prod>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("id")
                val name = jsonObject.getString("name")
                val description = jsonObject.getString("description")
                val price = jsonObject.getDouble("price")
                val unitsRemaining = jsonObject.getInt("Quantity")
                val imageUrl = jsonObject.getString("image_url")
                val status = if (jsonObject.getInt("Quantity") > 10) "Available"
                else if (jsonObject.getInt("Quantity") > 0) "Low Stock"
                else "Out of Stock"

                // Insert the product into the SQLite database
                val values = ContentValues().apply {
                    put("id", id)
                    put("name", name)
                    put("description", description)
                    put("price", price)
                    put("Quantity", unitsRemaining)
                    put("image_url", imageUrl)
                    put("status", status)
                }
                db.insert("products", null, values)

                products.add(Item_Prod(id, name, description, price, unitsRemaining, imageUrl, status))
            }

            db.close()

            withContext(Dispatchers.Main) {
                adapter = ProductAdapter(products) { product ->
                    (activity as? ManagerDashboard)?.ShowProductDetails((product))
                }
                recyclerView.adapter = adapter
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReorderStockFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReorderStockFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
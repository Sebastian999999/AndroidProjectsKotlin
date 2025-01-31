package com.example.retrofitpractice

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitpractice.Repository.Repository
import com.example.retrofitpractice.adapter.MyAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel
    private val myAdapter by lazy{ MyAdapter()}
    private lateinit var recyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
         viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]
        val btn = findViewById<Button>(R.id.btnUserAllPostsWithMultipleQueries1)
        val ev = findViewById<EditText>(R.id.metUserResponseSetId)
        recyclerView = findViewById<RecyclerView>(R.id.rv)
         setupRecyclerView()
        btn.setOnClickListener {
            viewModel.getUserPostsWithMultipleQueries(ev.text.toString().toInt(),"id","desc")
            viewModel.myResponse3.observe(this,Observer{response->
                if (response.isSuccessful){
                    response.body()?.let{myAdapter.setData(it)}
                }
                else {
                    Toast.makeText(this,response.code(), Toast.LENGTH_LONG).show()
                }
            })
        }





//        val repository = Repository()
//        val tvresponse = findViewById<TextView>(R.id.tvResponsePostBody)
//        val tvresponseUsersPosts = findViewById<TextView>(R.id.tvResponseUserPostsBody)
//        val tvresponseall = findViewById<TextView>(R.id.tvResponseAllPostsBody)
//        val ev = findViewById<EditText>(R.id.metResponseSetId)
//        val evUser = findViewById<EditText>(R.id.metUserResponseSetId)
//        val viewModelFactory = MainViewModelFactory(repository)
//        Toast.makeText(this, "Hallo", Toast.LENGTH_SHORT).show()
//
//        val btn = findViewById<Button>(R.id.btnPost)
//        val btnAll = findViewById<Button>(R.id.btnAllPosts)
//        val btnUserAllPosts = findViewById<Button>(R.id.btnUserAllPosts)
//        val btnUserAllPostsWithMultipleQueries = findViewById<Button>(R.id.btnUserAllPostsWithMultipleQueries1)
//
//        val tgbsort = findViewById<ToggleButton>(R.id.sortToggleButton)
//        val tgborder = findViewById<ToggleButton>(R.id.orderToggleButton)
//        var options:HashMap<String , String> = HashMap()
//        options["_sort"] = "id"
//        options["_order"] = "desc"

        //viewModel = ViewModelProvider(this,viewModelFactory)[MainViewModel::class.java]

//        btn.setOnClickListener {
//            val myNumber = ev.text.toString()
//            viewModel.getPost2(Integer.parseInt(myNumber))
//            viewModel.myResponse2.observe(this , Observer {response ->
//                if (response.isSuccessful){
//                    Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
//                    tvresponse.text = response.body().toString()
//                }
//                else {
//                    Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
//                    tvresponse.text = response.code().toString()
//                }
//            })
//        }
//
//        btnAll.setOnClickListener{
//            viewModel.getPosts()
//            viewModel.myResponse3.observe(this, Observer {response ->
//                if (response.isSuccessful){
//                    var text: String ? = null
//                    response.body()?.forEach{
//                        text+= it.title
//                    }
//                    tvresponseall.text = text
//                }
//                else {
//                    tvresponseall.text = response.code().toString()
//                }
//            })
//        }
//
//        btnUserAllPosts.setOnClickListener{
//            viewModel.getUserPosts(evUser.text.toString().toInt())
//            viewModel.userPostsResponse.observe(this , Observer { response ->
//                if (response.isSuccessful){
//                    tvresponseUsersPosts.text = response.body().toString()
//                    response.body()?.forEach{
//                        Log.d("Response",it.myUserId.toString())
//                        Log.d("Response",it.id.toString())
//                        Log.d("Response",it.title.toString())
//                        Log.d("Response",it.body.toString())
//                    }
//                }
//                else{
//                    tvresponseUsersPosts.text = response.code().toString()
//                }
//            })
//        }
//
//        btnUserAllPostsWithMultipleQueries.setOnClickListener{
//            var orderState : String? = null
//
//
//            orderState = when(tgborder.isChecked){
//                true -> "desc"
//                else -> "asc"
//            }
//            options["_order"] = orderState
//            viewModel.getUserPostsWithMapQuery(evUser.text.toString().toInt(),options)
//            //viewModel.getUserPostsWithMultipleQueries(evUser.text.toString().toInt(),"id",orderState)
//            var text : String? = null
//            viewModel.userPostsResponse.observe(
//                this,
//                Observer{response->
//                    if (response.isSuccessful){
//                        response.body()?.forEach{
//                            text += (it.id.toString() + "\n" +it.title.toString()+"\n")
//                            Log.d("Response1",it.myUserId.toString())
//                            Log.d("Response1",it.id.toString())
//                            Log.d("Response1",it.title.toString())
//                            Log.d("Response1",it.body.toString())
//                        }
//                        tvresponseUsersPosts.text = text
//
//                    }
//                    else{
//                        tvresponseUsersPosts.text = response.code().toString()
//                    }
//                }
//            )
//        }
    }

    private fun setupRecyclerView(){
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
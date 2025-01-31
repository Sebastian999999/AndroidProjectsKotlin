package com.hammadirfan.myrvex

import  android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list=ArrayList<Model>()
        list.add(Model("Ali","1234567","ali.irfan@gmail.com"))
        list.add(Model("Ahmed","1234567","ali.irfan@gmail.com"))
        list.add(Model("Asad","9876543","ali.irfan@gmail.com"))
        list.add(Model("Saad","1234567","ali.irfan@gmail.com"))
        list.add(Model("Pizza","7654123","ali.irfan@gmail.com"))
        list.add(Model("Lelo","1234567","ali.irfan@gmail.com"))
        list.add(Model("Hmmm","1234765","ali.irfan@gmail.com"))
        list.add(Model("Yoooo","1234567","ali.irfan@gmail.com"))
        list.add(Model("Kala","1234567","ali.irfan@gmail.com"))
        list.add(Model("plop","1234567","ali.irfan@gmail.com"))

        list.add(Model("Ali","1234567","ali.irfan@gmail.com"))
        list.add(Model("Ahmed","1234567","ali.irfan@gmail.com"))
        list.add(Model("Asad","9876543","ali.irfan@gmail.com"))
        list.add(Model("Saad","1234567","ali.irfan@gmail.com"))
        list.add(Model("Pizza","7654123","ali.irfan@gmail.com"))
        list.add(Model("Lelo","1234567","ali.irfan@gmail.com"))
        list.add(Model("Hmmm","1234765","ali.irfan@gmail.com"))
        list.add(Model("Yoooo","1234567","ali.irfan@gmail.com"))
        list.add(Model("Kala","1234567","ali.irfan@gmail.com"))
        list.add(Model("plop","1234567","ali.irfan@gmail.com"))

        val adapter = MyAdapter(list,this)
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager=LinearLayoutManager(this)
        rv.adapter=adapter

        var add=findViewById<Button>(R.id.addcontentbutton)
        var resultlauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                var data: Intent? = it.data
                var name = data?.getStringExtra("name")
                var phno = data?.getStringExtra("phno")
                var email = data?.getStringExtra("email")
                list.add(Model(name!!,phno!!,email!!))
                adapter.notifyDataSetChanged()
            }
        }

        add.setOnClickListener(){
            val i = Intent(this,NewContent::class.java)
            resultlauncher.launch(i)
        }
    }
}
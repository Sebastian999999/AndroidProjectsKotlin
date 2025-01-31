package com.hammadirfan.emailloginrecyclerview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hammadirfan.myrvex.MyAdapter

class MainScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        val logout=findViewById<Button>(R.id.button)
        val et=findViewById<EditText>(androidx.core.R.id.edit_text_id)
        val tv=findViewById<TextView>(R.id.tv)
        val save=findViewById<Button>(R.id.save)
        val add=findViewById<Button>(R.id.add)
        val rv=findViewById<RecyclerView>(R.id.row)

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "Permission is granted")
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        add.setOnClickListener {
            var i=Intent(this, Add::class.java)
            startActivity(i)
        }
        logout.setOnClickListener {
            var mAuth= FirebaseAuth.getInstance()
            mAuth.signOut()
            Toast.makeText(this,"Logged Out",Toast.LENGTH_LONG).show()
        }
        save.setOnClickListener {
            val database = Firebase.database
            val myRef = database.getReference("message")
            myRef.setValue(et.text.toString())
            et.text.clear()
        }



        val database = Firebase.database
        val myRef = database.getReference("message")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                tv.setText(value)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainScreen, "Failed to read value", Toast.LENGTH_LONG).show()
            }
        })

        val list=ArrayList<Model>()



        var db= com.google.firebase.Firebase.database.getReference("Contacts")
        db.addChildEventListener(object : com.google.firebase.database.ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var model=snapshot.getValue(Model::class.java)
                list.add(model!!)
                val adapter=MyAdapter(list,this@MainScreen)
                rv.layoutManager= LinearLayoutManager(this@MainScreen)
                rv.adapter=adapter
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}
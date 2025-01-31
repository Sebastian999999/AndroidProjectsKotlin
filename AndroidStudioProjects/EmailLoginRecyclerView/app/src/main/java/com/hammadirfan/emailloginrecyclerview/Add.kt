package com.hammadirfan.emailloginrecyclerview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.hammadirfan.emailloginrecyclerview.Model
import com.hammadirfan.emailloginrecyclerview.R
import java.net.URI

class Add : AppCompatActivity() {
    var ddp:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        var name=findViewById<EditText>(R.id.Name)
        var email=findViewById<EditText>(R.id.Email)
        var num=findViewById<EditText>(R.id.Number)
        var save=findViewById<Button>(R.id.Save)
        var dp=findViewById<ImageView>(R.id.dp)
        save.setOnClickListener {
            var model= Model(name.text.toString(),num.text.toString(),email.text.toString(),ddp!!)
            var db= Firebase.database.getReference("Contacts")
            db.push().setValue(model)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed to Add",Toast.LENGTH_LONG).show()
                }
        }
        //add runtime permission for storage'




        var pickImage=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
            if(result.resultCode== Activity.RESULT_OK && result.data?.data!=null){

                var img: Uri? =result.data?.data
                dp.setImageURI(img)

                var storageRef = FirebaseStorage.getInstance()
                var filename=System.currentTimeMillis().toString()+"dp.jpg"
                var ref=storageRef.getReference(filename)
                ref.putFile(img!!)
                    .addOnSuccessListener {
                        ref.downloadUrl
                            .addOnSuccessListener {
                                ddp=it.toString()
                            }
                    }
            }
        }
        dp.setOnClickListener {
            pickImage.launch(Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI))
        }
    }
}
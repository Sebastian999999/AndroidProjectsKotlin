package com.hammadirfan.webapis

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var get = findViewById<Button>(R.id.Get)
        var url = "http://172.16" +
                ".53.65/smd24a/get.php"

        get.setOnClickListener(){
            var requestQueue= Volley.newRequestQueue(this)

            var stringRequest = StringRequest(
                Request.Method.GET,
                url,
                { response ->
                    var response=JSONObject(response)
                    var student = response.getJSONObject("student")
                    var name = student.getString("name")
                    var age = student.getInt("age")
                    Log.d("name",name)
                    Log.d("age",age.toString())
                },
                {
                    println("That didnt work!") }
            )
            requestQueue.add(stringRequest)
        }
    }
}
package com.example.roompractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roompractice.data.Entities.User
import com.example.roompractice.data.Repositories.UserRepository
import com.example.roompractice.data.viewmodels.UserViewModel

class AddFragment : Fragment() {

    private lateinit var viewModel : UserViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        view.findViewById<Button>(R.id.mbadd).setOnClickListener{
            viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
            val firstName = view.findViewById<android.widget.EditText>(R.id.metaddfirstname).text.toString()
            val lastName = view.findViewById<android.widget.EditText>(R.id.metaddlastname).text.toString()
            val age = view.findViewById<android.widget.EditText>(R.id.metaddage).text.toString().toInt()
            //val user = User(firstName,lastName,age)
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && age != 0){
                viewModel.readAllData.observe(viewLifecycleOwner , Observer { list ->
                    if (list.isNotEmpty()){ //Can also use list.size > 0 but kotlin preferred isNotEmpty()
                        var user = User(list.size - 1 , firstName , lastName , age)
                        viewModel.AddUser(user)
                    } else{
                        var user = User(0, firstName , lastName , age)
                        viewModel.AddUser(user)
                    }
                })




                Toast.makeText(context, "User Added", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Fields cannot be empty and should be valid", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

}
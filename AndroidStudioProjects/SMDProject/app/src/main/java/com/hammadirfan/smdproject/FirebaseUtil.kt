package com.hammadirfan.smdproject

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore

class  FirebaseUtil{
    public fun currentUserId():String{
        return FirebaseAuth.getInstance().uid.toString()
    }
    public fun currentUserDetails():DocumentReference {
        return FirebaseFirestore.getInstance().collection("users").document("user1")
    }
}
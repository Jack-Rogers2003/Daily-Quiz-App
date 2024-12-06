package com.example.coursework_1

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object UserInformation{
    private var mAuth = getInstance()
    private val database = FirebaseDatabase.getInstance("https://courseworkcsc306-default-rtdb.europe-west1.firebasedatabase.app")

    fun createNewUser(userName : String, password : String) {
        mAuth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener {
            System.out.println("Done")
        }
    }

    fun getAuth(): FirebaseAuth {
        return mAuth
    }

    fun logOut() {
        mAuth.signOut()
    }

    fun getCurrentUser() : FirebaseUser? {
        return mAuth.currentUser
    }

    fun getCurrentUserID(): String? {
        return getCurrentUser()?.uid
    }

    fun getDatabase(): FirebaseDatabase {
        return database
    }

    fun getFromDatabase(): Task<DataSnapshot>? {
        return getCurrentUserID()?.let { database.getReference(it).get() }
    }

    fun getWholeDatabase(): DatabaseReference {
        return database.reference
    }

    fun setDatabase(property: String, toSet: String) {
        getCurrentUserID()?.let {
            database.getReference(it).child(property).setValue(toSet).addOnCompleteListener{

            } }
    }
}
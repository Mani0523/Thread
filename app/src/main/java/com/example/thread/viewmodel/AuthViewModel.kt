package com.example.thread.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.thread.model.UserModel
import com.example.thread.utils.SharedPref
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import java.util.UUID

class AuthViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val userRef = db.getReference("users")

    private val storageRef = Firebase.storage.reference
    private val imageRef = storageRef.child("users/${UUID.randomUUID()}.jpg")

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser> = _firebaseUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        _firebaseUser.value = auth.currentUser
    }

    fun login(email: String, password: String, context: Context) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _firebaseUser.postValue(auth.currentUser)
                getData(auth.currentUser!!.uid, context)
            } else {
                _error.postValue(it.exception?.message)
            }
        }
    }

    private fun getData(uid: String, context: Context) {
        userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserModel::class.java)
                if (userData != null) {
                    SharedPref.storeData(
                        userData.name,
                        userData.email,
                        userData.bio,
                        userData.userName,
                        userData.imageUrl,
                        context
                    )
                } else {
                    _error.postValue("User data not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.postValue(error.message)
            }
        })
    }

    fun register(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        imageUri: Uri,
        context: Context
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _firebaseUser.postValue(auth.currentUser)
                saveImage(email, password, name, bio, userName, imageUri, auth.currentUser?.uid, context)
            } else {
                _error.postValue(it.exception?.message ?: "Something went wrong")
            }
        }
    }

    private fun saveImage(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        imageUri: Uri,
        uid: String?,
        context: Context
    ) {
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                saveData(email, password, name, bio, userName, uri.toString(), uid, context)
            }.addOnFailureListener {
                _error.postValue(it.message)
            }
        }.addOnFailureListener {
            _error.postValue(it.message)
        }
    }

    private fun saveData(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        imageUrl: String,
        uid: String?,
        context: Context
    ) {
        val userData = UserModel(email, password, name, bio, userName, imageUrl, uid!!)
        userRef.child(uid).setValue(userData).addOnSuccessListener {
            SharedPref.storeData(name, email, bio, userName, imageUrl, context)
        }.addOnFailureListener {
            _error.postValue(it.message)
        }
    }

    fun logout() {
        auth.signOut()
    //    _firebaseUser.postValue(null)
    }
}

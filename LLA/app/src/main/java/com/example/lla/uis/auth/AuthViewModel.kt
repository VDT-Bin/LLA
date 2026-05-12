package com.example.lla.uis.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun login() {
        // Logic đăng nhập sử dụng 'auth'
    }

    fun signup() {
        // Logic đăng ký sử dụng 'auth'
    }
}

package com.example.lla.uis.auth

import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()


    init {
        viewModelScope.launch {
            auth.addAuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    _authState.value = AuthState.Success(currentUser)
                } else {
                    _authState.value = AuthState.Idle // Đảm bảo trả về Idle rõ ràng nếu chưa đăng nhập
                }
            }
        }
    }
//    fun login(email: String, pass: String) {
//        if (email.isBlank() || pass.isBlank()) {
//            _authState.value = AuthState.Error("Email và mật khẩu không được để trống")
//            return
//        }
//
//        _authState.value = AuthState.Loading
//        auth.signInWithEmailAndPassword(email, pass)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    _authState.value = AuthState.Success(auth.currentUser)
//                } else {
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Đăng nhập thất bại")
//                }
//            }
//    }
fun login(email: String, pass: String) {
    if (email.isBlank() || pass.isBlank()) {
        _authState.value = AuthState.Error("Email và mật khẩu không được để trống")
        return
    }

    _authState.value = AuthState.Loading
    viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, pass).await()
            _authState.value = AuthState.Success(auth.currentUser)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Đăng nhập thất bại")
        }
    }
}
    fun signup(email: String, pass: String, name: String) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            _authState.value = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "uid" to userId,
                            "name" to name,
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Success(auth.currentUser)
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error("Lỗi lưu thông tin: ${e.message}")
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Đăng ký thất bại")
                }
            }
    }

    fun changePassword(newPass: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        user?.updatePassword(newPass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun signInWithGoogle(credentialResponse: GetCredentialResponse) {
        _authState.value = AuthState.Loading
        val credential = credentialResponse.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val idToken = googleIdTokenCredential.idToken

        val firebaseCredential = GoogleAuthProvider.getCredential(idToken,null)

        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkAndCreateUserInFirestore(auth.currentUser)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Đăng nhập thất bại")
                }
            }
    }
    private fun checkAndCreateUserInFirestore(user: FirebaseUser?) {
        user?.let {
            val userRef = firestore.collection("users").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    val userMap = hashMapOf(
                        "uid" to it.uid,
                        "name" to (it.displayName ?: "Người dùng Google"),
                        "email" to it.email,
                        "createdAt" to System.currentTimeMillis()
                    )
                    userRef.set(userMap).addOnSuccessListener {
                        _authState.value = AuthState.Success(user)
                    }.addOnFailureListener { e ->
                        _authState.value = AuthState.Error("Lỗi lưu DB: ${e.message}")
                    }
                } else {
                    _authState.value = AuthState.Success(user)
                }
            }
        }    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

package com.fieldreport.ai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser)
        }
    }

    fun signInWithCredential(credential: AuthCredential) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Error("Sign in succeeded but user is null")
                        }
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Google Sign-In failed")
                    }
                }
        }
    }

    fun signInWithGoogleIdToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInWithCredential(credential)
    }

    fun signInWithEmail(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Error("Sign in succeeded but user is null")
                        }
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Email authentication failed")
                    }
                }
        }
    }

    fun signInAnonymously() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Error("Login succeeded but user is null")
                        }
                    } else {
                        _authState.value = AuthState.Error(task.exception?.message ?: "Authentication failed")
                    }
                }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}

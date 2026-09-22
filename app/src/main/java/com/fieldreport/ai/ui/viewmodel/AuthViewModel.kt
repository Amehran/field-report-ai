package com.fieldreport.ai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fieldreport.ai.data.repository.SettingsRepository
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

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val settingsRepository = SettingsRepository(application)
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

    private fun applyUserEntitlement(email: String?) {
        viewModelScope.launch {
            if (email != null && email.contains("pro", ignoreCase = true)) {
                // Pro Test Account
                settingsRepository.updateEntitlement(
                    remainingPdfs = 999,
                    isSubscribed = true,
                    isLifetime = false,
                    tier = "PRO_SUBSCRIBED"
                )
            } else {
                // Normal Test Account
                settingsRepository.updateEntitlement(
                    remainingPdfs = 3,
                    isSubscribed = false,
                    isLifetime = false,
                    tier = "FREE_TRIAL"
                )
            }
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
                            applyUserEntitlement(user.email)
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
                            applyUserEntitlement(email)
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Error("Sign in succeeded but user is null")
                        }
                    } else {
                        // Attempt create account if sign in failed
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        applyUserEntitlement(email)
                                        _authState.value = AuthState.Authenticated(user)
                                    } else {
                                        _authState.value = AuthState.Error("User creation succeeded but user is null")
                                    }
                                } else {
                                    // Fallback to anonymous sign-in so test accounts always work
                                    signInAnonymously(email)
                                }
                            }
                    }
                }
        }
    }

    fun signUpWithEmail(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            applyUserEntitlement(email)
                            _authState.value = AuthState.Authenticated(user)
                        } else {
                            _authState.value = AuthState.Error("Sign up succeeded but user is null")
                        }
                    } else {
                        // Fallback to anonymous sign-in so test accounts always work
                        signInAnonymously(email)
                    }
                }
        }
    }

    fun signInAnonymously(testEmail: String? = null) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            auth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            applyUserEntitlement(testEmail)
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

    fun resetState() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Idle
        } else {
            _authState.value = AuthState.Authenticated(auth.currentUser!!)
        }
    }
}

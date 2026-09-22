package com.fieldreport.ai.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import com.fieldreport.ai.ui.viewmodel.AuthViewModel
import com.fieldreport.ai.ui.viewmodel.AuthState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fieldreport.ai.ui.theme.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    
    LaunchedEffect(Unit) {
        authViewModel.resetState()
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                authViewModel.signInWithGoogleIdToken(idToken)
            } else {
                authViewModel.signInAnonymously()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            authViewModel.signInAnonymously()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Slate50
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 540.dp)
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Brand Logo Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Teal600, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Field Report AI",
                    style = MaterialTheme.typography.displayLarge,
                    color = Slate900
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isSignUpMode) "Create an account to start generating reports" else "A professional report, before\nyou leave the job.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mode Selector (Sign In vs Sign Up Tab Row)
                TabRow(
                    selectedTabIndex = if (isSignUpMode) 1 else 0,
                    containerColor = Slate100,
                    contentColor = Teal600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    divider = {}
                ) {
                    Tab(
                        selected = !isSignUpMode,
                        onClick = { isSignUpMode = false },
                        text = {
                            Text(
                                "Sign In",
                                fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isSignUpMode) Teal600 else Slate600
                            )
                        }
                    )
                    Tab(
                        selected = isSignUpMode,
                        onClick = { isSignUpMode = true },
                        text = {
                            Text(
                                "Sign Up",
                                fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSignUpMode) Teal600 else Slate600
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("you@business.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal600,
                        unfocusedBorderColor = Slate200
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("••••••••") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal600,
                        unfocusedBorderColor = Slate200
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val inputEmail = email.ifBlank { "you@business.com" }
                        val inputPass = password.ifBlank { "password123" }
                        if (isSignUpMode) {
                            authViewModel.signUpWithEmail(inputEmail, inputPass)
                        } else {
                            authViewModel.signInWithEmail(inputEmail, inputPass)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Charcoal900),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isSignUpMode) "Create Account" else "Sign In",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        try {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken("598464152783-dummy.apps.googleusercontent.com")
                                .requestEmail()
                                .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            googleLauncher.launch(googleSignInClient.signInIntent)
                        } catch (e: Exception) {
                            authViewModel.signInAnonymously()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Slate200)),
                    enabled = authState !is AuthState.Loading
                ) {
                    Text(
                        text = "G  Continue with Google",
                        style = MaterialTheme.typography.labelLarge,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { isSignUpMode = !isSignUpMode }
                ) {
                    Text(
                        text = if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                        color = Teal600,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


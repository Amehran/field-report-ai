package com.fieldreport.ai.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val mockAuth = mockk<FirebaseAuth>(relaxed = true)
    private val mockUser = mockk<FirebaseUser>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init checks current user and sets Authenticated if logged in`() {
        every { mockAuth.currentUser } returns mockUser

        val viewModel = AuthViewModel()

        assertTrue(viewModel.authState.value is AuthState.Authenticated)
        assertEquals(mockUser, (viewModel.authState.value as AuthState.Authenticated).user)
    }

    @Test
    fun `init sets Idle if no user logged in`() {
        every { mockAuth.currentUser } returns null

        val viewModel = AuthViewModel()

        assertEquals(AuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `signOut resets authState to Idle`() {
        every { mockAuth.currentUser } returns mockUser
        val viewModel = AuthViewModel()
        assertTrue(viewModel.authState.value is AuthState.Authenticated)

        viewModel.signOut()

        verify { mockAuth.signOut() }
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `authState data classes and object representations`() {
        val idle = AuthState.Idle
        val loading = AuthState.Loading
        val error = AuthState.Error("Test error")
        val auth = AuthState.Authenticated(mockUser)

        assertEquals("Test error", error.message)
        assertEquals(mockUser, auth.user)
        assertNotNull(idle)
        assertNotNull(loading)
    }
}

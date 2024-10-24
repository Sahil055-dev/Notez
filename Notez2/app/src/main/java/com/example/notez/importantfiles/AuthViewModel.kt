package com.example.notez.importantfiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notez.database.ModuleDao
import com.example.notez.database.ModuleEntity
import com.example.notez.database.SubjectDao
import com.example.notez.database.SubjectEntity
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserPreferences(
    val branch: String? = null,
    val year: Int? = null,
    val semester: Int? = null
) {
    // No-argument constructor (default constructor)
    constructor() : this(null, null, null)
}

class AuthViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val subjectDao: SubjectDao,
    private val moduleDao: ModuleDao// Injecting SubjectDao
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userPreferences = MutableStateFlow<UserPreferences?>(null)
    val userPreferences: StateFlow<UserPreferences?> = _userPreferences

    init {
        checkAuthentication()
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
            auth.currentUser?.let {
                loadUserPreferences(it.uid)
            }
        }
    }

    private fun checkAuthentication() {
        val currentUser = firebaseAuth.currentUser
        _authState.value = if (currentUser != null) AuthState.Authenticated else AuthState.Idle
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    firebaseAuth.currentUser?.let { user -> loadUserPreferences(user.uid) }
                } else {
                    _authState.value = AuthState.Error(getErrorMessage(task.exception))
                }
            }
    }

    fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        branch: String,
        year: Int,
        semester: Int
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Password and Confirm Password must be the same")
            return
        }

        _authState.value = AuthState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    val profileUpdates = userProfileChangeRequest { displayName = name }
                    firebaseAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "branch" to branch,
                                "year" to year,
                                "semester" to semester
                            )
                            userId?.let {
                                FirebaseFirestore.getInstance().collection("users").document(it)
                                    .set(user)
                                    .addOnSuccessListener {
                                        _authState.value = AuthState.Authenticated
                                        updateUserPreferences(userId, UserPreferences(branch, year, semester))
                                    }
                                    .addOnFailureListener { e -> _authState.value = AuthState.Error("Failed to save user data. Please try again.") }
                            }
                        } else {
                            _authState.value = AuthState.Error("Failed to update profile")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(getErrorMessage(task.exception))
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = AuthState.Idle
        _currentUser.value = null
        _userPreferences.value = null
    }

    fun getUserInfo(
        userId: String,
        onUserDataSuccess: ((Map<String, Any>?) -> Unit)? = null,
        onPreferencesSuccess: ((UserPreferences?) -> Unit)? = null,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val document = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                if (document != null && document.exists()) {
                    val data = document.data
                    onUserDataSuccess?.invoke(data)
                    val preferences = getUserPreferences(userId)
                    onPreferencesSuccess?.invoke(preferences)
                } else {
                    onFailure(Exception("User document not found"))
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    private fun loadUserPreferences(userId: String) {
        viewModelScope.launch {
            try {
                val preferences = getUserPreferences(userId)
                _userPreferences.value = preferences
            } catch (e: Exception) {
                _userPreferences.value = null
            }
        }
    }

    private suspend fun getUserPreferences(userId: String): UserPreferences? {
        return suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val preferences = document.toObject(UserPreferences::class.java)
                        continuation.resume(preferences)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { e -> continuation.resume(null) }
        }
    }
    fun updateUserPreferences(userId: String, preferences: UserPreferences) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).update(
            mapOf(
                "branch" to preferences.branch,
                "year" to preferences.year,
                "semester" to preferences.semester
            )
        ).addOnSuccessListener {
            // Handle success: Reload the updated preferences from Firestore
            loadUserPreferences(userId)
        }.addOnFailureListener { e ->
            // Handle failure
        }
    }

    fun fetchSubjects(semesterId: Int, onSuccess: (List<SubjectEntity>) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val subjects = subjectDao.getSubjects(semesterId) // Fetch subjects from Room database by semester
                onSuccess(subjects)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    fun fetchModules(subjectId: Int, onSuccess: (List<ModuleEntity>) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val modules = moduleDao.getModulesForSubject(subjectId) // Fetch modules based on subject ID
                onSuccess(modules)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }




    fun refreshUserPreferences() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            loadUserPreferences(userId)
        }
    }

    private fun getErrorMessage(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                if (exception.message?.contains("The email address is badly formatted") == true) {
                    "The email address is badly formatted. Please enter a valid email."
                } else {
                    "Invalid credentials. Please check your email and password."
                }
            }
            is FirebaseAuthInvalidUserException -> {
                when (exception.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                    "ERROR_USER_DISABLED" -> "Your account has been disabled."
                    else -> "Unknown user error. Please try again."
                }
            }
            is FirebaseAuthWeakPasswordException -> "Weak password. Please choose a stronger password."
            is FirebaseAuthEmailException -> "Email error. Please check your email format."
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "An account with this email already exists."
                    else -> "Authentication failed. Please try again."
                }
            }
            else -> exception?.message ?: "An unknown error occurred. Please try again."
        }
    }
}

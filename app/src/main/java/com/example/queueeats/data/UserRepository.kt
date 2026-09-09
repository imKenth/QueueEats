package com.example.queueeats.data

import com.example.queueeats.models.User

object UserRepository {
    // In-memory list to store registered users
    private val users = mutableListOf<User>()

    // Currently logged in user
    var currentUser: User? = null

    // Initialize with a default user for testing
    init {
        users.add(User("Kenth Daryl", "kenthdaryl@gmail.com", "12345678"))
    }

    fun addUser(user: User) {
        users.add(user)
    }

    fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    fun validateUser(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
    }
}

package com.example.drivocare.data

data class User(
    val id: String = "",
    val username: String = "",
    val email: String = ""
) {
    constructor() : this("", "", "")
}

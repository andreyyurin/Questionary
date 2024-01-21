package ru.sad.domain.model.users

data class User(
    val token: String = "",
    val photoUrl: String,
    val id: Int,
    val username: String,
    var subscribed: Boolean? = null
)
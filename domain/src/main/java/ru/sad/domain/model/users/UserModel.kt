package ru.sad.domain.model.users

import java.io.Serializable

data class UserModel(val nickname: String, val photoUrl: String, val id: String, val email: String) : Serializable

package ru.sad.data.repository.users

import ru.sad.domain.model.users.UserModel

interface UsersRepositoryImpl {
    suspend fun getUsers(sample: String): List<UserModel>

    suspend fun putUser(user: UserModel)
}
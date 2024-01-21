package ru.sad.data.repository.subscriptions

import ru.sad.data.repository.user.UserRepositoryImpl
import ru.sad.domain.model.subscriptions.SubscribeResponse
import ru.sad.domain.model.users.User

interface SubscriptionsRepositoryImpl {
    suspend fun subscribeToUser(userId: Int): SubscribeResponse

    suspend fun unsubscribeFromUser(userId: Int): SubscribeResponse

    suspend fun getAllSubscriptions(): List<User>

    suspend fun getAllFollowing(): List<User>

    suspend fun searchUsers(query: String): List<User>
}
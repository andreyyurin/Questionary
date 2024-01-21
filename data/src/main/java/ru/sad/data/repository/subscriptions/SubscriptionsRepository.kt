package ru.sad.data.repository.subscriptions

import android.app.Application
import android.util.Log
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.extensions.withContext
import ru.sad.data.extensions.withContextSingle
import ru.sad.domain.model.subscriptions.SubscribeRequest
import ru.sad.domain.model.subscriptions.SubscribeResponse
import ru.sad.domain.model.users.User

class SubscriptionsRepository(
    private val application: Application,
    private val api: QuestionaryApi
) : SubscriptionsRepositoryImpl {

    override suspend fun unsubscribeFromUser(userId: Int): SubscribeResponse = withContextSingle {
        api.unsubscribe(SubscribeRequest(userId))
    }

    override suspend fun subscribeToUser(userId: Int): SubscribeResponse = withContextSingle {
        api.subscribe(SubscribeRequest(userId))
    }

    override suspend fun getAllSubscriptions(): List<User> = withContext {
        api.subscriptions()
    }

    override suspend fun getAllFollowing(): List<User> = withContext {
        api.following()
    }

    override suspend fun searchUsers(query: String): List<User> = withContext {
        api.search(query)
    }
}
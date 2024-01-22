package ru.sad.base.di

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sad.data.api.QuestionaryApi
import ru.sad.data.repository.channels.ChannelsRepository
import ru.sad.data.repository.channels.ChannelsRepositoryImpl
import ru.sad.data.repository.push.PushRepository
import ru.sad.data.repository.push.PushRepositoryImpl
import ru.sad.data.repository.quiz.QuizRepository
import ru.sad.data.repository.quiz.QuizRepositoryImpl
import ru.sad.data.repository.simple.SimpleRepository
import ru.sad.data.repository.simple.SimpleRepositoryImpl
import ru.sad.data.repository.stories.StoriesRepository
import ru.sad.data.repository.stories.StoriesRepositoryImpl
import ru.sad.data.repository.subscriptions.SubscriptionsRepository
import ru.sad.data.repository.subscriptions.SubscriptionsRepositoryImpl
import ru.sad.data.repository.user.UserRepository
import ru.sad.data.repository.user.UserRepositoryImpl
import ru.sad.data.repository.users.UsersRepository
import ru.sad.data.repository.users.UsersRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        application: Application,
        firebaseAuth: FirebaseAuth,
        usersRepository: UsersRepositoryImpl,
        api: QuestionaryApi
    ): UserRepositoryImpl = UserRepository(application, firebaseAuth, usersRepository, api)

    @Singleton
    @Provides
    fun providePushRepository(
        application: Application,
        api: QuestionaryApi
    ): PushRepositoryImpl = PushRepository(application, api)

    @Singleton
    @Provides
    fun provideUsersRepository(
        application: Application,
        firebaseAuth: FirebaseAuth,
        database: FirebaseFirestore,
    ): UsersRepositoryImpl = UsersRepository(application, firebaseAuth, database)

    @Singleton
    @Provides
    fun provideStoriesRepository(
        application: Application,
        api: QuestionaryApi
    ): StoriesRepositoryImpl = StoriesRepository(application, api)

    @Singleton
    @Provides
    fun provideSubscriptionsRepository(
        application: Application,
        api: QuestionaryApi
    ): SubscriptionsRepositoryImpl = SubscriptionsRepository(application, api)

    @Singleton
    @Provides
    fun provideQuizRepository(
        application: Application,
        api: QuestionaryApi
    ): QuizRepositoryImpl = QuizRepository(application, api)

    @Singleton
    @Provides
    fun provideChannelsRepository(
        application: Application,
        api: QuestionaryApi
    ): ChannelsRepositoryImpl = ChannelsRepository(application, api)

    @Singleton
    @Provides
    fun provideSimpleRepository(
        application: Application
    ): SimpleRepositoryImpl = SimpleRepository(application)
}
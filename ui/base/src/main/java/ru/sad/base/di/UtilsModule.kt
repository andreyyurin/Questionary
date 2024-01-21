package ru.sad.base.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.sad.utils.imageseg.SegImage
import ru.sad.utils.imageseg.SegImageImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Singleton
    @Provides
    fun provideSegImage(): SegImage = SegImageImpl()
}
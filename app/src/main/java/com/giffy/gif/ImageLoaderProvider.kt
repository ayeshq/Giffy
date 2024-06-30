package com.giffy.gif

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderProvider {

    @Provides
    @Singleton
    fun provideImageLoader(
        imageLoader: ImageLoaderImpl
    ): ImageLoader = imageLoader
}

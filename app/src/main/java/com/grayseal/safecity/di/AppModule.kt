package com.grayseal.safecity.di

import com.grayseal.safecity.network.SafeCityAPI
import com.grayseal.safecity.repository.SafeCityRepository
import com.grayseal.safecity.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSafeCityRepository(api: SafeCityAPI) = SafeCityRepository(api)

    @Singleton
    @Provides
    fun provideSafeCityApi(): SafeCityAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SafeCityAPI::class.java)
    }
}
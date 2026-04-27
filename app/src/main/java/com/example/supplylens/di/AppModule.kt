package com.supplylens.app.di

import com.supplylens.app.data.remote.ApiService
import com.supplylens.app.data.remote.RetrofitClient
import com.supplylens.app.data.repository.SupplyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideRetrofitClient(): RetrofitClient = RetrofitClient()

    @Provides @Singleton
    fun provideApiService(client: RetrofitClient): ApiService = client.apiService

    @Provides @Singleton
    fun provideRepository(api: ApiService): SupplyRepository = SupplyRepository(api)
}
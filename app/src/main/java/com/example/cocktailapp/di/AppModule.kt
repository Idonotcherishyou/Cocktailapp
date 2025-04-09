package com.example.cocktailapp.di

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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCocktailApi(retrofit: Retrofit): CocktailApiService =
        retrofit.create(CocktailApiService::class.java)

    @Provides
    @Singleton
    fun provideCocktailRepository(api: CocktailApiService): CocktailRepository =
        CocktailRepository(api)
}
package com.depi.myapplicatio.domain.di


import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.depi.myapplicatio.util.constants.Constants.INTRODUCTION_SP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpModule {

    @Provides
    @Singleton
    fun provideIntroductionSharedPreferences(
        application: Application
    ): SharedPreferences = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)
}
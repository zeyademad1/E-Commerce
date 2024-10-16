package com.depi.myapplicatio.di

import android.util.Log
import com.depi.myapplicatio.data.User
import com.depi.myapplicatio.firebase.FirebaseCommon
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabse()= Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firestore : FirebaseFirestore,
        auth : FirebaseAuth
    ) = FirebaseCommon(firestore,auth)
}
package com.agsw.test_ble.di

import android.content.Context
import com.agsw.test_ble.App
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  fun instance(@ApplicationContext app: Context) = app as App
}
package com.agsw.test_ble.di

import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BTModule {

  @Provides
  fun btAdapter(@ApplicationContext context: Context) =
    context.getSystemService(BluetoothManager::class.java).adapter
}
package com.agsw.test_ble.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BLEViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val blAdapter: BluetoothAdapter,
) : ViewModel() {

//  init {
//    try {
//      scanBLEDevice()
//    } catch (_: Exception) {}
//  }

  private val _devices = MutableStateFlow<MutableList<BluetoothDevice>>(mutableListOf())
  val devices: Flow<List<BluetoothDevice>> get() = _devices

  private val _permGranted = MutableStateFlow(checkPermission())
  val permGranted: Flow<Boolean> get() = _permGranted
  fun updatePermGranted(granted: Boolean) = _permGranted.update { granted }

  private val _btEnabled = MutableStateFlow(blAdapter.isEnabled)
  val btEnabled: Flow<Boolean> get() = _btEnabled
  fun setBtEnabled(enabled: Boolean) = _btEnabled.update { enabled }

  private val _isScanning = MutableStateFlow(false)
  val isScanning: Flow<Boolean> get() = _isScanning

  private val scanCallback by lazy {
    object : ScanCallback() {
      override fun onScanResult(callbackType: Int, result: ScanResult?) {
        result?.let { device ->
          _devices.update { it.apply { add(device.device) } }
        }
      }

      override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun scanBLEDevice() {
    if (!_isScanning.value) {
      viewModelScope.launch(Dispatchers.IO) {
        _isScanning.update { true }

        blAdapter.bluetoothLeScanner.startScan(scanCallback)

        delay(10_000)
        blAdapter.bluetoothLeScanner.stopScan(scanCallback)
        _isScanning.update { false }
      }
    }
  }

  private fun checkPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
  } else {
    context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
  }
}
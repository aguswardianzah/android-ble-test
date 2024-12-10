package com.agsw.test_ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.agsw.test_ble.ui.composables.screens.HomeScreen
import com.agsw.test_ble.ui.theme.Test_bleTheme
import com.agsw.test_ble.viewmodels.BLEViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val viewModel: BLEViewModel by viewModels()
  private val permissionLauncher by lazy {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
      viewModel.updatePermGranted(it.values.all { granted -> granted })
    }
  }
  private val btEnablerLauncher by lazy {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      viewModel.setBtEnabled(it.resultCode == RESULT_OK)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleFlows()
    setView()
  }

  private fun handleFlows() {
    lifecycleScope.launch {
      viewModel.permGranted.collectLatest {
        if (!it) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
              arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
              )
            )
          } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH))
          }
        } else {
          viewModel.btEnabled.collectLatest { enabled ->
            if (!enabled) {
              btEnablerLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
              viewModel.scanBLEDevice()
            }
          }
        }
      }
    }
  }

  private fun setView() {
    enableEdgeToEdge()
    setContent {
      Test_bleTheme {
        HomeScreen { viewModel.scanBLEDevice() }
      }
    }
  }
}
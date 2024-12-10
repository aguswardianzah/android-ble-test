package com.agsw.test_ble.ui.composables.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agsw.test_ble.ui.composables.components.BLEItem
import com.agsw.test_ble.viewmodels.BLEViewModel

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  vm: BLEViewModel = hiltViewModel(),
  onClickScan: () -> Unit
) {
  val devices = vm.devices.collectAsState(initial = emptyList())
  val isScanning = vm.isScanning.collectAsState(initial = false)

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("Bluetooth List") }
      )
    }
  ) { padding ->
    Surface(
      modifier = Modifier.padding(padding).padding(16.dp)
    ) {
      Card (
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextScan(isScanning.value)
          ButtonScan(isScanning.value, onClickScan)
        }
      }
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(items = devices.value, key = { it.name }) { device ->
          BLEItem(device)
        }
      }
    }
  }
}

@Composable
fun TextScan(isScanning: Boolean) {
  Text(if (isScanning) "Scanning" else "Scan Result")
}

@Composable
fun ButtonScan(isScanning: Boolean, onClickScan: () -> Unit) {
  if (isScanning) {
    CircularProgressIndicator(
      modifier = Modifier.size(42.dp),
      strokeWidth = 4.dp
    )
  } else {
    Button(
      modifier = Modifier.height(42.dp),
      onClick = onClickScan
    ) {
      Text("Scan")
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewCard() {
  val isScanning = true
  Card(
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextScan(isScanning)
      ButtonScan(isScanning) {}
    }
  }
}
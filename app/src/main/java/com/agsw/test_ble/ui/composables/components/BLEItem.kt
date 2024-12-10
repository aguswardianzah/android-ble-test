package com.agsw.test_ble.ui.composables.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("MissingPermission")
@Composable
fun BLEItem(item: BluetoothDevice) {
  Card (
    modifier = Modifier.padding(8.dp),
    shape = RoundedCornerShape(8.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth().padding(all = 10.dp)
    ) {
      Text(item.uuids.joinToString(separator = ";"))
      Text(item.address)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      item.alias?.let { Text(it) }
    }
    Text(item.name)
  }
}

@Preview(showBackground = true)
@Composable
fun ItemPreview() {
//  BLEItem(BluetoothDevice.)
}
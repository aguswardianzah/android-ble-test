# Scan Process
- from viewmodel, I check the bluetooth requirements such as permission and status

  ```
  private val _permGranted = MutableStateFlow(checkPermission())
  val permGranted: Flow<Boolean> get() = _permGranted
  fun updatePermGranted(granted: Boolean) = _permGranted.update { granted }

  private val _btEnabled = MutableStateFlow(blAdapter.isEnabled)
  val btEnabled: Flow<Boolean> get() = _btEnabled
  fun setBtEnabled(enabled: Boolean) = _btEnabled.update { enabled }

  private fun checkPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
  } else {
    context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
  }
  ```
  
- make a permission request and/or status turning off dialog depend on device status

  ```
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
  ```
  
- start scan for available devices when all requirements met

  ```
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
  ```

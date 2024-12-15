/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.turtlepaw.wifihelper.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.turtlepaw.wifihelper.R
import com.turtlepaw.wifihelper.presentation.theme.WiFiHelperTheme

/** Check if a WiFi network might have a captive portal */
fun isPotentialCaptivePortal(scanResult: ScanResult): Boolean {
    // Open networks (no password) often have captive portals
    return scanResult.capabilities.contains("[ESS]") && !scanResult.capabilities.contains("WPA")
}

class MainActivity : ComponentActivity() {
    private lateinit var wifiManager: WifiManager
    private var wifiScanResults by mutableStateOf<List<ScanResult>>(emptyList())
    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val results = if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else wifiManager.scanResults
            wifiScanResults = results
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        // Initialize WiFi Manager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Register BroadcastReceiver for WiFi scan results
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        //wifiManager.startScan()

        setContent {
            WiFiHelperTheme {
                WearApp(this, wifiScanResults)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalHorologistApi::class)
@Composable
fun WearApp(context: Context, results: List<ScanResult>) {
    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            if(permissions.allPermissionsGranted){
                WifiSearch(context, results, navController)
            } else {
                Page {
                    item {
                        Icon(
                            imageVector = Icons.Rounded.Error,
                            contentDescription = "Error"
                        )
                    }
                    item {
                        Text(
                            "Location permissions are required to scan for networks",
                            textAlign = TextAlign.Center
                        )
                    }
                    item {
                        Chip(
                            onClick = {
                                permissions.launchMultiplePermissionRequest()
                            },
                            label = {
                                Text("Grant Permissions")
                            },
                            colors = ChipDefaults.secondaryChipColors()
                        )
                    }
                }
            }
        }
        composable("redirect") {
            RedirectionSelector(context, navController)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WiFiHelperTheme {
        WearApp(
            LocalContext.current, listOf(
            ScanResult(),
        ))
    }
}
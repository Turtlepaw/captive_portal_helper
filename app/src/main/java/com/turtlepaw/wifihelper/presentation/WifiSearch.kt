package com.turtlepaw.wifihelper.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiFind
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.colorspace.connect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl
import com.turtlepaw.wifihelper.presentation.theme.WiFiHelperTheme

private fun Context.showToast(s: String) {
    Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
}

fun connectToNetwork(context: Context, ssid: String): Pair<Int, WifiNetworkSuggestion?> {
    var lastSuggestedNetwork: WifiNetworkSuggestion? = null
    val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager

    val wifiNetworkSuggestion = WifiNetworkSuggestion.Builder()
        .setSsid(ssid)
        .build()
    val intentFilter =
        IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                return
            }
            context.showToast("Connection Suggestion Succeeded")
        }
    }


    context.registerReceiver(broadcastReceiver, intentFilter)

    lastSuggestedNetwork?.let {
        val status = wifiManager!!.removeNetworkSuggestions(listOf(it))
        Log.i("WifiNetworkSuggestion", "Removing Network suggestions status is $status")
    }
    val suggestionsList = listOf(wifiNetworkSuggestion)

    var status = wifiManager!!.addNetworkSuggestions(suggestionsList)
    Log.i("WifiNetworkSuggestion", "Adding Network suggestions status is $status")
    if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE) {
        context.showToast("Suggestion Update Needed")
        status = wifiManager!!.removeNetworkSuggestions(suggestionsList)
        Log.i("WifiNetworkSuggestion", "Removing Network suggestions status is $status")
        status = wifiManager!!.addNetworkSuggestions(suggestionsList)
    }
    if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
        lastSuggestedNetwork = wifiNetworkSuggestion
        context.showToast("Suggestion Added")
    }

    return status to lastSuggestedNetwork;
}

fun isPasswordProtected(scanResult: ScanResult): Boolean {
    val capabilities = scanResult.capabilities
    return capabilities.contains("WPA") || capabilities.contains("WEP") || capabilities.contains("PSK")
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WifiSearch(context: Context, results: List<ScanResult>, navController: NavController) {
    var captivePortalBehavior by remember { mutableIntStateOf(3) }
    LaunchedEffect(Unit) {
        captivePortalBehavior =
            Settings.Global.getInt(context.contentResolver, "captive_portal_mode")
    }
    Page {
        item {}
        item {
            Chip(
                label = "Connect to a network",
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
                },
                colors = ChipDefaults.secondaryChipColors(),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.WifiFind,
                        contentDescription = "Wifi Find"
                    )
                },
            )
        }
        item {
            Chip(
                label = "Select a redirect server",
                onClick = {
                    navController.navigate("redirect")
                },
                colors = ChipDefaults.secondaryChipColors(),
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Router,
                        contentDescription = "Router"
                    )
                },
                secondaryLabel = "and login"
            )
        }

        item {
            Text(
                "Captive Portal Behavior",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        items(3) {
            ToggleChip(
                toggleControl = ToggleChipToggleControl.Radio,
                onCheckedChanged = { isChecked ->
                    captivePortalBehavior = it
                    try {
                        Settings.Global.putInt(context.contentResolver, "captive_portal_mode", it)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Failed to set captive portal behavior",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                label = when (it) {
                    0 -> "No detection"
                    1 -> "Prompt login"
                    2 -> "Disconnect"
                    else -> "Unknown"
                },
                checked = captivePortalBehavior == it,
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun SearchPreview() {
    WiFiHelperTheme {
        WifiSearch(
            LocalContext.current, listOf(
                ScanResult(),
            ), NavHostController(LocalContext.current)
        )
    }
}
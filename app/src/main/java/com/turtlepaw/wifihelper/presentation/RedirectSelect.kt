package com.turtlepaw.wifihelper.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.material.ToggleChip
import com.google.android.horologist.compose.material.ToggleChipToggleControl

val redirections = listOf<Pair<String, String>>(
    "Google DNS" to "http://8.8.8.8",
    "Cloudflare DNS" to "http://1.1.1.1",
    "NeverSSL" to "http://neverssl.com"
)

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun RedirectionSelector(context: Context, navController: NavController){
    Page {
        item {
            Text(
                "DNS Server",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }
        items(redirections){
            ToggleChip(
                toggleControl = ToggleChipToggleControl.Radio,
                onCheckedChanged = { isChecked ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.second))
                    context.startActivity(intent)
                    navController.popBackStack()
                },
                label = it.first,
                checked = false,
            )
        }
    }
}
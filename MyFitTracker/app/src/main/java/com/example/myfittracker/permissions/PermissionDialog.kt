package com.example.myfittracker.permissions




import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismissClick: () -> Unit,
    onOkClick: () -> Unit,
    onGoToSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
){
   AlertDialog(onDismissRequest = onDismissClick,
               confirmButton = {
                   Column(modifier = Modifier.fillMaxWidth()) {
                       Divider()
                       Text(text = if (isPermanentlyDeclined){
                                        "Grant permission"
                                    }else{
                                        "OK"
                                    },
                           fontWeight = FontWeight.Bold,
                           textAlign = TextAlign.Center,
                           modifier = Modifier
                               .fillMaxWidth()
                               .clickable {
                                   if (isPermanentlyDeclined) {
                                       onGoToSettingsClick()
                                   } else {
                                       onOkClick()

                                   }
                               }
                               .padding(16.dp)
                       )

                   }
               },
               title = {
                   Text(text = "Permission Required")
               },

               text = {
                   Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined = isPermanentlyDeclined))
               },
               modifier = modifier

   )

}

interface PermissionTextProvider{
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class BluetoothPermissionTextProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined){
            "Bluetooth permission was permanently declined"
        }else{
            "Bluetooth is currently disabled. This app needs bluetooth permission to work."
        }
    }
}
class BluetoothAdminPermissionTextProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Bluetooth admin permission was permanently declined"
        } else {
            "Bluetooth admin is currently disabled. This app needs bluetooth admin permission to work."
        }
    }
}

class LocationPermissionTextProvider: PermissionTextProvider{
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Location permission was permanently declined"
        } else {
            "Location is currently disabled. This app needs location permission to work."
        }
    }
}




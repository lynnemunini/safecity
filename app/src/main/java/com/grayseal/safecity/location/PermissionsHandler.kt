package com.grayseal.safecity.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.grayseal.safecity.ui.theme.Orange


@ExperimentalPermissionsApi
@Composable
fun HandleRequest(
    permissionState: PermissionState,
    deniedContent: @Composable (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    when (permissionState.status) {
        is PermissionStatus.Granted -> {
            content()
        }
        is PermissionStatus.Denied -> {
            deniedContent(permissionState.status.shouldShowRationale)
        }
    }
}

@Composable
fun Content(showButton: Boolean = true, onClick: () -> Unit) {
    if (showButton) {
        val enableLocation = remember { mutableStateOf(true) }
        if (enableLocation.value) {
            CustomDialog(
                title = "Turn On Location Service",
                desc = "We understand that privacy is important and we only request access to " +
                        "your location for the purpose of providing you with accurate and relevant " +
                        "weather information.\n\n" +
                        "Unfortunately, without this permission you will not be able to utilize " +
                        "the app's functionalities.",
                enableLocation,
                onClick
            )
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun PermissionDeniedContent(
    rationaleMessage: String,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {

    if (shouldShowRationale) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Location Permission Request",
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    rationaleMessage,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    letterSpacing = 0.5.sp,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    color = Color.Black, textAlign = TextAlign.Justify
                )
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .background(
                            color = Orange,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                ) {
                    Text("Give Permission", color = Color.White)
                }
            },
        )

    } else {
        Content(onClick = onRequestPermission)
    }
}
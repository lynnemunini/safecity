package com.grayseal.safecity.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.poppinsFamily

@Composable
fun CustomDialog(
    title: String? = "Message",
    desc: String? = "Your Message",
    enableLocation: MutableState<Boolean>,
    onClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {}
    ) {
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(25.dp, 25.dp, 25.dp, 25.dp)
                )
                .verticalScroll(rememberScrollState())

        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // title
                Text(
                    text = title!!,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        //  .padding(top = 5.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontFamily = poppinsFamily,
                    fontSize = 20.sp,
                    color = Color.DarkGray,
                )
                Spacer(modifier = Modifier.height(8.dp))
                // description
                Text(
                    text = desc!!,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                )
                // Spacer
                Spacer(modifier = Modifier.height(24.dp))

                // OK button
                val cornerRadius = 16.dp
                val roundedCornerShape = RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp)

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp),
                    onClick = onClick,
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(cornerRadius),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Green,
                                shape = roundedCornerShape
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OK",
                            fontFamily = poppinsFamily,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = {
                    enableLocation.value = false
                    android.os.Process.killProcess(android.os.Process.myPid())
                }) {
                    Text(
                        "Cancel",
                        fontFamily = poppinsFamily,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
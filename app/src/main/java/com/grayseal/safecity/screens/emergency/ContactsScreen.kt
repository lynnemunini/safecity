package com.grayseal.safecity.screens.emergency

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.grayseal.safecity.R
import com.grayseal.safecity.model.Contact
import com.grayseal.safecity.data.contacts
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.poppinsFamily

@Composable
fun ContactsScreen(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = {
                        navController.popBackStack()
                    })
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Emergency Contacts", fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Discover a carefully selected list of emergency contacts in Kenya " +
                            "that you can rely on to report incidents or emergencies promptly",
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = contacts) { item: Contact ->
                    var pressed by remember {
                        mutableStateOf(false)
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { pressed = !pressed }
                                )
                            },
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .height(IntrinsicSize.Min)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.emergency),
                                    contentDescription = "Emergency Contact",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .padding(2.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        item.name.capitalize(),
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = poppinsFamily
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Phone number: " +
                                                item.contact.substring(4),
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = poppinsFamily
                                    )
                                }
                                if (pressed) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp)
                                    ) {
                                        Text(
                                            item.description,
                                            fontFamily = poppinsFamily,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = {
                                                if (ContextCompat.checkSelfPermission(
                                                        context,
                                                        android.Manifest.permission.CALL_PHONE
                                                    ) != PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    ActivityCompat.requestPermissions(
                                                        context as Activity,
                                                        arrayOf(android.Manifest.permission.CALL_PHONE),
                                                        101
                                                    )
                                                } else {
                                                    val intent =
                                                        Intent(
                                                            Intent.ACTION_CALL,
                                                            Uri.parse(item.contact)
                                                        )
                                                    ContextCompat.startActivity(
                                                        context,
                                                        intent,
                                                        null
                                                    )
                                                }
                                            }),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_call),
                                            contentDescription = "Call",
                                            tint = Green,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                        Text("Call now", fontFamily = poppinsFamily)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
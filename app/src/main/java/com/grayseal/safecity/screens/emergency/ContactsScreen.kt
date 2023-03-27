package com.grayseal.safecity.screens.emergency

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.safecity.R
import com.grayseal.safecity.data.Contact
import com.grayseal.safecity.data.contacts
import com.grayseal.safecity.ui.theme.Green
import com.grayseal.safecity.ui.theme.poppinsFamily

@Composable
fun ContactsScreen(navController: NavController) {
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
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    item.name.capitalize(),
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            if(pressed) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                                    Text(
                                        item.description,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        item.contact.substring(4),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_call),
                                            contentDescription = "Call",
                                            tint = Green,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                        Text("Call now")
                                    }
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
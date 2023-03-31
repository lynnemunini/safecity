package com.grayseal.safecity.screens.prevention

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grayseal.safecity.R
import com.grayseal.safecity.ui.theme.poppinsFamily

@Composable
fun PreventionsScreen(navController: NavController) {
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
                    text = "Prevention Tips", fontFamily = poppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "These tips can help you reduce the risk of becoming " +
                            "a victim and stay safe.",
                    fontFamily = poppinsFamily,
                    fontSize = 13.sp
                )
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Prevention(
                    image = R.drawable.prevtrust,
                    title = "Trust your instincts",
                    description = "If something doesn't feel right, it probably isn't. " +
                            "Trust your instincts and avoid situations or " +
                            "people that make you feel uncomfortable. \n\n" +
                            "If you feel threatened or in danger, don't hesitate to call " +
                            "for help or seek assistance from someone you trust."
                )
                Prevention(
                    image = R.drawable.prevhome,
                    title = "Secure your home.",
                    description = "Make sure your home is secured by installing deadbolts, " +
                            "securing windows and doors, and installing a security system " +
                            "if possible. \n\n" +
                            "This will deter criminals from attempting to break into your home " +
                            "and make it more difficult for them to do so."
                )
                Prevention(
                    image = R.drawable.prevrun,
                    title = "Be aware of your surroundings.",
                    description = "One of the most important things you can do to prevent crime is " +
                            "to be aware of your surroundings at all times. \n\n" +
                            "Avoid using your phone or other distractions that may prevent you from " +
                            "noticing any suspicious activity or people around you."
                )
                Prevention(
                    image = R.drawable.prevphone,
                    title = "Keep valuable items out of sight.",
                    description = "Thieves are often on the lookout for easy targets, such as " +
                            "people who leave their valuables in plain sight. To avoid becoming " +
                            "a target, keep your valuable items out of sight in your car or at home. \n\n" +
                            "Don't leave your purse, phone, or other valuables in plain sight in " +
                            "your car, and make sure your windows are closed and doors are " +
                            "locked when you leave your vehicle. At home, avoid leaving " +
                            "valuable items near windows or in plain sight from outside."
                )
            }
        }
    }
}

@Composable
fun Prevention(image: Int, title: String, description: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Prevention Tip",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = poppinsFamily
                    )
                    Text(
                        description,
                        fontFamily = poppinsFamily,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
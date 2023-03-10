package com.grayseal.safecity.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grayseal.safecity.R
import com.grayseal.safecity.ui.theme.Orange
import com.grayseal.safecity.ui.theme.poppinsFamily

enum class MultiFloatingState {
    Expanded,
    Collapsed
}

class MiniFabItem(
    val icon: ImageBitmap,
    val label: String,
    val identifier: String
)

@Composable
fun MultiFloatingActionButton(
    multiFloatingState: MultiFloatingState,
    onMultiFabStateChange: (MultiFloatingState) -> Unit,
    items: List<MiniFabItem>
) {
    val transition = updateTransition(targetState = multiFloatingState, label = "transition")
    val rotate by transition.animateFloat(label = "rotate") {
        if (it == MultiFloatingState.Expanded) 315f else 0f
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        if (transition.currentState == MultiFloatingState.Expanded) {
            items.forEach {
                MiniFab(
                    item = it,
                    onMiniFabItemClick = {},
                )
                Spacer(modifier = Modifier.size(20.dp))
            }
        }
        FloatingActionButton(
            modifier = Modifier.size(60.dp),
            onClick = {
                onMultiFabStateChange(
                    if (transition.currentState == MultiFloatingState.Expanded) {
                        MultiFloatingState.Collapsed
                    } else {
                        MultiFloatingState.Expanded
                    }
                )
            },
            shape = CircleShape,
            containerColor = Orange,
            interactionSource = MutableInteractionSource()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                modifier = Modifier.rotate(rotate)
            )
        }
    }
}

@Composable
fun MiniFab(
    item: MiniFabItem,
    showLabel: Boolean = true,
    onMiniFabItemClick: (MiniFabItem) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showLabel) {
            Text(
                text = item.label,
                fontSize = 14.sp,
                fontFamily = poppinsFamily,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                // modifier = Modifier.background(color = Color.White, shape = RoundedCornerShape(3.dp)).padding(horizontal = 5.dp)
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        Canvas(
            modifier = Modifier
                .size(60.dp)
                .clickable(
                    onClick = {
                        onMiniFabItemClick.invoke(item)
                    },
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(bounded = false, radius = 20.dp, color = Orange)
                ), onDraw = {
                drawCircle(
                    color = Orange,
                    radius = 60f

                )
                drawImage(
                    image = item.icon,
                    topLeft = Offset(
                        center.x - (item.icon.width / 2),
                        center.y - (item.icon.height / 2)
                    )
                )
            })
    }
}

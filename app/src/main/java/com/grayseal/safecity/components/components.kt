package com.grayseal.safecity.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
    val fabScale by transition.animateFloat(label = "FabScale") {
        if (it == MultiFloatingState.Expanded) 36f else 0f
    }
    val alpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = { tween(durationMillis = 50) }) {
        if (it == MultiFloatingState.Expanded) 1f else 0f
    }
    val textShadow by transition.animateDp(
        label = "alpha",
        transitionSpec = { tween(durationMillis = 50) }) {
        if (it == MultiFloatingState.Expanded) 2.0.dp else 0.0.dp
    }
    Column(
        horizontalAlignment = Alignment.End
    ) {
        if (transition.currentState == MultiFloatingState.Expanded) {
            items.forEach {
                MiniFab(
                    item = it,
                    onMiniFabItemClick = {},
                    alpha = alpha,
                    textShadow = textShadow,
                    fabScale = fabScale
                )
                Spacer(modifier = Modifier.size(35.dp))
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
    alpha: Float,
    textShadow: Dp,
    fabScale: Float,
    showLabel: Boolean = true,
    onMiniFabItemClick: (MiniFabItem) -> Unit
) {
    Row {
        if (showLabel) {
            Text(
                text = item.label, fontSize = 15.sp, fontFamily = poppinsFamily, color = Orange, fontWeight = FontWeight.SemiBold, modifier = Modifier
                    .alpha(
                        animateFloatAsState(targetValue = alpha, animationSpec = tween(50)).value
                    )
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Canvas(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    onClick = {
                        onMiniFabItemClick.invoke(item)
                    },
                    interactionSource = MutableInteractionSource(),
                    indication = rememberRipple(bounded = false, radius = 20.dp, color = Orange)
                ), onDraw = {
                drawCircle(
                    color = Orange,
                    radius = 70f

                )
                drawImage(
                    image = item.icon,
                    topLeft = Offset(
                        center.x - (item.icon.width / 2),
                        center.y - (item.icon.height / 2)
                    ),
                    alpha = alpha
                )
            })
    }
}

package com.grayseal.safecity.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.grayseal.safecity.ui.theme.Orange

enum class MultiFloatingState {
    Expanded,
    Collapsed
}

@Composable
fun MultiFloatingActionButton(
    multiFloatingState: MultiFloatingState,
    onMultiFabStateChange: (MultiFloatingState) -> Unit
) {
    val transition = updateTransition(targetState = multiFloatingState, label = "transition")
    val rotate by transition.animateFloat(label = "rotate") {
        if (it == MultiFloatingState.Expanded) 315f else 0f
    }
    FloatingActionButton(
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
            Icons.Rounded.Add,
            contentDescription = "Add",
            modifier = Modifier.rotate(rotate)
        )
    }
}

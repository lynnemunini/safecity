package com.grayseal.safecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.grayseal.safecity.navigation.SafeCityNavigation
import com.grayseal.safecity.ui.theme.SafeCityTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                SafeCity()
            }
        }
    }
}

@Composable
fun SafeCity() {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SafeCityNavigation()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SafeCityTheme {
        SafeCity()
    }
}
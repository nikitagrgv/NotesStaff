package com.nikitagrgv.notesstaff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nikitagrgv.notesstaff.ui.theme.NotesStaffTheme
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesStaffTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
                        name = "Android",
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun Content(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        MusicStaffCanvas()
    }
}

@Composable
fun MusicStaffCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val lineSpacing = 40f
        val startX = 50f
        val endX = size.width - 50f
        val topStaffY = 100f

        for (i in 0..4) {
            val y = topStaffY + (i * lineSpacing)
            drawLine(
                color = Color.Black,
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 3f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    NotesStaffTheme {
        Content("Android")
    }
}
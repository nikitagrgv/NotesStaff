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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
    val renderer = remember { StaffRenderer() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val lineSpacing = 40f
        val horizontalOffset = 50f
        val bottomY = 100f

        with(renderer)
        {
            drawStaffLines()
        }

        val notePositions = listOf(0, 11, -4, 5, 6, -1, 10)

        notePositions.forEachIndexed { index, pos ->
            with(renderer) {
                drawNote(index, pos)
            }
        }
    }
}

class StaffRenderer(
    var lineSpacing: Float = 40f,
    var horizontalOffset: Float = 50f,
    var bottomY: Float = 100f,
) {
    fun DrawScope.drawStaffLines() {
        val startX = horizontalOffset
        val endX = size.width - horizontalOffset

        for (i in 0..4) {
            val y = bottomY + (i * lineSpacing)
            drawLine(
                color = Color.Black,
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 3f
            )
        }
    }

    fun DrawScope.drawNote(index: Int, pos: Int) {
        val xOffset = 200f + (index * 120f)
        val yOffset = bottomY + (pos * (lineSpacing / 2))
        val height = 40f - 1f
        val width = 50f

        drawOval(
            color = Color.Black,
            topLeft = Offset(xOffset, yOffset - height / 2),
            size = Size(width, height)
        )

        val stemUp = pos > 4
        val stemX = if (stemUp) xOffset + width - 2 else xOffset + 2f
        val stemHeight = if (stemUp) -100f else 100f

        drawLine(
            color = Color.Black,
            start = Offset(stemX, yOffset),
            end = Offset(stemX, yOffset + stemHeight),
            strokeWidth = 4f
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    NotesStaffTheme {
        Content("Android")
    }
}
package com.nikitagrgv.notesstaff

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nikitagrgv.notesstaff.ui.theme.NotesStaffTheme
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesStaffTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
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
fun Content(modifier: Modifier = Modifier) {
    var notePositions by remember { mutableStateOf(listOf(0, 11, -1, 10)) }
    var lastPressedNote by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(20.dp))
        MusicStaffCanvas(notePositions)
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.6f), onClick = {
                val newNote = (-4..12).random()
                notePositions = notePositions + newNote
            }) {
            Text(lastPressedNote)
        }
        PianoKeyboard { note ->

        }
    }
}

@Composable
fun PianoKeyboard(onKeyClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B")
            whiteKeys.forEach { note ->
                WhiteKey(note, onClick = { onKeyClick(note) })
            }
        }

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1.5f))
            BlackKey("C#") { onKeyClick("C#") }
            Spacer(modifier = Modifier.weight(1f))
            BlackKey("D#") { onKeyClick("D#") }
            Spacer(modifier = Modifier.weight(3f))
            BlackKey("F#") { onKeyClick("F#") }
            Spacer(modifier = Modifier.weight(1f))
            BlackKey("G#") { onKeyClick("G#") }
            Spacer(modifier = Modifier.weight(1f))
            BlackKey("A#") { onKeyClick("A#") }
            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}

@Composable
fun RowScope.WhiteKey(note: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .border(1.dp, Color.Black)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(note, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
    }
}

@Composable
fun RowScope.BlackKey(note: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(0.6f)
            .background(Color.Black)
            .clickable { onClick() })
}

@Composable
fun MusicStaffCanvas(notePositions: List<Int>) {
    val color = MaterialTheme.colorScheme.onBackground
    val renderer = remember(color) { StaffRenderer(color = color) }

    Canvas(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
    ) {
        with(renderer) {
            drawStaffLines()
            notePositions.forEachIndexed { index, pos ->
                drawNote(index, pos)
            }
        }
    }
}

class StaffRenderer(
    var lineSpacing: Float = 40f,
    var horizontalOffset: Float = 50f,
    var bottomY: Float = 200f,
    var color: Color,
) {
    fun DrawScope.drawStaffLines() {
        val startX = horizontalOffset
        val endX = size.width - horizontalOffset

        for (i in 0..4) {
            val y = bottomY + (i * lineSpacing)
            drawLine(
                color = color, start = Offset(startX, y), end = Offset(endX, y), strokeWidth = 3f
            )
        }
    }

    fun DrawScope.drawNote(index: Int, pos: Int) {
        val xOffset = 200f + (index * 120f)
        val yOffset = bottomY + (pos * (lineSpacing / 2))
        val height = 40f - 1f
        val width = 50f

        drawOval(
            color = color,
            topLeft = Offset(xOffset, yOffset - height / 2),
            size = Size(width, height)
        )

        val stemUp = pos > 4
        val stemX = if (stemUp) xOffset + width - 2 else xOffset + 2f
        val stemHeight = if (stemUp) -100f else 100f

        drawLine(
            color = color,
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
        Content()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ContentPreviewBlack() {
    NotesStaffTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Content()
        }
    }
}

@Preview(showBackground = true, widthDp = 600)
@Composable
fun ContentPreviewWide() {
    NotesStaffTheme {
        Content()
    }
}
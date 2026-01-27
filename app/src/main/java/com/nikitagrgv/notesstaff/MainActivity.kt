package com.nikitagrgv.notesstaff

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nikitagrgv.notesstaff.ui.theme.NotesStaffTheme
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.max

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
                            .fillMaxSize(), notes = listOf()
                    )
                }
            }
        }
    }
}

@Composable
fun Content(modifier: Modifier = Modifier, notes: List<Int>) {
    var notePositions by remember { mutableStateOf(notes) }
    var numMistakes by remember { mutableIntStateOf(0) }
    val minNotePosition = -4
    val maxNotePosition = 8 + 4

    while (notePositions.count() < 8) {
        notePositions += (-minNotePosition..maxNotePosition).random()
    }

    val color = MaterialTheme.colorScheme.onBackground
    val renderer = remember(color) {
        StaffRenderer(color = color).apply {
            scrollOffset = 500f
        }
    }

    LaunchedEffect(Unit) {
        val speedPxPerSecond = 250
        var previousTimeNanos = System.nanoTime()
        while (true) {
            withFrameNanos { frameTimeNanos ->
                val timeDeltaSeconds = (frameTimeNanos - previousTimeNanos) / 1_000_000_000f
                val speedMultiplier = if (renderer.scrollOffset > 500) 7f else 1f
                val speed = speedPxPerSecond * speedMultiplier
                var nextOffset = renderer.scrollOffset - timeDeltaSeconds * speed
                nextOffset = max(0f, nextOffset)
                renderer.scrollOffset = nextOffset
                previousTimeNanos = frameTimeNanos
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(20.dp))
        MusicStaffCanvas(notePositions, renderer)
        PianoKeyboard { note ->
            if (!notePositions.isEmpty()) {
                val str = mainClefNoteToString(notePositions.first())
                if (note == str) {
                    notePositions = notePositions.drop(1)
                    notePositions += (-minNotePosition..maxNotePosition).random()
                    renderer.jumpNextNote()
                } else {
                    numMistakes++;
                }
            }
        }
        Text(
            text = "Mistakes: $numMistakes",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
        )
    }
}

fun absNoteToString(note: Int): String {
    val names = listOf("C", "D", "E", "F", "G", "A", "B")
    val index = note.mod(7)
    return names[index]
}

fun mainClefNoteToString(note: Int): String {
    return absNoteToString(-note + 3)
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
fun MusicStaffCanvas(notePositions: List<Int>, renderer: StaffRenderer) {
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
    var notesSpacing: Float = 120f,
    var horizontalOffset: Float = 50f,
    var bottomY: Float = 200f,
    var color: Color,
) {
    var scrollOffset by mutableFloatStateOf(0f)

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

    fun jumpNextNote() {
        scrollOffset += notesSpacing
    }

    fun DrawScope.drawNote(index: Int, pos: Int) {
        val xOffset = scrollOffset + 150f + (index * notesSpacing)
        val yOffset = bottomY + (pos * (lineSpacing / 2))
        val height = lineSpacing - 1f
        val width = 50f

        if (pos < 0) {
            for (i in 0 downTo pos step 2) {
                if (i % 2 == 0 && i < 0) {
                    val lineY = bottomY + (i * (lineSpacing / 2))
                    drawLedgerLine(xOffset, lineY, width)
                }
            }
        } else if (pos > 8) {
            for (i in 10..pos step 2) {
                if (i % 2 == 0) {
                    val lineY = bottomY + (i * (lineSpacing / 2))
                    drawLedgerLine(xOffset, lineY, width)
                }
            }
        }

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

    private fun DrawScope.drawLedgerLine(noteX: Float, lineY: Float, noteWidth: Float) {
        val padding = 10f
        drawLine(
            color = color,
            start = Offset(noteX - padding, lineY),
            end = Offset(noteX + noteWidth + padding, lineY),
            strokeWidth = 3f
        )
    }
}

fun getTestNotes(): List<Int> {
    return listOf(-6, -5, 10, 12);
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    NotesStaffTheme {
        Content(notes = getTestNotes())
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ContentPreviewBlack() {
    NotesStaffTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Content(notes = getTestNotes())
        }
    }
}

@Preview(showBackground = true, widthDp = 600)
@Composable
fun ContentPreviewWide() {
    NotesStaffTheme {
        Content(notes = getTestNotes())
    }
}
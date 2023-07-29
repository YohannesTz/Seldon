@file:Suppress("FunctionName")

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.layout.background
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Color.Companion.Black
import com.jakewharton.mosaic.ui.Color.Companion.Blue
import com.jakewharton.mosaic.ui.Color.Companion.BrightBlue
import com.jakewharton.mosaic.ui.Color.Companion.BrightCyan
import com.jakewharton.mosaic.ui.Color.Companion.BrightGreen
import com.jakewharton.mosaic.ui.Color.Companion.BrightWhite
import com.jakewharton.mosaic.ui.Color.Companion.BrightYellow
import com.jakewharton.mosaic.ui.Color.Companion.Cyan
import com.jakewharton.mosaic.ui.Color.Companion.Green
import com.jakewharton.mosaic.ui.Color.Companion.Red
import com.jakewharton.mosaic.ui.Color.Companion.White
import com.jakewharton.mosaic.ui.Color.Companion.Yellow
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import kotlinx.coroutines.delay
import java.io.File

fun main(args: Array<String>) = runMosaicBlocking {
    if (args.isEmpty()) {
        setContent {
            Column {
                Text(
                    "(◡︵◡)",
                    color = Yellow,
                    style = TextStyle.Bold,
                    modifier = Modifier.padding(horizontal = 2, vertical = 1)
                )
                Text(
                    "Oops! you need to supply the directory path as an arg...",
                    color = Red,
                    style = TextStyle.Bold
                )
            }
        }
        return@runMosaicBlocking
    }
    val folder = File(args[0])
    val fileTypes = mutableMapOf<String, Int>()
    var largestFile: File? = null
    var largestFileSize = 0L

    var currentFile by mutableStateOf("...")
    var showResult by mutableStateOf(false)

    setContent {
        Column {
            Text(
                "Analysing: $currentFile",
                color = Black,
                style = TextStyle.Bold,
                modifier = Modifier.background(Yellow).padding(horizontal = 1)
            )
            if (showResult) {
                ResultComposable(fileTypes, largestFile, largestFileSize)
            }
        }
    }

    for (file in folder.walkTopDown()) {
        if (file.isFile) {
            delay(5)
            currentFile = file.name
            val fileType = file.extension.lowercase()
            fileTypes[fileType] = fileTypes.getOrDefault(fileType, 0) + 1

            // Track the largest file
            val fileSize = file.length()
            if (fileSize > largestFileSize) {
                largestFile = file
                largestFileSize = fileSize
            }
        }
    }
    showResult = true
}

@Composable
fun ResultComposable(fileTypes: MutableMap<String, Int>, largestFile: File?, largestFileSize: Long) {
    val fileTypesCount = fileTypes.size
    val colors = listOf(
        Green, Yellow, Blue, Cyan, White,
        BrightGreen, BrightYellow, BrightBlue, BrightWhite
    )

    Column {
        Text(
            "\n",
        )
        Text(
            "Discovered FileTypes: $fileTypesCount",
            color = Black,
            style = TextStyle.Bold,
            modifier = Modifier.background(BrightGreen).padding(horizontal = 1)
        )
        Text(
            "Results",
            color = Black,
            style = TextStyle.Bold,
            modifier = Modifier.background(BrightCyan).padding(horizontal = 1)
        )
        fileTypes.entries.sortedByDescending { it.value }.forEach { result ->
            Text(
                "${result.key}: ${result.value}",
                color = Black,
                style = TextStyle.Bold,
                modifier = Modifier.background(colors.random()).padding(horizontal = 1)
            )
        }
        Text(
            "\n",
        )
        Text(
            "Largest File: ",
            color = Black,
            style = TextStyle.Bold,
            modifier = Modifier.background(BrightCyan).padding(horizontal = 1)
        )
        Text(
            "${largestFile?.name}: ${formatFileSize(largestFileSize)}",
            color = Black,
            style = TextStyle.Bold,
            modifier = Modifier.background(BrightCyan).padding(horizontal = 1)
        )
    }
}

fun formatFileSize(size: Long): String {
    val mb = size.toDouble() / (1024 * 1024)
    val gb = mb / 1024
    return "$size bytes (approximately ${"%.2f".format(mb)} MB or ${"%.2f".format(gb)} GB)"
}

package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toOffset
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts

@Composable
fun PhotoCard(index: Uri, indexCount: Int) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black
    )
    val offsetX = remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(IntOffset.Zero) }

    Box(modifier = Modifier.size(150.dp, 150.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .size(150.dp, 150.dp)
                .offset { offset }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        var newOffset = offset.toOffset()
                        newOffset += dragAmount
                        offset = newOffset.round()
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                },
        ) {
            Box(contentAlignment = Alignment.Center) {
                SubcomposeAsyncImage(
                    model = index,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.size(150.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colorStops = colorStops)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (indexCount == 0) "Showcase Photo" else "Photo ${indexCount + 1}",
                        color = Color.White,
                        fontFamily = robotoFonts,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 80.dp, start = 5.dp)
                    )
                }

            }
        }
    }
}
package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    isProgressRequirementsMet: Boolean,
    currentIcon: ImageVector,
    nextIcon: ImageVector,
    previousIcon: ImageVector,
    nextScreen: () -> Unit,
    previousScreen: () -> Unit,
    isThisNotFirstScreen: Boolean = true,
) {
    Box(contentAlignment = Alignment.Center) {
        val colors = if (isProgressRequirementsMet || isThisNotFirstScreen) {
            arrayOf(
                0.0f to Color(0xFF8A3CCE),
                0.8f to CardDefaults.cardColors().containerColor
            )
        } else {
            arrayOf(
                0.0f to CardDefaults.cardColors().containerColor,
                1.0f to CardDefaults.cardColors().containerColor
            )
        }
        val nextIconAvailable =
            if (isProgressRequirementsMet) Color.White else Color.Black
        Card {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .background(Brush.horizontalGradient(colorStops = colors))
            ) {
                Box(
                    modifier = Modifier
                        .clip(AbsoluteRoundedCornerShape(16)) // Change shape to Circle
                        .width(120.dp)
                        .height(50.dp)
                        .padding(end = 8.dp)
                        .clickable(onClick = { previousScreen() }), // Make the box clickable
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = previousIcon,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.25f))
                Box(
                    modifier = Modifier
                        .clip(AbsoluteRoundedCornerShape(16)) // Change shape to Circle
                        .width(120.dp)
                        .height(50.dp)
                        .padding(start = 8.dp)
                        .clickable(
                            onClick = { nextScreen() },
                            enabled = isProgressRequirementsMet
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = nextIcon,
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp),
                        tint = nextIconAvailable
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .size(75.dp)
                    .background(if (isProgressRequirementsMet) Color(0xFF8A3CCE) else Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = currentIcon,
                    contentDescription = "Upload",
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }
}
package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition()
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            )
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun SkeletonLoader(modifier: Modifier = Modifier, shape: Shape = RectangleShape) {
    Box(
        modifier = modifier
            .background(shimmerBrush(), shape)
    )
}

@Composable
fun ItemNameSkeleton() {
    SkeletonLoader(modifier = Modifier
        .height(24.dp)
        .fillMaxWidth(0.6f))
}

@Composable
fun ItemCategorySkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SkeletonLoader(modifier = Modifier
            .height(24.dp)
            .fillMaxWidth(0.4f))
        SkeletonLoader(modifier = Modifier
            .height(24.dp)
            .fillMaxWidth(0.2f))
    }
}

@Composable
fun ItemPriceSkeleton() {
    SkeletonLoader(modifier = Modifier
        .height(24.dp)
        .fillMaxWidth(0.2f))
}

@Composable
fun ItemConditionSkeleton() {
    SkeletonLoader(modifier = Modifier
        .height(24.dp)
        .fillMaxWidth(0.4f))
}

@Composable
fun ItemDescriptionSkeleton() {
    Column {
        SkeletonLoader(modifier = Modifier
            .height(24.dp)
            .fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLoader(modifier = Modifier
            .height(24.dp)
            .fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLoader(modifier = Modifier
            .height(24.dp)
            .fillMaxWidth())
    }
}

@Composable
fun ItemLocationMapSkeleton() {
    Column {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(AbsoluteRoundedCornerShape(10.dp)), contentAlignment = Alignment.Center
        ) {
            SkeletonLoader(
                modifier = Modifier
                    .height(280.dp)
                    .width(360.dp)
            )
        }
    }
}

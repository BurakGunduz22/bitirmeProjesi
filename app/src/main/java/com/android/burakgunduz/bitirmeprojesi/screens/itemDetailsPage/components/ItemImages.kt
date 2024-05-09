package com.android.burakgunduz.bitirmeprojesi.screens.itemDetailsPage.components

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.android.burakgunduz.bitirmeprojesi.ViewModels.NamedUri

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
    imageUris: List<NamedUri>,
    navController: NavController,
    isMapLoaded: MutableState<Boolean>
) {
    val colorStops = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.4f to Color.hsl(0f, 0f, 0.1f, 0.1f),
        1f to Color.Black.copy(alpha = 0.8f)
    )
    val colorStops2 = arrayOf(
        0.0f to Color.hsl(0f, 0f, 0f, 0f),
        0.0f to Color.hsl(0f, 0f, 0f, 0f)
    )
    val imageLinks = imageUris.map { it.uri }
    val isTouched = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { imageLinks.size })
    val scale = remember { mutableFloatStateOf(1f) }
    val zoomLevels = listOf(1f, 3f)
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val callback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTouched.value) {
                    isTouched.value = false
                    isMapLoaded.value = false
                } else {
                    navController.popBackStack()
                }
            }
        }
    }
    DisposableEffect(backDispatcher) {
        backDispatcher?.addCallback(callback)
        onDispose {
            callback.remove()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .fillMaxHeight(if (isTouched.value) 0.8f else 0.3f)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(scaleX = scale.floatValue, scaleY = scale.floatValue)
                .transformable(state = rememberTransformableState { zoomChange, _, _ ->
                    val newScale = scale.floatValue * zoomChange
                    if (newScale in zoomLevels[0]..zoomLevels[1]) {
                        scale.floatValue = newScale
                    }
                }
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {

                        },
                        onDoubleTap = {
                            scale.floatValue =
                                if (scale.floatValue == zoomLevels[0]) zoomLevels[1] else zoomLevels[0]
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) { page ->
            val uri = imageLinks[page]
            SubcomposeAsyncImage(
                model = uri,
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = if (isTouched.value) colorStops2 else colorStops))
        ) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.LightGray else Color.DarkGray
                    val size = if (pagerState.currentPage == iteration) 12.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(size)
                    )
                }
            }
        }
        if (isTouched.value) {
            IconButton(
                onClick = {
                    isTouched.value = false
                    isMapLoaded.value = false
                },
            ) {
                Icon(imageVector = Icons.Sharp.Close, contentDescription = "")
            }
        } else {
            Box(modifier = Modifier
                .matchParentSize()
                .clickable {
                    isTouched.value = true
                })
        }
    }
}
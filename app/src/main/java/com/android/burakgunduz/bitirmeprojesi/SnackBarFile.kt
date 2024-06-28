package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun SnackBarFile(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: String
) {
    val snackbarDuration= when (duration) {
        "Short"->
            SnackbarDuration.Short
        "Long"->
            SnackbarDuration.Long
        else-> SnackbarDuration.Indefinite
    }
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = message, duration = snackbarDuration)
        }
}

@Composable
fun SnackBarItem(snackbarHostState: SnackbarHostState){
    SnackbarHost(hostState = snackbarHostState){data->
        SnackBarDesign(data = data)
    }
}

@Composable
fun SnackBarDesign(data: SnackbarData){
    Snackbar(snackbarData = data, modifier = androidx.compose.ui.Modifier.padding(16.dp), contentColor = androidx.compose.ui.graphics.Color.White, containerColor = androidx.compose.ui.graphics.Color.Black)
}

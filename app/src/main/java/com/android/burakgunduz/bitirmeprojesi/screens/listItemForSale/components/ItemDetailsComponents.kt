package com.android.burakgunduz.bitirmeprojesi.screens.listItemForSale.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun ItemDetailsTextfield(
    fieldName: String,
    fieldValue: String,
    onValueChange: (String) -> Unit,
    maxLineCount: Int = 1,
    minLineCount: Int = 1,
    isItSingleLine: Boolean = true,
    focusManager: FocusManager,
    maxLength: Int = 25,
    cornerRound: Int = 16,
    keyboardOptions: KeyboardType = KeyboardType.Text,
    suffixText: String = ""
) {
    var lineCount by remember { mutableIntStateOf(1) }
    var lineCharCount by remember { mutableIntStateOf(0) }
    val imeAction =
        if (lineCount == maxLineCount || lineCharCount >= maxLength) ImeAction.Next else ImeAction.Default
    var textFieldValue by remember { mutableStateOf(TextFieldValue(fieldValue)) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.8f),
        shape = AbsoluteRoundedCornerShape(cornerRound),
        value = fieldValue,
        onValueChange = {
            lineCharCount = it.length
            if (lineCharCount <= maxLength) {
                onValueChange(it)
                lineCount = it.count { char -> char == '\n' } + 1
            }
        },
        label = { Text(fieldName) },
        minLines = minLineCount,
        maxLines = maxLineCount,
        trailingIcon = {
            Text(
                text = suffixText,
                fontFamily = archivoFonts,
            )
        },
        singleLine = isItSingleLine,
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardOptions),
        keyboardActions = KeyboardActions(
            onNext = {
                val text = textFieldValue.text
                lineCount = text.lineCount(minLineCount, maxLineCount)
                if (lineCount >= maxLineCount || lineCharCount >= maxLength) {
                    focusManager.moveFocus(FocusDirection.Down)
                } else {
                    textFieldValue = TextFieldValue("$text\n")
                }
            }
        )
    )
}

private fun String.lineCount(minLineCount: Int, maxLineCount: Int): Int {
    val textLines = this.split('\n')
    return when {
        textLines.size < minLineCount -> minLineCount
        textLines.size > maxLineCount -> maxLineCount
        else -> textLines.size
    }
}
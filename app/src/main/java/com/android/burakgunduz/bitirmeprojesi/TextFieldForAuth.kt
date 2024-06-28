package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts

@Composable
fun TextFieldForAuth(
    takeAuthValue: String,
    labelText: String,
    keyboardOpt: KeyboardOptions = KeyboardOptions.Default,
    fieldSpace: Int = 10,
    fieldSize: Int = 350,
    fieldCount: Int = 1,
    focusManager: FocusManager,
    errorText: String? = null,
    onAuthValueChange: (String) -> Unit,
) {
    val imeAction =
        if ((fieldCount == 2 && labelText == "Password") || (fieldCount == 5 && labelText == "Confirm Password") || fieldCount == 0) ImeAction.Done else ImeAction.Next
    var isPasswordVisible by remember { mutableStateOf(false) }
    var authValue = takeAuthValue
    OutlinedTextField(
        value = takeAuthValue,
        singleLine = true,
        shape = AbsoluteRoundedCornerShape(10.dp),
        label = {
            Text(
                text = labelText,
                fontFamily = archivoFonts,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left,
            )
        },
        supportingText = {
            if (!errorText.isNullOrEmpty()) {
                Text(
                    text = errorText,
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Left,
                )
            }
        },
        visualTransformation = if (!isPasswordVisible && keyboardOpt.keyboardType == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (keyboardOpt.keyboardType == KeyboardType.Password) {
                IconToggleButton(checked = isPasswordVisible, onCheckedChange = {
                    isPasswordVisible = it
                }) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Filled.VisibilityOff
                        } else {
                            Icons.Filled.Visibility
                        }, contentDescription = "PasswordAuthVisibility"
                    )
                }
            }

        },
        isError = !errorText.isNullOrEmpty(),
        modifier = Modifier
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = 5.dp,
                bottom = 5.dp
            )
            .size(fieldSize.dp, 80.dp),
        onValueChange = {
            authValue = it
            onAuthValueChange(authValue)
        },
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardOpt.keyboardType
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (imeAction == ImeAction.Next) {
                    focusManager.moveFocus(FocusDirection.Down)
                } else {
                    focusManager.clearFocus()
                }
            }
        )
    )
}

fun authKeyboardType(type: String): KeyboardOptions {
    return when (type) {
        "email" -> KeyboardOptions(keyboardType = KeyboardType.Email)
        "password" -> KeyboardOptions(keyboardType = KeyboardType.Password)
        "phone" -> KeyboardOptions(keyboardType = KeyboardType.Phone)
        else -> KeyboardOptions.Default
    }
}

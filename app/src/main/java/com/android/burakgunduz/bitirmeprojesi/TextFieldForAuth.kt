package com.android.burakgunduz.bitirmeprojesi

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldForAuth(
    takeAuthValue: String,
    labelText: String,
    keyboardOpt: KeyboardOptions = KeyboardOptions.Default,
    fieldSpace: Int = 10,
    fieldSize: Int = 350,
    onAuthValueChange: (String) -> Unit,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var authValue = takeAuthValue
    OutlinedTextField(
        value = takeAuthValue,
        keyboardOptions = keyboardOpt,
        singleLine = true,
        shape = AbsoluteRoundedCornerShape(10.dp),
        label = { Text(labelText) },
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
        modifier = Modifier
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = 10.dp,
                bottom = fieldSpace.dp
            )
            .size(fieldSize.dp, 65.dp),
        onValueChange = {
            authValue = it
            onAuthValueChange(authValue)
        }
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
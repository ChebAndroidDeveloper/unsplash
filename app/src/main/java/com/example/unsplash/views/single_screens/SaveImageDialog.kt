package com.example.unsplash.views.single_screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun SaveImageDialog(onSave: (String) -> Unit, onCancel: () -> Unit, text : String) {
    var fileName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = text) },
        text = {
            TextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text(text = "Имя файла") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(fileName) }) {
                Text(text = "Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = "Отмена")
            }
        }
    )
}

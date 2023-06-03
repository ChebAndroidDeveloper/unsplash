package com.example.unsplash.views.main_screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.view_model.MainViewModel
import com.example.unsplash.views.single_screens.PhotoDetailsScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun OpenScreen() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val showSearchBarOrDetailScreen = remember { mutableStateOf(false) }
    var textFromTextField by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val photoDetailsState = remember { mutableStateOf<PhotoDetails?>(null) }
    val photoDetails = photoDetailsState.value

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    if (!showSearchBarOrDetailScreen.value) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = textFromTextField,
                onValueChange = { textFromTextField = it },
                label = { Text("введите ссылку тут") },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val photoId = textFromTextField.substringAfterLast("/")
                        scope.launch {
                            val details = mainViewModel.getOnePhoto(photoId)
                            photoDetailsState.value = details
                            showSearchBarOrDetailScreen.value = true
                        }

                    }
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        val photoId = textFromTextField.substringAfterLast("/")
                        scope.launch {
                            val details = mainViewModel.getOnePhoto(photoId)
                            photoDetailsState.value = details
                            showSearchBarOrDetailScreen.value = true
                        }
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = "Поиск")
                    }
                }

            )
        }
    } else {
        if (photoDetails != null) {
            PhotoDetailsScreen(photoDetails){
                showSearchBarOrDetailScreen.value = false
            }
        }
    }
}
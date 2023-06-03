package com.example.unsplash.views.primal_screens

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.unsplash.model.get_token.Authorization
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun WebViewScreen(authUrl: String) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var webView: WebView? = null
    var showWebView by remember {
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val hasToken = sharedPreferences.contains("access_token")
        mutableStateOf(!hasToken)
    }

    if (showWebView) {
        AndroidView(
            factory = {
                WebView(context).apply {
                    webView = this
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url.toString()
                            if (url.startsWith("myapp://unsplash")) {
                                val uri = Uri.parse(url)
                                val code = uri.getQueryParameter("code")
                                if (code != null) {
                                    showWebView = false

                                    runBlocking {
                                            val token = Authorization().getAccessToken(code)
                                            val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                            val editor = sharedPreferences.edit()
                                            editor.putString("access_token", token)
                                            editor.apply()
                                        Log.d("WebViewScreen", "Токен доступа получен и сохранен в SharedPreferences: $token")

                                        }
                                }
                                return true
                            }
                            return false
                        }
                    }
                }
            },
            update = {
                it.loadUrl(authUrl)
            }
        )
    } else MainScreen()

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }
}

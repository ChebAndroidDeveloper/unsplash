package com.example.unsplash

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.unsplash.model.get_token.Authorization
import com.example.unsplash.ui.theme.UnsplashTheme
import com.example.unsplash.views.primal_screens.WebViewScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy {
        getSharedPreferences("onboarding", Context.MODE_PRIVATE)
    }
    private val skipOrNot by lazy {
        mutableStateOf(true)
    }
    private val url = Authorization().getAuthUrl()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences1 = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val token = sharedPreferences1.getString("access_token", null)

        setContent {

            UnsplashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (skipOrNot.value) {
                        if (shouldShowOnboarding()) {
                            OnboardingScreen(
                                onFinish = { setOnboardingShown() },
                                onSkip = { skipOrNot.value = false }
                            )
                        } else if (token == null) {
                            WebViewScreen(url)
                        } else WebViewScreen(url)
                    } else WebViewScreen(url)
                }
            }
        }
    }

    private fun shouldShowOnboarding(): Boolean {
        return sharedPreferences.getBoolean("show_onboarding", true)
    }

    private fun setOnboardingShown() {
        sharedPreferences.edit().putBoolean("show_onboarding", false).apply()
    }


    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun OnboardingScreen(onFinish: () -> Unit, onSkip: () -> Unit) {
        val pageCount = 7
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()
        var checkedState by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1500)
                if (pagerState.currentPage + 1 < pageCount) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    skipOrNot.value = false
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(state = pagerState, count = pageCount) { page ->
                    Image(
                        painter = painterResource(id = screenshotResourceId(page)),
                        contentDescription = "Screenshot $page",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Добро пожаловать в приложение!")
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Magenta.copy(alpha = 0.5f))                ,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan.copy(alpha = 0.5f) )) {
                    Text(text = "Пропустить")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = {
                        setOnboardingShown()
                        checkedState = true
                    }
                )
                Text(text = "Больше не показывать")
            }

        }
    }


    @Composable
    fun screenshotResourceId(page: Int): Int {
        return when (page) {
            0 -> R.drawable.screenshot_1
            1 -> R.drawable.screenshot_2
            2 -> R.drawable.screenshot_3
            3 -> R.drawable.screenshot_4
            4 -> R.drawable.screenshot_5
            5 -> R.drawable.screenshot_6
            6 -> R.drawable.screenshot_7
            else -> throw IllegalArgumentException("Invalid page: $page")
        }
    }
}












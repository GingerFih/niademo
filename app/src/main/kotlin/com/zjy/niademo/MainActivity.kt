package com.zjy.niademo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.metrics.performance.JankStats
import com.zjy.niademo.core.analytics.AnalyticsHelper
import com.zjy.niademo.core.data.repository.UserNewsResourceRepository
import com.zjy.niademo.core.data.util.NetworkMonitor
import com.zjy.niademo.core.data.util.TimeZoneMonitor
import dagger.hilt.android.AndroidEntryPoint
import org.checkerframework.checker.units.qual.A
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    // 性能统计库，监控应用界面的卡顿，定位哪些UI操作掉帧、卡顿
    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    // 网络
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    //时区
    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    // 性能分析
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    // 用户新闻数据
    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    val viewModel :MainActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        var splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
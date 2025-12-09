package com.zjy.niademo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import androidx.metrics.performance.JankStats
import com.zjy.niademo.core.analytics.AnalyticsHelper
import com.zjy.niademo.core.data.repository.UserNewsResourceRepository
import com.zjy.niademo.core.data.util.NetworkMonitor
import com.zjy.niademo.core.data.util.TimeZoneMonitor
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zjy.niademo.MainActivityUiState.Loading
import com.zjy.niademo.core.analytics.LocalAnalyticsHelper
import com.zjy.niademo.core.designsystem.theme.NiaTheme
import com.zjy.niademo.core.ui.LocalTimeZone
import com.zjy.niademo.ui.rememberNiaAppState
import com.zjy.niademo.util.isSystemInDarkTheme
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

    val viewModel: MainActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        var splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        var themeSettings by mutableStateOf(
            ThemeSettings(
                darkTheme = resources.configuration.isSystemInDarkTheme,
                androidTheme = Loading.shouldUseAndroidTheme,
                disableDynamicTheming = Loading.shouldDisableDynamicTheming,
            ),
        )

        lifecycleScope.launch {
            //在声明周期状态在启动状态时，会执行
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.i("MainActivity", "onCreate: Lifecycle.State.STARTED")
                // 组合两个流
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState,
                ) { systemDark, uiState ->
                    ThemeSettings(
                        darkTheme = systemDark,
                        androidTheme = uiState.shouldUseAndroidTheme,
                        disableDynamicTheming = uiState.shouldDisableDynamicTheming,
                    )

                }
                    .onEach { themeSettings = it }
                    .map { it.darkTheme }
                    .distinctUntilChanged()
                    .collect { darkTheme ->
                        // 性能追踪调试接口，用于标记和最终特定代码快执行
                        // 命名标识“niaEdgeToEdge” 是追踪点的标识名称，便于识别分类
                        trace("niaEdgeToEdge") {
                            // 可追踪的执行单元
                            enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.auto(
                                    lightScrim = android.graphics.Color.TRANSPARENT,
                                    darkScrim = darkScrim,
                                ) { darkTheme },
                                navigationBarStyle = SystemBarStyle.auto(
                                    lightScrim = android.graphics.Color.TRANSPARENT,
                                    darkScrim = android.graphics.Color.TRANSPARENT,
                                ) { darkTheme },

                                )

                        }

                    }
            }
        }

        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.shouldKeepSplashScreen() }

        setContent {
            val appState = rememberNiaAppState(
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            // 提供本地化,是jetpack Compose 中向子树传递依赖的机制，类似于React的Context或flutter 的inheritedWidget
            // 不建议频繁使用，可能会触发大范围recomposition
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
                LocalTimeZone provides currentTimeZone,
            ) {
                NiaTheme(
                    darkTheme = themeSettings.darkTheme,
                    androidTheme = themeSettings.androidTheme,
                    disableDynamicTheming = themeSettings.disableDynamicTheming,
                ){
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

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

data class ThemeSettings(
    val darkTheme: Boolean,
    val androidTheme: Boolean,
    val disableDynamicTheming: Boolean,
)
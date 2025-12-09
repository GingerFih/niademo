package com.zjy.niademo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zjy.niademo.core.data.repository.UserDataRepository
import com.zjy.niademo.MainActivityUiState.Loading
import com.zjy.niademo.MainActivityUiState.Success
import com.zjy.niademo.core.model.data.DarkThemeConfig
import com.zjy.niademo.core.model.data.ThemeBrand
import com.zjy.niademo.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Loading
    )
}

/**
 * sealed 密封接口
 */
sealed interface MainActivityUiState {
    //初始化值
    data object Loading : MainActivityUiState

    //数据加载成功后，进行赋值
    data class Success(val userData: UserData) : MainActivityUiState {

        //用户禁止使用动态主题
        override val shouldDisableDynamicTheming = !userData.useDynamicColor

        //用户主题品牌，是否为Android
        override val shouldUseAndroidTheme: Boolean = when (userData.themeBrand) {
            ThemeBrand.DEFAULT -> false
            ThemeBrand.ANDROID -> true
        }

        //是否跟随系统主题
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean): Boolean =
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }

    fun shouldKeepSplashScreen() = this is Loading

    val shouldDisableDynamicTheming: Boolean get() = true

    val shouldUseAndroidTheme: Boolean get() = false

    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}
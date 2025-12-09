package com.zjy.niademo.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.protobuf.duration
import com.zjy.niademo.R
import com.zjy.niademo.core.designsystem.component.NiaBackground
import com.zjy.niademo.core.designsystem.component.NiaGradientBackground
import com.zjy.niademo.core.designsystem.theme.GradientColors
import com.zjy.niademo.core.designsystem.theme.LocalGradientColors
import com.zjy.niademo.navigation.TopLevelDestination

@Composable
fun NiaApp(
    appState: NiaAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
){
    val shouldShowGradientBackGroup = appState.currentTopLevelDestination == TopLevelDestination.FOR_YOU

    var showSettingsDialog by rememberSaveable{ mutableStateOf(false) }

    NiaBackground(modifier = modifier) {
        NiaGradientBackground (
            gradientColors = if (shouldShowGradientBackGroup) {
                LocalGradientColors.current
            } else {
                GradientColors()
            }
        ){
            val snackbarHostState = remember {
                SnackbarHostState()
            }

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            val netConnectedMessage = stringResource(R.string.not_connected)

            LaunchedEffect(isOffline) {
                if (isOffline){
                    snackbarHostState.showSnackbar(
                        message = netConnectedMessage,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }

//            NiaApp(
//                appState = appState,
//                snackbarHostState = snackbarHostState,
//                showSettingsDialog = showSettingsDialog,
//                onSettingsDismissed = { showSettingsDialog = false },
//                onTopAppBarActionClick = { showSettingsDialog = true },
//                windowAdaptiveInfo = windowAdaptiveInfo,
//            )
        }
    }

}
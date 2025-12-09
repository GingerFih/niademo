/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjy.niademo.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.zjy.niademo.core.analytics.AnalyticsEvent
import com.zjy.niademo.core.analytics.AnalyticsEvent.Param
import com.zjy.niademo.core.analytics.AnalyticsEvent.ParamKeys
import com.zjy.niademo.core.analytics.AnalyticsEvent.Types
import com.zjy.niademo.core.analytics.AnalyticsHelper
import com.zjy.niademo.core.analytics.LocalAnalyticsHelper

/**
 * Classes and functions associated with analytics events for the UI.
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

fun AnalyticsHelper.logNewsResourceOpened(newsResourceId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "news_resource_opened",
            extras = listOf(
                Param("opened_news_resource", newsResourceId),
            ),
        ),
    )
}

/**
 * A side-effect which records a screen view event.
 * 用于屏幕浏览事件追踪
 * 当屏幕加载时，通过analyticsHelper.logScreenView(screenName)记录屏幕浏览事件
 * 使用DisposableEffect确保只在组件首次加载时执行一次日志记录
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}

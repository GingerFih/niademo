/*
 * Copyright 2022 The Android Open Source Project
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

package com.zjy.niademo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.zjy.niademo.feature.bookmarks.navigation.bookmarksScreen
import com.zjy.niademo.feature.foryou.navigation.ForYouBaseRoute
import com.zjy.niademo.feature.foryou.navigation.forYouSection
import com.zjy.niademo.feature.interests.navigation.navigateToInterests
import com.zjy.niademo.feature.search.navigation.searchScreen
import com.zjy.niademo.feature.topic.navigation.navigateToTopic
import com.zjy.niademo.feature.topic.navigation.topicScreen
import com.zjy.niademo.navigation.TopLevelDestination.INTERESTS
import com.zjy.niademo.ui.NiaAppState
import com.zjy.niademo.ui.interests2pane.interestsListDetailScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun NiaNavHost(
    appState: NiaAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = ForYouBaseRoute,
        modifier = modifier,
    ) {
        forYouSection(
            onTopicClick = navController::navigateToTopic,
        ) {
            topicScreen(
                showBackButton = true,
                onBackClick = navController::popBackStack,
                onTopicClick = navController::navigateToTopic,
            )
        }
        bookmarksScreen(
            onTopicClick = navController::navigateToInterests,
            onShowSnackbar = onShowSnackbar,
        )
        searchScreen(
            onBackClick = navController::popBackStack,
            onInterestsClick = { appState.navigateToTopLevelDestination(INTERESTS) },
            onTopicClick = navController::navigateToInterests,
        )
        interestsListDetailScreen()
    }
}

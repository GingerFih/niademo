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

package com.zjy.niademo.feature.foryou

import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.zjy.niademo.core.designsystem.component.DynamicAsyncImage
import com.zjy.niademo.core.designsystem.component.NiaButton
import com.zjy.niademo.core.designsystem.component.NiaIconToggleButton
import com.zjy.niademo.core.designsystem.component.NiaOverlayLoadingWheel
import com.zjy.niademo.core.designsystem.component.scrollbar.DecorativeScrollbar
import com.zjy.niademo.core.designsystem.component.scrollbar.DraggableScrollbar
import com.zjy.niademo.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.zjy.niademo.core.designsystem.component.scrollbar.scrollbarState
import com.zjy.niademo.core.designsystem.icon.NiaIcons
import com.zjy.niademo.core.designsystem.theme.NiaTheme
import com.zjy.niademo.core.model.data.UserNewsResource
import com.zjy.niademo.core.ui.DevicePreviews
import com.zjy.niademo.core.ui.NewsFeedUiState
import com.zjy.niademo.core.ui.TrackScreenViewEvent
import com.zjy.niademo.core.ui.TrackScrollJank
import com.zjy.niademo.core.ui.UserNewsResourcePreviewParameterProvider
import com.zjy.niademo.core.ui.launchCustomChromeTab
import com.zjy.niademo.core.ui.newsFeed

@Composable
internal fun ForYouScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForYouViewModel = hiltViewModel(),
) {
    //获取用户引导状态（如主题选择界面）
    val onboardingUiState by viewModel.onboardingUiState.collectAsStateWithLifecycle()
    //获取新闻源状态（加载中或成功获取的数据）
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    //获取数据同步状态（是否正在同步数据）
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    //获取深度链接的新闻资源
    val deepLinkedUserNewsResource by viewModel.deepLinkedNewsResource.collectAsStateWithLifecycle()

    ForYouScreen(
        isSyncing = isSyncing,
        onboardingUiState = onboardingUiState,
        feedState = feedState,
        deepLinkedUserNewsResource = deepLinkedUserNewsResource,
        onTopicCheckedChanged = viewModel::updateTopicSelection,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onTopicClick = onTopicClick,
        saveFollowedTopics = viewModel::dismissOnboarding,
        onNewsResourcesCheckedChanged = viewModel::updateNewsResourceSaved,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
        modifier = modifier,
    )
}

/**
 * internal 修饰的函数、类、属性等只能在同一个模块内访问
 * 它比 private 和 protected 的可见范围更大，但比 public 的范围更小
 * 在 Android 开发中，这意味着标记为 internal 的代码只能在同一个 Android library module 或 application module 内部访问
 */

@Composable
internal fun ForYouScreen(
    isSyncing: Boolean,
    onboardingUiState: OnboardingUiState,
    feedState: NewsFeedUiState,
    deepLinkedUserNewsResource: UserNewsResource?,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeepLinkOpened: (String) -> Unit,
    saveFollowedTopics: () -> Unit,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    //状态管理 检查引导状态和新闻源是否正在加载
    val isOnboardingLoading = onboardingUiState is OnboardingUiState.Loading
    val isFeedLoading = feedState is NewsFeedUiState.Loading

    //性能优化 报告屏幕完全绘制的时间点，用于性能监控。
    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isOnboardingLoading && !isFeedLoading }

    val itemsAvailable = feedItemsSize(feedState, onboardingUiState)

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    TrackScrollJank(scrollableState = state, stateName = "forYou:feed")

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        //使用 LazyVerticalStaggeredGrid 创建瀑布流布局：
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("forYou:feed"),
            state = state,
        ) {
            //onboarding: 用户引导部分（主题选择）
            onboarding(
                onboardingUiState = onboardingUiState,
                onTopicCheckedChanged = onTopicCheckedChanged,
                saveFollowedTopics = saveFollowedTopics,
                // Custom LayoutModifier to remove the enforced parent 16.dp contentPadding
                // from the LazyVerticalGrid and enable edge-to-edge scrolling for this section
                interestsItemModifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth + 32.dp.roundToPx(),
                        ),
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                },
            )
            //newsFeed: 新闻内容展示
            newsFeed(
                feedState = feedState,
                onNewsResourcesCheckedChanged = onNewsResourcesCheckedChanged,
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
            )

            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    // Add space for the content to clear the "offline" snackbar.
                    // TODO: Check that the Scaffold handles this correctly in NiaApp
                    // if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        //加载指示器 当数据加载时显示动画加载轮。
        AnimatedVisibility(
            visible = isSyncing || isFeedLoading || isOnboardingLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_foryou_loading)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                NiaOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
        //滚动条支持 为网格列表添加可拖拽滚动条。
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
    //屏幕浏览事件追踪
    TrackScreenViewEvent(screenName = "ForYou")
    //请求通知权限
    NotificationPermissionEffect()
    //处理深度链接导航
    DeepLinkEffect(
        deepLinkedUserNewsResource,
        onDeepLinkOpened,
    )
}

/**
 * An extension on [LazyListScope] defining the onboarding portion of the for you screen.
 * Depending on the [onboardingUiState], this might emit no items.
 *
 */
private fun LazyStaggeredGridScope.onboarding(
    onboardingUiState: OnboardingUiState,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    saveFollowedTopics: () -> Unit,
    interestsItemModifier: Modifier = Modifier,
) {
    when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
        -> Unit

        is OnboardingUiState.Shown -> {
            item(span = StaggeredGridItemSpan.FullLine, contentType = "onboarding") {
                Column(modifier = interestsItemModifier) {
                    Text(
                        text = stringResource(R.string.feature_foryou_onboarding_guidance_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.feature_foryou_onboarding_guidance_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TopicSelection(
                        onboardingUiState,
                        onTopicCheckedChanged,
                        Modifier.padding(bottom = 8.dp),
                    )
                    // Done button
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        NiaButton(
                            onClick = saveFollowedTopics,
                            enabled = onboardingUiState.isDismissable,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .widthIn(364.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.feature_foryou_done),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicSelection(
    onboardingUiState: OnboardingUiState.Shown,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyGridState = rememberLazyGridState()
    val topicSelectionTestTag = "forYou:topicSelection"

    TrackScrollJank(scrollableState = lazyGridState, stateName = topicSelectionTestTag)

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        LazyHorizontalGrid(
            state = lazyGridState,
            rows = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(24.dp),
            modifier = Modifier
                // LazyHorizontalGrid has to be constrained in height.
                // However, we can't set a fixed height because the horizontal grid contains
                // vertical text that can be rescaled.
                // When the fontScale is at most 1, we know that the horizontal grid will be at most
                // 240dp tall, so this is an upper bound for when the font scale is at most 1.
                // When the fontScale is greater than 1, the height required by the text inside the
                // horizontal grid will increase by at most the same factor, so 240sp is a valid
                // upper bound for how much space we need in that case.
                // The maximum of these two bounds is therefore a valid upper bound in all cases.
                .heightIn(max = max(240.dp, with(LocalDensity.current) { 240.sp.toDp() }))
                .fillMaxWidth()
                .testTag(topicSelectionTestTag),
        ) {
            items(
                items = onboardingUiState.topics,
                key = { it.topic.id },
            ) {
                SingleTopicButton(
                    name = it.topic.name,
                    topicId = it.topic.id,
                    imageUrl = it.topic.imageUrl,
                    isSelected = it.isFollowed,
                    onClick = onTopicCheckedChanged,
                )
            }
        }
        lazyGridState.DecorativeScrollbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomStart),
            state = lazyGridState.scrollbarState(itemsAvailable = onboardingUiState.topics.size),
            orientation = Orientation.Horizontal,
        )
    }
}

@Composable
private fun SingleTopicButton(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: (String, Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(312.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        color = MaterialTheme.colorScheme.surface,
        selected = isSelected,
        onClick = {
            onClick(topicId, !isSelected)
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp),
        ) {
            TopicIcon(
                imageUrl = imageUrl,
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
            )
            NiaIconToggleButton(
                checked = isSelected,
                onCheckedChange = { checked -> onClick(topicId, checked) },
                icon = {
                    Icon(
                        imageVector = NiaIcons.Add,
                        contentDescription = name,
                    )
                },
                checkedIcon = {
                    Icon(
                        imageVector = NiaIcons.Check,
                        contentDescription = name,
                    )
                },
            )
        }
    }
}

@Composable
fun TopicIcon(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    DynamicAsyncImage(
        placeholder = painterResource(R.drawable.feature_foryou_ic_icon_placeholder),
        imageUrl = imageUrl,
        // decorative
        contentDescription = null,
        modifier = modifier
            .padding(10.dp)
            .size(32.dp),
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // Permission requests should only be made from an Activity Context, which is not present
    // in previews
    //首先检查是否处于预览模式，如果是则直接返回不执行
    //然后检查 Android 版本是否低于 TIRAMISU (Android 13)，如果是则也直接返回
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    //使用 rememberPermissionState 记住通知权限的状态
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    //当权限状态发生变化时触发副作用
    //如果权限被拒绝且不需要显示理由说明，则自动发起权限请求
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
private fun DeepLinkEffect(
    userNewsResource: UserNewsResource?,
    onDeepLinkOpened: (String) -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(userNewsResource) {
        if (userNewsResource == null) return@LaunchedEffect
        if (!userNewsResource.hasBeenViewed) onDeepLinkOpened(userNewsResource.id)

        launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userNewsResource.url),
            toolbarColor = backgroundColor,
        )
    }
}

private fun feedItemsSize(
    feedState: NewsFeedUiState,
    onboardingUiState: OnboardingUiState,
): Int {
    val feedSize = when (feedState) {
        NewsFeedUiState.Loading -> 0
        is NewsFeedUiState.Success -> feedState.feed.size
    }
    val onboardingSize = when (onboardingUiState) {
        OnboardingUiState.Loading,
        OnboardingUiState.LoadFailed,
        OnboardingUiState.NotShown,
        -> 0

        is OnboardingUiState.Shown -> 1
    }
    return feedSize + onboardingSize
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenOfflinePopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.NotShown,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenTopicSelection(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Shown(
                topics = userNewsResources.flatMap { news -> news.followableTopics }
                    .distinctBy { it.topic.id },
            ),
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenLoading() {
    NiaTheme {
        ForYouScreen(
            isSyncing = false,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = NewsFeedUiState.Loading,
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedAndLoading(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NiaTheme {
        ForYouScreen(
            isSyncing = true,
            onboardingUiState = OnboardingUiState.Loading,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onTopicCheckedChanged = { _, _ -> },
            saveFollowedTopics = {},
            onNewsResourcesCheckedChanged = { _, _ -> },
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

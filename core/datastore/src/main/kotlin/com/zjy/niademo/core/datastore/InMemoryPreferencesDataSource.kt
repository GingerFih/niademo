package com.zjy.niademo.core.datastore

import com.zjy.niademo.core.model.data.DarkThemeConfig
import com.zjy.niademo.core.model.data.ThemeBrand
import com.zjy.niademo.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryPreferencesDataSource @Inject constructor() : NiaPreferencesDataSource {
    private val changeLists = MutableStateFlow(ChangeListVersions())
    private val userDataState = MutableStateFlow(
        UserData(
            bookmarkedNewsResources = emptySet(),
            viewedNewsResources = emptySet(),
            followedTopics = emptySet(),
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = false,
        ),
    )

    override val userData: Flow<UserData> = userDataState.asStateFlow()

    override suspend fun getChangeListVersions(): ChangeListVersions = changeLists.value

    override suspend fun updateChangeListVersion(
        update: ChangeListVersions.() -> ChangeListVersions,
    ) {
        changeLists.value = changeLists.value.update()
    }

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {
        userDataState.value = userDataState.value.copy(followedTopics = followedTopicIds)
    }

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        val current = userDataState.value.followedTopics.toMutableSet()
        if (followed) current.add(followedTopicId) else current.remove(followedTopicId)
        userDataState.value = userDataState.value.copy(followedTopics = current)
    }

    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        val current = userDataState.value.bookmarkedNewsResources.toMutableSet()
        if (bookmarked) current.add(newsResourceId) else current.remove(newsResourceId)
        userDataState.value = userDataState.value.copy(bookmarkedNewsResources = current)
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        val current = userDataState.value.viewedNewsResources.toMutableSet()
        if (viewed) current.add(newsResourceId) else current.remove(newsResourceId)
        userDataState.value = userDataState.value.copy(viewedNewsResources = current)
    }

    override suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean) {
        val current = userDataState.value.viewedNewsResources.toMutableSet()
        if (viewed) current.addAll(newsResourceIds) else current.removeAll(newsResourceIds.toSet())
        userDataState.value = userDataState.value.copy(viewedNewsResources = current)
    }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userDataState.value = userDataState.value.copy(themeBrand = themeBrand)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userDataState.value = userDataState.value.copy(darkThemeConfig = darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userDataState.value = userDataState.value.copy(useDynamicColor = useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userDataState.value = userDataState.value.copy(shouldHideOnboarding = shouldHideOnboarding)
    }
}


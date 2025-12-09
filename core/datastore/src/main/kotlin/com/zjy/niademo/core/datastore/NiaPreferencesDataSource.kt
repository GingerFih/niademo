package com.zjy.niademo.core.datastore

import com.zjy.niademo.core.model.data.DarkThemeConfig
import com.zjy.niademo.core.model.data.ThemeBrand
import com.zjy.niademo.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface NiaPreferencesDataSource {
    val userData: Flow<UserData>
    suspend fun getChangeListVersions(): ChangeListVersions
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions)
    suspend fun setFollowedTopicIds(followedTopicIds: Set<String>)
    suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean)
    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean)
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)
    suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean)
    suspend fun setThemeBrand(themeBrand: ThemeBrand)
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}

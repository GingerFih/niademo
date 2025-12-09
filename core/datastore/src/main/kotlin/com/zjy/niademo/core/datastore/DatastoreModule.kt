package com.zjy.niademo.core.datastore

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DatastoreModule {
    @Binds
    abstract fun bindPreferencesDataSource(
        impl: InMemoryPreferencesDataSource,
    ): NiaPreferencesDataSource
}


plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.zjy.niademo.core.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
    api(project(":core:datastoreProto"))
    api(project(":core:model"))

    implementation(project(":core:common"))

//    testImplementation(project(":core:datastoreTest"))
    testImplementation(libs.kotlinx.coroutines.test)
}
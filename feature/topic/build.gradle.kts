
plugins {
    alias(libs.plugins.nowinandroid.android.feature)
    alias(libs.plugins.nowinandroid.android.library.compose)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.zjy.niademo.feature.topic"
}

dependencies {
    implementation(project(":core:data"))

    testImplementation(project(":core:testing"))
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(project(":core:testing"))
}
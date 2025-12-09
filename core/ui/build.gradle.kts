plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.android.library.compose)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
}

android {
    namespace = "com.zjy.niademo.core.ui"
}

dependencies {
    api(libs.androidx.metrics)
    api(project(":core:analytics"))
    api(project(":core:designsystem"))
    api(project(":core:model"))

    implementation(libs.androidx.browser)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(project(":core:testing"))
}
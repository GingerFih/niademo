plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    namespace = "com.zjy.niademo.core.notifications"
}

dependencies {
    api(project(":core:model"))

    implementation(project(":core:common"))

    compileOnly(platform(libs.androidx.compose.bom))
}
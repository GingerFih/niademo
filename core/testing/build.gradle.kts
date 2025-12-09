plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    namespace = "com.zjy.niademo.core.testing"
}

dependencies {
    api(libs.kotlinx.coroutines.test)
    api(project(":core:analytics"))
    api(project(":core:common"))
    api(project(":core:data"))
    api(project(":core:model"))
    api(project(":core:notifications"))


    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.datetime)
}
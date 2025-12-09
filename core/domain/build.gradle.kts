plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zjy.niademo.core.domain"
}

dependencies {
    api(project(":core:data"))
    api(project(":core:model"))

    implementation(libs.javax.inject)

    testImplementation(project(":core:testing"))
}
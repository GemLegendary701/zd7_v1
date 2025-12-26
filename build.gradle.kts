// Файл: C:\Users\boris\Desktop\rmp\zd7_v1\build.gradle.kts
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.6" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
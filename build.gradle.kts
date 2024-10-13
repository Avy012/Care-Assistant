// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    dependencies {
        // 다른 의존성들...
        classpath ("com.google.gms:google-services:4.3.10") // 버전은 최신으로 변경 가능
    }
}

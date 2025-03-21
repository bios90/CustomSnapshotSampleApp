plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.core_test"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            // exclude allure from robolectric shadowing
            it.ignoreFailures = true
            it.systemProperty("org.robolectric.packagesToNotAcquire", "io.qameta.")
            it.testLogging {
                setEvents(listOf("STARTED", "PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR"))
            }
            it.maxHeapSize = "2g"
        }
    }
}

dependencies {
    //JUnit
    implementation("androidx.test.ext:junit-ktx:1.2.1")
    implementation("androidx.compose.ui:ui-test-junit4-android:1.7.8")
    //Robolectric
    api("org.robolectric:robolectric:4.14.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.8")
    //Allure
    implementation("io.qameta.allure:allure-kotlin-model:2.4.0")
    implementation("io.qameta.allure:allure-kotlin-commons:2.4.0")
    implementation("io.qameta.allure:allure-kotlin-junit4:2.4.0")
    api("io.qameta.allure:allure-kotlin-android:2.4.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.junit)
}

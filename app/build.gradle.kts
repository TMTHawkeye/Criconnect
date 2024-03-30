plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.criconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.criconnect"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //imageslider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    //firebase
    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.firebase:firebase-database:20.3.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.1")



    //graph for bar chart
    implementation("com.github.philjay:MPAndroidChart:v3.0.2")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //image picker for profile pic
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    /*Live Data*/
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    /* ViewModel Dependency*/
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    implementation ("com.google.code.gson:gson:2.10.1")

    /*Koin*/
    implementation ("io.insert-koin:koin-android:3.5.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.github.smarteist:autoimageslider:1.4.0")

    implementation ("io.github.pilgr:paperdb:2.7.2")

    implementation ("com.github.deano2390:MaterialShowcaseView:1.3.7")

}
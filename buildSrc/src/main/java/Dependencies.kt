object ApplicationId {
    const val id = "kg.optimabank.prototype"
    const val dev = "kg.optimabank.prototype.dev"
}

object Releases {
    const val versionCode = 1
    const val versionName = "1.0"
}

object SdkConfigs {
    const val compileSdk = 31
    const val minSdk = 21
    const val targetSdk = 30
}

object Versions {
    const val coreKtx = "1.7.0"
    const val ktx = "1.4.0"
    const val appCompat = "1.4.0"
    const val constraintLayout = "2.1.2"
    const val material = "1.4.0"
    const val multidex = "1.0.3"
    const val navigation = "2.4.0"
    const val lifecycle = "2.4.0"

    const val junit = "4.12"
    const val androidJunit = "1.1.2"
    const val espresso = "3.3.0"

    const val dagger = "2.17"
    const val coroutines = "1.5.2"
    const val retrofit = "2.9.0"
    const val gsonConverter = "2.7.0"
    const val okhttp = "4.9.0"
    const val room = "2.4.1"
    const val dataStore = "1.0.0"
    const val protobuf = "3.11.0"
    const val timber = "5.0.1"
    const val yandexMap = "4.0.0-lite"
    const val rootBeer = "0.1.0"
    const val leakCanary = "2.8.1"
    const val protoBufProtoc = "3.14.0"
}

object AndroidLibraries {
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val activityKtx = "androidx.activity:activity-ktx:${Versions.ktx}"
    const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.ktx}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val multidex = "com.android.support:multidex:${Versions.multidex}"
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val lifecycleCommon = "androidx.lifecycle:lifecycle-common:${Versions.lifecycle}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
}

object TestLibraries {
    const val jUnitRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val jUnit = "junit:junit:${Versions.junit}"
    const val androidJUnit = "androidx.test.ext:junit:${Versions.androidJunit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}

object Libraries {
    // Dagger
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

    // Corountines
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    // Network
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.gsonConverter}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    // Storage
    const val room = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomPaging = "androidx.room:room-paging:${Versions.room}"

    const val dataStore = "androidx.datastore:datastore:${Versions.dataStore}"
    const val dataStoreCore = "androidx.datastore:datastore-core:${Versions.dataStore}"
    const val dataStorePreferences = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    const val dataStorePreferencesCore = "androidx.datastore:datastore-preferences-core:${Versions.dataStore}"
    const val protobuf = "com.google.protobuf:protobuf-javalite:${Versions.protobuf}"

    // Other
    const val rootBeer = "com.scottyab:rootbeer-lib:${Versions.rootBeer}"
    const val yandexMap = "com.yandex.android:maps.mobile:${Versions.yandexMap}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    const val protobufProtoc = "com.google.protobuf:protoc:${Versions.protoBufProtoc}"
}
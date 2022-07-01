package kg.optimabank.prototype.base

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import kg.optimabank.prototype.BuildConfig
import kg.optimabank.prototype.di.component.AppComponent
import kg.optimabank.prototype.di.component.DaggerAppComponent
import kg.optimabank.prototype.di.modules.AppModule
import kg.optimabank.prototype.di.modules.NetworkModule
import kg.optimabank.prototype.di.modules.StorageModule
import timber.log.Timber

class BaseApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("144da177-1d69-4438-a989-9f6548d6f3d0")

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initDagger(this)
    }

    private fun initDagger(application: Application) {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(application))
            .networkModule(NetworkModule())
            .storageModule(StorageModule(application))
            .build()
    }
}
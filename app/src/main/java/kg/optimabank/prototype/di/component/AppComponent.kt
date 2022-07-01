package kg.optimabank.prototype.di.component

import dagger.Component
import kg.optimabank.prototype.di.modules.ApiActionModule
import kg.optimabank.prototype.di.modules.AppModule
import kg.optimabank.prototype.di.modules.NetworkModule
import kg.optimabank.prototype.di.modules.StorageModule
import kg.optimabank.prototype.di.viewmodel.ViewModelModule
import kg.optimabank.prototype.features.auth.createPin.CreatePinFragment
import kg.optimabank.prototype.features.auth.enterPin.EnterPinFragment
import kg.optimabank.prototype.features.auth.login.LoginFragment
import kg.optimabank.prototype.features.auth.touchEnter.TouchBottomSheetDialog
import kg.optimabank.prototype.features.department.details.DepartmentsDetailsFragment
import kg.optimabank.prototype.features.department.list.DepartmentsFragment
import kg.optimabank.prototype.features.main.MainActivity
import kg.optimabank.prototype.features.profile.ProfileFragment
import kg.optimabank.prototype.features.splash.SplashFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        StorageModule::class,
        ViewModelModule::class,
        ApiActionModule::class
    ]
)
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: SplashFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: CreatePinFragment)
    fun inject(fragment: EnterPinFragment)
    fun inject(fragment: TouchBottomSheetDialog)
    fun inject(fragment: DepartmentsFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: DepartmentsDetailsFragment)
}
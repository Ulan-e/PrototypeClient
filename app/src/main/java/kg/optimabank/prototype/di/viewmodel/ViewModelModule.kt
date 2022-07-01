package kg.optimabank.prototype.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kg.optimabank.prototype.features.auth.createPin.CreatePinViewModel
import kg.optimabank.prototype.features.auth.enterPin.EnterPinViewModel
import kg.optimabank.prototype.features.auth.login.LoginViewModel
import kg.optimabank.prototype.features.auth.touchEnter.TouchDialogViewModel
import kg.optimabank.prototype.features.department.details.DepartmentsDetailsViewModel
import kg.optimabank.prototype.features.department.list.DepartmentsViewModel
import kg.optimabank.prototype.features.main.MainViewModel

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EnterPinViewModel::class)
    internal abstract fun enterViewModel(viewModel: EnterPinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreatePinViewModel::class)
    internal abstract fun createViewModel(viewModel: CreatePinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TouchDialogViewModel::class)
    internal abstract fun touchDialogViewModel(viewModel: TouchDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DepartmentsViewModel::class)
    internal abstract fun departmentsViewModel(viewModel: DepartmentsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DepartmentsDetailsViewModel::class)
    internal abstract fun departmentDetailsViewModel(viewModel: DepartmentsDetailsViewModel): ViewModel
}
package kg.optimabank.prototype.di.modules

import dagger.Binds
import dagger.Module
import kg.optimabank.prototype.data.network.repository.AuthAction
import kg.optimabank.prototype.data.network.repository.AuthActionImpl
import kg.optimabank.prototype.data.network.repository.DepartmentsRepository
import kg.optimabank.prototype.data.network.repository.DepartmentsRepositoryImpl

@Module
abstract class ApiActionModule {

    @Binds
    abstract fun provideLoginApiAction(authActionImpl: AuthActionImpl): AuthAction

    @Binds
    abstract fun provideDepartmentsApiAction(departmentsRepositoryImpl: DepartmentsRepositoryImpl): DepartmentsRepository
}
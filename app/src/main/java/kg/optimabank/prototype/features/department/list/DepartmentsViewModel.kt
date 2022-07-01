package kg.optimabank.prototype.features.department.list

import kg.optimabank.prototype.base.BaseViewModel
import kg.optimabank.prototype.data.network.repository.DepartmentsRepository
import kg.optimabank.prototype.data.network.api.resourse.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DepartmentsViewModel @Inject constructor() : BaseViewModel() {

    @Inject
    lateinit var departmentsRepository: DepartmentsRepository

    fun getDepartments() = flow {
        emit(Resource.loading())
        try {
            emit(Resource.success(data = departmentsRepository.getAllDepartments()))
        } catch (exception: Exception) {
            emit(Resource.error(message = exception.localizedMessage ?: "Error occurred"))
        }
    }
}
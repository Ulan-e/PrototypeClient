package kg.optimabank.prototype.data.network.repository

import kg.optimabank.prototype.data.network.api.ServerApi
import kg.optimabank.prototype.features.department.dto.Department
import kg.optimabank.prototype.features.department.dto.DepartmentMapper
import javax.inject.Inject

interface DepartmentsRepository {
    suspend fun getAllDepartments(): List<Department>
}

class DepartmentsRepositoryImpl @Inject constructor(
    private val api: ServerApi
) : DepartmentsRepository {

    override suspend fun getAllDepartments(): List<Department> {
        val mapper = DepartmentMapper()
        val departments = api.getAllDepartments()
        return mapper.dtoToVo(departments)
    }
}
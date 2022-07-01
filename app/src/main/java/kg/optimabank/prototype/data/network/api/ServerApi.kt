package kg.optimabank.prototype.data.network.api

import kg.optimabank.prototype.features.department.dto.DepartmentsListDto
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET

interface ServerApi {

    @GET(ALL_DEPARTMENTS)
    suspend fun getAllDepartments(): DepartmentsListDto

    companion object Factory {

        const val ALL_DEPARTMENTS = "auth-service/users/allDepartments"

        operator fun invoke(retrofit: Retrofit): ServerApi = retrofit.create()
    }
}
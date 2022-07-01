package kg.optimabank.prototype.features.department.dto

import com.google.gson.annotations.SerializedName
import kg.optimabank.prototype.features.department.dto.DepartmentDto

data class DepartmentsListDto(
    @SerializedName("listFnCash")
    val departments: List<DepartmentDto>? = null
)
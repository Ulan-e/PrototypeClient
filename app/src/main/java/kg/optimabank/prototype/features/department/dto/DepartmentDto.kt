package kg.optimabank.prototype.features.department.dto

import com.google.gson.annotations.SerializedName

data class DepartmentDto(
    @SerializedName("level") val level: String?,
    @SerializedName("parentcode") val parentCode: String?,
    @SerializedName("bic") val bic: String?,
    @SerializedName("way4") val way4: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("code") val code: String?
)
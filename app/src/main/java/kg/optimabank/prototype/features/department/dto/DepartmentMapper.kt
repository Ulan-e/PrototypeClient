package kg.optimabank.prototype.features.department.dto

class DepartmentMapper {

    fun dtoToVo(dto: DepartmentsListDto): List<Department> {
        return dto.departments?.map {
            Department(
                level = it.level,
                parentСode = it.parentCode,
                bic = it.bic,
                way4 = it.way4,
                name = it.name,
                code = it.code
            )
        } ?: emptyList()
    }
}
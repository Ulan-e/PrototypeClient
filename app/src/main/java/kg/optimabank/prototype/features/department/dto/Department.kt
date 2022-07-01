package kg.optimabank.prototype.features.department.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Department(
    var level: String?,
    var parentСode: String?,
    var bic: String?,
    var way4: String?,
    var name: String?,
    var code: String?
) : Parcelable
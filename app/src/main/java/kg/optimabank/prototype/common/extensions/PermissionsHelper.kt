package kg.optimabank.prototype.common.extensions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionsHelper {

    fun requestMultiplePermission(fragment: Fragment, action: (msg: String?) -> Unit) =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { permission ->
                when (permission.value) {
                    true -> {
                    }
                    !fragment.shouldShowRequestPermissionRationale(permission.key) -> {
                        action("shouldShowRequestPermissionRationale")
                        return@registerForActivityResult
                    }
                    false -> {
                        action("false")
                        return@registerForActivityResult
                    }
                }
            }
        }

    fun checkMultiplePermissions(fragment: Fragment, resultLauncher: ActivityResultLauncher<Array<String>>): Boolean {
        val notGrantedPermissions = mutableListOf<String>()
        val isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            fragment.requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val isCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isCameraPermissionGranted) {
            notGrantedPermissions.add(Manifest.permission.CAMERA)
        }
        if (!isCoarseLocationPermissionGranted) {
            notGrantedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!isFineLocationPermissionGranted) {
            notGrantedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (notGrantedPermissions.isNotEmpty()) {
            resultLauncher.launch(notGrantedPermissions.toTypedArray())
            return false
        }
        return true
    }

    fun requestCameraPermission(fragment: Fragment, action: (msg: String?) -> Unit) =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> {
                    action("Granted")
                    return@registerForActivityResult
                }
                !fragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    action("shouldShowRequestPermissionRationale")
                    return@registerForActivityResult
                }
                else -> {
                    action(null)
                    return@registerForActivityResult
                }
            }
        }

    fun isGrantCameraPermission(
        fragment: Fragment,
        resultLauncher: ActivityResultLauncher<String>,
        action: (Boolean) -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            action(true)
        } else {
            resultLauncher.launch(Manifest.permission.CAMERA)
            action(false)
        }
    }
}
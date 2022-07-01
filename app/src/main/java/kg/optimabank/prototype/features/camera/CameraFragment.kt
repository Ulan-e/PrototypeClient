package kg.optimabank.prototype.features.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kg.optimabank.prototype.BuildConfig
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.common.extensions.PermissionsHelper
import kg.optimabank.prototype.databinding.FragmentCameraBinding
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {

    private lateinit var fusedLocation: FusedLocationProviderClient
    private val filename = "IMAGE_01.jpg"

    private val permissionHelper = PermissionsHelper.requestMultiplePermission(this) {
        // takePicture.launch()
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        saveImageFile(bitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())

        checkCameraPermission()
        clickTakePicture()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1


        fusedLocation.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocation.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            val location = task.result
            if (location == null) {
                requestLocationData()
            } else {
                Log.d("ulanbek", "latitude ${location?.latitude}  longitude ${location?.longitude}")
                binding.textCurrentLocation.text = "${location?.latitude}  ${location?.longitude}"
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val location = p0?.lastLocation
            Log.d("ulanbek", "latitude ${location?.latitude}  longitude ${location?.longitude}")
            binding.textCurrentLocation.text = "${location?.latitude}  ${location?.longitude}"
        }
    }

    private fun checkCameraPermission() {
        val isGrantPermission = PermissionsHelper.checkMultiplePermissions(this, permissionHelper)
        if (isGrantPermission) {
            // takePicture.launch()
            requestLocationData()
        }
    }

    private fun clickTakePicture() {
        binding.btnTakePicture.setOnClickListener {
            //   getBitmap()
            getLastLocation()
        }
    }

    private fun getBitmap() {
        if (Build.VERSION.SDK_INT < 28) {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, getUri())
            binding.imageViewPhoto.setImageBitmap(bitmap)
        } else {
            val s = ImageDecoder.createSource(requireContext().contentResolver, getUri())
            val b = ImageDecoder.decodeBitmap(s)
            binding.imageViewPhoto.setImageBitmap(b)
        }
    }

    private fun saveImageFile(bitmap: Bitmap?) {
        if (bitmap == null) return
        try {
            val imageFile = File(requireContext().cacheDir, "images/")
            imageFile.mkdirs()
            val outputStream = FileOutputStream("$imageFile/$filename")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            Timber.tag("saveImageFile ${e.localizedMessage}")
        }
    }

    private fun getUri(): Uri {
        val fileAuthority = BuildConfig.APPLICATION_ID + ".provider"
        val imagePath = File(requireContext().cacheDir, "images/")
        val imageFile = File(imagePath, filename)
        return FileProvider.getUriForFile(requireContext(), fileAuthority, imageFile)
    }

    private fun showMessage(message: String?) {
        message?.let { msg ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }
}
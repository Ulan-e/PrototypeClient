package kg.optimabank.prototype.features.department.details

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.common.extensions.PermissionsHelper
import kg.optimabank.prototype.databinding.FragmentDepartmentDetailsBinding

class DepartmentsDetailsFragment : BaseFragment<FragmentDepartmentDetailsBinding>(
    FragmentDepartmentDetailsBinding::inflate
) {

    private val args: DepartmentsDetailsFragmentArgs by navArgs()

    private val permissionHelper = PermissionsHelper.requestMultiplePermission(this) {
        showMessage(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(binding.toolbar, findNavController())
        checkPermission()
        initData()
    }

    private fun initData() {
        val department = args.department
        binding.apply {
            textTitle.text = department.level
            textTitle.text = department.name
        }
    }

    private fun checkPermission() {
        if (PermissionsHelper.checkMultiplePermissions(this, permissionHelper)) {
            showMessage("checkPermission")
        }
    }

    private fun showMessage(message: String?) {
        message?.let { msg ->
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }
    }
}
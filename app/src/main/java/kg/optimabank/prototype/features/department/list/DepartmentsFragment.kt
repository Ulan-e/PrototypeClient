package kg.optimabank.prototype.features.department.list

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.common.extensions.collectWhenStarted
import kg.optimabank.prototype.data.network.api.resourse.Status
import kg.optimabank.prototype.databinding.FragmentDepartmentsBinding
import kg.optimabank.prototype.di.viewmodel.ViewModelFactory
import kg.optimabank.prototype.features.department.dto.Department
import kg.optimabank.prototype.features.department.list.adapter.DepartmentsAdapter
import timber.log.Timber
import javax.inject.Inject

class DepartmentsFragment : BaseFragment<FragmentDepartmentsBinding>(FragmentDepartmentsBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: DepartmentsViewModel

    private val departmentsAdapter: DepartmentsAdapter by lazy {
        DepartmentsAdapter(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[DepartmentsViewModel::class.java]

        viewModel.getDepartments().collectWhenStarted(lifecycleScope) { response ->
            when (response.status) {
                Status.LOADING -> showLoading(isVisible = true)
                Status.SUCCESS -> doOnSuccess(response.data)
                Status.ERROR -> doOnError(response.message)
            }
        }
    }

    private fun doOnSuccess(departments: List<Department>?) {
        if (departments == null) return

        showLoading(false)
        departmentsAdapter.apply {
            setData(departments)
            setClickAction { openDepartmentDetails(it) }
            binding.departmentList.adapter = departmentsAdapter
        }
    }

    private fun openDepartmentDetails(department: Department) {
        val action = DepartmentsFragmentDirections.actionItemDepartmentsToDepartmentsDetails(department)
        findNavController().navigate(action)
    }

    private fun doOnError(errorMessage: String?) {
        showLoading(false)
        errorMessage?.let { toast(it) }
        Timber.d(errorMessage)
    }

    private fun showLoading(isVisible: Boolean) {
        binding.progressBar.isVisible = isVisible
    }
}
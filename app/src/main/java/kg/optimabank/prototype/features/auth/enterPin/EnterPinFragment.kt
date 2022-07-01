package kg.optimabank.prototype.features.auth.enterPin

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kg.optimabank.prototype.R
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.common.dialogs.ProgressLoadingDialog
import kg.optimabank.prototype.common.extensions.collectWhenStarted
import kg.optimabank.prototype.common.extensions.formatCount
import kg.optimabank.prototype.data.network.api.resourse.Resource
import kg.optimabank.prototype.data.network.api.resourse.Status
import kg.optimabank.prototype.databinding.BottomsheetDialogFingerprintBinding
import kg.optimabank.prototype.databinding.FragmentPinBinding
import kg.optimabank.prototype.di.viewmodel.ViewModelFactory
import kg.optimabank.prototype.features.auth.data.AuthInfo
import kg.optimabank.prototype.features.auth.touchEnter.TouchBottomSheetDialog
import timber.log.Timber
import javax.inject.Inject

class EnterPinFragment : BaseFragment<FragmentPinBinding>(FragmentPinBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val progressDialog = ProgressLoadingDialog.getInstance()

    private lateinit var viewModel: EnterPinViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        binding.titlePinCode.text = getString(R.string.text_input_pin_code)

        viewModel = ViewModelProvider(this, viewModelFactory)[EnterPinViewModel::class.java]

        setupPinInputView()
        setupBtnCantEnter()

        viewModel.successLogin.collectWhenStarted(lifecycleScope) { isSuccessLogin ->
            if (isSuccessLogin) doLogin()
        }

        viewModel.entryType.collectWhenStarted(lifecycleScope) { entryType ->
            when (entryType) {
                EntryType.PIN -> {
                }
                EntryType.TOUCH -> {
                }
            }
        }

        viewModel.pinAttempts.collectWhenStarted(lifecycleScope) { attempts ->
            if (attempts == 0) {
                binding.inputPinCode.blockEnterPin()
                viewModel.logout()
                showLoginScreen()
                return@collectWhenStarted
            }
            if (attempts == 3) {
                return@collectWhenStarted
            }

            binding.apply {
                titlePinCode.text = getString(R.string.text_wrong_pin_code)
                textWrongPin.isVisible = true
                textWrongPin.text = attempts.formatCount(
                    requireContext(),
                    R.string.text_error_count_1,
                    R.string.text_error_count_2,
                    R.string.text_error_count_3
                )
                inputPinCode.cleanDigits()
            }
        }

        viewModel.dialogMessage.collectWhenStarted(lifecycleScope) { dialogError ->
            showAlertDialog(dialogError) {
                // viewModel.authenticate()
            }
        }

        viewModel.authError.collectWhenStarted(lifecycleScope) { dialogError ->
            showAlertDialog(
                error = dialogError,
                okay = {
                    //viewModel.authenticate()
                },
                cancel = {
                    viewModel.logout()
                    showLoginScreen()
                })
        }

        viewModel.loadingState.collectWhenStarted(lifecycleScope) { isLoading ->
            if (isLoading) {
                progressDialog.show(parentFragmentManager, ProgressLoadingDialog::class.simpleName)
            }
        }

        viewModel.isTouchBtnVisible().collectWhenStarted(lifecycleScope) { isTouchVisible ->
            binding.inputPinCode.toggleTouchVisibility(isTouchVisible)
        }
    }

    private fun doLogin() {
        viewModel.doLogin().collectWhenStarted(lifecycleScope) { resource ->
            when (resource.status) {
                Status.LOADING -> doOnLoading()
                Status.SUCCESS -> doOnSuccess(resource)
                Status.ERROR -> doOnError(resource)
            }
        }
    }

    private fun doOnLoading() {
        progressDialog.show(
            parentFragmentManager,
            BottomsheetDialogFingerprintBinding::class.simpleName
        )
    }

    private fun doOnSuccess(resource: Resource<AuthInfo>) {
        progressDialog.dismiss()
        viewModel.saveUserData(resource.data)
        showHomeScreen()
        Timber.d(resource.data.toString())
    }

    private fun doOnError(resource: Resource<AuthInfo>) {
        progressDialog.dismiss()
        snackBar(binding.root, resource.message ?: "Ошибка при авторизации")
        Timber.d(resource.message)
    }

    private fun setupPinInputView() {
        binding.inputPinCode.apply {
            onPinComplete = {
                onPinEntered(it)
            }
            onPinChange = {}
            setOnTouchClick {
                showTouchEnterDialog()
            }
        }
    }

    private fun setupBtnCantEnter() {
        binding.btnCantEnter.setOnClickListener {
            showLoginScreen()
        }
    }

    private fun onPinEntered(pin: String) {
        lifecycleScope.launchWhenStarted {
            viewModel.checkPin(pin)
        }
    }

    private fun showHomeScreen() {
        val actionToHomeScreen = EnterPinFragmentDirections.actionEnterPinToItemDepartments()
        findNavController().navigate(actionToHomeScreen)
    }

    private fun showLoginScreen() {
        val actionToLoginFragment = EnterPinFragmentDirections.actionEnterPinToLoginFragment()
        findNavController().navigate(actionToLoginFragment)
    }

    private fun showTouchEnterDialog() {
        val dialog = TouchBottomSheetDialog.Builder()
            .title(requireActivity().resources.getString(R.string.text_enter_to_app))
            .dialogType(TouchBottomSheetDialog.DialogType.ENTER)
            .build()
        dialog.isCancelable = false
        dialog.show(parentFragmentManager, TouchBottomSheetDialog::class.simpleName)
    }
}
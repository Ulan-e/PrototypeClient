package kg.optimabank.prototype.features.auth.createPin

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
import kg.optimabank.prototype.common.extensions.collectWhenStarted
import kg.optimabank.prototype.common.extensions.formatCount
import kg.optimabank.prototype.databinding.FragmentPinBinding
import kg.optimabank.prototype.di.viewmodel.ViewModelFactory
import kg.optimabank.prototype.features.auth.touchEnter.TouchBottomSheetDialog
import javax.inject.Inject

class CreatePinFragment : BaseFragment<FragmentPinBinding>(FragmentPinBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CreatePinViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel = ViewModelProvider(this, viewModelFactory)[CreatePinViewModel::class.java]

        setupPinInputView()

        viewModel.isTouchBtnVisible().collectWhenStarted(lifecycleScope) { isTouchVisible ->
            binding.inputPinCode.toggleTouchVisibility(isTouchVisible)
        }

        viewModel.repeatPin.collectWhenStarted(lifecycleScope) { createPinType ->
            when (createPinType) {
                CreatePinType.START -> {
                    binding.apply {
                        titlePinCode.text = getString(R.string.text_create_pin_code)
                        inputPinCode.cleanDigits()
                    }
                }
                CreatePinType.CONFIRM -> {
                    binding.apply {
                        titlePinCode.text = getString(R.string.text_create_pin_code_repeat)
                        inputPinCode.cleanDigits()
                    }
                }
                CreatePinType.WRONG -> {
                    showLoginScreen()
                }
                CreatePinType.SUCCESS -> {
                    showHomeScreen()
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
    }

    private fun setupPinInputView() {
        binding.btnCantEnter.visibility = View.INVISIBLE
        binding.inputPinCode.apply {
            onPinComplete = { pin ->
                viewModel.setPin(pin)
            }

            onPinChange = {}

            setOnTouchClick {
                showTouchDialog()
            }
        }
    }

    private fun showHomeScreen() {
        val actionToHomeFragment = CreatePinFragmentDirections.actionCreatePinToItemDepartments()
        findNavController().navigate(actionToHomeFragment)
    }

    private fun showLoginScreen() {
        val actionToLoginFragment = CreatePinFragmentDirections.actionCreatePinToLoginFragment()
        findNavController().navigate(actionToLoginFragment)
    }

    private fun showTouchDialog() {
        val dialog = TouchBottomSheetDialog.Builder()
            .title(requireActivity().resources.getString(R.string.text_enter_to_app))
            .dialogType(TouchBottomSheetDialog.DialogType.CREATE)
            .build()
        dialog.isCancelable = false
        dialog.show(parentFragmentManager, TouchBottomSheetDialog::class.simpleName)
    }
}
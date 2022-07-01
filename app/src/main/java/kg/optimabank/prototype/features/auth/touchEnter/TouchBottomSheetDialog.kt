package kg.optimabank.prototype.features.auth.touchEnter

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kg.optimabank.prototype.R
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.common.extensions.collectWhenStarted
import kg.optimabank.prototype.data.network.api.resourse.Resource
import kg.optimabank.prototype.data.network.api.resourse.Status
import kg.optimabank.prototype.databinding.BottomsheetDialogFingerprintBinding
import kg.optimabank.prototype.di.viewmodel.ViewModelFactory
import kg.optimabank.prototype.features.auth.data.AuthInfo
import timber.log.Timber
import javax.inject.Inject

class TouchBottomSheetDialog private constructor(
    private var title: String?,
    private var dialogType: DialogType?
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: BottomsheetDialogFingerprintBinding

    private lateinit var viewModel: TouchDialogViewModel

    private constructor(builder: Builder) : this(
        builder.title,
        builder.dialogType
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Custom_BottomSheetDialog_Style)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startWaitTouch()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetDialogFingerprintBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }

        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[TouchDialogViewModel::class.java]

        binding.apply {
            dialogTitle.text = title

            when (dialogType) {
                DialogType.CREATE -> {
                    btnUsePinCode.isVisible = false
                    switchLoginTouch.isVisible = true
                }
                DialogType.ENTER -> {
                    btnUsePinCode.isVisible = true
                    switchLoginTouch.isVisible = false
                }
                else -> {
                    btnUsePinCode.isVisible = false
                    switchLoginTouch.isVisible = false
                }
            }
            btnUsePinCode.setOnClickListener { dismiss() }

            switchLoginTouch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.useTouchId(isChecked)
            }

        }

        viewModel.successEnter.collectWhenStarted(lifecycleScope) { isSuccess ->
            if (isSuccess) login()
        }

        viewModel.touchType.collectWhenStarted(lifecycleScope) { touchState ->
            if (activity == null) return@collectWhenStarted

            when (touchState) {
                TouchType.NORMAL -> {
                    Timber.i(TouchType.NORMAL.name)
                }
                TouchType.WRONG_FINGER -> {
                    Timber.i(TouchType.WRONG_FINGER.name)
                    binding.textFingerprintDescription.apply {
                        isEnabled = false
                        text = getString(R.string.text_wrong_fingerprint)
                    }
                }
                TouchType.EMPTY_ATTEMPT -> {
                    Timber.i(TouchType.EMPTY_ATTEMPT.name)
                    dismiss()
                }
                else -> {
                    Timber.i(TouchType.CLOSED.name)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopWaitFingerprint()
    }

    private fun login() {
        viewModel.doLogin().collectWhenStarted(lifecycleScope) {
            it.let { response ->
                when (response.status) {
                    Status.LOADING -> doOnLoading()
                    Status.SUCCESS -> doOnSuccess(response)
                    Status.ERROR -> donOnError(response)
                }
            }
        }
    }

    private fun doOnLoading() {
        binding.progressBar.isVisible = true
    }

    private fun doOnSuccess(response: Resource<AuthInfo>) {
        binding.progressBar.isVisible = false
        viewModel.saveUserData(response.data)
        dismiss()
        findNavController().navigate(R.id.item_departments)
        Timber.d(response.data.toString())
    }

    private fun donOnError(response: Resource<AuthInfo>) {
        binding.progressBar.isVisible = false
        Toast.makeText(requireContext(), "Ошибка при авторизации", Toast.LENGTH_SHORT).show()
        dismiss()
        Timber.d(response.message)
    }

    class Builder {
        var title: String? = null
            private set

        fun title(title: String) = apply { this.title = title }

        var dialogType: DialogType? = null
            private set

        fun dialogType(dialogType: DialogType?) =
            apply { this.dialogType = dialogType }

        fun build() = TouchBottomSheetDialog(this)
    }

    enum class DialogType {
        ENTER,
        CREATE
    }
}
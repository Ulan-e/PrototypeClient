package kg.optimabank.prototype.features.auth.login

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.common.extensions.collectWhenStarted
import kg.optimabank.prototype.data.network.api.resourse.Resource
import kg.optimabank.prototype.data.network.api.resourse.Status
import kg.optimabank.prototype.data.preferences.SessionPreferences
import kg.optimabank.prototype.databinding.FragmentLoginBinding
import kg.optimabank.prototype.di.viewmodel.ViewModelFactory
import kg.optimabank.prototype.features.auth.data.AuthInfo
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    companion object {
        const val CLIENT_ID = "crm-app" // временная константа
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionPreferences: SessionPreferences

    private lateinit var viewModel: LoginViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        binding.btnLogin.changeEnableBtn(false)

        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        setupLoginBtn()
        setupRegistrationBtn()

        checkInputLogin()
        checkInputPassword()
    }

    private fun setupLoginBtn() {
        binding.btnLogin.apply {
            setActionClickListener {
                toggleLoader(isLoading = true)
                doLogin()
            }
        }
    }

    private fun doLogin() {
        val username = binding.inputLogin.text.toString()
        val password = binding.inputPassword.text.toString()

        viewModel.doLogin(CLIENT_ID, username, password)
            .collectWhenStarted(lifecycleScope) { response ->
                when (response.status) {
                    Status.LOADING -> doOnLoading()
                    Status.SUCCESS -> doOnSuccessAuth(username, password, response)
                    Status.ERROR -> doOnErrorAuth(response)
                }
            }
    }

    private fun doOnLoading() {
        binding.btnLogin.toggleLoader(true)
        Timber.d("Loading...")
    }

    private fun doOnSuccessAuth(username: String, password: String, response: Resource<AuthInfo>) {
        binding.btnLogin.toggleLoader(isLoading = false)

        viewModel.saveUserData(
            username = username,
            password = password,
            clientId = CLIENT_ID,
            accessToken = response.data?.accessToken,
            refreshToken = response.data?.refreshToken
        )

        showCreatePinFragment()
        Timber.d(response.data.toString())
    }

    private fun doOnErrorAuth(response: Resource<AuthInfo>) {
        binding.btnLogin.toggleLoader(isLoading = false)
        snackBar(binding.root, response.message ?: "Ошибка при авторизации")
        Timber.d(response.message)
    }

    private fun checkInputLogin() {
        binding.inputLogin.doAfterTextChanged { editable ->
            editable?.let { inputLogin ->
                setEnableBtnLogin(inputLogin, binding.inputPassword.text)
            }
        }
    }

    private fun checkInputPassword() {
        binding.inputPassword.doAfterTextChanged { editable ->
            editable?.let { inputPassword ->
                setEnableBtnLogin(inputPassword, binding.inputLogin.text)
            }
        }
    }

    private fun setEnableBtnLogin(inputLogin: Editable, inputPassword: Editable) {
        val isBtnLoginEnabled = inputLogin.isNotEmpty() && inputPassword.isNotEmpty()
        binding.btnLogin.changeEnableBtn(isBtnLoginEnabled)
    }

    private fun setupRegistrationBtn() {
        binding.btnRegistration.setOnClickListener {
            showTempScreen()
            // Toast.makeText(requireContext(), getDeviceOld(), Toast.LENGTH_LONG).show()
        }
    }

    private fun showCreatePinFragment() {
        val actionToCreatePinFragment = LoginFragmentDirections.actionLoginToCreatePinFragment()
        findNavController().navigate(actionToCreatePinFragment)
    }

    private fun showTempScreen() {
        val actionToHomeFragment = LoginFragmentDirections.actionCreatePinToItemDepartments()
        findNavController().navigate(actionToHomeFragment)
    }

    private fun getDeviceOld(): String {
        return Build.MANUFACTURER + " " + Build.PRODUCT
    }
}
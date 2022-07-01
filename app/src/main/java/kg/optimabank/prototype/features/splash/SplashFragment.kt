package kg.optimabank.prototype.features.splash

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.common.extensions.collectWhenStarted
import kg.optimabank.prototype.data.preferences.LoginPreferences
import kg.optimabank.prototype.features.main.StartScreen
import kg.optimabank.prototype.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    companion object {
        const val SMOOTH_NAV_DELAY = 500L
    }

    @Inject
    lateinit var loginPreferences: LoginPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginPreferences.getStartScreen().collectWhenStarted(lifecycleScope) { screen ->
            when (screen) {
                StartScreen.LOGIN -> openLoginFragment()
                StartScreen.ENTER_PIN -> openEnterPinCodeFragment()
            }
        }
    }

    private fun openEnterPinCodeFragment() {
        lifecycleScope.launch {
            delay(SMOOTH_NAV_DELAY)
            val actionToEnterPinFragment = SplashFragmentDirections.actionSplashToEnterPinFragment()
            findNavController().navigate(actionToEnterPinFragment)
        }
    }

    private fun openLoginFragment() {
        lifecycleScope.launch {
            delay(SMOOTH_NAV_DELAY)
            val actionToLoginFragment = SplashFragmentDirections.actionSplashToLoginFragment()
            findNavController().navigate(actionToLoginFragment)
        }
    }
}

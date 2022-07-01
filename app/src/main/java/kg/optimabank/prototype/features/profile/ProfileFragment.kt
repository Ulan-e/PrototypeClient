package kg.optimabank.prototype.features.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.base.BaseFragment
import kg.optimabank.prototype.data.preferences.SessionPreferences
import kg.optimabank.prototype.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    @Inject
    lateinit var sessionPreferences: SessionPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserData()
    }

    private fun getUserData() {
        lifecycleScope.launchWhenStarted {
            val userSettings = sessionPreferences.getUserPreferences().first()
            binding.textFullName.text = userSettings?.username ?: "so1 so1"
        }
    }
}
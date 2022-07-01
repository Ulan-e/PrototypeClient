package kg.optimabank.prototype.features.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.scottyab.rootbeer.BuildConfig
import kg.optimabank.prototype.R
import kg.optimabank.prototype.base.BaseApplication
import kg.optimabank.prototype.common.extensions.AppSafety
import kg.optimabank.prototype.databinding.ActivityMainBinding
import kg.optimabank.prototype.di.viewmodel.ViewModelFactory
import javax.inject.Inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private val appSafety: AppSafety by lazy {
        AppSafety(applicationContext)
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkRoot()

        // launchSafetyChecker()

        (applicationContext as BaseApplication).appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        binding.mainBottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.item_departments -> binding.mainBottomNavigation.isVisible = true
                R.id.item_map -> binding.mainBottomNavigation.isVisible = true
                R.id.item_email -> binding.mainBottomNavigation.isVisible = true
                else -> binding.mainBottomNavigation.isVisible = false
            }
        }
    }

    private fun launchSafetyChecker() {
        // if (BuildConfig.DEBUG) {
        // checkEmulator()
        //checkRoot()
        // checkAppFromStore()
        //  }
    }

    private fun checkAppFromStore() {
        if (appSafety.isPlayStoreVersion() == true) {
            showAlertDialog(getString(R.string.text_app_is_not_from_store))
        }
    }

    private fun checkRoot() {
        if (appSafety.isDeviceRooted()) {
            showAlertDialog(getString(R.string.text_device_is_rooted))
        }
    }

    private fun checkEmulator() {
        if (appSafety.isRunningOnEmulator()) {
            showAlertDialog(getString(R.string.text_app_is_running_on_emulator))
        }
    }

    private fun showAlertDialog(text: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(text)
            .setPositiveButton(R.string.text_okay) { dialog, _ ->
                dialog.dismiss()
                finish()
                exitProcess(0)
            }
            .setCancelable(false)
            .create()
        alertDialog?.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }
}
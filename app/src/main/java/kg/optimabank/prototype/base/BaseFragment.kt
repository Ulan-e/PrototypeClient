package kg.optimabank.prototype.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import kg.optimabank.prototype.R
import kg.optimabank.prototype.common.dialogs.DialogError

typealias Inflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: Inflater<VB>
) : Fragment() {

    private var alertDialog: AlertDialog? = null

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun snackBar(rootView: View, message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showAlertDialog(error: DialogError, action: (() -> Unit)? = null) {
        alertDialog?.dismiss()

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(error.title)
            .setMessage(error.message)
            .setPositiveButton(R.string.text_okay) { dialog, _ ->
                dialog.dismiss()
                action?.invoke()
            }
            .create()
        alertDialog?.show()
    }

    fun showAlertDialog(error: DialogError, okay: (() -> Unit)?, cancel: (() -> Unit)? = null) {
        alertDialog?.dismiss()

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(error.title)
            .setMessage(error.message)
            .setPositiveButton(R.string.text_okay) { dialog, _ ->
                dialog.dismiss()
                okay?.invoke()
            }
            .setNegativeButton(R.string.text_cancel) { dialog, _ ->
                dialog.dismiss()
                cancel?.invoke()
            }
            .create()
        alertDialog?.show()
    }

    private val backPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }

    private fun onBackPressed() {

    }
}
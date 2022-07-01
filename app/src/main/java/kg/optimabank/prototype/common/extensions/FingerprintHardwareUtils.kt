package kg.optimabank.prototype.common.extensions

import android.content.Context
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal

class FingerprintHardwareUtils(private val context: Context) {

    private val fm: FingerprintManagerCompat = FingerprintManagerCompat.from(context)
    private var fingerprintCancellation = CancellationSignal()

    fun isTouchAvailable(): Boolean {
        return fm.isHardwareDetected && fm.hasEnrolledFingerprints()
    }

    fun startListenTouch(
        onTouchSuccess: () -> Unit,
        onTouchFailed: () -> Unit,
        onTouchError: () -> Unit
    ) {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        fingerprintCancellation = CancellationSignal()
        fingerprintManager.authenticate(
            null,
            0,
            fingerprintCancellation,
            FingerprintAuthListener(onTouchSuccess, onTouchFailed, onTouchError),
            null
        )
    }

    fun stopListenTouch() {
        fingerprintCancellation.cancel()
    }

    private inner class FingerprintAuthListener(
        val onTouchSuccess: () -> Unit,
        val onTouchFailed: () -> Unit,
        val onTouchError: () -> Unit
    ) : FingerprintManagerCompat.AuthenticationCallback() {
        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
            super.onAuthenticationError(errMsgId, errString)
            onTouchError()
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            onTouchSuccess()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onTouchFailed()
        }
    }
}
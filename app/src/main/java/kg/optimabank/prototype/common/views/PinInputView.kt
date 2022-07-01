package kg.optimabank.prototype.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import kg.optimabank.prototype.R

class PinInputView : FrameLayout {

    constructor(ctx: Context) : this(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet? = null) : this(ctx, attrs, 0)

    private val root: View
    private var pin: String = ""

    private var imageDigitOne: ImageView
    private var imageDigitTwo: ImageView
    private var imageDigitThree: ImageView
    private var imageDigitFour: ImageView

    private var btnClear: ImageView
    private var btnFingerprint: ImageView

    private var btnZero: AppCompatButton
    private var btnOne: AppCompatButton
    private var btnTwo: AppCompatButton
    private var btnThree: AppCompatButton
    private var btnFour: AppCompatButton
    private var btnFive: AppCompatButton
    private var btnSix: AppCompatButton
    private var btnSeven: AppCompatButton
    private var btnEight: AppCompatButton
    private var btnNine: AppCompatButton

    var onPinComplete: ((String) -> Unit)? = null
    var onPinChange: (() -> Unit)? = null

    private val pinMaxLength = 4

    private val pinBtns = arrayListOf<AppCompatButton>()

    @SuppressLint("CutPasteId")
    constructor(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        ctx,
        attrs,
        defStyleAttr
    ) {
        root = inflate(ctx, R.layout.view_pin_input, this)

        imageDigitOne = findViewById(R.id.digit_1)
        imageDigitTwo = findViewById(R.id.digit_2)
        imageDigitThree = findViewById(R.id.digit_3)
        imageDigitFour = findViewById(R.id.digit_4)

        btnClear = findViewById(R.id.btn_clear)
        btnFingerprint = findViewById(R.id.btn_fingerprint)

        btnZero = findViewById(R.id.zero_button)
        btnOne = findViewById(R.id.one_button)
        btnTwo = findViewById(R.id.two_button)
        btnThree = findViewById(R.id.three_button)
        btnFour = findViewById(R.id.four_button)
        btnFive = findViewById(R.id.five_button)
        btnSix = findViewById(R.id.six_button)
        btnSeven = findViewById(R.id.seven_button)
        btnEight = findViewById(R.id.eight_button)
        btnNine = findViewById(R.id.nine_button)

        pinBtns.addAll(
            arrayListOf(
                btnOne,
                btnTwo,
                btnThree,
                btnFour,
                btnFive,
                btnSix,
                btnSeven,
                btnEight,
                btnNine,
                btnZero
            )
        )

        setupDigitListeners()


        btnClear.setOnClickListener {
            if (pin.isNotEmpty()) {
                pin = pin.removeRange(pin.length - 1, pin.length)
            }

            onPinChange?.invoke()
            updateCleanButton()
            updateIndicator()
        }
    }

    private fun updateCleanButton() {
        if (pin.isNotEmpty()) {
            ImageViewCompat.setImageTintList(
                btnClear, ColorStateList.valueOf(ContextCompat.getColor(btnClear.context, R.color.red))
            )
        } else ImageViewCompat.setImageTintList(btnClear, null)
    }

    private fun updateIndicator() {
        toggleIndicator(imageDigitOne, pin.length >= 1)
        toggleIndicator(imageDigitTwo, pin.length >= 2)
        toggleIndicator(imageDigitThree, pin.length >= 3)
        toggleIndicator(imageDigitFour, pin.length >= 4)
    }

    fun toggleTouchVisibility(isVisible: Boolean) {
        btnFingerprint.isVisible = isVisible
    }

    fun setOnTouchClick(onClickListener: OnClickListener) {
        btnFingerprint.setOnClickListener(onClickListener)
    }

    private fun setupDigitListeners() {
        val onDigitClick = OnClickListener {

            if (pin.length < pinMaxLength) {
                val digit = when (it.id) {
                    R.id.one_button -> "1"
                    R.id.two_button -> "2"
                    R.id.three_button -> "3"
                    R.id.four_button -> "4"
                    R.id.five_button -> "5"
                    R.id.six_button -> "6"
                    R.id.seven_button -> "7"
                    R.id.eight_button -> "8"
                    R.id.nine_button -> "9"
                    R.id.zero_button -> "0"
                    else -> ""
                }

                pin += digit

                updateIndicator()
                updateCleanButton()
            }

            onPinChange?.invoke()

            if (pin.length == pinMaxLength) {
                clearDigitListeners()
                onPinComplete?.invoke(pin)
            }
        }

        pinBtns.forEach { it.setOnClickListener(onDigitClick) }
    }

    private fun clearDigitListeners() {
        pinBtns.forEach { it.setOnClickListener(null) }
    }

    fun cleanDigits() {
        pin = ""
        updateIndicator()
        updateCleanButton()
        setupDigitListeners()
    }

    fun blockEnterPin() {
        pinBtns.forEach { it.setOnClickListener(null) }
        btnClear.setOnClickListener(null)
    }

    private fun toggleIndicator(indicator: ImageView, isEnabled: Boolean) {
        val res = if (isEnabled)
            R.drawable.bg_checked_pin_indicator
        else R.drawable.bg_unchecked_pin_indicator

        indicator.setImageDrawable(ContextCompat.getDrawable(context, res))
    }
}
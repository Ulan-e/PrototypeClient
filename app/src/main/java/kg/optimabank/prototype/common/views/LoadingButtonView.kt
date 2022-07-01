package kg.optimabank.prototype.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import kg.optimabank.prototype.R

class LoadingButtonView : FrameLayout {

    private val view: View
    private val content: FrameLayout
    private var button: AppCompatButton
    private var progress: ProgressBar
    private var textView: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context, attrs,
        defStyleAttr
    ) {
        view = inflate(context, R.layout.view_loading_button, this)

        content = findViewById(R.id.content_button_login)
        button = findViewById(R.id.btn_action)
        progress = findViewById(R.id.progress_bar)
        textView = findViewById(R.id.btn_text)
    }

    fun setActionClickListener(onClickListener: OnClickListener) {
        button.setOnClickListener(onClickListener)
    }

    fun changeEnableBtn(isEnabled: Boolean) {
        button.isEnabled = isEnabled
    }

    fun toggleLoader(isLoading: Boolean) {
        progress.isVisible = isLoading
        textView.isVisible = !isLoading
        button.isEnabled = !isLoading
    }
}
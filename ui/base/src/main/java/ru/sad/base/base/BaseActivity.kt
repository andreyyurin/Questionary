package ru.sad.base.base

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    abstract fun showError(errorText: String?)

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val config = Configuration(newBase!!.resources.configuration)
        config.fontScale = 1.0f
        applyOverrideConfiguration(config)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        return super.onCreateView(name, context, attrs)
    }

    abstract fun bottomNavigation(rule: Boolean)
}
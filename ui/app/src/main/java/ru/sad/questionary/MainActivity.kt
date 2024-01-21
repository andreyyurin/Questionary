package ru.sad.questionary

import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseActivity
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.base.BaseFragment
import ru.sad.base.base.BaseRouterImpl
import ru.sad.base.error.ErrorResult
import ru.sad.base.ext.currentNavigationFragment
import ru.sad.base.ext.toPx
import ru.sad.base.navigation.NavigationKey
import ru.sad.base.views.BottomBarView
import ru.sad.data.prefs.AuthPref
import ru.sad.questionary.databinding.ActivityMainBinding
import java.lang.RuntimeException

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    companion object {
        private val navigationScreenKeys = listOf(
            NavigationKey.SUBSCRIPTION_SCREEN,
            NavigationKey.TOP_QUIZ_SCREEN,
            NavigationKey.PROFILE_SCREEN
        )

        private const val USER_ID = "USER_ID"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val activityViewModel: MainActivityViewModel by viewModels()

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding

    private lateinit var appRouter: BaseRouterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindCreate()
        observeNavigation()
        observeErrors()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragment = supportFragmentManager.currentNavigationFragment

        (fragment as BaseFragment<*>).onFragmentResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val fragment = supportFragmentManager.currentNavigationFragment

        try {
            (fragment as BaseFragment<*>).onFragmentRequestPermissionsResult(
                requestCode,
                permissions.toList(),
                grantResults
            )
        } catch (e: RuntimeException) {

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun bottomNavigation(rule: Boolean) {
        if (!this::binding.isInitialized) return

        when (rule) {
            true -> binding.bottomBar.show()
            else -> binding.bottomBar.hide()
        }
    }

    override fun showError(errorText: String?) {
        var textError = errorText ?: getString(ru.sad.base.R.string.error_unknown)
        if (textError.length >= 100) textError = getString(ru.sad.base.R.string.error_unknown)

        val snack = Snackbar.make(binding.root, textError, Snackbar.LENGTH_SHORT)

        snack.view.layoutParams.apply {
            (this as FrameLayout.LayoutParams).gravity = Gravity.TOP
        }

        snack.setTextColor(getColor(ru.sad.base.R.color.error_text_color))
        snack.setBackgroundTint(getColor(ru.sad.base.R.color.error_background_color))
        snack.setTextMaxLines(10)
        snack.show()
    }

    private fun bindCreate() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createBottomBar()

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        appRouter = AppRouter(navController, ::openDialog)
    }

    private fun observeNavigation() {
        activityViewModel.navigationListener.observe(this) {
            if (it.key in navigationScreenKeys) {
                val indexKey = navigationScreenKeys.indexOf(it.key)
                binding.bottomBar.updateColor(indexKey)
            }
            appRouter.navigate(it.key, it.bundles)
        }
    }

    private fun observeErrors() {
        ErrorResult.observe(this) {
            showError(it)
        }
    }

    private fun createBottomBar() {
        with(binding) {
            bottomBar.isGone = true

            bottomBar.addItem(R.drawable.ic_home) {
                activityViewModel.navigate(NavigationKey.SUBSCRIPTION_SCREEN)
            }

            bottomBar.addItem(R.drawable.ic_main) {
                activityViewModel.navigate(NavigationKey.TOP_QUIZ_SCREEN)
            }

            bottomBar.addItem(R.drawable.ic_profile) {
                activityViewModel.navigate(NavigationKey.PROFILE_SCREEN, bundles = bundleOf(
                    USER_ID to AuthPref.userId
                ))
            }
        }
    }

    private fun openDialog(
        destination: BaseBottomSheetDialogFragment<*>
    ) {
        val tag = destination.tagDialog
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            destination.show(supportFragmentManager, tag)
        }
    }
}
package ru.sad.login

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.getSpanned
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.login.databinding.FragmentLoginBinding
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    companion object {
        private const val NAME_REGEX = "^[a-zA-Z0-9]+$"
    }

    @Inject
    lateinit var oneTapClient: SignInClient

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var request: BeginSignInRequest

    override val bindingInflater: (LayoutInflater) -> FragmentLoginBinding =
        FragmentLoginBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        bindGoogleSignInParams()
        bindListeners()
        observeData()
        setupTitle()
        setupWindow()
        setupNameFilter()
    }

    override fun onDestroyView() {
        removeWindowInsets()
        super.onDestroyView()
    }

    private fun removeWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root.rootView) { _, i -> i }
    }

    private fun setupWindow() {
        activity?.let {
            binding.mainLayout.transitionToEnd {
                bindKeyboardListeners()
            }
        }
    }

    private fun bindKeyboardListeners() {
        var startMarginButton = 0

        binding.mainLayout.getConstraintSet(R.id.endAnimEnter)?.let { anim ->
            anim.getConstraint(R.id.btnSave).apply {
                startMarginButton = this.layout.bottomMargin
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root.rootView) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            updateButtonConstraint(imeVisible, imeHeight + 10, startMarginButton)
            insets
        }
    }

    private fun updateButtonConstraint(isKeyBoardShown: Boolean, margin: Int, startMargin: Int) {
        binding.btnSave.layoutParams = binding.btnSave.layoutParams.apply {
            val params = (this as ConstraintLayout.LayoutParams)
            bottomToBottom = R.id.etName
        }

        binding.mainLayout.getConstraintSet(R.id.endAnimEnter)?.let { anim ->
            anim.getConstraint(R.id.btnSave).apply {
                this.layout.bottomMargin = if (isKeyBoardShown) margin else startMargin
            }
        }
    }

    private fun setupTitle() {
        val firstColor = resources.getColor(ru.sad.base.R.color.main_text_color)
        val firstPart = getString(R.string.login_title).getSpanned(firstColor.toString())

        val secondColor = resources.getColor(ru.sad.base.R.color.main_second_color)
        val secondPart = getString(ru.sad.base.R.string.app_name).getSpanned(secondColor.toString())

        binding.tvLoginTitle.text = Html.fromHtml("$firstPart $secondPart")
    }

    private fun bindGoogleSignInParams() {
        request = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(resources.getString(ru.sad.base.R.string.google_client_id))
                    .build()
            )
            .build()

    }

    private fun observeData() {
        viewModel.loginProgress.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    removeWindowInsets()
                    viewModel.openMainScreen()
                }
                onError {
                    showError(this.toString())
                    hideLoading()
                }
            }
        }
    }

    private fun bindListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.login(binding.etName.text.toString(), binding.etPassword.text.toString())
        }
    }

    private fun setupNameFilter() {
        val filter = InputFilter { source: CharSequence, _, _, _, _, _ ->
            if (source.toString().matches(NAME_REGEX.toRegex())) {
                return@InputFilter null
            }
            ""
        }

        binding.etName.filters = arrayOf(filter)
    }
}
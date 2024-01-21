package ru.sad.splash

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.splash.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val viewModel: SplashViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> FragmentSplashBinding =
        FragmentSplashBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        /* */
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTimer()
    }
}
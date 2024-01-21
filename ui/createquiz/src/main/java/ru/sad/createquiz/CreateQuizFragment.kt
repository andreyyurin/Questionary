package ru.sad.createquiz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.attach
import ru.sad.base.ext.observeAndClear
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.ext.postSuccess
import ru.sad.base.ext.smoothHide
import ru.sad.base.ext.smoothShow
import ru.sad.createquiz.adapter.CreateFragment
import ru.sad.createquiz.adapter.CreateQuizAdapter
import ru.sad.createquiz.databinding.FragmentCreateQuizBinding
import ru.sad.data.exceptions.QuestionaryException
import ru.sad.domain.model.quiz.QuizAnswer
import ru.sad.domain.model.quiz.QuizQuestion
import ru.sad.domain.model.quiz.QuizResponse

@AndroidEntryPoint
class CreateQuizFragment : BaseFragment<FragmentCreateQuizBinding>() {

    private lateinit var adapter: CreateQuizAdapter

    private val viewModel: CreateQuizViewModel by activityViewModels()

    override val isSaveState: Boolean = true

    override val bindingInflater: (LayoutInflater) -> FragmentCreateQuizBinding =
        FragmentCreateQuizBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        if (!isRestore) {
            initAdapter()
        } else {
            hideLoadingButton()
        }
        observeData()
        bindListeners()
    }

    private fun observeData() {
        viewModel.createQuizLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoadingButton()
                }
                onSuccess {
                    if (this) {
                        viewModel.openFinalScreen()
                        viewModel.createQuizLive.postSuccess(false)
                    }
                }
                onError {
                    hideLoadingButton()
                    showError(this)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = CreateQuizAdapter(this)
        adapter.addFragment(CreateFragment.newInstance(0), CreateFragment.newInstance(1))

        viewModel.currentQuiz = QuizResponse("", -1, ArrayList(), -1, "")
        viewModel.currentQuiz?.questions?.add(QuizQuestion("", ArrayList()))
        viewModel.currentQuiz?.questions?.add(QuizQuestion("", ArrayList()))

        binding.viewPager.adapter = adapter

        binding.viewPager.isSaveEnabled = false

        binding.tabDots attach binding.viewPager
    }

    private fun hideLoadingButton() {
        binding.ivSave.smoothShow()
        binding.frameLoading.smoothHide()
    }

    private fun showLoadingButton() {
        binding.ivSave.smoothHide()
        binding.frameLoading.smoothShow()
    }

    private fun bindListeners() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == adapter.itemCount - 1) {
                    viewModel.currentQuiz?.questions?.add(
                        QuizQuestion(
                            question = "",
                            answers = ArrayList()
                        )
                    )

                    adapter.addFragment(CreateFragment.newInstance(position + 1))

                    binding.viewPager.offscreenPageLimit = adapter.itemCount
                }
            }
        })

        binding.ivSave.setOnClickListener {
            viewModel.currentQuiz?.title = binding.etTitle.text.toString()
            viewModel.checkQuiz()
        }
    }
}
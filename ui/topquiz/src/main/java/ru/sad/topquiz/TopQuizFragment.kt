package ru.sad.topquiz

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.base.BaseFragment
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.State
import ru.sad.base.ext.addAll
import ru.sad.base.ext.applyScrollOffsetToolbar
import ru.sad.base.ext.initHorizontal
import ru.sad.base.ext.initVertical
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.ext.paginationEvents
import ru.sad.base.simple.SimpleListBottomSheet
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.domain.model.quiz.QuizSort
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import ru.sad.topquiz.databinding.FragmentTopQuizBinding
import ru.sad.topquiz.item.FilterItem
import ru.sad.topquiz.item.QuizItem

@AndroidEntryPoint
class TopQuizFragment : BaseFragment<FragmentTopQuizBinding>() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    private val viewModel: TopQuizViewModel by viewModels()

    private val adapterQuiz: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    private val adapterFilters: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    private lateinit var filtersDialog: BottomSheetDialogFragment
    private lateinit var categoriesDialog: BottomSheetDialogFragment
    private lateinit var sortDialog: BottomSheetDialogFragment
    private lateinit var countriesDialog: BottomSheetDialogFragment

    override val isShowBottomMenu: Boolean = true

    override val isSaveState: Boolean = false

    override val bindingInflater: (LayoutInflater) -> FragmentTopQuizBinding =
        FragmentTopQuizBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        setupDialogs()

        bindGridItems()
        bindFilterRecycler()
        bindListeners()
        observeData()

        refreshRecyclerEvents()
        viewModel.loadQuizes()
    }

    private fun setupDialogs() {
        viewModel.loadCategories()
        viewModel.loadSorts()
        viewModel.loadCountries()


        filtersDialog = FiltersBottomSheetDialog.Builder().build()

    }

    private fun bindListeners() {
        /* */
    }

    private fun bindGridItems() {
        binding.recyclerQuizes.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerQuizes.adapter = adapterQuiz
    }

    private fun refreshRecyclerEvents() {
        adapterQuiz.clear()
        binding.nestedQuizes.paginationEvents(viewModel.quizesLive) { page ->
            viewModel.loadQuizes(page, PAGE_SIZE)
        }
    }

    private fun bindFilterRecycler() {
        binding.recyclerFilters.initHorizontal(adapterFilters) {}

        adapterFilters.updateAsync(
            createFilters()
        )
    }

    private fun createFilters(): List<BaseItem> = listOf(
        FilterItem(
            titleIfEmpty = getString(R.string.top_quiz_filter_create),
            quiz = null
        ).apply {
            itemClicks = {
                viewModel.openCreateQuizScreen()
            }
        },
        FilterItem(
            titleIfEmpty = getString(R.string.top_quiz_filter_category),
            quiz = viewModel.quizSelectedCategoryLive.map { it?.name }
        ).apply {
            itemClicks = {
                if (this@TopQuizFragment::categoriesDialog.isInitialized) {
                    openDialog(categoriesDialog as BaseBottomSheetDialogFragment<*>)
                }
            }
        },

        FilterItem(
            titleIfEmpty = getString(R.string.top_quiz_filter_sort),
            quiz = viewModel.quizSelectedSortLive.map { it?.name }
        ).apply {
            itemClicks = {
                if (this@TopQuizFragment::sortDialog.isInitialized) {
                    openDialog(sortDialog as BaseBottomSheetDialogFragment<*>)
                }
            }
        },

        FilterItem(
            titleIfEmpty = getString(R.string.top_quiz_filter_countries),
            quiz = viewModel.quizSelectedCountryLive.map { it?.name }
        ).apply {
            itemClicks = {
                if (this@TopQuizFragment::countriesDialog.isInitialized) {
                    openDialog(countriesDialog as BaseBottomSheetDialogFragment<*>)
                }
            }
        }
    )

    private fun observeData() {
        viewModel.quizesLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    if (this != null) {
                        showShimmerLoading()
                        refreshRecyclerEvents()
                    }
                }
                onSuccess {
                    hideShimmerLoading()

                    if (this.first) adapterQuiz.clear()

                    this.second.map { quiz ->
                        adapterQuiz.add(QuizItem(quiz).apply {
                            itemClicks = {
                                viewModel.openQuizScreen(quiz.id)
                            }
                        })
                    }

                    checkEmptyLayout()
                }
                onError {
                    hideShimmerLoading()
                    showError(this)
                    checkEmptyLayout()
                }
            }
        }

        viewModel.categoriesLive.observe(viewLifecycleOwner) {
            with(it) {
                onSuccess {
                    categoriesDialog =
                        SimpleListBottomSheet
                            .Builder<QuizCategory>()
                            .setData(
                                title = getString(R.string.top_quiz_filter_category),
                                data = this,
                                type = SimpleTypeScreenEnum.TOP_QUIZ
                            )
                            .build()
                }
            }
        }

        viewModel.sortsLive.observe(viewLifecycleOwner) {
            with(it) {
                onSuccess {
                    sortDialog =
                        SimpleListBottomSheet
                            .Builder<QuizSort>()
                            .setData(
                                title = getString(R.string.top_quiz_filter_sort),
                                data = this,
                                type = SimpleTypeScreenEnum.TOP_QUIZ
                            )
                            .build()
                }
            }
        }

        viewModel.countriesLive.observe(viewLifecycleOwner) {
            with(it) {
                onSuccess {
                    countriesDialog =
                        SimpleListBottomSheet
                            .Builder<QuizCountry>()
                            .setData(
                                title = getString(R.string.top_quiz_filter_countries),
                                data = this,
                                type = SimpleTypeScreenEnum.TOP_QUIZ
                            )
                            .build()
                }
            }
        }

        viewModel.quizSelectedCategoryLive.observe(viewLifecycleOwner) {
            viewModel.loadQuizes(0, PAGE_SIZE)
        }

        viewModel.quizSelectedSortLive.observe(viewLifecycleOwner) {
            viewModel.loadQuizes(0, PAGE_SIZE)
        }

        viewModel.quizSelectedCountryLive.observe(viewLifecycleOwner) {
            viewModel.loadQuizes(0, PAGE_SIZE)
        }
    }

    private fun checkEmptyLayout() {
        binding.layoutEmpty.isVisible = adapterQuiz.itemCount <= 0
    }

    private fun hideEmptyLayout() {
        binding.layoutEmpty.isVisible = false
    }
    private fun hideShimmerLoading() {
        binding.shimmerLoading.isGone = true
        binding.collapsingLayout.isVisible = true
    }

    private fun showShimmerLoading() {
        binding.shimmerLoading.isVisible = true
        binding.collapsingLayout.isGone = true
        hideEmptyLayout()
    }
}
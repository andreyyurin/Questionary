package ru.sad.topquiz

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.mapWithScreenType
import ru.sad.base.ext.postSuccess
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.repository.quiz.QuizRepositoryImpl
import ru.sad.data.repository.simple.SimpleRepositoryImpl
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.domain.model.quiz.QuizSort
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import javax.inject.Inject

@HiltViewModel
class TopQuizViewModel @Inject constructor(
    private val quizRepository: QuizRepositoryImpl,
    private val simpleRepository: SimpleRepositoryImpl
) : BaseFragmentViewModel() {

    companion object {
        private const val QUIZ_ID = "QUIZ_ID"
    }

    val quizesLive = MutableLiveData<State<Pair<Boolean, List<QuizShortResponse>>>>()
    val categoriesLive = MutableLiveData<State<List<QuizCategory>>>()
    val sortsLive = MutableLiveData<State<List<QuizSort>>>()
    val countriesLive = MutableLiveData<State<List<QuizCountry>>>()

    val quizSelectedCategoryLive: MutableLiveData<QuizCategory> =
        simpleRepository.simpleSelectedData.mapWithScreenType(SimpleTypeScreenEnum.TOP_QUIZ)

    val quizSelectedSortLive: MutableLiveData<QuizSort> =
        simpleRepository.simpleSelectedData.mapWithScreenType(SimpleTypeScreenEnum.TOP_QUIZ)

    val quizSelectedCountryLive: MutableLiveData<QuizCountry> =
        simpleRepository.simpleSelectedData.mapWithScreenType(SimpleTypeScreenEnum.TOP_QUIZ)

    fun loadQuizes(page: Int = 0, pageSize: Int = 10) {
        launchIO({
            quizesLive.error(it)
        }) {
            if (page == 0) {
                quizesLive.loading(Pair(true, emptyList()))
            } else {
                quizesLive.loading(null)
            }

            val selectedCategory = quizSelectedCategoryLive.value?.id ?: -1
            val selectedSort = quizSelectedSortLive.value ?: QuizSort("", -1)
            val selectedCountry = quizSelectedCountryLive.value ?: QuizCountry("", "", "")

            quizesLive.postSuccess(
                Pair(
                    page == 0,
                    quizRepository.getTopQuizes(
                        page,
                        pageSize,
                        selectedCategory,
                        selectedSort,
                        selectedCountry.id
                    )
                )
            )
        }
    }

    fun loadCategories() {
        launchIO({
            categoriesLive.error(it)
        }) {
            categoriesLive.postSuccess(quizRepository.getCategories())
        }
    }

    fun loadSorts() {
        launchIO({
            sortsLive.error(it)
        }) {
            sortsLive.postSuccess(quizRepository.getSorts())
        }
    }

    fun loadCountries() {
        launchIO({
            countriesLive.error(it)
        }) {
            countriesLive.postSuccess(quizRepository.getCountries())
        }
    }

    fun openQuizScreen(id: Int) {
        navigate(NavigationKey.QUIZ_SCREEN, bundles = bundleOf(QUIZ_ID to id))
    }

    fun openCreateQuizScreen() {
        navigate(NavigationKey.CREATE_QUIZ_SCREEN)
    }
}
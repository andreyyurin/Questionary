package ru.sad.createquiz

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.base.BaseFragment
import ru.sad.base.simple.SimpleListBottomSheet
import ru.sad.base.ext.load
import ru.sad.base.ext.observeAndClear
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.ext.postSuccess
import ru.sad.createquiz.databinding.FragmentCreateQuizFinalBinding
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizResponse
import ru.sad.domain.model.simple.SimpleTypeScreenEnum


@AndroidEntryPoint
class CreateQuizFinalFragment : BaseFragment<FragmentCreateQuizFinalBinding>() {

    companion object {
        const val QUIZ_RESPONSE = "QUIZ_RESPONSE"
        private const val PHOTO_URI = "PHOTO_URI"
        private const val DATA_PHOTO = "DATA_PHOTO"
    }

    override val isSaveState: Boolean = true

    override val bindingInflater: (LayoutInflater) -> FragmentCreateQuizFinalBinding =
        FragmentCreateQuizFinalBinding::inflate

    private lateinit var categoriesDialog: BottomSheetDialogFragment
    private lateinit var countriesDialog: BottomSheetDialogFragment

    private val viewModel: CreateQuizViewModel by activityViewModels()

    private val pickImage =
        registerForActivityResult(object : ActivityResultContracts.GetContent() {
            override fun createIntent(context: Context, input: String): Intent {
                val intent = super.createIntent(context, input)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                return intent
            }
        }) {
            it?.let {
                setupPhoto(it)
            }
        }

    override fun setup(savedInstanceState: Bundle?) {
        bindListeners()
        observeData()

        viewModel.loadCategories()
        viewModel.loadCountries()

        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(DATA_PHOTO)
            ?.observe(viewLifecycleOwner) {
                it?.let {
                    val uri = Uri.parse(it)
                    setupPhoto(uri)
                }
            }
    }

    private fun observeData() {
        viewModel.dialogSelectCategoryLive.observe(viewLifecycleOwner) {
            binding.autoCategoriesSpinner.text = it?.name
        }

        viewModel.dialogSelectCountryLive.observe(viewLifecycleOwner) {
            binding.autoCountriesSpinner.text = it?.name
        }

        viewModel.photoLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                }
                onError {
                    hideLoading()
                    showError(this)
                }
            }
        }

        viewModel.categoriesLive.observe(viewLifecycleOwner) {
            with(it) {
                onSuccess {
                    hideLoading()

                    categoriesDialog =
                        SimpleListBottomSheet
                            .Builder<QuizCategory>()
                            .setData(
                                title = getString(R.string.create_quiz_dialog_categories_title),
                                data = this.filter { category -> category.id != -1 },
                                type = SimpleTypeScreenEnum.CREATE_QUIZ_TYPE
                            )
                            .build()
                }
            }
        }

        viewModel.countriesLive.observe(viewLifecycleOwner) {
            with(it) {
                onSuccess {
                    hideLoading()

                    countriesDialog =
                        SimpleListBottomSheet
                            .Builder<QuizCountry>()
                            .setData(
                                title = getString(R.string.create_quiz_dialog_countries_title),
                                data = this.filter { it.id.isNotEmpty() },
                                type = SimpleTypeScreenEnum.CREATE_QUIZ_TYPE
                            )
                            .build()
                }
            }
        }

        viewModel.quizUploadLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    if (this != null) {
                        viewModel.clear()
                        viewModel.openMainScreen()
                        viewModel.quizUploadLive.postSuccess(null)
                    }
                }
                onError {
                    hideLoading()
                    showError(this)
                }
            }
        }
    }

    private fun setupPhoto(uri: Uri) {
        binding.ivPickImage.load(uri.toString())

        viewModel.savePhoto(uri)
    }

    private fun bindListeners() {
        binding.ivPickImage.setOnClickListener {
            showPicker()
        }

        binding.autoCategoriesSpinner.setOnClickListener {
            if (this::categoriesDialog.isInitialized) {
                openDialog(categoriesDialog as BaseBottomSheetDialogFragment<*>)
            }
        }

        binding.autoCountriesSpinner.setOnClickListener {
            if (this::countriesDialog.isInitialized) {
                openDialog(countriesDialog as BaseBottomSheetDialogFragment<*>)
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.currentQuiz?.category = viewModel.dialogSelectCategoryLive.value?.id ?: -1
            viewModel.currentQuiz?.country = viewModel.dialogSelectCountryLive.value?.id ?: ""

            viewModel.createQuiz()
        }
    }

    private fun showPicker() {
        createPickerDialog(
            Pair(getString(R.string.create_quiz_final_pick_from_gallery)) {
                pickImage.launch("image/*")
            },
            Pair(getString(R.string.create_quiz_final_take_photo)) {
                checkPermissions(
                    android.Manifest.permission.CAMERA,
                    onGranted = ::takePicture
                )
            }
        )
    }

    private fun takePicture() {
        activity?.let {
            viewModel.openCamera()
        }
    }
}
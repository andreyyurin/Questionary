package ru.sad.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.applyAlphaByScrollOffsetWithCustomToolbar
import ru.sad.base.ext.applyScrollOffsetToolbar
import ru.sad.base.ext.decodeUri
import ru.sad.base.ext.load
import ru.sad.base.ext.loadUserPhoto
import ru.sad.base.ext.observeAndClear
import ru.sad.base.ext.onError
import ru.sad.base.ext.onLoading
import ru.sad.base.ext.onSuccess
import ru.sad.base.ext.paginationEvents
import ru.sad.data.prefs.AuthPref
import ru.sad.data.prefs.AuthPref.userId
import ru.sad.profile.databinding.FragmentProfileBinding
import ru.sad.profile.items.ItemProfilePhoto
import ru.sad.profile.items.ItemProfileQuiz
import ru.sad.profile.items.ItemProfileVideo
import java.io.File

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    companion object {
        private const val PHOTO_URI = "PHOTO_URI"
        private const val DATA_PHOTO = "DATA_PHOTO"
        private const val PAGE_SIZE = 20

        private const val USER_ID = "USER_ID"
    }

    override val isShowBottomMenu = true

    override val bindingInflater: (LayoutInflater) -> FragmentProfileBinding =
        FragmentProfileBinding::inflate

    private val viewModel: ProfileViewModel by viewModels()

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

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
        bindGridItems()
        bindListeners()
        observeData()

        setupData()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.ivPickImage.layoutParams.apply {
                    height = binding.ivPickImage.measuredWidth
                }

                binding.appBarLayout.applyScrollOffsetToolbar(
                    binding.ivPickImage,
                    binding.tvName,
                    (binding.ivPickImage.measuredWidth + binding.ivPickImage.y + 50f).toInt(),
                    0.3f,
                    binding.toolbar.measuredHeight.toFloat()
                )

                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setupData() {
        val currentUserId = requireArguments().getInt(USER_ID) ?: AuthPref.userId

        binding.ivPickImage.loadUserPhoto(currentUserId)

        viewModel.loadUser(currentUserId)

        viewModel.getUserQuizes(userId = currentUserId)

        binding.ivLogout.isGone = arguments?.getInt(USER_ID) != userId
    }

    private fun bindGridItems() {
        binding.gridItems.layoutManager = GridLayoutManager(context, 3)
        binding.gridItems.adapter = adapter
    }

    private fun refreshRecyclerEvents() {
        val currentUserId = requireArguments().getInt(USER_ID) ?: AuthPref.userId

        adapter.clear()
        binding.nestedScrollView.paginationEvents(viewModel.quizesLive) { page ->
            viewModel.getUserQuizes(currentUserId, page, PAGE_SIZE)
        }
    }

    private fun bindListeners() {
        binding.btnUploadFirstly.setOnClickListener {
            viewModel.openTopQuizScreen()
        }

        if (arguments?.getInt(USER_ID) != userId) return

        binding.ivPickImage.setOnClickListener {
            showPicker()
        }

        binding.ivLogout.setOnClickListener {
            viewModel.logout()
        }


    }

    private fun observeData() {
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

        viewModel.logoutLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    viewModel.openStartScreen()
                }
                onError {
                    hideLoading()
                    viewModel.openStartScreen()
                    showError(this)
                }
            }
        }

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

        viewModel.userUpdatingLive.observeAndClear(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    viewModel.openMainScreen()
                }
                onError {
                    hideLoading()
                    showError(this)
                }
            }
        }

        viewModel.userLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                }
                onSuccess {
                    binding.tvName.text = this.username
                }
                onError {
                    showError(this)
                }
            }
        }

        viewModel.videosLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading { showLoading() }
                onError { hideLoading() }
                onSuccess {
                    if (this.isNotEmpty()) viewModel.getImages(this)

                    binding.layoutEmpty.isVisible = this.isEmpty()
                    binding.gridItems.isGone = this.isEmpty()
                }
            }
        }

        viewModel.quizesLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    if (this != null) {
                        showShimmerLoading()
                        refreshRecyclerEvents()
                    }
                }
                onError {
                    hideShimmerLoading()
                }
                onSuccess {
                    hideShimmerLoading()

                    this.map { res ->
                        adapter.add(ItemProfileQuiz(res).apply {
                            itemClicks = {
                                viewModel.openQuizScreen(res.id)
                            }

                            itemLongClicks = {
                                showRemoveDialog(this, res.id)
                                true
                            }
                        })
                    }

                    binding.layoutEmpty.isVisible = adapter.itemCount == 0
                    binding.gridItems.isGone = adapter.itemCount == 0
                }
            }
        }

        viewModel.quizRemoveLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading() {
                    showLoading()
                }
                onError {
                    hideLoading()
                    showError(this)
                }
                onSuccess {
                    hideLoading()
                    adapter.removeGroupAtAdapterPosition(this.second)
                }
            }
        }

        viewModel.imagesLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    adapter.updateAsync(this.map { res ->
                        ItemProfileVideo(res.second).apply {
                            itemClicks = {
                                viewModel.openMainScreen(res.first)
                            }

                            itemLongClicks = {
                                showRemoveDialog(this, res.first)
                                true
                            }
                        }
                    })
                }
                onError {
                    hideLoading()
                }
            }
        }

        viewModel.videoRemoveLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    adapter.remove(this)
                }
                onError {
                    hideLoading()
                    showError(this)
                }
            }
        }

        viewModel.photoRemoveLive.observe(viewLifecycleOwner) {
            with(it) {
                onLoading {
                    showLoading()
                }
                onSuccess {
                    hideLoading()
                    adapter.remove(this)
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

    private fun hideShimmerLoading() {
        binding.shimmerLoading.isGone = true
        binding.collapsingLayout.isVisible = true
    }

    private fun showShimmerLoading() {
        binding.shimmerLoading.isVisible = true
        binding.collapsingLayout.isInvisible = true
    }

    private fun showRemoveDialog(itemProfileVideo: ItemProfileVideo, uri: Uri) {
        createPickerDialog(
            Pair(getString(R.string.profile_remove_from_gallery)) {
                viewModel.removeVideo(itemProfileVideo, uri)
            }
        )
    }

    private fun showRemoveDialog(itemProfilePhoto: ItemProfilePhoto, uri: Uri) {
        createPickerDialog(
            Pair(getString(R.string.profile_remove_from_gallery)) {
                viewModel.removePhoto(itemProfilePhoto, uri)
            }
        )
    }

    private fun showRemoveDialog(itemProfileQuiz: ItemProfileQuiz, id: Int) {
        if (arguments?.getInt(USER_ID) != userId) return

        createPickerDialog(
//            Pair(getString(R.string.profile_quiz_edit)) {
//
//            },
//            Pair(getString(R.string.profile_quiz_statistics)) {
//
//            },
            Pair(getString(R.string.profile_remove_from_gallery)) {
                viewModel.removeQuiz(id, adapter.getAdapterPosition(itemProfileQuiz))
            }
        )
    }

    private fun showPicker() {
        createPickerDialog(
            Pair(getString(R.string.profile_pick_from_gallery)) {
                pickImage.launch("image/*")
            },
            Pair(getString(R.string.profile_take_photo)) {
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
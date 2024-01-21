package ru.sad.onboarding.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.CropTransformation.CropType
import ru.sad.base.base.AnimationEdge
import ru.sad.base.base.BaseFragment
import ru.sad.onboarding.OnBoardingViewModel
import ru.sad.onboarding.R
import ru.sad.onboarding.databinding.FragmentPageFourthBinding

@AndroidEntryPoint
class OnBoardingFourthPageFragment : BaseFragment<FragmentPageFourthBinding>() {

    override val bindingInflater: (LayoutInflater) -> FragmentPageFourthBinding =
        FragmentPageFourthBinding::inflate

    private val rightScene = R.xml.onboarding_scene_page_4_right
    private val leftScene = R.xml.onboarding_scene_page_4_left

    private val viewModel: OnBoardingViewModel by activityViewModels()

    override fun setup(savedInstanceState: Bundle?) {

        setupImage()
        bindListeners()
    }

    private fun bindListeners() {
        binding.btnContinue.setOnClickListener {
            viewModel.onButtonClickLive.postValue(true)
        }
    }

    override fun animate(edge: AnimationEdge, percent: Float) {
        with(binding.motionLayoutFourth) {
            when {
                edge == AnimationEdge.RIGHT && this.tag != AnimationEdge.RIGHT -> {
                    this.loadLayoutDescription(rightScene)
                    this.tag = AnimationEdge.RIGHT
                }
                edge == AnimationEdge.LEFT && this.tag != AnimationEdge.LEFT -> {
                    this.loadLayoutDescription(leftScene)
                    this.tag = AnimationEdge.LEFT
                }
            }

            this.progress = percent
        }
    }

    private fun setupImage() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Glide.with(binding.ivMainImageFourth)
                    .load(R.drawable.img_page_4_part_2)
                    .apply(
                        RequestOptions.bitmapTransform(
                            MultiTransformation(
                                CropTransformation(
                                    0,
                                    0,
                                    CropType.TOP
                                ),
                                CenterCrop()
                            )
                        )
                    )
                    .into(binding.ivMainImageFourth)

                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}
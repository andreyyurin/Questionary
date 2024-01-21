package ru.sad.onboarding.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.CropTransformation
import ru.sad.base.base.AnimationEdge
import ru.sad.base.base.BaseFragment
import ru.sad.onboarding.OnBoardingViewModel
import ru.sad.onboarding.R
import ru.sad.onboarding.databinding.FragmentPageThirdBinding

@AndroidEntryPoint
class OnBoardingThirdPageFragment : BaseFragment<FragmentPageThirdBinding>() {

    override val bindingInflater: (LayoutInflater) -> FragmentPageThirdBinding =
        FragmentPageThirdBinding::inflate

    private val rightScene = R.xml.onboarding_scene_page_3_right
    private val leftScene = R.xml.onboarding_scene_page_3_left

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
        with(binding.motionLayoutThird) {
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
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Glide.with(binding.ivImageFromFourthPage)
                    .load(R.drawable.img_page_3_part_1)
                    .apply(
                        RequestOptions.bitmapTransform(
                            MultiTransformation(
                                CropTransformation(
                                    0,
                                    0,
                                    CropTransformation.CropType.TOP
                                ),
                                CenterCrop()
                            )
                        )
                    )
                    .into(binding.ivImageFromFourthPage)

                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}
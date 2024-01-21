package ru.sad.photoviewer

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.decodeUri
import ru.sad.photoviewer.databinding.FragmentImageViewerBinding
import ru.sad.photoviewer.item.ItemColor

@AndroidEntryPoint
class PhotoViewerFragment : BaseFragment<FragmentImageViewerBinding>() {

    companion object {
        private const val DATA_PHOTO = "DATA_PHOTO"

        private val COLORS =
            arrayOf(
                Color.TRANSPARENT,
                Color.WHITE,
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.BLACK,
                Color.GRAY,
                Color.YELLOW,
                Color.CYAN,
                Color.MAGENTA,
                Color.parseColor("#800080"),
                Color.parseColor("#03DAC5"),
                Color.parseColor("#FFC0CB")
            )
    }


    private var lastSelectedItem: ItemColor? = null

    private val viewModel: PhotoViewerViewModel by viewModels()

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override val bindingInflater: (LayoutInflater) -> FragmentImageViewerBinding =
        FragmentImageViewerBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
        setupImage()
        setupAdapter()

        setupColors()
    }

    private fun setupImage() {
        val bitmap = decodeUri(Uri.parse(requireArguments().getString(DATA_PHOTO)), 500)
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                binding.ivPhoto.init(
//                    bitmap,
//                    binding.ivPhoto.measuredWidth,
//                    binding.ivPhoto.measuredHeight
//                )


                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setupColors() {
//        adapter.updateAsync(
//            COLORS.map {
//                ItemColor(it) { _, item ->
//                    item.select(true)
//                    if (lastSelectedItem != item) lastSelectedItem?.select(false)
//                    lastSelectedItem = item
//                    binding.ivPhoto.selectColor(it)
//                }
//            }
//        )

        (adapter.getItem(0) as ItemColor).select(true)
        lastSelectedItem = (adapter.getItem(0) as ItemColor)
    }

    private fun setupAdapter() {
        binding.recyclerColors.adapter = adapter
        binding.recyclerColors.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            viewModel.exit()
        }

//        binding.seekPaintSize.onChangeValue = {
//            binding.ivPhoto.selectPaintSize(it)
//        }
//
//        binding.ivCancelLast.setOnClickListener {
//            binding.ivPhoto.removeLastStep()
//        }
//
//        binding.btnSave.setOnClickListener {
//            findNavController().previousBackStackEntry?.savedStateHandle?.set(
//                DATA_PHOTO,
//                binding.ivPhoto.getBitmap(requireArguments().getString(DATA_PHOTO)).toString()
//            )
//            viewModel.exit()
//        }
    }
}
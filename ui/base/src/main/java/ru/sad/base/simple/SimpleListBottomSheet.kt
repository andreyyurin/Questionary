package ru.sad.base.simple

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.base.Ascii.SO
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseBottomSheetDialogFragment
import ru.sad.base.databinding.DialogSimpleListBinding
import ru.sad.base.ext.initVertical
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import java.io.Serializable


@AndroidEntryPoint
class SimpleListBottomSheet : BaseBottomSheetDialogFragment<DialogSimpleListBinding>() {

    companion object {
        private const val TITLE = "TITLE"
        private const val DATA = "DATA"
        private const val TYPE = "TYPE"
    }

    private val viewModel: SimpleViewModel by viewModels()

    private val adapter: GroupAdapter<GroupieViewHolder> by lazy {
        GroupAdapter<GroupieViewHolder>()
    }

    override val bindingInflater: (LayoutInflater) -> DialogSimpleListBinding =
        DialogSimpleListBinding::inflate

    override val tagDialog: String = "SimpleListBottomSheet"

    override fun setup() {
        setupTitle()
        setupAdapter()
        observeData()
        bindListeners()

        setupData()
    }

    private fun observeData() {
        val type = requireArguments().getSerializable(TYPE) as SimpleTypeScreenEnum

        viewModel.dataLive.observe(viewLifecycleOwner) {
            adapter.update(
                it.map { dataMain ->
                    SimpleItem(dataMain.toSimpleData()).apply {
                        itemClicks = {
                            viewModel.click(type, dataMain)
                            dismiss()
                        }
                    }
                }
            )
        }
    }

    private fun bindListeners() {
        binding.ivBtnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupTitle() {
        binding.tvTitle.text = requireArguments().getString(TITLE)
    }

    private fun setupData() {
        viewModel.dataLive.postValue(requireArguments().getSerializable(DATA) as? List<Any>)
    }

    private fun setupAdapter() {
        binding.recyclerSimple.initVertical(adapter)
    }

    class SimpleData(val title: String, val image: String?) : Serializable

    private fun List<Any>.toSimpleData(): List<SimpleData> = this.map {
        val fieldTitle = getValueOfField(fields = arrayOf("name", "title"), instance = it)
        SimpleData(fieldTitle, "fieldImage")
    }

    private fun Any.toSimpleData(): SimpleData {
        val fieldTitle = getValueOfField(fields = arrayOf("name", "title"), instance = this)
        val fieldImage = getValueOfField(fields = arrayOf("image"), instance = this)
        return SimpleData(fieldTitle, fieldImage)
    }

    private fun getValueOfField(vararg fields: String, instance: Any): String {
        fields.forEach {
            try {
                val field = instance::class.java.getDeclaredField(it)
                field.isAccessible = true
                return field.get(instance)?.toString() ?: ""
            } catch (_: Exception) {
            }
        }
        return ""
    }

    class Builder<T> {
        private var title: String? = null
        private var data: List<T>? = null
        private var type: SimpleTypeScreenEnum? = null

        fun setData(data: List<T>, title: String, type: SimpleTypeScreenEnum): Builder<T> {
            return apply {
                this.title = title
                this.data = data
                this.type = type
            }
        }

        fun build(): BottomSheetDialogFragment {
            return SimpleListBottomSheet().apply {
                arguments = bundleOf(
                    TITLE to title,
                    DATA to data,
                    TYPE to type
                )
            }
        }
    }
}
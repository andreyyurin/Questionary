package ru.sad.base.base

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.viewbinding.ViewBinding
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job
import ru.sad.base.R
import ru.sad.base.analytics.AnalyticsLogger
import ru.sad.base.views.PickerTextView
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    @Inject
    lateinit var analytics: AnalyticsLogger

    abstract val bindingInflater: (LayoutInflater) -> VB

    abstract val tagDialog: String

    private var onDismissListener: DialogDismiss? = null

    private var _binding: ViewBinding? = null

    private var _viewLifecycleOwner: ViewLifecycleOwner? = null

    private lateinit var dialogLoading: AlertDialog

    protected val viewLifecycleOwnerP: LifecycleOwner
        get() = _viewLifecycleOwner
            ?: throw IllegalStateException("Cannot access ViewLifecycleOwner before onViewCreated and after onDestroyView.")

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(layoutInflater)
        return _binding!!.root
    }

    abstract fun setup()

    fun showFullscreen() {
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        val metrics = DisplayMetrics()
        activity?.let {
            it.windowManager?.defaultDisplay?.getMetrics(metrics)
            (dialog as? BottomSheetDialog)?.behavior?.peekHeight = metrics.heightPixels
            binding.root.layoutParams.height = metrics.heightPixels
            binding.root.requestLayout()
        }
    }

    open fun setOnDismissListener(onDismiss: DialogDismiss) {
        onDismissListener = onDismiss
    }

    override fun dismiss() {
        super.dismiss()
        onDismissListener.let {
            it?.onDismiss(0)
        }
    }

    override fun onStart() {
        super.onStart()
        _viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        createLoadingDialog()
        setup()
    }

    private fun createLoadingDialog() {
        dialogLoading = AlertDialog
            .Builder(requireContext()).create()

        dialogLoading.setView(
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_progress, null)
        )

        dialogLoading.setCancelable(false)

        dialogLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppThemeBase_BottomSheetPickerDialog)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        _viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun checkPermissions(
        permissions: List<String>,
        onGranted: () -> Unit = {},
        onDenied: () -> Unit = {}
    ) {
        val perm = permissions[0]
        val perms = ArrayList<String>().apply {
            addAll(permissions)
            removeAt(0)
        }
        permissionsBuilder(perm, *perms.toTypedArray()).build().send() { result ->
            if (result.allGranted()) {
                onGranted.invoke()
            } else {
                onDenied.invoke()
            }
        }
    }

    open fun showLoading() {
        dialogLoading.show()
    }

    open fun hideLoading() {
        dialogLoading.dismiss()
    }

    open fun createPickerDialog(vararg buttons: Pair<String, (() -> Unit)>) {
        context?.let { context ->
            val picker =
                AlertDialog
                    .Builder(context)
                    .create()

            picker.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            picker.setView(
                LayoutInflater.from(context)
                    .inflate(R.layout.dialog_picker, null)
            )

            picker.show()

            buttons.forEachIndexed { index, pair ->
                val textViewPicker =
                    PickerTextView(
                        context = context,
                        click = {
                            pair.second.invoke()
                            picker.dismiss()
                        },
                        text = pair.first,
                        isFirstElement = index == 0
                    )

                picker.findViewById<LinearLayout>(R.id.layoutPicker).addView(textViewPicker)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        _viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onDestroyView() {
        _viewLifecycleOwner?.lifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _viewLifecycleOwner = null
        super.onDestroyView()
    }

    private class ViewLifecycleOwner : LifecycleOwner {
        override val lifecycle: LifecycleRegistry
            get() = LifecycleRegistry(this)
    }

    fun openDialog(
        destination: BaseBottomSheetDialogFragment<*>
    ) {
        val tag = destination.tagDialog
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            destination.show(childFragmentManager, tag)
        }
    }

    interface DialogDismiss {
        fun onDismiss(reason: Int)
    }
}
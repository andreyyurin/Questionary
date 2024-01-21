package ru.sad.base.base

import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.fondesa.kpermissions.request.PermissionRequest
import ru.sad.base.R
import ru.sad.base.analytics.AnalyticsLogger
import ru.sad.base.views.PickerTextView
import ru.sad.utils.toDp
import javax.inject.Inject


abstract class BaseFragment<VB : ViewBinding> : Fragment(), FragmentResult, AnimationScrollListener {

    companion object {
        private const val ERROR_TAG = "GEOMATE-EXCEPTION"
    }

    open val isShowBottomMenu: Boolean? = false

    open val isSaveState: Boolean? = false

    private var _binding: ViewBinding? = null

    private var savedViewState: ViewBinding? = null

    var isRestore = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        }

    private lateinit var dialog: AlertDialog

    abstract val bindingInflater: (LayoutInflater) -> VB

    @Inject
    lateinit var analytics: AnalyticsLogger

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onResume() {
        super.onResume()
        setupBottomNavigation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedViewState != null && isSaveState == true) {
            _binding = savedViewState
            isRestore = true
        } else {
            _binding = bindingInflater.invoke(layoutInflater)
            savedViewState = _binding
        }
        return _binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createLoadingDialog()
        setup(savedInstanceState)
    }

    private fun setupBottomNavigation() {
        when (isShowBottomMenu) {
            true -> showBottomNavigation()
            false -> hideBottomNavigation()
            else -> {}
        }
    }

    private fun createLoadingDialog() {
        dialog = AlertDialog
            .Builder(requireContext()).create()

        dialog.setView(
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_progress, null)
        )

        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showToast(toast: String) {
        context?.let {
            Toast.makeText(it, toast, Toast.LENGTH_SHORT).show()
        }
    }

    fun showError(error: String?) {
        activity?.let {
            (it as BaseActivity).showError(error)
        }
    }

    abstract fun setup(savedInstanceState: Bundle?)

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun animate(edge: AnimationEdge, percent: Float) {

    }

    override fun onFragmentRequestPermissionsResult(
        requestCode: Int,
        permissions: List<String>,
        results: IntArray
    ) {

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

    open fun createDropdownDialog(vararg buttons: Pair<String, (() -> Unit)>, view: View) {
        context?.let { context ->
            val popupView = View.inflate(context, R.layout.dialog_picker, null)
            val picker = PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            picker.isOutsideTouchable = true
            picker.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            picker.elevation = 12f
            picker.isFocusable = true

            buttons.forEachIndexed { index, pair ->
                val textViewPicker =
                    PickerTextView(
                        context = context,
                        click = {
                            pair.second.invoke()
                            picker.dismiss()
                        },
                        text = pair.first,
                        isFirstElement = index == 0,
                        textSize = 11
                    )

                popupView.findViewById<LinearLayout>(R.id.layoutPicker).addView(textViewPicker)
            }

            picker.showAsDropDown(view, -400.toDp.toInt(), 10.toDp.toInt())
        }
    }

    open fun showLoading() {
        dialog.show()
    }

    open fun hideLoading() {
        dialog.dismiss()
    }

    open fun showBottomNavigation() {
        (activity as? BaseActivity)?.bottomNavigation(true)
    }

    open fun hideBottomNavigation() {
        (activity as? BaseActivity)?.bottomNavigation(false)
    }

    fun setupInputMode(mode: Int) {
        activity?.window?.setSoftInputMode(mode)
    }

    fun checkPermission(
        permission: String,
        onGranted: () -> Unit = {},
        onDenied: () -> Unit = {}
    ) {
        permissionsBuilder(permission).build().send { result ->
            if (result.allGranted()) {
                onGranted.invoke()
            } else {
                onDenied.invoke()
            }
        }
    }

    fun checkPermissions(
        vararg permissions: String,
        onGranted: () -> Unit = {},
        onDenied: () -> Unit = {}
    ) {
        val perm = permissions[0]
        val perms = ArrayList<String>().apply {
            addAll(permissions)
            removeAt(0)
        }
        permissionsBuilder(perm, *perms.toTypedArray()).build().send { result ->
            if (result.allGranted()) {
                onGranted.invoke()
            } else {
                onDenied.invoke()
            }
        }
    }

    open fun checkPushes() {
        context?.let {
            val isPushesAllowed = NotificationManagerCompat.from(it).areNotificationsEnabled()

            if (isPushesAllowed) return

            if (Build.VERSION.SDK_INT >= 33) {
                requestPermissionForNotifications()
            } else {

                startIntentForNotifications()
            }
        }
    }

    private fun requestPermissionForNotifications() {
        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun startIntentForNotifications() {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        activity?.let {
            intent.putExtra("app_package", it.packageName)
            intent.putExtra("app_uid", it.applicationInfo.uid)
            intent.putExtra("android.provider.extra.APP_PACKAGE", it.packageName)
        }

        startActivity(intent)
    }

    fun openDialog(
        destination: BaseBottomSheetDialogFragment<*>
    ) {
        val tag = destination.tagDialog
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            destination.show(childFragmentManager, tag)
        }
    }
}

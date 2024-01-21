package ru.sad.base.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class AnalyticsLogger @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseAnalytics: FirebaseAnalytics
) {
    private fun sendEvent(eventName: String, params: HashMap<String, Any> = createDefaultHasMap()) {
        firebaseAnalytics.logEvent(eventName, params.toBundle())
    }

    private fun createDefaultHasMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        if (firebaseAuth.currentUser?.uid != null) {
            map[LogParam.USER_ID] = firebaseAuth.currentUser!!.uid
        }
        return map
    }

    private fun HashMap<String, Any>.toBundle(): Bundle = bundleOf(*this.toList().toTypedArray())
}
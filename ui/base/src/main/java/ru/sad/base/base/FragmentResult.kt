package ru.sad.base.base

import android.content.Intent

interface FragmentResult {
    fun onFragmentResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun onFragmentRequestPermissionsResult(
        requestCode: Int,
        permissions: List<String>,
        results: IntArray
    )
}
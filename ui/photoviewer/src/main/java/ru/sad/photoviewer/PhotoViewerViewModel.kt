package ru.sad.photoviewer

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import javax.inject.Inject

@HiltViewModel
class PhotoViewerViewModel @Inject constructor() : BaseFragmentViewModel() {
    fun exit() {
        back()
    }
}
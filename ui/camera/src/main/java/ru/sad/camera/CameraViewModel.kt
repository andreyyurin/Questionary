package ru.sad.camera

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
) : BaseFragmentViewModel() {
    fun exit() {
        back()
    }

    fun getUri() {

    }
}
package ru.sad.base.simple

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.data.repository.simple.SimpleRepositoryImpl
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import javax.inject.Inject

@HiltViewModel
class SimpleViewModel @Inject constructor(
    private val simpleRepository: SimpleRepositoryImpl
) : BaseFragmentViewModel() {

    val dataLive = simpleRepository.simpleWaitingData

    fun <T : Any> setup(data: List<T>) {
        if (dataLive.value?.contains(data) == true) return
        dataLive.postValue(data)
    }

    fun <T : Any> click(type: SimpleTypeScreenEnum, data: T) {
        simpleRepository.simpleSelectedData.postValue(Pair(type, data))
    }

    fun check() {

    }
}
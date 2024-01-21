package ru.sad.data.repository.simple

import androidx.lifecycle.MutableLiveData
import ru.sad.domain.model.simple.SimpleTypeScreenEnum

interface SimpleRepositoryImpl {
    val simpleSelectedData: MutableLiveData<Pair<SimpleTypeScreenEnum, Any>>

    val simpleWaitingData: MutableLiveData<List<Any>>
        get() = MutableLiveData<List<Any>>()
}
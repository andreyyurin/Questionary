package ru.sad.data.repository.simple

import android.app.Application
import androidx.lifecycle.MutableLiveData
import ru.sad.domain.model.simple.SimpleTypeScreenEnum

class SimpleRepository(private val application: Application) : SimpleRepositoryImpl {

    override val simpleSelectedData: MutableLiveData<Pair<SimpleTypeScreenEnum, Any>> by lazy { MutableLiveData() }

    override val simpleWaitingData: MutableLiveData<List<Any>> by lazy { MutableLiveData() }

}
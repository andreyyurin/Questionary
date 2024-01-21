package ru.sad.subscriptions.following

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.sad.base.analytics.LogParam.USER_ID
import ru.sad.base.base.BaseFragment
import ru.sad.base.base.BaseFragmentViewModel
import ru.sad.base.ext.State
import ru.sad.base.ext.error
import ru.sad.base.ext.loading
import ru.sad.base.ext.postSuccess
import ru.sad.base.navigation.NavigationKey
import ru.sad.data.repository.subscriptions.SubscriptionsRepositoryImpl
import ru.sad.domain.model.subscriptions.SubscribeResponse
import ru.sad.domain.model.users.User
import ru.sad.subscriptions.item.ItemUser
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepositoryImpl
) : BaseFragmentViewModel() {

    companion object {
        private const val USER_ID = "USER_ID"
    }

    val followingLive = MutableLiveData<State<List<User>>>()
    val actionLive = MutableLiveData<State<Pair<ItemUser, SubscribeResponse?>>>()

    fun loadFollowingUsers() {
        launchIO({
            followingLive.error(it)
        }) {
            followingLive.loading()
            followingLive.postSuccess(subscriptionsRepository.getAllFollowing())
        }
    }

    fun follow(id: Int, itemUser: ItemUser) {
        launchIO({
            actionLive.postValue(State.Error(it, Pair(itemUser, null)))
        }) {
            actionLive.postValue(State.Loading(Pair(itemUser, null)))
            actionLive.postSuccess(Pair(itemUser, subscriptionsRepository.subscribeToUser(id)))
        }
    }

    fun unfollow(id: Int, itemUser: ItemUser) {
        launchIO({
            actionLive.postValue(State.Error(it, Pair(itemUser, null)))
        }) {
            actionLive.postValue(State.Loading(Pair(itemUser, null)))
            actionLive.postSuccess(Pair(itemUser, subscriptionsRepository.unsubscribeFromUser(id)))
        }
    }


    fun openProfile(userId: Int) {
        navigate(NavigationKey.PROFILE_SCREEN, bundles = bundleOf(
            USER_ID to userId
        ))
    }
}
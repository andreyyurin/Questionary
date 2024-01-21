package ru.sad.data.repository.users

import android.app.Application
import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.sad.domain.model.users.UserModel
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val application: Application,
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseFirestore
) : UsersRepositoryImpl {

    companion object {
        private const val USER_COLLECTION = "users"
    }

    override suspend fun getUsers(sample: String): List<UserModel> =
        withContext(Dispatchers.IO) {
            if (sample.isNullOrEmpty().not()) {
                val task = database
                    .collection(USER_COLLECTION)
                    .whereEqualTo("nickname", sample)
                    .get()
                toUsers(Tasks.await(task))
            }
            emptyList()
        }

    override suspend fun putUser(user: UserModel) {
        withContext(Dispatchers.IO) {
            database
                .collection(USER_COLLECTION)
                .document(user.email)
                .set(
                    hashMapOf(
                        "nickname" to user.nickname,
                        "id" to user.id,
                        "photoUrl" to user.photoUrl,
                        "email" to user.email
                    )
                )
                .await()
        }
    }

    private fun toUsers(document: QuerySnapshot, sample: String = ""): List<UserModel> {
        val finalList = ArrayList<UserModel>()
        document
            .documents
            .map {
                it.toObject(UserModel::class.java)?.let { user ->
                    finalList.add(user)
                }
            }
        return finalList
    }
}
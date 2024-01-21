package ru.sad.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import ru.sad.domain.base.BaseModel
import ru.sad.domain.base.BaseModelSingle
import ru.sad.domain.model.quiz.CheckResultsResponse
import ru.sad.domain.model.quiz.CreateQuizResponse
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.domain.model.quiz.QuizCountry
import ru.sad.domain.model.quiz.QuizResponse
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.domain.model.quiz.QuizSort
import ru.sad.domain.model.quiz.RateQuizRequest
import ru.sad.domain.model.quiz.RateQuizResponse
import ru.sad.domain.model.quiz.RemoveQuizResponse
import ru.sad.domain.model.stories.CreateStoryResponse
import ru.sad.domain.model.stories.StoriesResponse
import ru.sad.domain.model.subscriptions.SubscribeRequest
import ru.sad.domain.model.subscriptions.SubscribeResponse
import ru.sad.domain.model.users.FirebaseRequest
import ru.sad.domain.model.users.FirebaseTokenResponse
import ru.sad.domain.model.users.LogoutResponse
import ru.sad.domain.model.users.UploadPhotoResponse
import ru.sad.domain.model.users.User
import ru.sad.domain.model.users.UserRequest

interface QuestionaryApi {

    @POST("auth")
    suspend fun auth(@Body userRequest: UserRequest): BaseModelSingle<User>

    @POST("logout")
    suspend fun logout(): BaseModelSingle<LogoutResponse>

    @Multipart
    @POST("create-avatar")
    suspend fun uploadPhoto(@Part image: MultipartBody.Part): BaseModelSingle<UploadPhotoResponse>

    @GET("stories")
    suspend fun getStories(): BaseModel<StoriesResponse>

    @Multipart
    @POST("create-story")
    suspend fun createStory(
        @Part image: MultipartBody.Part,
        @Query("filename") fileName: String,
        @Query("userId") userId: String
    ): BaseModelSingle<CreateStoryResponse>

    @POST("subscribe")
    suspend fun subscribe(@Body subscribeRequest: SubscribeRequest): BaseModelSingle<SubscribeResponse>

    @POST("unsubscribe")
    suspend fun unsubscribe(@Body subscribeRequest: SubscribeRequest): BaseModelSingle<SubscribeResponse>

    @GET("subscriptions")
    suspend fun subscriptions(): BaseModel<User>

    @GET("following")
    suspend fun following(): BaseModel<User>

    @POST("firebase")
    suspend fun sendFirebaseToken(@Body firebaseRequest: FirebaseRequest): BaseModelSingle<FirebaseTokenResponse>

    @GET("user")
    suspend fun getUser(@Query("userId") userId: Int): BaseModelSingle<User>

    @GET("search")
    suspend fun search(@Query("username") query: String): BaseModel<User>

    @Multipart
    @POST("create-quiz")
    suspend fun createQuiz(
        @Part("quiz") quizResponse: RequestBody,
        @Part image: MultipartBody.Part
    ): BaseModelSingle<CreateQuizResponse>

    @GET("quiz")
    suspend fun getQuiz(@Query("id") id: Int): BaseModelSingle<QuizResponse>

    @POST("check-results")
    suspend fun checkResults(@Body quiz: QuizResponse): BaseModelSingle<CheckResultsResponse>

    @GET("quiz-by")
    suspend fun getQuizByUser(
        @Query("author") id: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
    ): BaseModel<QuizShortResponse>

    @GET("quizes")
    suspend fun getTopQuizes(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("category") category: Int,
        @Query("sort") sort: Int,
        @Query("country") country: String
    ): BaseModel<QuizShortResponse>

    @GET("categories")
    suspend fun getCategories(): BaseModel<QuizCategory>

    @GET("countries")
    suspend fun getCountries(): BaseModel<QuizCountry>

    @GET("sorts")
    suspend fun getSorts(): BaseModel<QuizSort>

    @GET("quiz/remove")
    suspend fun removeQuiz(@Query("quiz") id: Int): BaseModelSingle<RemoveQuizResponse>

    @POST("rate-quiz")
    suspend fun rateQuiz(@Body quizRequest: RateQuizRequest): BaseModelSingle<RateQuizResponse>

    @GET("quiz/filter")
    suspend fun getFiltered(
        @Query("category") category: Int,
        @Query("sort") sort: Int
    ): BaseModel<QuizShortResponse>
}
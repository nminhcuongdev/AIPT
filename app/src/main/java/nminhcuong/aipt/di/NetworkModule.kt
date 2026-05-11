package nminhcuong.aipt.di

import com.google.gson.Gson
import nminhcuong.aipt.BuildConfig
import nminhcuong.aipt.core.network.ApiService
import nminhcuong.aipt.feature.chat.data.remote.AiTrainerChatApi
import nminhcuong.aipt.feature.workout.data.remote.WorkoutPlanApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .callTimeout(120, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL.withTrailingSlash())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideWorkoutPlanApi(retrofit: Retrofit): WorkoutPlanApi =
        retrofit.create(WorkoutPlanApi::class.java)

    @Provides
    @Singleton
    fun provideAiTrainerChatApi(retrofit: Retrofit): AiTrainerChatApi =
        retrofit.create(AiTrainerChatApi::class.java)

    private fun String.withTrailingSlash(): String = if (endsWith('/')) this else "$this/"
}
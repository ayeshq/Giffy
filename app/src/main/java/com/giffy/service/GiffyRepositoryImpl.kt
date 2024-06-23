package com.giffy.service

import com.giffy.BuildConfig
import com.giffy.model.Gif
import com.giffy.model.GiffyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

class GiffyRepositoryImpl @Inject constructor(
) : GiffyRepository {

    private val giffyService: GiffyService = createService()

    override suspend fun randomGif(): Flow<Gif?> {
        val gif = giffyService.randomGif().body()?.data
        return flowOf(gif)
    }

    override suspend fun trendingGifs(query: String): Flow<List<Gif>?> {
        val trendingGifs = giffyService.trendingGifs(query).body()?.data
        return flowOf(trendingGifs)
    }

    override suspend fun searchGif(query: String): Flow<List<Gif>?> {
        val searchResult = giffyService.searchGif(query).body()?.data
        return flowOf(searchResult)
    }

    private fun createService(): GiffyService {
        val baseUrl = "https://api.giphy.com/"

        val httpClient: OkHttpClient =
            OkHttpClient
                .Builder()
                //Adding HttpLoggingInterceptor for debugging purposes
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(ApiKeyInterceptor())
                .build()

        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiffyService::class.java)
    }

    private class ApiKeyInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            val urlBuilder = request.url.newBuilder()

            val url = urlBuilder
                .addQueryParameter("api_key", BuildConfig.GIFFY_API_KEY)
                .build()

            request = request.newBuilder().url(url).build()
            return chain.proceed(request)
        }
    }

    interface GiffyService {

        @GET("v1/gifs/random")
        suspend fun randomGif(): Response<GiffyResponse<Gif>>

        @GET("v1/gifs/{gifId}")
        suspend fun gif(
            @Path("gifId") gifId: String
        ): Response<GiffyResponse<Gif>>

        @GET("v1/gifs/search")
        suspend fun searchGif(
            @Query("q") q: String,
            @Query("limit") limit: Int = 50
        ): Response<GiffyResponse<List<Gif>>>

        @GET("v1/gifs/trending")
        suspend fun trendingGifs(
            @Query("q") q: String,
            @Query("limit") limit: Int = 50
        ): Response<GiffyResponse<List<Gif>>>
    }
}

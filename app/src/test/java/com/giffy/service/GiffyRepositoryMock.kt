package com.giffy.service

import com.giffy.model.Gif
import com.giffy.model.GiffyResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

import java.io.InputStreamReader

class GiffyRepositoryMock : GiffyRepository {

    var error = false
    var empty = false

    //A predefined sample gif parsed from a json file containing a random giffy api response
    private val gifStub: Gif = gifStub()

    override suspend fun randomGif(): Flow<Gif?> {
        TODO("Not yet implemented")
    }

    override suspend fun trendingGifs(): Flow<List<Gif>?> = if (error) {
        throw Exception("Some random error!")
    } else if (empty) {
        flowOf(listOf())
    } else {
        flowOf(listOf(gifStub))
    }

    override suspend fun searchGif(query: String): Flow<List<Gif>?> {
        TODO("Not yet implemented")
    }

    /**
     * Creates a real Gif object, by parsing a stored json file containing a successful giffy api response
     */
    private fun gifStub(): Gif {
        val inputStream = this::class.java.classLoader!!.getResourceAsStream("GiffyApiResponse.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<GiffyResponse<Gif>>() {}.type
        val randomGiffyResponse = Gson().fromJson<GiffyResponse<Gif>>(
            reader,
            type
        )
        return randomGiffyResponse.data
    }
}

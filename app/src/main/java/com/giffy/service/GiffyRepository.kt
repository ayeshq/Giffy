package com.giffy.service

import com.giffy.model.Gif
import kotlinx.coroutines.flow.Flow

interface GiffyRepository {

    suspend fun randomGif(): Flow<Gif?>

    suspend fun trendingGifs(): Flow<List<Gif>?>

    suspend fun searchGif(query: String): Flow<List<Gif>?>
}

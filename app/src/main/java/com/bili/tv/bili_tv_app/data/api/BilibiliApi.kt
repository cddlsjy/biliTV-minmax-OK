package com.bili.tv.bili_tv_app.data.api

import com.bili.tv.bili_tv_app.data.model.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * 哔哩哔哩 API 服务
 */
object BilibiliApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    // API 基础 URL
    private const val BASE_URL = "https://api.bilibili.com"

    /**
     * 获取推荐视频列表
     */
    suspend fun getRecommendVideos(idx: Int = 0): List<Video> = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}/x/web-interface/index/top/rcmd?ps=20&pn=${idx + 1}&fp=0&type=normal&r_type=1"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://www.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val recommendResponse = gson.fromJson(body, RecommendResponse::class.java)
            recommendResponse.data?.map { it.apply { cid = aid } } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取热门视频列表
     */
    suspend fun getPopularVideos(page: Int = 1): List<Video> = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}/x/web-interface/ranking/v2?type=all&pn=$page&ps=20"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://www.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val popularResponse = gson.fromJson(body, PopularResponse::class.java)
            popularResponse.data?.list?.map { it.apply { cid = aid } } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取分区视频列表
     */
    suspend fun getRegionVideos(tid: Int, page: Int = 1): List<Video> = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}/x/tag/ranking/region?rid=$tid&type=0&pn=$page&ps=20"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://www.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val popularResponse = gson.fromJson(body, PopularResponse::class.java)
            popularResponse.data?.list?.map { it.apply { cid = aid } } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 搜索视频
     */
    suspend fun searchVideos(keyword: String, page: Int = 1): List<Video> = withContext(Dispatchers.IO) {
        try {
            val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
            val url = "${BASE_URL}/x/search/type?search_type=video&keyword=$encodedKeyword&page=$page"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://search.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            val searchResponse = gson.fromJson(body, SearchResponse::class.java)
            searchResponse.data?.result?.map { it.apply { cid = aid } } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取视频播放地址
     */
    suspend fun getPlayUrl(bvid: String, cid: Long): PlayUrlResponse? = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}/x/player/playurl?bvid=$bvid&cid=$cid&qn=80&fnval=4048&fnver=0&fourk=1"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://www.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext null

            gson.fromJson(body, PlayUrlResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取视频详情（选集列表）
     */
    suspend fun getVideoDetail(bvid: String): VideoDetailData? = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}/x/web-interface/view?bvid=$bvid"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://www.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext null

            val detailResponse = gson.fromJson(body, VideoDetailResponse::class.java)
            detailResponse.data
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取弹幕
     */
    suspend fun getDanmaku(cid: Long): String = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.bilibili.com/x/v1/dm/list.so?oid=$cid"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("Referer", "https://www.bilibili.com/")
                .build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
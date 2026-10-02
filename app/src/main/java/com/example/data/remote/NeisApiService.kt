package com.example.data.remote

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface NeisApiService {

  @GET("hub/mealServiceDietInfo")
  suspend fun getMealInfo(
    @Query("Type") type: String = "json",
    @Query("pIndex") pIndex: Int = 1,
    @Query("pSize") pSize: Int = 100,
    @Query("ATPT_OFCDC_SC_CODE") officeCode: String = "C10",
    @Query("SD_SCHUL_CODE") schoolCode: String = "7150597",
    @Query("MLSV_YMD") mealDate: String? = null,
    @Query("MLSV_FROM_YMD") fromDate: String? = null,
    @Query("MLSV_TO_YMD") toDate: String? = null,
    @Query("MMEAL_SC_CODE") mealCode: String? = null
  ): Response<ResponseBody>

  companion object {
    private const val BASE_URL = "https://open.neis.go.kr/"

    fun create(): NeisApiService {
      val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
      }

      val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

      return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .build()
        .create(NeisApiService::class.java)
    }
  }
}

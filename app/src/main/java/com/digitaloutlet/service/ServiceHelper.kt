package com.digitaloutlet.service

import android.util.Log
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.utils.DOPrefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceHelper {

    val TAG = ServiceHelper::class.java.simpleName

    var logging = HttpLoggingInterceptor()
    private const val BASE_URL = "https://yobhaiya.in/api/";
    const val PRODUCTS_BASE_URL = "https://yobhaiya.in/upload_docs/admin/products/"
    var OUTLET_URL = "https://yobhaiya.in/api/digioutlet/digitalMenuMerchant?Merchant_id="

    init {
        logging.level = HttpLoggingInterceptor.Level.BODY
    }
    fun getClient(): APIClient {
        val resInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)
                val newResponse = response.newBuilder()
                val strResponse = response.body()?.string()

                // Print Request/Response
                /*Log.d(TAG, "URL :: ${request.url()}")
                if (request.method().equals("post", true)) {
                    Log.d(TAG, "Request :: ${request.body()}")
                }
                printResult(strResponse)*/

                newResponse.body(ResponseBody.create(response.body()?.contentType(), strResponse))
                return newResponse.build()
            }
        }

        val okHttpClient = OkHttpClient.Builder().let {
            it.connectTimeout(1, TimeUnit.MINUTES)
            it.readTimeout(30, TimeUnit.SECONDS)
            it.writeTimeout(1, TimeUnit.MINUTES)
            it.addInterceptor(logging)
            it.addInterceptor(resInterceptor)
            it.build()
        }

        val retrofit = Retrofit.Builder().let {
            it.baseUrl(BASE_URL)
            it.addConverterFactory(GsonConverterFactory.create())
            it.client(okHttpClient)
            it.build()
        }

        return retrofit.create(APIClient::class.java)
    }

    private fun printResult(strResponse: String?) {
        val maxLogSize = 2048
        strResponse?.let {
            for (i in 0..it.length / maxLogSize) {
                val start = i * maxLogSize
                var end = (i + 1) * maxLogSize
                end = if (end > it.length) {
                    it.length
                } else {
                    end
                }
                Log.v(TAG, "Response :: " + it.substring(start, end))
            }
        }
    }
}
package com.estyle.httpmockdemo

import android.app.Application

import com.estyle.httpmock.HttpMock
import com.estyle.httpmock.HttpMockGenerator
import com.readystatesoftware.chuck.ChuckInterceptor

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {

    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        initRetrofit()
    }

    private fun initRetrofit() {
        val builder = OkHttpClient.Builder()
            .addInterceptor(ChuckInterceptor(this))

        // 添加HttpMockInterceptor后依然返回OkHttpClient.Builder
        val client = HttpMock.addHttpMockInterceptor(
            builder,
            this,
            true,// true表示全局可用假数据，false表示全局禁用，release版不受影响
            HttpMockGenerator::class.java
        )
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://www.qubaobei.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }
}

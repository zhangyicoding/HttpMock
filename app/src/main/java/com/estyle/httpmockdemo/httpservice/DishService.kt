package com.estyle.httpmockdemo.httpservice

import com.estyle.httpmock.annotation.HttpMock
import com.estyle.httpmockdemo.entity.DishEntity

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface DishService {

    @HttpMock(fileName = "dish.json", enable = true, delayMillis = 3500L)
    @GET("ios/cf/dish_list.php?stage_id=1&limit=10")
    fun getDish(@Query("page") page: Int): Observable<DishEntity>
}
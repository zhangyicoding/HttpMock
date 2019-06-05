package com.estyle.httpmockdemo.entity

data class DishEntity(
    val `data`: List<DataEntity>,
    val ret: Int
) {
    data class DataEntity(
        val collect_num: String,
        val food_str: String,
        val id: String,
        val num: Int,
        val pic: String,
        val title: String
    )
}
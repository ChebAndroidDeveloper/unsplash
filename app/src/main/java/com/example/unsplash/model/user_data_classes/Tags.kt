package com.example.unsplash.model.user_data_classes

data class Tags(
    val aggregated: List<Aggregated>,
    val custom: List<Custom>
)
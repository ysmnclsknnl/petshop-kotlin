package com.example.petshopkotlin.dto

import com.example.petshopkotlin.collection.Role

class TokenDTO(
    private val email: String? = null,
    private val role: Role? = null,
    private val accessToken: String? = null,
    private val refreshToken: String? = null,
)


package com.example.civicconnect.model

data class Representative(
    val id: String = "",
    val name: String = "",
    val position: String = "",
    val party: String = "",
    val photoUrl: String? = null,
    val email: String = "",
    val phone: String = "",
    val office: String = "",
    val district: String = ""
) 
package com.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AppConfig(val url: String, val user: String, val pass: String)

fun loadConfigFromResources(resourceName: String): AppConfig {
    val json = Json { ignoreUnknownKeys = true }
    val resourceText = AppConfig::class.java.classLoader.getResource(resourceName)?.readText()
    return json.decodeFromString(resourceText ?: "")
}
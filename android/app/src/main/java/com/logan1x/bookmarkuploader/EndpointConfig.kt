package com.logan1x.bookmarkuploader

import android.content.Context
import android.net.Uri

object EndpointConfig {
  private const val prefsName = "bookmark_uploader_prefs"
  private const val endpointKey = "endpoint_url"
  private const val bearerTokenKey = "bearer_token"

  const val defaultEndpoint = "http://192.168.31.176:8787/v1/items"

  fun getEndpoint(context: Context): String {
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    return prefs.getString(endpointKey, defaultEndpoint).orEmpty().ifBlank { defaultEndpoint }
  }

  fun saveEndpoint(context: Context, endpoint: String) {
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    prefs.edit().putString(endpointKey, endpoint.trim()).apply()
  }

  fun resetEndpoint(context: Context) {
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    prefs.edit().remove(endpointKey).apply()
  }

  fun getBearerToken(context: Context): String {
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    return prefs.getString(bearerTokenKey, "").orEmpty().trim()
  }

  fun saveBearerToken(context: Context, token: String) {
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    prefs.edit().putString(bearerTokenKey, token.trim()).apply()
  }

  fun resetBearerToken(context: Context) {
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    prefs.edit().remove(bearerTokenKey).apply()
  }

  fun isValidEndpoint(endpoint: String): Boolean {
    val candidate = endpoint.trim()
    if (candidate.isEmpty()) return false

    val uri = runCatching { Uri.parse(candidate) }.getOrNull() ?: return false
    val scheme = uri.scheme?.lowercase()

    return (scheme == "http" || scheme == "https") && !uri.host.isNullOrBlank()
  }
}

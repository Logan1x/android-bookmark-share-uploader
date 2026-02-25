package com.logan1x.bookmarkuploader

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.regex.Pattern

class ShareReceiverActivity : Activity() {

  // MVP: hardcode your LAN endpoint. Later we can add a settings UI.
  // Confirmed by you: http allowed + no auth.
  private val endpointUrl = "http://192.168.31.176:8787/v1/items"

  private val http = OkHttpClient()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Immediately handle the share, show a small toast, and exit.
    try {
      val action = intent?.action
      val type = intent?.type

      if (Intent.ACTION_SEND != action || type != "text/plain") {
        toast("Unsupported share")
        finish()
        return
      }

      val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
      val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT)

      val url = extractFirstUrl(sharedText)
      if (url == null) {
        toast("No URL found")
        finish()
        return
      }

      val content = buildString {
        if (!subject.isNullOrBlank()) {
          append(subject.trim())
          append("\n")
        }
        append(sharedText.trim())
      }

      upload(url = url, content = content, rawText = sharedText)

    } catch (e: Exception) {
      toast("Failed")
      finish()
    }
  }

  private fun upload(url: String, content: String, rawText: String) {
    toast("Uploading…")

    val json = JSONObject().apply {
      put("url", url)
      put("content", content.ifBlank { url })
      put("type", "link")
      put("source", "manual")
      put("raw", JSONObject().apply {
        put("sharedText", rawText)
        put("sourceApp", callingPackage ?: "")
      })
    }

    val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val req = Request.Builder()
      .url(endpointUrl)
      .post(body)
      .build()

    // Fire-and-forget on a background thread (MVP).
    Thread {
      try {
        http.newCall(req).execute().use { resp ->
          runOnUiThread {
            if (resp.isSuccessful) {
              toast("Saved")
            } else {
              toast("Failed (${resp.code})")
            }
            finish()
          }
        }
      } catch (e: Exception) {
        runOnUiThread {
          toast("Failed (network)")
          finish()
        }
      }
    }.start()
  }

  private fun toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
  }

  private fun extractFirstUrl(text: String): String? {
    // Basic URL matcher for MVP.
    val pattern = Pattern.compile("https?://[^\\s]+", Pattern.CASE_INSENSITIVE)
    val m = pattern.matcher(text)
    return if (m.find()) m.group() else null
  }
}

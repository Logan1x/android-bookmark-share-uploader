package com.logan1x.bookmarkuploader

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

class SettingsActivity : Activity() {

  private lateinit var endpointInput: EditText
  private lateinit var tokenInput: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    endpointInput = findViewById(R.id.endpointInput)
    tokenInput = findViewById(R.id.tokenInput)
    val saveButton: Button = findViewById(R.id.saveEndpointButton)
    val resetButton: Button = findViewById(R.id.resetEndpointButton)
    val tokenInfoButton: ImageButton = findViewById(R.id.tokenInfoButton)

    endpointInput.setText(EndpointConfig.getEndpoint(this))
    tokenInput.setText(EndpointConfig.getBearerToken(this))

    tokenInfoButton.setOnClickListener {
      AlertDialog.Builder(this)
        .setTitle(R.string.token_info_title)
        .setMessage(R.string.token_info_message)
        .setPositiveButton(android.R.string.ok, null)
        .show()
    }

    saveButton.setOnClickListener {
      val endpoint = endpointInput.text?.toString().orEmpty().trim()
      if (!EndpointConfig.isValidEndpoint(endpoint)) {
        toast("Enter a valid http/https URL")
        return@setOnClickListener
      }

      EndpointConfig.saveEndpoint(this, endpoint)
      EndpointConfig.saveBearerToken(this, tokenInput.text?.toString().orEmpty())
      toast("Settings saved")
      finish()
    }

    resetButton.setOnClickListener {
      EndpointConfig.resetEndpoint(this)
      EndpointConfig.resetBearerToken(this)
      endpointInput.setText(EndpointConfig.defaultEndpoint)
      tokenInput.setText("")
      toast("Reset to default")
    }
  }

  private fun toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
  }
}

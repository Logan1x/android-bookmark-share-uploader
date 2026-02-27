package com.logan1x.bookmarkuploader

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SettingsActivity : Activity() {

  private lateinit var endpointInput: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    endpointInput = findViewById(R.id.endpointInput)
    val saveButton: Button = findViewById(R.id.saveEndpointButton)
    val resetButton: Button = findViewById(R.id.resetEndpointButton)

    endpointInput.setText(EndpointConfig.getEndpoint(this))

    saveButton.setOnClickListener {
      val endpoint = endpointInput.text?.toString().orEmpty().trim()
      if (!EndpointConfig.isValidEndpoint(endpoint)) {
        toast("Enter a valid http/https URL")
        return@setOnClickListener
      }

      EndpointConfig.saveEndpoint(this, endpoint)
      toast("Endpoint saved")
      finish()
    }

    resetButton.setOnClickListener {
      EndpointConfig.resetEndpoint(this)
      endpointInput.setText(EndpointConfig.defaultEndpoint)
      toast("Reset to default")
    }
  }

  private fun toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
  }
}

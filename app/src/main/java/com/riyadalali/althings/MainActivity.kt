package com.riyadalali.althings

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var ssidEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var statusButton: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ssidEditText = findViewById(R.id.ssidEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        connectButton = findViewById(R.id.connectButton)
        statusButton = findViewById(R.id.statusButton)
        textView = findViewById(R.id.textView)

        connectButton.setOnClickListener {
            val ssid = ssidEditText.text.toString()
            val password = passwordEditText.text.toString()
            SendDataTask().execute("http://192.168.4.1/connect", ssid, password)
        }

        statusButton.setOnClickListener {
            CheckStatusTask().execute("http://192.168.4.1/status")
        }

    }

    private inner class SendDataTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            val url = params[0]
            val ssid = params[1]
            val password = params[2]
            return try {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                urlConnection.doOutput = true
                val outputStream = OutputStreamWriter(urlConnection.outputStream)
                val postData = "ssid=$ssid&password=$password"
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    "Data sent successfully"
                } else {
                    "Error: $responseCode"
                }
            } catch (e: Exception) {
                "Exception: ${e.message}"
            }
        }

        override fun onPostExecute(result: String) {
            textView.text = result
        }
    }

    private inner class CheckStatusTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            val url = params[0]
            return try {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"

                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = urlConnection.inputStream
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()
                    bufferedReader.useLines { lines -> lines.forEach { response.append(it) } }
                    bufferedReader.close()
                    response.toString()
                } else {
                    "Error: $responseCode"
                }
            } catch (e: Exception) {
                "Exception: ${e.message}"
            }
        }

        override fun onPostExecute(result: String) {
            textView.text = result
        }
    }
}

package com.example.internetconnectionexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.internetconnectionexercise.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.progressBar.visibility = View.INVISIBLE

        binding.btnSearch.setOnClickListener {
            searchBook()
        }

    }

    private fun searchBook() {
        binding.progressBar.visibility = View.VISIBLE
        val query = binding.edtInputBook.text.toString()
        val client = AsyncHttpClient()
        val url = "https://www.googleapis.com/books/v1/volumes?q=${query}"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.v("myTag", result)
                binding.progressBar.visibility = View.INVISIBLE

                try {
                    val jsonObject = JSONObject(result)
                    val itemsArray = jsonObject.getJSONArray("items")

                    var i = 0
                    var bookTitle = ""
                    var bookAuthor = ""

                    while (i < itemsArray.length()){
                        val book = itemsArray.getJSONObject(i)
                        val volumeInfo =  book.getJSONObject("volumeInfo")
                        try {
                            bookTitle = volumeInfo.getString("title")
                            bookAuthor = volumeInfo.getString("authors")
                        } catch (e: Exception){
                            e.printStackTrace()
                        }
                        i = itemsArray.length() // Chỉ lấy thông tin của đối tượng đầu tiên trong itemsArray
                    }
                    binding.apply {
                        tvTitleResult.text = bookTitle
                        tvAuthorResult.text = bookAuthor
                    }
                } catch (e: Exception){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when(statusCode){
                    401 -> "Bad request"
                    403 -> "Forbidden"
                    404 -> "Not Found"
                    else -> "${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}
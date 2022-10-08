package com.example.switcher

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.switcher.databinding.ActivityMainBinding
import com.example.switcher.api.SwitcherService
import com.example.switcher.models.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.IOException
import java.io.InputStream

private const val FILE_PATH : String = "sw.txt"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var fileReadError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.tvText.text = readFileFromAssets(FILE_PATH)

        val str = readFileFromAssets(FILE_PATH)
        val lines = str.split("\r?\n|\r".toRegex()).toTypedArray()

        val login = lines[0]
        val password = lines[1]
        val ipadress = lines[2]

        //outs.cgi?out0=1
        //val linkTrang = "http://81.25.229.50:8888/outs.cgi?out0=1"
        val linkTrang = "http://81.25.229.50:8888/st0.xml"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = object: StringRequest(Request.Method.GET, linkTrang,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: $response")
            },
            Response.ErrorListener { error ->
                Log.d("A", "Response is: $error")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic YWRtaW46OTkwMjU2"
                return headers
            }
        }

        queue.add(stringRequest)

        val retrofit = Retrofit.Builder().baseUrl("http://81.25.229.50:8888").addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        val switcherService = retrofit.create(SwitcherService::class.java)
        switcherService.getStatus("http://81.25.229.50:8888/st0.xml", "Basic YWRtaW46OTkwMjU2")
            ?.enqueue(object  : Callback<Status> {
                override fun onResponse(call: Call<Status>, response: retrofit2.Response<Status>) {
                    Log.i("Retrofit", "onResponse $response")
                }

                override fun onFailure(call: Call<Status>, t: Throwable) {
                    Log.i("Retrofit", "onFailure $t")
                }
            })
    }

    private fun readFileFromAssets(filename: String): String {
        fileReadError = false
        Log.i("MainActivity", "read file called")
        // A string variable to store the text from the text file
        var myOutput: String = ""
        // Declaring an input stream to read data
        val myInputStream: InputStream
        // Try to open the text file, reads
        // the data and stores it in the string
        try {
            myInputStream = assets.open(FILE_PATH)
            val size: Int = myInputStream.available()
            val buffer = ByteArray(size)
            myInputStream.read(buffer)
            myOutput = String(buffer)

            // Sets the TextView with the string
        } catch (e: IOException) {
            // Exception
            e.printStackTrace()
            fileReadError = true
            return "could not read file '\\Switcher\\app\\src\\main\\assets\\sw.txt'"
        }
        return myOutput
    }

    private fun volleyRequest(){
        val linkTrang = "http://81.25.229.50:8888/st0.xml"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = object: StringRequest(Request.Method.GET, linkTrang,
            Response.Listener<String> { response ->
                Log.d("A", "Response is: $response")
            },
            Response.ErrorListener { error ->
                Log.d("A", "Response is: $error")
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Basic YWRtaW46OTkwMjU2"
                return headers
            }
        }

        queue.add(stringRequest)
    }


}
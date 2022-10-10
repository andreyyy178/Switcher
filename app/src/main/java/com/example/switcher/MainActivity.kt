package com.example.switcher

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.switcher.api.SwitcherApi
import com.example.switcher.databinding.ActivityMainBinding
import com.example.switcher.models.SwitcherStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.IOException
import java.io.InputStream


//outs.cgi?out0=1
//val linkTrang = "http://81.25.229.50:8888/outs.cgi?out0=1"

private const val FILE_PATH : String = "sw.txt"
private const val SWITCH_STATUS_PATH : String = "st0.xml"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var fileReadError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromFile = getBaseUrlAndFormattedCredentialsFromAssetsTxtFile()
        val baseUrl = fromFile[0]
        val auth = fromFile[1]

        val viewModelFactory = MainViewModelFactory(baseUrl + SWITCH_STATUS_PATH, auth)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]


    }

    private fun readFileFromAssets(filename: String): String {
        fileReadError = false
        Log.i("MainActivity", "read file called")
        var myOutput = ""
        val myInputStream: InputStream
        try {
            myInputStream = assets.open(FILE_PATH)
            val size: Int = myInputStream.available()
            val buffer = ByteArray(size)
            myInputStream.read(buffer)
            myOutput = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            fileReadError = true
            return "could not read file '\\Switcher\\app\\src\\main\\assets\\sw.txt'"
        }
        return myOutput
    }

    private fun getBaseUrlAndFormattedCredentialsFromAssetsTxtFile(): Array<String> {
        //auth format: "Basic <<Base64(username:password)>>
        val str = readFileFromAssets(FILE_PATH)
        val lines =  str.split("\r?\n|\r".toRegex()).toTypedArray()
        val auth = lines[1]
        val byte = auth.toByteArray(charset("UTF-8"))
        val result: String = Base64.encodeToString(byte, Base64.DEFAULT)
        val convertedAuth = "Basic ".plus(result)
            .replace("\n","")
        lines[1] = convertedAuth
        return lines
    }

    private fun simpleVolleyRequestForInitialTesting() {
        val linkTran = "http://81.25.229.50:8888/st0.xml"

        val queue = Volley.newRequestQueue(this)

        val stringRequest = object: StringRequest(Request.Method.GET, linkTran,
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

    private fun simpleRetrofitStatusRequestViaCallback() {
        val retrofit = Retrofit.Builder().baseUrl("http://81.25.229.50:8888").addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        val switcherService = retrofit.create(SwitcherApi::class.java)
        switcherService.getStatusViaCallback("http://81.25.229.50:8888/st0.xml", "Basic YWRtaW46OTkwMjU2")
            ?.enqueue(object  : Callback<SwitcherStatus> {
                override fun onResponse(call: Call<SwitcherStatus>, response: retrofit2.Response<SwitcherStatus>) {
                    Log.i("Retrofit", "onResponse $response")
                }

                override fun onFailure(call: Call<SwitcherStatus>, t: Throwable) {
                    Log.i("Retrofit", "onFailure $t")
                }
            })
    }

}


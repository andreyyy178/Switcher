package com.example.switcher

import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.switcher.api.SwitcherApi
import com.example.switcher.databinding.ActivityMainBinding
import com.example.switcher.models.Switcher
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

        val fromFile = getBaseUrlAndFormattedCredentialsFromAssetsTxtFile()
        val baseUrl = fromFile[0]
        val auth = fromFile[1]
        val viewModelFactory = MainViewModelFactory(baseUrl, auth)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        val switches = arrayOf(binding.switch0, binding.switch1, binding.switch2, binding.switch3, binding.switch4)
        for((i, switch) in switches.withIndex()) {
            switch.setOnClickListener {
                viewModel.flipPower(i, switch.isChecked)
            }
        }

        viewModel.switcher.observe(this) { switcher ->
            binding.textViewTemp1.text = temperatureFormat(switcher.ia12)
            binding.textViewTemp2.text = temperatureFormat(switcher.ia0)
            binding.textViewTemp3.text = temperatureFormat(switcher.ia0)
            binding.textViewTemp4.text = temperatureFormat(switcher.ia13)

            binding.switch0.isChecked = getSwitchState(switcher.out0)
            binding.switch1.isChecked = getSwitchState(switcher.out1)
            binding.switch2.isChecked = getSwitchState(switcher.out2)
            binding.switch3.isChecked = getSwitchState(switcher.out3)
            binding.switch4.isChecked = getSwitchState(switcher.out4)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressGroup.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonRefresh.visibility = if (isLoading) View.GONE else View.VISIBLE
            if (isLoading) {
                binding.textViewStatus.text = "Загрузка..."
                binding.textViewStatus.setTextColor(Color.parseColor("#FF6200EE"))
            }
            else {
                binding.textViewStatus.text = "Ожидание запроса"
                binding.textViewStatus.setTextColor(Color.parseColor("#FFBB86FC"))
            }
        }
        viewModel.isError.observe(this)  { isError->
           if (isError) {
               binding.textViewStatus.text = "Повторите запрос"
               binding.textViewStatus.setTextColor(Color.parseColor("#FF0000"))
           }
        }
        viewModel.isStateUnknown.observe(this) { isStateUnknown->
            if (isStateUnknown) {
                binding.textViewStatus.text = "Состояние не известно"
                binding.textViewStatus.setTextColor(Color.parseColor("#FFBA01"))
            } else {
                binding.textViewStatus.text = "Ожидание запроса"
                binding.textViewStatus.setTextColor(Color.parseColor("#FFBB86FC"))
            }
        }

        binding.buttonTurnAllOn.setOnClickListener {
            for((i, switch) in switches.withIndex()) {
                switch.isChecked = true
            }
        }

        binding.buttonRefresh.setOnClickListener { viewModel.getStatus() }
    }

    //TODO: implement data binding and binding adapters
    private fun temperatureFormat(temperature: String): String {
        return buildString { append(temperature).insert((temperature.length - 1), ".").append(" ºC") }
    }
    private fun getSwitchState(out: Int): Boolean {
        return out != 1
    }

    //Want to drop a text file with network configuration for each employee to folder into assets as sw.txt
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
            ?.enqueue(object  : Callback<Switcher> {
                override fun onResponse(call: Call<Switcher>, response: retrofit2.Response<Switcher>) {
                    Log.i("Retrofit", "onResponse $response")
                }

                override fun onFailure(call: Call<Switcher>, t: Throwable) {
                    Log.i("Retrofit", "onFailure $t")
                }
            })
    }

}




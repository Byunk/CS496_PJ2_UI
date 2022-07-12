package com.example.cs496_pj2_ui.profile

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.cs496_pj2_ui.R
import com.example.cs496_pj2_ui.databinding.ProfileDailyScheduleAddActivityBinding
import com.example.cs496_pj2_ui.service.RetrofitService
import com.example.cs496_pj2_ui.service.SocketService
import com.example.cs496_pj2_ui.service.model.PromiseRequest
import com.example.cs496_pj2_ui.service.model.ResponseCode
import com.example.cs496_pj2_ui.service.model.UserData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProfileDailyScheduleAddActivity : AppCompatActivity() {

    private lateinit var binding: ProfileDailyScheduleAddActivityBinding
    private lateinit var id: String
    private lateinit var sender: UserData
    private lateinit var receiver: UserData

    private var year = -1
    private var month = -1
    private var date = -1
    private var time = -1
    private var duration = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileDailyScheduleAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val today = Calendar.getInstance()
        year = today.get(Calendar.YEAR)
        month = today.get(Calendar.MONTH)
        date = today.get(Calendar.DATE)
        time = today.get(Calendar.HOUR)*100 + today.get(Calendar.MINUTE)

        id = intent.getStringExtra("id")!!
        receiver = intent.getParcelableExtra("receiver")!!

        binding.tvReceiverName.text = receiver.name
        if (receiver.imgUrl == null) {
            binding.imgProfileAdd.setImageResource(R.drawable.account)
        } else {
            Glide.with(baseContext).load(receiver.imgUrl)
                .apply(RequestOptions().centerCrop())
                .into(binding.imgProfileAdd)
        }

        binding.numberPickerAdd.minValue = 10
        binding.numberPickerAdd.maxValue = 300

        binding.numberPickerAdd.setOnValueChangedListener { numberPicker, old, new ->
            duration = new
        }

        binding.btnCalendarAdd.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, date ->
                this.year = year
                this.month = month
                this.date = date

                val dateString = "${year}년 ${month+1}월 ${date}일"
                binding.tvDateAdd.text = dateString
            }
            DatePickerDialog(this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnTimeAdd.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                this.time = hour * 100 + minute
                val timeString = "${hour}시 ${minute}분"
                binding.tvTimeAdd.text = timeString
            }
            TimePickerDialog(this, timeSetListener, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), false).show()
        }


        binding.btnSaveAdd.setOnClickListener {
            val todo = binding.etTodoAdd.text.toString()
            val location = binding.etLocationAdd.text.toString()
            val message = binding.etMessageAdd.text.toString()

            val promiseRequest = PromiseRequest(
                id, "sender name", "sender Image", receiver.id, year, month+1, date, time, duration, todo, location, message
            )

            val call = RetrofitService.retrofitInterface.sendRequest(promiseRequest)
            call.enqueue(object: Callback<ResponseCode> {
                override fun onFailure(call: Call<ResponseCode>, t: Throwable) {
                    Log.e(RetrofitService.TAG, t.message!!)
                }

                override fun onResponse(
                    call: Call<ResponseCode>,
                    response: Response<ResponseCode>
                ) {
                    if (response.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(this@ProfileDailyScheduleAddActivity, "Request Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        binding.btnCancleAdd.setOnClickListener {
            finish()
        }
    }
}
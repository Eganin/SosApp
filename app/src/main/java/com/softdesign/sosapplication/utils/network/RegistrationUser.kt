package com.softdesign.sosapplication.utils.network

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import com.softdesign.sosapplication.mvp.map.MapYandexView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationUser(val name: String,
                       val email: String,
                       val password: String,
                       val numberUser: String,
                       val context: Context) {
    val json: JsonObject = JsonObject()

    fun registrationUser() {
        json.addProperty("name", name)
        json.addProperty("email", email)
        json.addProperty("password", password)
        json.addProperty("number", numberUser)

        API.service
                .registrationUser(json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "Ошибка сервера вы не зарегистрированы",
                                Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        try {
                            Log.d("registration",response.body()?.string()!!.toBoolean().toString())
                            if (response.body()?.string()!!.toBoolean()) {
                                Toast.makeText(context, "Вы успешно зарегистрированы",
                                        Toast.LENGTH_LONG).show()
                                val intent = Intent(context, MapYandexView::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Ошибка сервера вы не зарегистрированы",
                                        Toast.LENGTH_LONG).show()
                                val intent = Intent(context, MapYandexView::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent)
                            }
                        } catch (e: KotlinNullPointerException) {
                            Toast.makeText(context, "Ошибка сервера вы не зарегистрированы",
                                    Toast.LENGTH_LONG).show()
                            val intent = Intent(context, MapYandexView::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent)
                        }
                    }
                })
    }
}

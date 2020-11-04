package com.softdesign.sosapplication.utils.network

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.beust.klaxon.Klaxon
import com.google.gson.JsonObject
import com.softdesign.sosapplication.mvp.map.MapYandexView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthUser(val name: String, val password: String, val context: Context) {
    val json: JsonObject = JsonObject()

    fun authorizationUser() {

        json.addProperty("name", name)
        json.addProperty("password", password)

        API
                .service
                .authUser(body = json)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "Вы не вошли в систему", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        try {
                            val result = Klaxon().parse<ServerAnswer>(response.body()?.string()!!)
                            println(result?.server_answer == "true")
                            println(result?.server_answer)
                            if (result?.server_answer == "true") {
                                Toast.makeText(context, "Вы вошли", Toast.LENGTH_LONG).show()
                                val intent = Intent(context, MapYandexView::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context,
                                        "Вы не вошли в систему проверьте валидность данных",
                                        Toast.LENGTH_LONG).show()
                            }
                        } catch (e: KotlinNullPointerException) {
                            Toast.makeText(context,
                                    "Вы не вошли в систему проверьте валидность данных",
                                    Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }

                    }
                })
    }
}
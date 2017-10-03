package rolrence.calculator.converter

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlin.reflect.KClass

/**
 * Created by Rolrence on 10/3/2017.
 *
 */
class AvatarData(val url: String) {
    fun get(): String {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        try {
            val response = client.newCall(request).execute()
            return response.body().string()
        } catch (e: Exception) {
            // Log.w("AvatarData", e.message)
            // throw Exception("error while get exchange rate info")
            throw e
        }
    }
}
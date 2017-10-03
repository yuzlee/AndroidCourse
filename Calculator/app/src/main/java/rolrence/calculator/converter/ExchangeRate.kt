package rolrence.calculator.converter

import com.google.gson.Gson
import java.io.Serializable
import java.text.DecimalFormat

/**
 * Created by Rolrence on 10/3/2017.
 *
 */
class Message {
    data class Data(val buyPic: Double,
                    val closePri: Double,
                    val code: String,
                    val color: String,
                    val currency: String,
                    val datatime: String,
                    val date: String,
                    val diffAmo: Double,
                    val diffPer: String,
                    val highPic: Double,
                    val lowPic: Double,
                    val openPri: Double,
                    val range: String,
                    val sellPri: Double,
                    val yesPic: Double
    )

    data class Result(val data1: Data,
                      val data2: Data,
                      val data3: Data,
                      val data4: Data,
                      val data5: Data,
                      val data6: Data,
                      val data7: Data,
                      val data8: Data,
                      val data9: Data,
                      val data10: Data,
                      val data11: Data,
                      val data12: Data,
                      val data13: Data
    )

    data class Msg(val error_code: Int, val reason: String, val result: Result)

    companion object {
        fun get() = Gson().fromJson(
                AvatarData("http://api.avatardata.cn/Currency/CurrencyList?key=609f0f92450e40bdbe18860e2cf553cf").get(),
                Msg::class.java)
    }
}

class ExchangeRate: Serializable {
    val kindList = listOf(
            "美元",
            "澳元",
            "欧元",
            "英镑",
            "新西兰元",
            "加元",
            "瑞郎",
            "人民币",
            "港元",
            "日元",
            "马币",
            "新加坡元",
            "台币"
    )

    val msg = Message.get()

    // US dollar to everything
    val simpleRate = mapOf<String, Double>(
            "美元" to 1.0,
            "澳元" to (1 / msg.result.data1.buyPic),
            "欧元" to (1 / msg.result.data3.buyPic),
            "英镑" to (1 / msg.result.data4.buyPic),
            "新西兰元" to (1 / msg.result.data5.buyPic),
            "加元" to (msg.result.data6.buyPic),
            "瑞郎" to (msg.result.data7.buyPic),
            "人民币" to (msg.result.data8.buyPic),
            "港元" to (msg.result.data9.buyPic),
            "日元" to (msg.result.data10.buyPic),
            "马币" to (msg.result.data11.buyPic),
            "新加坡元" to (msg.result.data12.buyPic),
            "台币" to (msg.result.data13.buyPic)
    )

    fun convert(from: String, to: String): (Double) -> String {
        val rate = simpleRate.getOrDefault(to, 0.0) / simpleRate.getOrDefault(from, 1.0)
        return { DecimalFormat("#0.00").format(it * rate) }
    }

    fun list(from: String, value: Double): Map<String, String> {
        val m = mutableMapOf<String, String>()
        for (k in kindList) {
            if (k != from) {
                m[k] = convert(from, k)(value)
            }
        }
        return m.toMap()
    }
}
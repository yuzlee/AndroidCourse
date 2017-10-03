package rolrence.calculator.converter

import com.google.gson.Gson

/**
 * Created by Rolrence on 10/3/2017.
 *
 */
class MeasureUnit {
    class Message {
        data class Item(val unit: Int, val name: String, val value: String)
        data class Msg(val error_code: Int, val reason: String,
                       val result: List<Item>)

        companion object {
            fun get(value: String, unit: Int) = Gson().fromJson(
                    AvatarData("http://api.avatardata.cn/UnitConvert/Weight?key=0c153a338b2a4f11aa5a0a575aa79c62&value=$value&unit=$unit")
                            .get(), Msg::class.java)
        }
    }

    companion object {
        val unitList = mapOf(
                "公里" to 1, "km" to 1,
                "米" to 2, "m" to 2,
                "分米" to 3, "dm" to 3,
                "厘米" to 4, "cm" to 4,
                "毫米" to 5, "mm" to 5,
                "微米" to 6, "um" to 6,
                "里" to 7,
                "丈" to 8,
                "尺" to 9,
                "寸" to 10,
                "分" to 11,
                "厘" to 12,
                "英寸" to 13, "in" to 13,
                "码" to 14, "yd" to 14,
                "英尺" to 15, "ft" to 15,
                "海里" to 16, "nmi" to 16
        )

        fun convert(value: String, unit: String) = Message.get(value, unitList[unit]!!).result.map {
            Pair(it.name, it.value)
        }.toMap()

    }
}
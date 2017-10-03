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
            fun get(type: String, value: String, unit: Int) = Gson().fromJson(
                    AvatarData("http://api.avatardata.cn/UnitConvert/$type?key=0c153a338b2a4f11aa5a0a575aa79c62&value=$value&unit=$unit")
                            .get(), Msg::class.java)
        }
    }

    companion object {
        val unitKindList = mapOf(
                "长度" to "Length",
                "面积" to "Area",
                "重量" to "Weight"
        )
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
                "海里" to 16, "nmi" to 16,

                "平方公里" to 1,
                "平方米" to 2,
                "平方分米" to 3,
                "平方厘米" to 4,
                "平方毫米" to 5,
                "平方英里" to 6,
                "平方英尺" to 7,
                "平方英寸" to 8,
                "公顷" to 9,
                "平方码" to 10,
                "市亩" to 11,
                "英亩" to 12,

                "吨" to 1,
                "公斤" to 2,
                "克" to 3,
                "毫克" to 4,
                "市斤" to 5,
                "担" to 6,
                "两" to 7,
                "钱" to 8,
                "金衡磅" to 9,
                "金衡盎司" to 10,
                "英钱" to 11,
                "金衡格令" to 12,
                "长吨" to 13,
                "短吨" to 14,
                "英担" to 15,
                "美担" to 16,
                "英石" to 17,
                "磅" to 18,
                "盎司" to 19,
                "打兰" to 20,
                "格令" to 21
        )

        fun list(type: String, value: String, unit: String) = Message.get(
                unitKindList[type]!!, value, unitList[unit]!!).result.map {
            Pair(it.name, it.value)
        }.toMap()

    }
}
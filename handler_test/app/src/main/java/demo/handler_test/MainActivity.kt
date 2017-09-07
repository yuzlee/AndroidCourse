package demo.handler_test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val h = object : Handler() {
        override fun handleMessage(msg: Message) {
            val bundle = msg.peekData()
            Toast.makeText(this@MainActivity, bundle.getString("text"), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btnClick).setOnClickListener {
            Thread {
                val string = "Hello, World! (From New Thread)"

                val msg = Message()
                val bundle = Bundle()
                bundle.putString("text", string)
                msg.data = bundle
                h.sendMessage(msg)
            }.start()
        }
    }
}

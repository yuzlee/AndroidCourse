package demo.intent_test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.support.v4.app.NotificationCompat.getExtras
import android.content.Intent
import android.widget.Button


class Main2Activity : AppCompatActivity() {

    companion object {
        val RESULT_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val intent = intent
        val bundle = intent.extras
        val str = bundle.getString("test")
        val txtSecond = findViewById(R.id.txtSecond) as TextView
        txtSecond.setText(str)

        val btnTest = findViewById(R.id.btnTest) as Button
        btnTest.setOnClickListener({
            val intent2 = Intent()
            intent2.putExtra("back", "Back Data (From Activity 2)")
            setResult(RESULT_CODE, intent2)
            finish()
        })
    }
}

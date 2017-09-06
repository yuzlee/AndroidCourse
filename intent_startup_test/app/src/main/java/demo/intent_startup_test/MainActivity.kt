package demo.intent_startup_test

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btnClick).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rolrence.cn"))
            startActivity(intent)
        }
    }
}

package demo.intent_test

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.R.attr.data
import android.support.v4.app.NotificationCompat.getExtras
import android.widget.EditText


class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.btnTest).setOnClickListener {
            val intent = Intent()
            intent.setClass(this, Main2Activity::class.java)
            intent.putExtra("test", (findViewById(R.id.txtMsg) as EditText).text.toString())
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Main2Activity.RESULT_CODE) {
                val bundle = data!!.getExtras()
                val str = bundle.getString("back")
                Toast.makeText(this@MainActivity, str, Toast.LENGTH_LONG).show()
            }
        }
    }
}

package demo.local_file

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.MODE_PRIVATE
import android.os.Environment
import android.text.BoringLayout
import android.widget.EditText
import android.widget.Toast
import android.os.Environment.getExternalStorageDirectory
import java.io.*


class MainActivity : AppCompatActivity() {
    val filename = "data.txt"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtName = findViewById(R.id.txtName) as EditText
        val txtNo = findViewById(R.id.txtNo) as EditText
        val txtEmail = findViewById(R.id.txtEmail) as EditText

        findViewById(R.id.btnSubmit).setOnClickListener {
            val name = txtName.text.toString()
            val no = txtNo.text.toString()
            val email = txtEmail.text.toString()
            if (this.save(name, no, email)) {
                Toast.makeText(this, "Ok!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById(R.id.btnLoad).setOnClickListener {
            val info = this.readInfo()
            if (info != null && info.size == 3) {
                txtName.setText(info[0])
                txtNo.setText(info[1])
                txtEmail.setText(info[2])
            } else {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById(R.id.btnClear).setOnClickListener {
            txtName.setText("")
            txtNo.setText("")
            txtEmail.setText("")
        }
    }

    fun save(name: String, no: String, email: String): Boolean {
        val string = "$name,$no,$email"
        try {
            val outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            outputStream.write(string.toByteArray())
            outputStream.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun readInfo(): List<String>? {
        try {
            val fin = openFileInput(filename)
            val length = fin.available()
            val buffer = ByteArray(length)
            fin.read(buffer)
            val res = String(buffer)
            fin.close()
            val info = res.split(",")
            return info
        } catch (e: Exception) {
            return null
        }
    }
}

package demo.dialog_test

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = LayoutInflater.from(this)
        val dialogView = factory.inflate(R.layout.activity_dialog, null)
        var dlg = AlertDialog.Builder(this)
                .setTitle("Login")
                .setView(dialogView)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    try {
                        var account = (dialogView.findViewById(R.id.txtAccount) as EditText).getText().toString().trim()
                        var password = (dialogView.findViewById(R.id.txtPassword) as EditText).getText().toString().trim()
                        if (account == "abc" && password == "123") {
                            Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        (findViewById(R.id.txtInfo) as TextView).setText(e.message);
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show()
                })
                .create()

        (findViewById(R.id.btnLogin) as Button).setOnClickListener({
            dlg.show()
        })

        (findViewById(R.id.btnMessage) as Button).setOnClickListener({
            AlertDialog.Builder(this)
                    .setTitle("message")
                    .setMessage("Hello World!")
                    .create()
                    .show()
        })
    }
}

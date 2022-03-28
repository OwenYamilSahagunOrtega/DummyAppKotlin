package com.example.dummyappkotlin

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var switchTest: Switch? = null
    var contentExist = false
    var JsonPayload = ""
    var configG7 = ConfigG7()

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchTest = findViewById(R.id.switchtest)

        //Read the location of the content provider and using a cursor to navigate the data.
        val uri = Uri.parse(URI_DIRECTORY)
        val c = contentResolver.query(uri!!, null, null, null, null)

        /*Verify if the database exist in the phone. In case that exist will retrieve the
        information from it.
         */
        if (c != null) {
            ReadContentProvider(uri!!)
            contentExist = true
        }
    }

    // this is just to reload on resume (It wont be use in g7)
    override fun onResume() {
        if (contentExist == true) {
            ReadContentProvider(Uri.parse(URI_DIRECTORY))
        }
        super.onResume()
    }

    /*We use a cursor to navigate the database, that right now It's just saving one value
    * we point to that value and retrieve the information to transformet it to a json
    * once is done we read the new value in order to show/hide the switch*/
    @SuppressLint("Range")
    fun ReadContentProvider(uri: Uri?) {
        val c = contentResolver.query(uri!!, null, null, null, null)
        c!!.moveToLast()
        JsonPayload = JsonConverter(c).toString()
        c.close()
        createConfigObject()
        try {
            switchTest!!.visibility = if (configG7.switchStatus.equals("1")) View.VISIBLE else View.GONE
        } catch (ex: Exception) {
            Log.e("go", ex.message!!)
        }
    }

    /*We read the information from the json file and filled in to our class "ConfigG7" values */
    private fun createConfigObject() {
        try {
            val jsonObject = JSONObject(JsonPayload)
            configG7.switchStatus = jsonObject.getString("flagSwitch")
        } catch (ex: Exception) {
            Log.e("er", ex.message!!)
        }
    }

    /*We convert the data from the only row in to a json*/
    fun JsonConverter(cursor: Cursor?): JSONObject {
        cursor!!.moveToLast()
        val totalColumn = cursor.columnCount
        val rowObject = JSONObject()
        for (i in 0 until totalColumn) {
            if (cursor.getColumnName(i) != null) {
                try {
                    rowObject.put(
                        cursor.getColumnName(i),
                        cursor.getString(i)
                    )
                } catch (e: Exception) {
                    Log.d("Error", e.message!!)
                }
            }
        }
        return rowObject
    }

    /*The direction from where are we get the data and we are pointing to the package in the
    * manifest by queries*/
    companion object {
        var URI_DIRECTORY = "content://com.example.configuratorappkotlin/flagName"
    }
}
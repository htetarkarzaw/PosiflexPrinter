package com.htetarkarzaw.printer

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pos.printer.PrinterFunctions
import com.pos.printer.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var portName = "/dev/ttyACM1"
    var portSettings = 115200
    private var deviceId=""
    private var deviceName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val androidId = Settings.Secure.getString(contentResolver,
                Settings.Secure.ANDROID_ID)
            deviceId = androidId
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        } catch (e: Exception) {
            Log.e("DeviceIdException", e.localizedMessage ?: "")
        }
        btnConnect.setOnClickListener {
            if(PrinterFunctions.OpenPort2(portName, portSettings)==0){
                tvStatus.text = "Success"
            }else{
                tvStatus.text = "Fail"
            }
        }
        btnPrint.setOnClickListener {
            var textData = "Hello World Hello World Hello Wo"
            textData += "\\n"
            textData += "Hello World Hello World Hello Wo"
            textData += "\\n"
            printPosiflexText(textData)
        }
    }

    private fun printPosiflexText(textData: String) {
        PrinterFunctions.PrintText(
            portName,
            portSettings,
            0, 0, 0, 0, 0, 0, 0, 0,
            textData);

        PrinterFunctions.PreformCut(portName, portSettings,1);
    }
}
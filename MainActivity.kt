package com.example.kalorymetr

import android.content.Context
import android.content.DialogInterface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity(), SensorEventListener{

    var sensorManager: SensorManager? = null
    var totalSteps = 0f
    var previousTotalSteps = 0f
    var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //resetSteps()
        loadData()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun speedCalc(timeVar: Float, distanceVar: Float): Float{
        var speedMin: Float = distanceVar/timeVar
        var speedH: Float = speedMin * 60
        return speedH
    }

    fun messageBoxFun() {
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Too high or to low speed!")
        builder.setMessage("Change values or change to right activity")
        builder.setIcon(R.drawable.ic_launcher_background)

        builder.setPositiveButton("Ok" , DialogInterface.OnClickListener{dialog, which -> dialog.dismiss()})

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    

    var tymczasowa = 0f
    var resultVar =0
    var steps = 0

    fun cycFun(view: View) {
        val kcal = findViewById<TextView>(R.id.kaloriedzien)
        val timeVal = findViewById<EditText>(R.id.recznieczas).text.toString().toFloat()
        val distance = findViewById<EditText>(R.id.reczniedystans).text.toString().toFloat()
        var speedH = speedCalc(timeVal, distance).toInt()
        var timef: Float = timeVal/60

        val kcalOnHourless24: Float = 400f
        val kcalOnHourmore24: Float = 800f
        if (speedH > 50 || speedH < 13)
        {
            messageBoxFun()
        }
        else if (speedH >= 13 && speedH < 24)
        {
            var resultoffinalkcal = (kcalOnHourless24*timef)
            resultVar += resultoffinalkcal.toInt()
            kcal.text = "$resultVar"
        }
        else
        {
            var resultoffinalkcal = (kcalOnHourmore24*timef)
            resultVar += resultoffinalkcal.toInt()
            kcal.text = "$resultVar"
        }

    }

    fun runFun(view: View) {
        val kcal = findViewById<TextView>(R.id.kaloriedzien)
        val timeVal = findViewById<EditText>(R.id.recznieczas).text.toString().toFloat()
        val distance = findViewById<EditText>(R.id.reczniedystans).text.toString().toFloat()
        var speedH = speedCalc(timeVal, distance).toInt()
        var timef: Float = timeVal/60

        val kcalOnHourless8: Float = 400f
        val kcalOnHourmore8: Float = 550f
        if (speedH > 13 || speedH < 5)
        {
            messageBoxFun()
        }
        else if (speedH >= 5 && speedH < 8)
        {
            var resultoffinalkcal = (kcalOnHourless8*timef)
            resultVar += resultoffinalkcal.toInt()
            kcal.text = "$resultVar"
        }
        else
        {
            var resultoffinalkcal = (kcalOnHourmore8*timef)
            resultVar += resultoffinalkcal.toInt()
            kcal.text = "$resultVar"
        }

    }

    fun walkFun(view: View) {
        val kcal = findViewById<TextView>(R.id.kaloriedzien)
        val timeVal = findViewById<EditText>(R.id.recznieczas).text.toString().toFloat()
        val distance = findViewById<EditText>(R.id.reczniedystans).text.toString().toFloat()
        var speedH = speedCalc(timeVal, distance)
        var timef: Float = timeVal/60

        val kcalOnHourless4: Float = 200f
        val kcalOnHourmore4: Float = 300f
        if (speedH > 6 || speedH < 0.5)
        {
            messageBoxFun()
        }
        else if (speedH >= 0.5 && speedH < 3.5)
        {
            var resultoffinalkcal = (kcalOnHourless4*timef)
            resultVar += resultoffinalkcal.toInt()
            kcal.text = "$resultVar"
        }
        else
        {
            var resultoffinalkcal = (kcalOnHourmore4*timef)
            resultVar += resultoffinalkcal.toInt()
            kcal.text = "$resultVar"
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onResume() {
    super.onResume()
    running = true
    val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    if(stepSensor == null){
        Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
    }else{
        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }
}

    override fun onSensorChanged(event: SensorEvent?) {
        val stepsTaken = findViewById<TextView>(R.id.krokidziennie)
        val kcal = findViewById<TextView>(R.id.kaloriedzien)
        if(running){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            stepsTaken.text = ("Steps Today: $currentSteps")
            tymczasowa += 0.04f
            if (tymczasowa >= 1)
            {
                resultVar+= 1
                kcal.text = "$resultVar"
                tymczasowa = 0f
            }

        }
    }

    fun resetSteps(){
        val stepsTaken = findViewById<TextView>(R.id.krokidziennie)
        stepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset", Toast.LENGTH_SHORT).show()
        }

        stepsTaken.setOnClickListener {
            previousTotalSteps = totalSteps
            stepsTaken.text = 0.toString()
            saveData()
        }
    }

    fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("MainActivity","$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}
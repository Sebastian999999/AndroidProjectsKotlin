package com.hammadirfan.sensorexample

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hammadirfan.sensorexample.ui.theme.SensorExampleTheme

class MainActivity : ComponentActivity(),SensorEventListener {
    lateinit var sensorManager: SensorManager
    lateinit var sensor: Sensor
    var value:String by mutableStateOf("Initial Value")
    override fun onCreate(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        super.onCreate(savedInstanceState)
        setContent {
            SensorExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //var value as remember { mutableStateOf("Hello") }
                    Text(text = value)
                }
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {

        if (p0!!.values != null) {
            Toast.makeText(
                this, "X: " + p0.values[0] +
                        "---Y: " + p0.values[1] + "---Z: " + p0.values[2],
                Toast.LENGTH_LONG
            ).show()

            value="X: " + p0.values[0] +
                    "---Y: " + p0.values[1] + "---Z: " + p0.values[2]

        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Toast.makeText(this, "Sensor Accuracy Changed", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

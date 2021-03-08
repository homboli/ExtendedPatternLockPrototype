package me.gergelytusko.extendedpatternlockprototype;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class PatternLockSensorManager implements SensorEventListener {
    private SensorManager sensorManager;
    private PatternLockSensorData patternLockSensorData;
    private Context mContext;
    private Sensor accelerometerSensor;
    private Sensor gravitySensor;
    private Sensor gyroscopeSensor;
    private Sensor linearAccelerationSensor;
    private Sensor rotationVectorSensor;
    private Sensor gameRotationVectorSensor;

    //https://stackoverflow.com/questions/4870667/how-can-i-use-getsystemservice-in-a-non-activity-class-locationmanager
    PatternLockSensorManager(Context context){
        mContext = context;
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        patternLockSensorData = new PatternLockSensorData();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null){
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null){
            gameRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
            sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            patternLockSensorData.setAccelerometerXValue(event.values[0]);
            patternLockSensorData.setAccelerometerYValue(event.values[1]);
            patternLockSensorData.setAccelerometerZValue(event.values[2]);
        } else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
            patternLockSensorData.setGravityXValue(event.values[0]);
            patternLockSensorData.setGravityYValue(event.values[1]);
            patternLockSensorData.setGravityZValue(event.values[2]);
        }  else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            patternLockSensorData.setGyroscopeXValue(event.values[0]);
            patternLockSensorData.setGyroscopeYValue(event.values[1]);
            patternLockSensorData.setGyroscopeZValue(event.values[2]);
        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            patternLockSensorData.setLinearAccelerationXValue(event.values[0]);
            patternLockSensorData.setLinearAccelerationYValue(event.values[1]);
            patternLockSensorData.setLinearAccelerationZValue(event.values[2]);
        } else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            patternLockSensorData.setRotationVectorXValue(event.values[0]);
            patternLockSensorData.setRotationVectorYValue(event.values[1]);
            patternLockSensorData.setRotationVectorZValue(event.values[2]);
        } else if (sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            patternLockSensorData.setGameRotationVectorXValue(event.values[0]);
            patternLockSensorData.setGameRotationVectorYValue(event.values[1]);
            patternLockSensorData.setGameRotationVectorZValue(event.values[2]);
        }

    }

    public void unregisterListeners(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public PatternLockSensorData getPatternLockSensorData() {
        return patternLockSensorData;
    }
}

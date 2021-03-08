package me.gergelytusko.extendedpatternlockprototype;

public class PatternLockSensorData {
    private float accelerometerXValue;
    private float accelerometerYValue;
    private float accelerometerZValue;
    private float gravityXValue;
    private float gravityYValue;
    private float gravityZValue;
    private float gyroscopeXValue;
    private float gyroscopeYValue;
    private float gyroscopeZValue;
    private float linearAccelerationXValue;
    private float linearAccelerationYValue;
    private float linearAccelerationZValue;
    private float rotationVectorXValue;
    private float rotationVectorYValue;
    private float rotationVectorZValue;
    private float gameRotationVectorXValue;
    private float gameRotationVectorYValue;
    private float gameRotationVectorZValue;

    public PatternLockSensorData(PatternLockSensorData one){
        this.accelerometerXValue = one.getAccelerometerXValue();
        this.accelerometerYValue = one.getAccelerometerYValue();
        this.accelerometerZValue = one.getAccelerometerZValue();
        this.gravityXValue = one.getGravityXValue();
        this.gravityYValue = one.getGravityYValue();
        this.gravityZValue = one.getGravityZValue();
        this.gyroscopeXValue = one.getGyroscopeXValue();
        this.gyroscopeYValue = one.getGyroscopeYValue();
        this.gyroscopeZValue = one.getGyroscopeZValue();
        this.linearAccelerationXValue = one.getLinearAccelerationXValue();
        this.linearAccelerationYValue = one.getLinearAccelerationYValue();
        this.linearAccelerationZValue = one.getLinearAccelerationZValue();
        this.rotationVectorXValue = one.getRotationVectorXValue();
        this.rotationVectorYValue = one.getRotationVectorYValue();
        this.rotationVectorZValue = one.getRotationVectorZValue();
        this.gameRotationVectorXValue = one.getGameRotationVectorXValue();
        this.gameRotationVectorYValue = one.getGameRotationVectorYValue();
        this.gameRotationVectorZValue = one.getGameRotationVectorZValue();
    }

    public PatternLockSensorData(){}
    public void setAccelerometerXValue(float value) {
        this.accelerometerXValue = value;
    }

    public void setAccelerometerYValue(float value) {
        this.accelerometerYValue = value;
    }

    public void setAccelerometerZValue(float value) {
        this.accelerometerZValue = value;
    }

    public void setGravityXValue(float value) {
        this.gravityXValue = value;
    }

    public void setGravityYValue(float value) {
        this.gravityYValue = value;
    }

    public void setGravityZValue(float value) {
        this.gravityZValue = value;
    }

    public void setGyroscopeXValue(float value) {
        this.gyroscopeXValue = value;
    }

    public void setGyroscopeYValue(float value) {
        this.gyroscopeYValue = value;
    }

    public void setGyroscopeZValue(float value) {
        this.gyroscopeZValue = value;
    }

    public void setLinearAccelerationXValue(float value) {
        this.linearAccelerationXValue = value;
    }

    public void setLinearAccelerationYValue(float value) {
        this.linearAccelerationYValue = value;
    }

    public void setLinearAccelerationZValue(float value) {
        this.linearAccelerationZValue = value;
    }

    public void setRotationVectorXValue(float value) {
        this.rotationVectorXValue = value;
    }

    public void setRotationVectorYValue(float value) {
        this.rotationVectorYValue = value;
    }

    public void setRotationVectorZValue(float value) {
        this.rotationVectorZValue = value;
    }

    public void setGameRotationVectorXValue(float value) {
        this.gameRotationVectorXValue = value;
    }

    public void setGameRotationVectorYValue(float value) {
        this.gameRotationVectorYValue = value;
    }

    public void setGameRotationVectorZValue(float value) {
        this.gameRotationVectorZValue = value;
    }

    public float getAccelerometerXValue() {
        return accelerometerXValue;
    }

    public float getAccelerometerYValue() {
        return accelerometerYValue;
    }

    public float getAccelerometerZValue() {
        return accelerometerZValue;
    }

    public float getGravityXValue() {
        return gravityXValue;
    }

    public float getGravityYValue() {
        return gravityYValue;
    }

    public float getGravityZValue() {
        return gravityZValue;
    }

    public float getGyroscopeXValue() {
        return gyroscopeXValue;
    }

    public float getLinearAccelerationZValue() {
        return linearAccelerationZValue;
    }

    public float getGyroscopeYValue() {
        return gyroscopeYValue;
    }

    public float getGyroscopeZValue() {
        return gyroscopeZValue;
    }

    public float getGameRotationVectorYValue() {
        return gameRotationVectorYValue;
    }

    public float getLinearAccelerationXValue() {
        return linearAccelerationXValue;
    }

    public float getLinearAccelerationYValue() {
        return linearAccelerationYValue;
    }

    public float getRotationVectorXValue() {
        return rotationVectorXValue;
    }

    public float getRotationVectorYValue() {
        return rotationVectorYValue;
    }

    public float getRotationVectorZValue() {
        return rotationVectorZValue;
    }

    public float getGameRotationVectorXValue() {
        return gameRotationVectorXValue;
    }

    public float getGameRotationVectorZValue() {
        return gameRotationVectorZValue;
    }
}

package me.gergelytusko.extendedpatternlockprototype;

import android.icu.text.IDNA;
import android.util.Log;
import android.view.MotionEvent;

public class PatternPointData {
    private int row;
    private int column;
    private float xCoord;
    private float yCoord;
    private float pressure;
    private float size;
    private float orientation;
    private float rawX;
    private float rawY;
    private float touchMajor;
    private float touchMinor;
    private long time;
    private float xVelocity;
    private float yVelocity;
    private boolean isMissedPoint; //Point is added by heuristic
    private PatternLockSensorData sensorData;
    private String motionEventType;
    private Integer deltaTime;
    public PatternPointData(MotionEvent motionEvent, boolean isMissedPoint){
        this.xCoord = motionEvent.getX();
        this.yCoord = motionEvent.getY();
        this.pressure = motionEvent.getPressure();
        this.size = motionEvent.getSize();
        this.orientation = motionEvent.getOrientation();
        this.rawX = motionEvent.getRawX();
        this.rawY = motionEvent.getRawY();
        this.touchMajor = motionEvent.getTouchMajor();
        this.touchMinor = motionEvent.getTouchMinor();
        this.time = motionEvent.getEventTime();
        this.isMissedPoint = isMissedPoint;
        int action = motionEvent.getAction();
        this.sensorData = new PatternLockSensorData();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.motionEventType = "action_down";
                break;
            case MotionEvent.ACTION_UP:
                this.motionEventType = "action_up";
                break;
            case MotionEvent.ACTION_MOVE:
                this.motionEventType = "action_move";
                break;
            case MotionEvent.ACTION_CANCEL:
                this.motionEventType = "action_cancel";
                break;
        }
    }

    public PatternPointData(){}

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public float getXCoord() {
        return xCoord;
    }

    public void setXCoord(float xCoord) {
        this.xCoord = xCoord;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public float getRawX() {
        return rawX;
    }

    public void setRawX(float rawX) {
        this.rawX = rawX;
    }

    public float getRawY() {
        return rawY;
    }

    public void setRawY(float rawY) {
        this.rawY = rawY;
    }

    public float getTouchMajor() {
        return touchMajor;
    }

    public void setTouchMajor(float touchMajor) {
        this.touchMajor = touchMajor;
    }

    public float getTouchMinor() {
        return touchMinor;
    }

    public void setTouchMinor(float touchMinor) {
        this.touchMinor = touchMinor;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    public PatternLockSensorData getSensorData() {
        return sensorData;
    }

    public void setSensorData(PatternLockSensorData sensorData) {
        this.sensorData = new PatternLockSensorData(sensorData);
    }

    public boolean isMissedPoint() {
        return isMissedPoint;
    }

    public void setMissedPoint(boolean missedPoint) {
        isMissedPoint = missedPoint;
    }

    public float getyCoord() {
        return yCoord;
    }

    public void setyCoord(float yCoord) {
        this.yCoord = yCoord;
    }

    public String getMotionEventType() {
        return motionEventType;
    }

    public void setMotionEventType(String motionEventType) {
        this.motionEventType = motionEventType;
    }

    public Double getFeatureValue(String feature) {
        switch(feature) {
            case "xVelocity":
                return Double.valueOf(getxVelocity());
            case "yVelocity":
                return Double.valueOf(getyVelocity());
            case "touchMajor":
                return Double.valueOf(getTouchMajor());
            case "touchMinor":
                return Double.valueOf(getTouchMinor());
            case "orientation":
                return Double.valueOf(getOrientation());
            case "xcoord":
                return  Double.valueOf(getXCoord());
            case "yCoord":
                return  Double.valueOf(getyCoord());
            case "size":
                return  Double.valueOf(getSize());
            case "pressure":
                return  Double.valueOf(getPressure());
            case "gameRotationVectorXValue":
                return  Double.valueOf(getSensorData().getGameRotationVectorXValue());
            case "gameRotationVectorYValue":
                return  Double.valueOf(getSensorData().getGameRotationVectorYValue());
            case "gameRotationVectorZValue":
                return  Double.valueOf(getSensorData().getGameRotationVectorZValue());
            case "gravityXValue":
                return  Double.valueOf(getSensorData().getGravityXValue());
            case "gravityYValue":
                return  Double.valueOf(getSensorData().getGravityYValue());
            case "gravityZValue":
                return  Double.valueOf(getSensorData().getGravityZValue());
            case "accelerometerXValue":
                return  Double.valueOf(getSensorData().getAccelerometerXValue());
            case "accelerometerYValue":
                return  Double.valueOf(getSensorData().getAccelerometerYValue());
            case "accelerometerZValue":
                return  Double.valueOf(getSensorData().getAccelerometerZValue());
            case "linearAccelerationXValue":
                return  Double.valueOf(getSensorData().getLinearAccelerationXValue());
            case "linearAccelerationYValue":
                return  Double.valueOf(getSensorData().getLinearAccelerationYValue());
            case "linearAccelerationZValue":
                return  Double.valueOf(getSensorData().getLinearAccelerationZValue());
            case "rotationVectorXValue":
                return  Double.valueOf(getSensorData().getRotationVectorXValue());
            case "rotationVectorYValue":
                return  Double.valueOf(getSensorData().getRotationVectorYValue());
            case "rotationVectorZValue":
                return  Double.valueOf(getSensorData().getRotationVectorZValue());
            case "gyroscopeXValue":
                return  Double.valueOf(getSensorData().getGyroscopeXValue());
            case "gyroscopeYValue":
                return  Double.valueOf(getSensorData().getGyroscopeYValue());
            case "gyroscopeZValue":
                return  Double.valueOf(getSensorData().getGyroscopeZValue());
            case "deltaTime":
                return  Double.valueOf(getDeltaTime());
            default:
                return 0.0;
        }

    }

    public Integer getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(Integer deltaTime) {
        this.deltaTime = deltaTime;
    }
}

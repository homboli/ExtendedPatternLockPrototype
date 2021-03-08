package me.gergelytusko.extendedpatternlockprototype;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Pair;
import android.view.Display;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PatternLockSession {
    //Original pattern

    //Device ID
    //https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
    private String deviceId; //change this

    //User ID
    private String userId;

    //Phone Type
    //https://stackoverflow.com/questions/1995439/get-android-phone-model-programmatically
    private String manufacturer;
    private String model;
    private String product;
    private String device;
    private String display;
    private List<PatternPointData> patternPointDataList;
    private String finger;
    private String position;
    private int displayHeight;
    private int displayWidth;
    private Context context;
    private HashMap<String, HashMap<String, Float>> circlePositions;

    //Start DateTime
    private long sessionStartTime;
    private String comment;

    public PatternLockSession(Context context) {
        this.context = context;
        this.manufacturer = Build.MANUFACTURER;
        this.model = Build.MODEL;
        this.product = Build.PRODUCT;
        this.device = Build.DEVICE;
        this.display = Build.DISPLAY;
        this.sessionStartTime = Calendar.getInstance().getTimeInMillis();
        this.patternPointDataList = new ArrayList<>();
        this.deviceId = generatePseudoUniqueId();
        Point displayDimensions = getDisplayDimensions();
        this.displayHeight = displayDimensions.y;
        this.displayWidth = displayDimensions.x;
        circlePositions = new HashMap<>();
    }

    public PatternLockSession(){}

    public void addPatternPointData(PatternPointData patternPointData){
        this.patternPointDataList.add(patternPointData);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public String getProduct() {
        return this.product;
    }

    public String getDevice() {
        return this.device;
    }

    public String getDisplay() {
        return this.display;
    }

    public List<PatternPointData> getPatternPointDataList() {
        return patternPointDataList;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment(){
        return this.comment;
    }

    //https://www.pocketmagic.net/android-unique-device-id/
    private String generatePseudoUniqueId(){
        String id = "35" +
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10;
        return id;
    }

    public void setDeltaTimeValues(){
        long previousTime = 0;
        for(int i = 0; i < patternPointDataList.size(); i++){
            if(i == 0){
                patternPointDataList.get(i).setDeltaTime(0);
                previousTime = patternPointDataList.get(i).getTime();
            } else {
                patternPointDataList.get(i).setDeltaTime((int)(patternPointDataList.get(i).getTime() - previousTime));
                previousTime = patternPointDataList.get(i).getTime();
            }
        }
    }

    private Point getDisplayDimensions(){
        Activity activity = (Activity) context;
        Display display =  activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    public void setCirclePosition(int row, int column, float xCoord, float yCoord) {
        HashMap<String, Float> coordinates = new HashMap<>();
        coordinates.put("x", xCoord);
        coordinates.put("y", yCoord);
        circlePositions.put(String.valueOf(new Pair<Integer, Integer>(row, column)), coordinates);
    }

    public HashMap<String, HashMap<String, Float>> getCirclePositions() {
        return circlePositions;
    }
}

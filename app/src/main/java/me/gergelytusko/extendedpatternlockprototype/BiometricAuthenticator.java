package me.gergelytusko.extendedpatternlockprototype;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.addAll;
import static java.util.Collections.sort;

public class BiometricAuthenticator {
    private static final int VALUE_COUNT_LIMIT = 50;
    private Map<String, Map<String, Double>> rules;
    private Double acceptanceScore;
    private Double acceptanceScoreForFirstPoint;
    private List<String> featuresFirstPointExcluded;
    private List<PatternLockSession> unacceptedSessions;
    private Map<String, Map<String, LinkedList<Double>>> values;
    private static Double POINT_ACCEPTANCE_RATIO = 0.7;
    private static Double SCORE_ACCEPTANCE_RATIO = 0.2857;
    private Double scoreSum;
    private Double scoreSumForFirstPoint;
    private Context context;

    public BiometricAuthenticator(Context context){
        this.context = context;
        featuresFirstPointExcluded = new ArrayList<>();
        String[] featuresFirstPointExcludedArray = {"deltaTime", "xVelocity", "yVelocity"};
        addAll(featuresFirstPointExcluded, featuresFirstPointExcludedArray);
        unacceptedSessions = new ArrayList<>();
        values = new HashMap<String, Map<String, LinkedList<Double>>>();
    }

    public void loadAuthenticatorData(){
        try {
            loadModel();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws IOException{
        String jsonString;
        try {
            InputStream is = context.getAssets().open("rules.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type rulesType = new TypeToken<HashMap<String, HashMap<String, Double>>>(){}.getType();
            this.rules = gson.fromJson(jsonString, rulesType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean authenticate(PatternLockSession session){
        List<PatternPointData> patternPointDataList = session.getPatternPointDataList();
        Integer authenticatedPointNumber = 0;
        for(PatternPointData patternPointData: patternPointDataList){
            String point = new Pair<>(patternPointData.getRow(), patternPointData.getColumn()).toString();
            Double scoreSum = 0.0;
            Integer indexOfPoint = patternPointDataList.indexOf(patternPointData);
            for (Map.Entry<String, Map<String, Double>> entry : rules.entrySet()) {
                String feature = entry.getKey();
                if(!featuresFirstPointExcluded.contains(feature) || indexOfPoint != 0) {
                    Map<String, Double> featureRule = entry.getValue();
                    Double bottomPercentile = featureRule.get("threshold");
                    Double score = featureRule.get("score");
                    Log.e("min", feature);
                    Log.e("min", indexOfPoint.toString());
                    if (getPercentile(values.get(feature).get(point), bottomPercentile) <= patternPointData.getFeatureValue(feature) && getPercentile(values.get(feature).get(point), 100 - bottomPercentile) >= patternPointData.getFeatureValue(feature)) {
                        scoreSum += score;
                    } else {
                        Double min = getPercentile(values.get(feature).get(point), bottomPercentile);
                        Double max = getPercentile(values.get(feature).get(point), 100 - bottomPercentile);
                        Log.e("min", min.toString());
                        Log.e("max", max.toString());
                        //Log.e("val", patternPointData.getFeatureValue(feature).toString());
                    }

                }
            }
            if(indexOfPoint == 0){
                if(scoreSum >= acceptanceScoreForFirstPoint){
                    authenticatedPointNumber += 1;
                }
            } else {
                if(scoreSum >= acceptanceScore){
                    authenticatedPointNumber += 1;
                }
            }
        }

        if(authenticatedPointNumber >= Math.ceil(session.getPatternPointDataList().size() * POINT_ACCEPTANCE_RATIO)){
            addUnacceptedSessionsToValues();
            addSessionValues(session);
            removeOutliersFromValues();
            keepLimitedNumberOfValues();
            return true;
        } else {
            unacceptedSessions.add(session);
            return false;
        }
    }

    public void acceptedPassword(){
        addUnacceptedSessionsToValues();
        removeOutliersFromValues();
        keepLimitedNumberOfValues();
    }

    private void addUnacceptedSessionsToValues(){
        for(PatternLockSession sessionToAdd: unacceptedSessions){
            addSessionValues(sessionToAdd);
        }
    }

    private void addSessionValues(PatternLockSession session){
        for(PatternPointData patternPointData: session.getPatternPointDataList()){
            for (Map.Entry<String, Map<String, Double>> entry : rules.entrySet()) {
                String feature = entry.getKey();
                String point = new Pair<>(patternPointData.getRow(), patternPointData.getColumn()).toString();
                Integer indexOfPoint = session.getPatternPointDataList().indexOf(patternPointData);
                if(!featuresFirstPointExcluded.contains(feature) || indexOfPoint != 0) {
                    values.get(feature).get(point).addLast(patternPointData.getFeatureValue(feature));
                }
            }
        }
    }

    public void initialize(List<PatternLockSession> sessions){
        for (Map.Entry<String, Map<String, Double>> entry : rules.entrySet()) {
            String feature = entry.getKey();
            if(values.get(feature) == null){
                values.put(feature, new HashMap<>());
            }
            for(PatternLockSession session: sessions){
                for(PatternPointData patternPointData: session.getPatternPointDataList()){
                    Integer indexOfPoint = session.getPatternPointDataList().indexOf(patternPointData);
                    if(!featuresFirstPointExcluded.contains(feature) || indexOfPoint != 0) {
                        String point = new Pair<>(patternPointData.getRow(), patternPointData.getColumn()).toString();
                        if (values.get(feature).get(point) == null) {
                            values.get(feature).put(point, new LinkedList<>());
                        }
                        values.get(feature).get(point).add(patternPointData.getFeatureValue(feature));
                    }
                }
            }
        }

        //Remove unusable features(E.g. whem touchMinor is always 0)
        removeUnusableFeatures();
        this.scoreSum = calculateScoreSum();
        this.scoreSumForFirstPoint = calculateScoreSumForFirstPoint();
        this.acceptanceScore = this.scoreSum * SCORE_ACCEPTANCE_RATIO;
        this.acceptanceScoreForFirstPoint = this.scoreSumForFirstPoint * SCORE_ACCEPTANCE_RATIO;
        try {
            this.saveModel();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Double calculateScoreSum(){
        Double scoreSum = 0d;
        for (Map.Entry<String, Map<String, Double>> entry : rules.entrySet()) {
            String feature = entry.getKey();
            scoreSum += entry.getValue().get("score");
        }
        return  scoreSum;
    }

    private Double calculateScoreSumForFirstPoint(){
        Double scoreSum = 0d;
        for (Map.Entry<String, Map<String, Double>> entry : rules.entrySet()) {
            String feature = entry.getKey();
            if(!featuresFirstPointExcluded.contains(feature)){
                scoreSum += entry.getValue().get("score");
            }
        }
        return scoreSum;
    }

    private void saveModel() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                "secret_shared_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String valuesString = gson.toJson(values);
        String ruleString = gson.toJson(rules);
        editor.putString("values", valuesString);
        editor.putString("rules", ruleString);
        editor.putString("acceptanceScore", String.valueOf(acceptanceScore));
        editor.putString("acceptanceScoreForFirstPoint", String.valueOf(acceptanceScoreForFirstPoint));
        editor.commit();
    }

    private void loadModel() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                "secret_shared_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        String valuesString = sharedPreferences.getString("values", "");
        String rulesString = sharedPreferences.getString("rules", "");
        String acceptanceScoreString = sharedPreferences.getString("acceptanceScore", "");
        String acceptanceScoreForFirstPointString = sharedPreferences.getString("acceptanceScoreForFirstPoint", "");
        Gson gson = new Gson();
        Type valuesType = new TypeToken<HashMap<String, HashMap<String, LinkedList<Double>>>>(){}.getType();
        Type rulesType = new TypeToken<HashMap<String, HashMap<String, Double>>>(){}.getType();
        this.rules = gson.fromJson(rulesString, rulesType);
        this.values = gson.fromJson(valuesString, valuesType);


        this.acceptanceScoreForFirstPoint = Double.valueOf(acceptanceScoreForFirstPointString);
        this.acceptanceScore = Double.valueOf(acceptanceScoreString);
    }

    private void removeUnusableFeatures(){
        List<String> unusableFeatures = getUnusableFeatures();
        for(String feature: unusableFeatures){
            rules.remove(feature);
            values.remove(feature);
        }
    }

    private List<String> getUnusableFeatures(){
        List<String> unusableFeatures = new ArrayList<>();
        for (Map.Entry<String, Map<String, LinkedList<Double>>> featureEntry : values.entrySet()) {
            String feature = featureEntry.getKey();
            Boolean featureIsUsable = false;
            for(Map.Entry<String, LinkedList<Double>> pointEntry: featureEntry.getValue().entrySet()){
                if(!allValuesAreTheSame(pointEntry.getValue())){
                    featureIsUsable = true;
                }
            }
            if(!featureIsUsable){
                unusableFeatures.add(feature);
            }
        }
        return unusableFeatures;
    }

    private boolean allValuesAreTheSame(List<Double> list) {
        for (Double element: list) {
            if (!element.equals(list.get(0)))
                return false;
        }
        return true;
    }

    private Double getPercentile(List<Double> values, Double percentile) {

        List<Double> sortedList = new LinkedList<>(values);
        sort(sortedList);
        int idx = (int) Math.ceil(percentile / 100.0 * sortedList.size());
        if(percentile == 0  && sortedList.size() != 0){
            return sortedList.get(idx);
        }
        return sortedList.get(idx-1);
    }

    //Based on: https://stackoverflow.com/questions/11686720/is-there-a-numpy-builtin-to-reject-outliers-from-a-list
    public LinkedList<Double> rejectOutliers(LinkedList<Double> originalList, Double m) {
        if(originalList.size() > 40){
            LinkedList<Double> numbers = new LinkedList<>(originalList);
            Double median = getMedian(numbers);
            LinkedList<Double> listWithoutOutliers = new LinkedList<>();
            LinkedList<Double> distanceFromMedianNumbers = new LinkedList<>();
            for(Double d: numbers){
                distanceFromMedianNumbers.add(Math.abs(d-median));
            }
            Double mdev = getMedian(distanceFromMedianNumbers);
            for(int i = 0; i < numbers.size(); i++){
                double s;
                if(!mdev.equals(0d)){
                    s = distanceFromMedianNumbers.get(i) / mdev;
                } else {
                    s = 0;
                }
                if(s<m){
                    listWithoutOutliers.add(numbers.get(i));
                }
            }
            if(listWithoutOutliers.size() <30){
                return originalList;
            }
            return listWithoutOutliers;
        }
        return originalList;
    }

    private void keepLimitedNumberOfValues(){
        for (Map.Entry<String, Map<String, LinkedList<Double>>> featureEntry : values.entrySet()) {
            for(Map.Entry<String, LinkedList<Double>> pointEntry: featureEntry.getValue().entrySet()){
                while(pointEntry.getValue().size() > VALUE_COUNT_LIMIT){
                    pointEntry.getValue().removeFirst();
                }
            }
        }
    }

    private void removeOutliersFromValues(){
        for (Map.Entry<String, Map<String, LinkedList<Double>>> featureEntry : values.entrySet()) {
            for(Map.Entry<String, LinkedList<Double>> pointEntry: featureEntry.getValue().entrySet()){
                pointEntry.setValue(rejectOutliers(pointEntry.getValue(), 2d));
            }
        }
    }

    private Double getMedian(LinkedList<Double> numbers){
        LinkedList<Double> sortedNumbers = new LinkedList<>(numbers);
        Collections.sort(sortedNumbers);
        if(numbers.isEmpty()){
            return 0d;
        }
        if((sortedNumbers.size() % 2) == 0){
            Double a = sortedNumbers.get(sortedNumbers.size()/2-1);
            Double b = sortedNumbers.get(sortedNumbers.size()/2);
            return (a + b) / 2;
        }
        return Math.floor(sortedNumbers.size()/2);
    }

}

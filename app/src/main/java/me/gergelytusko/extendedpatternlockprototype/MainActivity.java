package me.gergelytusko.extendedpatternlockprototype;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements LockPatternView.OnPatternListener {
    private static final int HASH_BYTES = 24;
    private int PBKDF2_ITERATIONS = 1000;
    private static String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512"; //https://stackoverflow.com/questions/19348501/pbkdf2withhmacsha512-vs-pbkdf2withhmacsha1
    private LockPatternView mPatternView;
    private TextView mProgressTextView;
    private int attemptCount;
    private int TOTAL_ATTEMPT_COUNT;
    private int INIT_ATTEMPT_COUNT;
    private LinearLayout bgElement;
    private boolean initMode;
    private LinearLayout mPasswordContainerLinearLayout;
    private Button passwordButton;
    private EditText passwordEditText;
    private List<List<LockPatternView.Cell>> patterns;
    private List<PatternLockSession> patternLockSessions;
    private boolean newPasswordIsSet;
    private boolean newPatternIsSet;
    private BiometricAuthenticator biometricAuthenticator;
    private boolean validatePassword(String enteredPassword) {
        int iterations = PBKDF2_ITERATIONS;
        SharedPreferences patternLockPreferences = getSharedPreferences("patternLockPrefs", MODE_PRIVATE);
        byte[] salt = SecurityUtils.fromHex(patternLockPreferences.getString("salt", ""));
        byte[] hash = SecurityUtils.fromHex(patternLockPreferences.getString("password", ""));
        byte[] testHash = new byte[0];
        try {
            testHash = SecurityUtils.pbkdf2(enteredPassword.toCharArray(), salt, iterations, hash.length, PBKDF2_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return SecurityUtils.slowEquals(hash, testHash);
    }

    private boolean validatePattern(List<LockPatternView.Cell> pattern){
        int iterations = PBKDF2_ITERATIONS;
        byte[] patternByteArray = LockPatternUtils.patternToByteArray(pattern);
        SharedPreferences patternLockPreferences = getSharedPreferences("patternLockPrefs", MODE_PRIVATE);
        byte[] salt = SecurityUtils.fromHex(patternLockPreferences.getString("patternSalt", ""));
        byte[] hash = SecurityUtils.fromHex(patternLockPreferences.getString("patternHash", ""));
        byte[] testHash = new byte[0];
        try {
            testHash = SecurityUtils.pbkdf2(Arrays.toString(patternByteArray).toCharArray(), salt, iterations, hash.length, PBKDF2_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return SecurityUtils.slowEquals(hash, testHash);
    }


    private void savePassword(String password) {
    //https://gist.github.com/jtan189/3804290
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[24];
        random.nextBytes(salt);
        byte[] hash;
        try {
            hash = SecurityUtils.pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES, PBKDF2_ALGORITHM);
            SharedPreferences patternLockPreferences = getSharedPreferences("patternLockPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = patternLockPreferences.edit();
            editor.putString("password", SecurityUtils.toHex(hash));
            editor.putString("salt", SecurityUtils.toHex(salt));
            editor.apply();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private void savePattern(List<LockPatternView.Cell> pattern){
        byte[] patternByteArray = LockPatternUtils.patternToByteArray(pattern);
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[24];
        random.nextBytes(salt);
        byte[] hash;
        try {
            hash = SecurityUtils.pbkdf2(Arrays.toString(patternByteArray).toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES, PBKDF2_ALGORITHM);
            SharedPreferences patternLockPreferences = getSharedPreferences("patternLockPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = patternLockPreferences.edit();
            editor.putString("patternHash", SecurityUtils.toHex(hash));
            editor.putString("patternSalt", SecurityUtils.toHex(salt));
            editor.apply();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }



    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }

    private void showSnackBar(int message, int color){
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(color);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        //SharedPreferences settings = getSharedPreferences("patternLockPrefs", MODE_PRIVATE); //ONLY FOR DEVELOPMENT
        //settings.edit().clear().commit();
        biometricAuthenticator = new BiometricAuthenticator(this);
        mPatternView = (LockPatternView)findViewById(R.id.pl_pattern);
        mPatternView.setOnPatternListener(this);
        bgElement = (LinearLayout) findViewById(R.id.backgroundLayout);
        mProgressTextView = (TextView) findViewById(R.id.progress);
        passwordEditText = (EditText) findViewById(R.id.txtPassword);
        attemptCount = 0;
        TOTAL_ATTEMPT_COUNT = 3;
        INIT_ATTEMPT_COUNT = 10;
        newPasswordIsSet = false; //To check if the new password is set
        newPatternIsSet = false; //To check if the new pattern is entered INIT_ATTEMPT_COUNT times correctly
        mProgressTextView.setText(String.format("%s/%s", Integer.toString(attemptCount), Integer.toString(TOTAL_ATTEMPT_COUNT)));
        mPasswordContainerLinearLayout = (LinearLayout) findViewById(R.id.pasword_container_linear_layout);
        SharedPreferences patternLockPreferences = getSharedPreferences("patternLockPrefs", MODE_PRIVATE);
        passwordButton = (Button) findViewById(R.id.password_button);
        initMode = !patternLockPreferences.contains("password");
        newPasswordIsSet = false;
        patterns = new ArrayList();
        patternLockSessions = new ArrayList<>();
        passwordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickEventHandler(v);
            }
        });
        if(initMode){
            mProgressTextView.setText(String.format("%s/%s", Integer.toString(attemptCount), Integer.toString(INIT_ATTEMPT_COUNT)));
            try {
                biometricAuthenticator.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            biometricAuthenticator.loadAuthenticatorData();
            mPasswordContainerLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private boolean authenticateBehaviour(PatternLockSession ps){
        return biometricAuthenticator.authenticate(ps);
    }

    private void onClickEventHandler(View v){
        hideKeybaord(v);
        if(!initMode){
            if(validatePassword(passwordEditText.getText().toString())){
                bgElement.setBackgroundColor(Color.GREEN);
                showSnackBar(R.string.successfulAuthentication, Color.GREEN);
                biometricAuthenticator.acceptedPassword();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 2000);
            } else{
                showSnackBar(R.string.wrongPasswordErrorMessage, Color.RED);
            }
        } else {
            newPasswordIsSet = true;
            savePassword(passwordEditText.getText().toString());
            passwordEditText.setEnabled(false);
            if(newPatternIsSet){
                finishInit();
            } else {
                showSnackBar(R.string.notEnoughAttemptErrorMessage, Color.RED);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPatternStart() {}

    @Override
    public void onPatternCleared() {}

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {}

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern, PatternLockSession patternLockSession) {
        patternLockSession.setDeltaTimeValues();
        if(initMode){
            if(attemptCount!=0){
                if(patterns.get(0).equals(pattern)){
                    patterns.add(copyPattern(pattern));
                    patternLockSessions.add(patternLockSession);
                    mProgressTextView.setText(Integer.toString(attemptCount+1) + "/" + Integer.toString(INIT_ATTEMPT_COUNT));
                } else {
                    attemptCount -= 1;
                }
            } else {
                mProgressTextView.setText(Integer.toString(attemptCount+1) + "/" + Integer.toString(INIT_ATTEMPT_COUNT));
                patterns.add(copyPattern(pattern));
                patternLockSessions.add(patternLockSession);
            }
            if(attemptCount == INIT_ATTEMPT_COUNT-1){
                newPatternIsSet = true;
                if(newPasswordIsSet){
                    finishInit();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2000);
                } else {
                    mPatternView.disableInput();
                    showSnackBar(R.string.noPasswordSetErrorMessage, Color.RED);
                }
            }
        } else {
            Boolean isValidationSuccessful = validatePattern(pattern);
            Boolean isBehaviouralAuthenticationSuccessful = false;
            if(isValidationSuccessful){
                isBehaviouralAuthenticationSuccessful = authenticateBehaviour(patternLockSession);
            }

            if(isValidationSuccessful && isBehaviouralAuthenticationSuccessful){
                mProgressTextView.setText(Integer.toString(attemptCount+1) + "/" + Integer.toString(TOTAL_ATTEMPT_COUNT));
                bgElement.setBackgroundColor(Color.GREEN);
                showSnackBar(R.string.successfulAuthentication, Color.GREEN);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 2000);
            } else if(attemptCount == TOTAL_ATTEMPT_COUNT-1) {
                mProgressTextView.setText(Integer.toString(attemptCount+1) + "/" + Integer.toString(TOTAL_ATTEMPT_COUNT));
                reachedMaximumAttemptCount();
            } else {
                mProgressTextView.setText(Integer.toString(attemptCount+1) + "/" + Integer.toString(TOTAL_ATTEMPT_COUNT));
                bgElement.setBackgroundColor(Color.RED);
            }
        }
        mPatternView.clearPattern();
        attemptCount += 1;
    }

    private void reachedMaximumAttemptCount() {
        showSnackBar(R.string.reachedMaximumAttemptErrorMessage, Color.RED);
        mPatternView.disableInput();
        passwordButton.setText("OK");
        mPasswordContainerLinearLayout.setVisibility(View.VISIBLE);
    }

    private void finishInit() {
        showSnackBar(R.string.successfulPatternSetUpMessage, Color.GREEN);
        biometricAuthenticator.initialize(patternLockSessions);
        mPatternView.disableInput();
        savePattern(patterns.get(0));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 2000);

        //startActivity(getIntent());
        //recreate();
    }
    
    private List<LockPatternView.Cell> copyPattern(List<LockPatternView.Cell> op){
        List<LockPatternView.Cell> newPattern = new ArrayList<>();
        for (LockPatternView.Cell oc:
             op) {
            newPattern.add(LockPatternView.Cell.of(oc.row, oc.column));
        }
        return newPattern;
    }
}
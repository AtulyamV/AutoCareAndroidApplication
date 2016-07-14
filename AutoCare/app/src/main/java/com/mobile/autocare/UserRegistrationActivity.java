package com.mobile.autocare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;
import com.mobile.autocare.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserRegistrationActivity extends AppCompatActivity {
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mMobileView;
    private ProgressDialog progressDialog;
    SharedPreferences autoCarePreferences;
    Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        util = new Util(this);

        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);

        mFirstNameView = (EditText) findViewById(R.id.firstName);
        mLastNameView = (EditText) findViewById(R.id.lastName);
        mEmailView = (EditText) findViewById(R.id.email);
        mMobileView = (EditText) findViewById(R.id.registration_mobile);


        mFirstNameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mobileView, MotionEvent event) {
                mFirstNameView.setFocusable(true);
                mFirstNameView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mLastNameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mobileView, MotionEvent event) {
                mLastNameView.setFocusable(true);
                mLastNameView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mEmailView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mobileView, MotionEvent event) {
                mEmailView.setFocusable(true);
                mEmailView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mMobileView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mobileView, MotionEvent event) {
                mMobileView.setFocusable(true);
                mMobileView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.userRegistration || id == EditorInfo.IME_NULL) {

                    if (util.isConnected()) {
                        hideKeypad();
                        progressDialog = new ProgressDialog(UserRegistrationActivity.this);
                        progressDialog.setMax(100);
                        progressDialog.setMessage(Constants.USER_LOADING_MESSAGE);
                        progressDialog.show();
                        checkUserRegistration();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Please connect your device to internet connection", Toast.LENGTH_LONG)
                                .show();
                    }
                    return true;
                }
                return false;
            }
        });

        Button mUserRegNextButton = (Button) findViewById(R.id.user_registration_next_button);
        mUserRegNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (util.isConnected()) {
                    hideKeypad();
                    progressDialog = new ProgressDialog(UserRegistrationActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage(Constants.USER_LOADING_MESSAGE);
                    progressDialog.show();
                    checkUserRegistration();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }



            }
        });

    }

    private void checkUserRegistration() {

        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mMobileView.setError(null);

        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String mobileNo = mMobileView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mLastNameView;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(mobileNo)) {
            mMobileView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mMobileView;
                cancel = true;
            }
        } else if (!MobileNoValid(mobileNo)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            if(!cancel) {
                focusView = mMobileView;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mEmailView;
                cancel = true;
            }
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            if(!cancel) {
                focusView = mEmailView;
                cancel = true;
            }
        }

        if (cancel) {
            progressDialog.cancel();
            focusView.requestFocus();
            focusView.setFocusableInTouchMode(true);
            showKeypad();
        } else {

            hideKeypad();
            SharedPreferences.Editor autoCareEditor = autoCarePreferences.edit();
            autoCareEditor.putString(Constants.PREF_FIRST_NAME,firstName);
            autoCareEditor.putString(Constants.PREF_LAST_NAME,lastName);
            autoCareEditor.putString(Constants.PREF_EMAIL,email);
            autoCareEditor.putString(Constants.PREF_MOBILE,mobileNo);

            autoCareEditor.commit();
            triggerUserDetails(mobileNo, email);


        }
    }

    public void triggerUserDetails(String mobileNo, String email) {
        JSONObject user = new JSONObject();
        StringEntity entity = null;
        try {
            user.put("mobileNo", mobileNo);
            user.put("email", email);
            entity = new StringEntity(user.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!Constants.APP_DEMO_MODE) {

            AutoCareHttpClient.post(getApplicationContext(), "UserService/isUserExist", entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.cancel();
                    try {

                        if (response.getString("status").equals(Constants.SUCCESS)) {
                            Intent intent = new Intent(getApplicationContext(), PasswordRegistrationActivity.class);
                            startActivity(intent);
                        } else if (response.getString("status").equals(Constants.USEREXIST)) {
                            mEmailView.setError("Email already exist");
                            mMobileView.setError("Mobile No already exist");
                            mMobileView.requestFocus();
                            mMobileView.setFocusableInTouchMode(true);
                            showKeypad();

                        } else if (response.getString("status").equals(Constants.MOBILENOEXIST)) {
                            mMobileView.setError("Mobile No already exist");
                            mMobileView.requestFocus();
                            mMobileView.setFocusableInTouchMode(true);
                            showKeypad();

                        } else if (response.getString("status").equals(Constants.EMAILEXIST)) {
                            mEmailView.setError("Email already exist");
                            mEmailView.requestFocus();
                            mEmailView.setFocusableInTouchMode(true);
                            showKeypad();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Issue in Login Account", Toast.LENGTH_LONG)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Issue in Login Account", Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    handleFailure(e);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    handleFailure(throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    handleFailure(throwable);
                }
            });
        } else {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), PasswordRegistrationActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }
    }

    private  void handleFailure (Throwable e) {
        progressDialog.cancel();
        String className = e.getClass().getName();
        String toastMessage;
        if (className != null && className.indexOf("ConnectTimeoutException")!= -1) {
            toastMessage = "Please connect your device to internet connection";
        } else {
            toastMessage = "Issue in Login Account";
        }
        Toast.makeText(getApplicationContext(),
                toastMessage, Toast.LENGTH_LONG)
                .show();
    }
    private boolean MobileNoValid(String mobileNo) {
        boolean isValidMobileNo = false;
        String mobileNoRegex = "\\d{10}";
        if (mobileNo.length() == 10 && mobileNo.matches(mobileNoRegex)) {
            isValidMobileNo = true;
        }
        return isValidMobileNo;
    }

    private boolean isEmailValid(String email) {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        boolean isValidPassword = false;
        if (password.length() >= 4) {
            isValidPassword = true;
        }
        return isValidPassword;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("Test in Home button");
        if (item.getItemId() == android.R.id.home) {
            hideKeypad();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showKeypad() {
        InputMethodManager inputMgrObj = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgrObj.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeypad() {
        InputMethodManager inputMgrObj = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMgrObj.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeypad();
    }
}


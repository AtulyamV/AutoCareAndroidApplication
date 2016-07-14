package com.mobile.autocare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;
import com.mobile.autocare.util.PasswordUtil;
import com.mobile.autocare.util.Util;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class SignInActivity extends AppCompatActivity {

    private EditText mMobileView;
    private EditText mPasswordView;
    private CheckBox mShowPasswordView;
    private Button mSignInButton;
    private Button mForgotPasswordButton;
    private TextView mCarNameView;
    private FloatingActionButton mNewUser;
    private Activity currentActivity;
    private ProgressDialog progressDialog;
    private boolean visible = false;
    private Util util;

    SharedPreferences autoCarePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        util = new Util(this);
        autoCarePreferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        mMobileView = (EditText) findViewById(R.id.mobile);
        mPasswordView = (EditText) findViewById(R.id.password);
        mShowPasswordView = (CheckBox) findViewById(R.id.signPasswordCheckBox);
        mNewUser = (FloatingActionButton) findViewById(R.id.newUser);
        mMobileView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mobileView, MotionEvent event) {
                mobileView.setFocusable(true);
                mobileView.setFocusableInTouchMode(true);
                return false;
            }
        });
        mPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View passwordView, MotionEvent event) {
                passwordView.setFocusable(true);
                passwordView.setFocusableInTouchMode(true);
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.signin || id == EditorInfo.IME_NULL) {
                    if (util.isConnected()) {
                        hideKeypad();
                        progressDialog = new ProgressDialog(SignInActivity.this);
                        progressDialog.setMax(100);
                        progressDialog.setMessage(Constants.AUTHENTICATION_LOADING_MESSAGE);
                        progressDialog.show();
                        checkUser();
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

        mShowPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();

                if (isChecked) {
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordView.setSelection(mPasswordView.getText().length());

                } else {
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordView.setSelection(mPasswordView.getText().length());
                }

            }
        });

        mSignInButton = (Button) findViewById(R.id.signIn_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (util.isConnected()) {
                    hideKeypad();
                    progressDialog = new ProgressDialog(SignInActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage(Constants.AUTHENTICATION_LOADING_MESSAGE);
                    progressDialog.show();
                    checkUser();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        mForgotPasswordButton = (Button) findViewById(R.id.forgot_button);
        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMobileView.setFocusable(false);
                mMobileView.setError(null);
                mPasswordView.setFocusable(false);
                mPasswordView.setError(null);
                if (util.isConnected()) {
                    hideKeypad();
                    Intent intent = new Intent(currentActivity, ForgotPasswordActivity.class);
                    startActivity(intent);
                } else  {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        mNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMobileView.setFocusable(false);
                mMobileView.setError(null);
                mPasswordView.setFocusable(false);
                mPasswordView.setError(null);
                if (util.isConnected()) {
                    hideKeypad();
                    Intent intent = new Intent(currentActivity, UserRegistrationActivity.class);
                    startActivity(intent);
                } else  {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    private void checkUser() {
        String mobileNo = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mobileNo)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileNoValid(mobileNo)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mPasswordView;
                cancel = true;
            }
        }

        if (cancel) {
            progressDialog.cancel();
            focusView.requestFocus();
            focusView.setFocusableInTouchMode(true);
            showKeypad();
        } else {
            triggerLogin(mobileNo, password);
        }
    }

    private boolean isMobileNoValid(String mobileNo) {
        boolean isValidMobileNo = false;
        String mobileNoRegex = "\\d{10}";
        if (mobileNo.length() == 10 && mobileNo.matches(mobileNoRegex)) {
            isValidMobileNo = true;
        }
        return isValidMobileNo;
    }



    public void triggerLogin(String mobileNo, String password){
        JSONObject user = new JSONObject();
        StringEntity entity = null;
        String securePassword = PasswordUtil.getSecurePassword(password);

        final String userId = mobileNo;
        try {
            user.put("mobileNo", mobileNo);
            user.put("password", securePassword);
            entity = new StringEntity(user.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!Constants.APP_DEMO_MODE) {

            AutoCareHttpClient.post(getApplicationContext(), "UserService/loginUser", entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.cancel();
                    try {

                        if (response.getString(Constants.STATUS).equals(Constants.SUCCESS)) {
                            JSONObject carObj = response.getJSONObject("car");
                            String carName = carObj.getString("carName");
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("carName", carName);
                            startActivity(intent);
                        } else if (response.getString(Constants.STATUS).equals(Constants.PWD_CHANGE_REQUIRED)) {
                            Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                            intent.putExtra("mobileNo", userId);
                            startActivity(intent);
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
                    progressDialog.cancel();
                    String className = e.getClass().getName();
                    String toastMessage;
                    if (className != null && className.indexOf("ConnectTimeoutException")!= -1) {
                        toastMessage = "Please connect your device to internet connection";
                    } else {
                        toastMessage = "Issue in Login Account";
                    }
                    System.out.println("Error1 " + e.getClass().getName());
                    Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                    String className = e.getClass().getName();
                    String toastMessage;
                    if (className != null && className.indexOf("ConnectTimeoutException")!= -1) {
                        toastMessage = "Please connect your device to internet connection";
                    } else {
                        toastMessage = "Issue in Login Account";
                    }
                    System.out.println("Error1 " + e.getClass().getName());
                    Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
                    String className = e.getClass().getName();
                    String toastMessage;
                    if (className != null && className.indexOf("ConnectTimeoutException")!= -1) {
                        toastMessage = "Please connect your device to internet connection";
                    } else {
                        toastMessage = "Issue in Login Account";
                    }
                    System.out.println("Error1 " + e.getClass().getName());
                    Toast.makeText(getApplicationContext(),
                            toastMessage, Toast.LENGTH_LONG)
                            .show();
                }
            });
        } else {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
            }, 1000);


        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void showKeypad() {
        InputMethodManager inputMgrObj = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgrObj.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeypad() {
        InputMethodManager inputMgrObj = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
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

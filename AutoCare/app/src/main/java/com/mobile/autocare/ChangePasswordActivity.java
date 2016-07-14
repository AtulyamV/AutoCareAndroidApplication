package com.mobile.autocare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobile.autocare.constants.Constants;
import com.mobile.autocare.httpclient.AutoCareHttpClient;
import com.mobile.autocare.util.PasswordUtil;
import com.mobile.autocare.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText mCurrentPasswordView;
    private EditText mNewPasswordView;
    private EditText mConfirmNewPasswordView;
    private CheckBox mShowCurrentPasswordView;
    private CheckBox mShowNewPasswordView;
    private CheckBox mShowConfirmNewPasswordView;
    private Button mChangePwdButton;
    private ProgressDialog progressDialog;
    private Util util;
    String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        util = new Util(this);

        mCurrentPasswordView = (EditText) findViewById(R.id.currentPassword);
        mNewPasswordView = (EditText) findViewById(R.id.newPassword);
        mConfirmNewPasswordView = (EditText) findViewById(R.id.confirmNewPassword);
        mShowCurrentPasswordView = (CheckBox) findViewById(R.id.showCurrentPasswordCheckBox);
        mShowNewPasswordView = (CheckBox) findViewById(R.id.showNewPasswordCheckBox);
        mShowConfirmNewPasswordView = (CheckBox) findViewById(R.id.showConfirmNewPasswordCheckBox);

        if(getIntent() != null && getIntent().getExtras() != null) {
            mobileNo = getIntent().getExtras().getString("mobileNo");
        }

        mChangePwdButton = (Button) findViewById(R.id.change_pwd_button);

        mCurrentPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mPasswordView, MotionEvent event) {
                mCurrentPasswordView.setFocusable(true);
                mCurrentPasswordView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mShowCurrentPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();

                if (isChecked) {
                    mCurrentPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mCurrentPasswordView.setSelection(mCurrentPasswordView.getText().length());

                } else {
                    mCurrentPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mCurrentPasswordView.setSelection(mCurrentPasswordView.getText().length());
                }
            }
        });

        mNewPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mPasswordView, MotionEvent event) {
                mNewPasswordView.setFocusable(true);
                mNewPasswordView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mShowNewPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();

                if (isChecked) {
                    mNewPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mNewPasswordView.setSelection(mNewPasswordView.getText().length());

                } else {
                    mNewPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mNewPasswordView.setSelection(mNewPasswordView.getText().length());
                }
            }
        });

        mConfirmNewPasswordView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mPasswordView, MotionEvent event) {
                mConfirmNewPasswordView.setFocusable(true);
                mConfirmNewPasswordView.setFocusableInTouchMode(true);
                return false;
            }
        });

        mShowConfirmNewPasswordView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();

                if (isChecked) {
                    mConfirmNewPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConfirmNewPasswordView.setSelection(mConfirmNewPasswordView.getText().length());

                } else {
                    mConfirmNewPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConfirmNewPasswordView.setSelection(mConfirmNewPasswordView.getText().length());
                }
            }
        });

        mConfirmNewPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.change_pwd_button || id == EditorInfo.IME_NULL) {

                    if (util.isConnected()) {
                        hideKeypad();
                        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
                        progressDialog.setMax(100);
                        progressDialog.setMessage(Constants.CHANGE_PWD_LOADING_MESSAGE);
                        progressDialog.show();
                        changePwdDetails();
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

        mChangePwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (util.isConnected()) {
                    hideKeypad();
                    progressDialog = new ProgressDialog(ChangePasswordActivity.this);
                    progressDialog.setMax(100);
                    progressDialog.setMessage(Constants.CHANGE_PWD_LOADING_MESSAGE);
                    progressDialog.show();
                    changePwdDetails();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect your device to internet connection", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    public void changePwdDetails () {

        String currentPwd = mCurrentPasswordView.getText().toString();
        String newPwd = mNewPasswordView.getText().toString();
        String confirmNewPwd = mConfirmNewPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;



        if (TextUtils.isEmpty(currentPwd)) {
            mCurrentPasswordView.setError(getString(R.string.error_field_required));
            focusView = mCurrentPasswordView;
            cancel = true;
        } else if(!isPasswordValid(currentPwd)) {
            mCurrentPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mCurrentPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(newPwd)) {
            mNewPasswordView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mNewPasswordView;
                cancel = true;
            }
        } else if(!isPasswordValid(newPwd)) {
            mNewPasswordView.setError(getString(R.string.error_invalid_password));
            if(!cancel) {
                focusView = mNewPasswordView;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(confirmNewPwd)) {
            mConfirmNewPasswordView.setError(getString(R.string.error_field_required));
            if(!cancel) {
                focusView = mConfirmNewPasswordView;
                cancel = true;
            }
        } else if(!isPasswordValid(confirmNewPwd)) {
            mConfirmNewPasswordView.setError(getString(R.string.error_invalid_password));
            if(!cancel) {
                focusView = mConfirmNewPasswordView;
                cancel = true;
            }
        } else if (!passwordsValidation(newPwd, confirmNewPwd)) {
            mConfirmNewPasswordView.setError(getString(R.string.error_password_are_not_equal));
            if(!cancel)  {
                focusView = mConfirmNewPasswordView;
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
            triggerChangePassword(currentPwd, newPwd);
        }

    }

    private void triggerChangePassword(String currentPassword, String newPassword) {
        JSONObject user = new JSONObject();
        StringEntity entity = null;
        String secureCurrentPassword = PasswordUtil.getSecurePassword(currentPassword);
        String secureNewPassword = PasswordUtil.getSecurePassword(newPassword);
        try {
            user.put("mobileNo", mobileNo);
            user.put("password", secureCurrentPassword);
            user.put("newPassword", secureNewPassword);
            entity = new StringEntity(user.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!Constants.APP_DEMO_MODE) {

            AutoCareHttpClient.post(getApplicationContext(), "UserService/changePassword", entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.cancel();
                    try {

                        if (response.getString("status").equals(Constants.SUCCESS)) {
                            JSONObject carObj = response.getJSONObject("car");
                            String carName = carObj.getString("carName");
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("carName", carName);
                            startActivity(intent);
                        } else if (response.getString("status").equals(Constants.INCORRECTPASSWORD)) {
                            mCurrentPasswordView.setError("Issue in current password");
                            mCurrentPasswordView.requestFocus();
                            mCurrentPasswordView.setFocusableInTouchMode(true);
                            showKeypad();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Issue in Changing  Password", Toast.LENGTH_LONG)
                                    .show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),
                                "Issue in Changing  Password", Toast.LENGTH_LONG)
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
                        toastMessage = "Issue in Changing  Password";
                    }
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
                        toastMessage = "Issue in Changing  Password";
                    }
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
                        toastMessage = "Issue in Changing  Password";
                    }
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


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean passwordsValidation(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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


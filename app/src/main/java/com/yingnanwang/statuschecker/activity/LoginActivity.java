package com.yingnanwang.statuschecker.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Bind;
import cn.refactor.library.SmoothCheckBox;

import com.yingnanwang.statuschecker.R;

/**
 * Created by YingnanWang on 8/5/16.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.input_username) EditText _usernameText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.login_checkbox) SmoothCheckBox mCheckbox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Animation mBtnShake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isRemember = pref.getBoolean("remember_login", false);
        if(isRemember){
            String account = pref.getString("username", "");
            String password = pref.getString("password", "");
            _usernameText.setText(account);
            _passwordText.setText(password);
            mCheckbox.setChecked(true, true);
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        _usernameText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN&& keyCode == KeyEvent.KEYCODE_ENTER) {
                    login();
                }
                return false;
            }
        });

        _passwordText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    login();
                }
                return false;
            }
        });
    }

    public void login() {

        if (!validate()) {
            onValidFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登陆中...");
        progressDialog.show();

        final String username = _usernameText.getText().toString();
        final String password = _passwordText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (username.equals("admin") && password.equals("123")) {
                            onLoginSuccess(username, password);
                        } else {
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 2000);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(_usernameText.getWindowToken(), 0);
        inputManager.hideSoftInputFromWindow(_passwordText.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String u, String p) {
        editor = pref.edit();
        if(mCheckbox.isChecked()){
            editor.putBoolean("remember_login", true);
            editor.putString("username", u);
            editor.putString("password", p);
        }else{
            editor.clear();
        }
        editor.apply();

        _loginButton.setEnabled(true);
        MainActivity.actionStart(LoginActivity.this);
        finish();
    }

    public void onLoginFailed() {
        mBtnShake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.btn_shake);
        _loginButton.startAnimation(mBtnShake);
        Toast.makeText(getBaseContext(), "登陆失败，请重试", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public void onValidFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("请输入用户名");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("请输入密码");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}

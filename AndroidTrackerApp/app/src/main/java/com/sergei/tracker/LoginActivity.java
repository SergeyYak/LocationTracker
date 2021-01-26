package com.sergei.tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText login;
    private AppConfig appConfig;
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Context context = getApplicationContext();
        appConfig = new AppConfig(context);

        messageView = findViewById(R.id.PasswordIncorrectView);
        login = findViewById(R.id.LoginInput);
        Button signInBtn = findViewById(R.id.SignIn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });
    }

    private void SignIn() {

        boolean loginCorrect = login.getText().toString()
                .equals(appConfig.Read
                        (AppConfig.Values.LoginOptionName, AppConfig.Values.DefaultLogin));

        if(loginCorrect) {
            Intent activityIntent = new Intent(this, StartupActivity.class);
            startActivity(activityIntent);
            finish();
        }
        else messageView.setText(R.string.DataIncorrect);
    }
}
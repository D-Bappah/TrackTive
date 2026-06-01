package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void Login(View v) {
        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText emailInput = findViewById(R.id.emailInput);

        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String email = emailInput.getText().toString();

        Log.d("Login", "Username: " + username);

        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("username", username);
        i.putExtra("password", password);
        i.putExtra("email", email);

        startActivity(i);
    }

}
package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    String username, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Now it's safe to call getIntent()
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            email = intent.getStringExtra("email");
        }
    }

    public void navigateHome() {
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("username", username);
        i.putExtra("password", password);
        i.putExtra("email", email);
        startActivity(i);
    }

    public void authenticate(View v) {
        EditText passwordInput = findViewById(R.id.PasswordInput);
        EditText emailInput = findViewById(R.id.EmailInput);

        String login_password = passwordInput.getText().toString();
        String login_email = emailInput.getText().toString();

        if ((login_email.equals(username) || login_email.equals(email)) && login_password.equals(password)) {
            navigateHome();
        } else {
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            System.out.println("Invalid username or password");
        }
    }
}

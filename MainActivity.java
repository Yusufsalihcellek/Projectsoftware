package com.mainPackage.randevuapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mainPackage.randevuapp.Database.DatabaseHelper;
import com.mainPackage.randevuapp.Model.Doctor;
import com.mainPackage.randevuapp.Model.Hospital;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.Model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText idNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView falseLoginTextView;
    private CheckBox termsCheckBox;
    private TextView checkBoxErrorTextView;
    private DatabaseHelper dbHelper;

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

        dbHelper = new DatabaseHelper(this);

        // Populate database with initial data only if it's empty
        if (dbHelper.getAllDoctors().isEmpty()) {
            addInitialUsers();
            addInitialHospitals();
            addInitialDoctors();
            List<Doctor> doctors = dbHelper.getAllDoctors();
            for (Doctor doctor : doctors) {
                // Generate schedule for a sample date for each doctor
                giveTimeIntervals(doctor.getDoctorId(), "2024-06-10");
            }
        }
        printTimeIntervals();
        idNumberEditText = findViewById(R.id.number);
        passwordEditText = findViewById(R.id.Passwort1);
        loginButton = findViewById(R.id.Button1);
        falseLoginTextView = findViewById(R.id.FalseLogin);
        termsCheckBox = findViewById(R.id.checkBox);
        checkBoxErrorTextView = findViewById(R.id.chechBoxText);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        };

        idNumberEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        termsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxErrorTextView.setVisibility(View.INVISIBLE);
            }
        });

        loginButton.setOnClickListener(v -> {
            if (!termsCheckBox.isChecked()) {
                checkBoxErrorTextView.setVisibility(View.VISIBLE);
                falseLoginTextView.setVisibility(View.INVISIBLE);
                return;
            }

            String idNumber = idNumberEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Admin Login
            if (idNumber.equals("11111111111") && password.equals("admin123")) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            User user = dbHelper.getUser(idNumber);

            if (user != null && user.getPassword().equals(password)) {
                falseLoginTextView.setVisibility(View.INVISIBLE);
                checkBoxErrorTextView.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                finish();
            } else {
                falseLoginTextView.setVisibility(View.VISIBLE);
            }
        });

        validateInputs();
    }

    private void addInitialUsers() {
        // Add some sample users to the database
        dbHelper.addUser(new User("12345678901", "password123"));
        dbHelper.addUser(new User("11122233344", "testpassword"));
    }

    private void addInitialHospitals() {
        // Add some sample hospitals to the database
        dbHelper.addHospital(new Hospital("Ankara", "Çankaya", "Ankara Şehir Hastanesi"));
        dbHelper.addHospital(new Hospital("Ankara", "Keçiören", "Keçiören Eğitim ve Araştırma Hastanesi"));
        dbHelper.addHospital(new Hospital("İstanbul", "Fatih", "İstanbul Eğitim ve Araştırma Hastanesi"));
        dbHelper.addHospital(new Hospital("İstanbul", "Kadıköy", "Kadıköy Devlet Hastanesi"));
    }

    private void addInitialDoctors() {
        // Add some sample doctors to the database
        dbHelper.addDoctor(new Doctor("Ankara Şehir Hastanesi", "Dr. Ahmet Yılmaz", "Kardiyoloji"));
        dbHelper.addDoctor(new Doctor("Ankara Şehir Hastanesi", "Dr. Ayşe Kaya", "Nöroloji"));
        dbHelper.addDoctor(new Doctor("Keçiören Eğitim ve Araştırma Hastanesi", "Dr. Mehmet Demir", "Ortopedi"));
        dbHelper.addDoctor(new Doctor("Keçiören Eğitim ve Araştırma Hastanesi", "Dr. Zeynep Şahin", "Nöroloji"));
        dbHelper.addDoctor(new Doctor("İstanbul Eğitim ve Araştırma Hastanesi", "Dr. Fatma Şahin", "Dahiliye"));
        dbHelper.addDoctor(new Doctor("İstanbul Eğitim ve Araştırma Hastanesi", "Dr. Ali Kaya", "Kardiyoloji"));
        dbHelper.addDoctor(new Doctor("İstanbul Eğitim ve Araştırma Hastanesi", "Dr. Mehmet Kılıç", "Kardiyoloji"));
        dbHelper.addDoctor(new Doctor("Kadıköy Devlet Hastanesi", "Dr. Zeynep Demir", "Ortopedi"));
        dbHelper.addDoctor(new Doctor("Kadıköy Devlet Hastanesi", "Dr. Ahmet Şahin", "Nöroloji"));
    }
    private void validateInputs() {
        String idNumber = idNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        loginButton.setEnabled(!idNumber.isEmpty() && !password.isEmpty());
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void giveTimeIntervals(int doctorId, String date){
        // Morning session: 9:00 to 12:00
        for (int hour = 9; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute += 5) {
                String time = String.format("%02d:%02d", hour, minute);
                dbHelper.addTimeInterval(new TimeInterval(doctorId, date, time, true));
            }
        }

        // Afternoon session: 13:00 to 16:00
        for (int hour = 13; hour < 16; hour++) {
            for (int minute = 0; minute < 60; minute += 5) {
                String time = String.format("%02d:%02d", hour, minute);
                dbHelper.addTimeInterval(new TimeInterval(doctorId, date, time, true));
            }
        }
    }

    public void printTimeIntervals() {
        List<TimeInterval> timeIntervals = dbHelper.getAllTimeIntervals();
        Log.d("DB_INSPECTION", "--- All Time Intervals ---");
        for (TimeInterval ti : timeIntervals) {
            Log.d("DB_INSPECTION",
                    "ID: " + ti.getTimeIntervalId() +
                            ", Dr. ID: " + ti.getDoctorId() +
                            ", Date: " + ti.getDate() +
                            ", Time: " + ti.getStartTime() +
                            ", Available: " + ti.isAvailable() +
                            ", User ID: " + ti.getUserId()
            );
        }
        Log.d("DB_INSPECTION", "--------------------------");
    }
}
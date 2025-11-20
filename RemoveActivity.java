package com.mainPackage.randevuapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mainPackage.randevuapp.Database.DatabaseHelper;

public class RemoveActivity extends AppCompatActivity {

    private EditText removeHospitalName;
    private Button removeHospitalButton;
    private EditText removeDoctorName;
    private Button removeDoctorButton;
    private Button backToAddPageButton;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_remove);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        removeHospitalName = findViewById(R.id.removeHospitalName);
        removeHospitalButton = findViewById(R.id.removeHospitalButton);
        removeDoctorName = findViewById(R.id.removeDoctorName);
        removeDoctorButton = findViewById(R.id.removeDoctorButton);
        backToAddPageButton = findViewById(R.id.backToAddPageButton);

        removeHospitalButton.setOnClickListener(v -> removeHospital());
        removeDoctorButton.setOnClickListener(v -> removeDoctor());

        backToAddPageButton.setOnClickListener(v -> {
            Intent intent = new Intent(RemoveActivity.this, AdminActivity.class);
            startActivity(intent);
        });
    }

    private void removeHospital() {
        String name = removeHospitalName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a hospital name to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.removeHospital(name);
        Toast.makeText(this, "Hospital removed successfully", Toast.LENGTH_SHORT).show();
        removeHospitalName.setText("");
    }

    private void removeDoctor() {
        String name = removeDoctorName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a doctor name to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.removeDoctor(name);
        Toast.makeText(this, "Doctor removed successfully", Toast.LENGTH_SHORT).show();
        removeDoctorName.setText("");
    }
}

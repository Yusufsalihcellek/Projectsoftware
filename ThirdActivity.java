package com.mainPackage.randevuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mainPackage.randevuapp.Database.DatabaseHelper;

import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    private Spinner departmentSpinner;
    private Spinner doctorSpinner;
    private Button confirmAppointmentButton;
    private TextView selectedHospitalTextView;
    private DatabaseHelper dbHelper;
    private String selectedHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_third);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        departmentSpinner = findViewById(R.id.departmentSpinner);
        doctorSpinner = findViewById(R.id.doctorSpinner);
        confirmAppointmentButton = findViewById(R.id.confirmAppointmentButton);
        selectedHospitalTextView = findViewById(R.id.selectedHospitalTextView);

        confirmAppointmentButton.setEnabled(false);

        selectedHospital = getIntent().getStringExtra("SELECTED_HOSPITAL");
        selectedHospitalTextView.setText(selectedHospital);

        loadDepartmentSpinner(selectedHospital);

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = parent.getItemAtPosition(position).toString();
                loadDoctorSpinner(selectedDepartment, selectedHospital);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Not needed
            }
        });

        doctorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                confirmAppointmentButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                confirmAppointmentButton.setEnabled(false);
            }
        });

        // TODO: Add OnClickListener for confirmAppointmentButton
    }

    private void loadDepartmentSpinner(String hospital) {
        List<String> departments = dbHelper.getDepartmentsByHospital(hospital);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(dataAdapter);
    }

    private void loadDoctorSpinner(String department, String hospital) {
        List<String> doctors = dbHelper.getDoctorsByDepartmentAndHospital(department, hospital);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctors);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpinner.setAdapter(dataAdapter);
    }

    public void Back(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}

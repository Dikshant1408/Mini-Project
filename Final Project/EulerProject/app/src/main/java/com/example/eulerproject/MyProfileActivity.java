package com.example.eulerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfileActivity extends AppCompatActivity {

    private EditText name, email, phone;
    private LinearLayout editB, button_layout;
    private Button cancelB, saveB;
    private TextView profileText;
    private String nameStr, phoneStr;
    private Dialog progressDialog;
    private TextView dialogText;
    private String profileName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        name = findViewById(R.id.mp_name);
        email = findViewById(R.id.mp_emailID);
        phone = findViewById(R.id.mp_phone);
        editB = findViewById(R.id.editB);
        cancelB = findViewById(R.id.cancelB);
        saveB = findViewById(R.id.saveB);
        profileText = findViewById(R.id.profile_text);
        button_layout = findViewById(R.id.button_layout);

        progressDialog = new Dialog(MyProfileActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Saving Data ...");

        disableEditing();

        editB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditing();
            }
        });

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEditing();
            }
        });

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    saveData();
                }
            }
        });
    }

    private void disableEditing(){
        name.setEnabled(false);
        email.setEnabled(false);
        phone.setEnabled(false);
        button_layout.setVisibility(View.GONE);

        name.setText(DbQuery.myProfile.getName());
        email.setText(DbQuery.myProfile.getEmail());

        if (DbQuery.myProfile != null) {
            name.setText(DbQuery.myProfile.getName());
            email.setText(DbQuery.myProfile.getEmail());
            if (DbQuery.myProfile.getPhone() != null)
                phone.setText(DbQuery.myProfile.getPhone());

            if (DbQuery.myProfile.getName() != null) {
                String profileName = DbQuery.myProfile.getName();
                profileText.setText(profileName.toUpperCase().substring(0,1));
            }
        }

    }

    private void enableEditing(){
        name.setEnabled(true);
        phone.setEnabled(true);

        button_layout.setVisibility(View.VISIBLE);
    }

    private boolean validate(){
        nameStr = name.getText().toString();
        phoneStr = phone.getText().toString();

        if (nameStr.isEmpty()){
            name.setError("Name can not be empty !");
            return false;
        }

        if (! phoneStr.isEmpty())
        {
            if (! ((phoneStr.length() == 10) && (TextUtils.isDigitsOnly(phoneStr)))){
                phone.setError("Enter Valid Phone Number");
                return false;
            }
        }

        return true;
    }

    private void saveData(){
        progressDialog.show();

        if (phoneStr.isEmpty())
            phoneStr = null;

        DbQuery.saveProfileData(nameStr, phoneStr, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(MyProfileActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                disableEditing();

            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(MyProfileActivity.this,"Something went Wrong!",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){
            MyProfileActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
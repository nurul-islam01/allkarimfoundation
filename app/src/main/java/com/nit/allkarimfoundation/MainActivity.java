package com.nit.allkarimfoundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;

    private SweetAlertDialog alertDialog;
    private EditText nameET, bloodGroupET, phoneET, addressET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        alertDialog = new SweetAlertDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser == null || currentUser.getUid() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void onAddClick(View view) {

        View addView = getLayoutInflater().inflate(R.layout.add_contact, null);

        nameET = addView.findViewById(R.id.nameET);
        bloodGroupET = addView.findViewById(R.id.bloodGroupET);
        phoneET = addView.findViewById(R.id.phoneET);
        addressET = addView.findViewById(R.id.addressET);

        alertDialog.setCustomView(addView);
        alertDialog.setTitle("Add new contact");
        alertDialog.setConfirmButton("Save", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitle("Processing...");
                String name, blood, phone, address;
                try {
                    name = nameET.getText().toString().trim();
                    blood = bloodGroupET.getText().toString().trim();
                    phone = phoneET.getText().toString().trim();
                    address = addressET.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        nameET.setError("Name is require");
                    }
                    if (TextUtils.isEmpty(blood)) {
                        bloodGroupET.setError("Blood group is require");
                    }
                    if (TextUtils.isEmpty(phone)) {
                        phoneET.setError("phone number is require");
                    }

                    if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(blood) && TextUtils.isEmpty(phone)) {
                        saveContact(name, blood, phone, address, sweetAlertDialog);
                    }


                }catch (Exception e){
                    Log.d(TAG, "onClick: "+ e.getMessage());
                }

            }
        }).setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        alertDialog.show();

    }

    private void saveContact(String name, String blood, String phone, String address, SweetAlertDialog sweetAlertDialog) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("CONTACT");
        String id = myRef.push().getKey();
        Contact contact = new Contact(id, name, blood, phone, address);

        myRef.child(id).setValue(contact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitle("Added successfully");
                    sweetAlertDialog.dismissWithAnimation();
                } else {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setTitle("Added failed");
                    sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog2) {
                            sweetAlertDialog2.dismissWithAnimation();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitle("Added failed");
                sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog2) {
                        sweetAlertDialog2.dismissWithAnimation();
                    }
                });
            }
        });

    }
}
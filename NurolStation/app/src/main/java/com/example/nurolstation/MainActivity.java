package com.example.nurolstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.nurolstation.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivityMainBinding binding;
    String userString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);;
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user!=null){
            Intent intent = new Intent(MainActivity.this,CallActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void signInClicked(View view) {

        String email = binding.loginText.getText().toString();
        String password = binding.loginPassword.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(this, "E-mail ve Şifre Gerekli", Toast.LENGTH_LONG).show();
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    userString = auth.getCurrentUser().toString();
                    Intent intent = new Intent(MainActivity.this,CallActivity.class);
                    intent.putExtra("UserName",userString);

                    startActivity(intent);
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }


    }


}
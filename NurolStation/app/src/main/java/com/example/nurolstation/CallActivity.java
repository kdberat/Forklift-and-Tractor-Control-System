package com.example.nurolstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.UUID;

public class CallActivity extends AppCompatActivity {

    TextView userName;
    Boolean forkliftRequest;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    DocumentReference docRef;
    CommunicationData userdata = new CommunicationData();
    DocumentReference docRefTow;


    String userString;
    String fullname;

    String station;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getStationName();

        userName = findViewById(R.id.userName);

        userName.setText("İstasyon ismi isteniyor");



        firebaseFirestore.collection("Users").document("userInfo");

    }

    private void getStationName(){
        String uid = auth.getCurrentUser().getUid();

        DocumentReference docRef = firebaseFirestore.collection("StationNames").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                this.station =   document.get("StationName").toString();
                this.userName.setText(station);
            } else {
                this.userName.setText("Hata: Servera bağlanılamıyor!");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_post){
            //upload
            Intent intentToMalfunction = new Intent(CallActivity.this,MalfunctionActivity.class);
            startActivity(intentToMalfunction);

        }
        else if(item.getItemId() == R.id.sign_out){
            //signout
            firebaseFirestore.terminate();
            auth.signOut();
            Intent intentToLogout = new Intent(CallActivity.this,MainActivity.class);
            startActivity(intentToLogout);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    public void callForklift(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(CallActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("Forklift çağırmak istediğinize emin misiniz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(CallActivity.this, SelectionActivity.class);
                intent.putExtra("type", TypeClass.FORKLIFT_TYPE);
                startActivity(intent);




            }
        });

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userdata.setRequestStatus(false);
                forkliftRequest = userdata.getRequestStatus();
                Toast.makeText(CallActivity.this, "İstek Geri Çekildi", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();


    }
    public void callTowTruck(View view){


        AlertDialog.Builder alert = new AlertDialog.Builder(CallActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("Çekici çağırmak istediğinize emin misiniz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Intent intent = new Intent(CallActivity.this, SelectionActivity.class);
                intent.putExtra("type", TypeClass.TOW_TRUCK_TYPE);
                startActivity(intent);

//                docRefTow = firebaseFirestore.collection("Requests").document("TowTruckRequest");
//                docRefTow.update("requests", FieldValue.arrayUnion(station)).addOnCompleteListener(task ->
//                {
//                    if (task.isSuccessful()){
//
//                    }
//                    else {
//                        Toast.makeText(CallActivity.this, "Çağıralamadı", Toast.LENGTH_SHORT).show();
//                    }
//                });

            }
        });

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userdata.setRequestStatus(false);
                forkliftRequest = userdata.getRequestStatus();
                Toast.makeText(CallActivity.this, "İstek Geri Çekildi", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();


    }

    public void accepttrans(View view){


        AlertDialog.Builder alert = new AlertDialog.Builder(CallActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("Mal Bekleme Ekranına geçmek istediğinize emin misiniz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                docRefTow = firebaseFirestore.collection("TowRequests").document("requests");
                docRefTow.update("requests", FieldValue.arrayUnion(station)).addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(CallActivity.this, AcceptanceStation.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(CallActivity.this, "Çağıralamadı", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userdata.setRequestStatus(false);
                forkliftRequest = userdata.getRequestStatus();
                Toast.makeText(CallActivity.this, "İstek Geri Çekildi", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();


    }




}
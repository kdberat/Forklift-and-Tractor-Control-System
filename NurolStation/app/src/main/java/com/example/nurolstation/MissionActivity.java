package com.example.nurolstation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;


public class MissionActivity extends AppCompatActivity {
    Chronometer requestTimer;
    TextView forkliftMessage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private int type = 0;
    private String sentStation;
    String station;
    private DocumentReference requestDocRef;
    private ListenerRegistration listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        Bundle b = getIntent().getExtras();
        type = b.getInt("type");
        sentStation = b.getString("sentStation");

        forkliftMessage = findViewById(R.id.forkliftMessage);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        initRef();
        getStationName();

        requestTimer = (Chronometer) findViewById(R.id.timerForklift);
        requestTimer.setFormat("İstek Gönderildi: - %s");
        requestTimer.start();



    }

    private void initRef() {
        requestDocRef = firebaseFirestore.collection("Requests").document(TypeClass.getCollectionName(type));
    }

    private void getStationName(){
        String uid = auth.getCurrentUser().getUid();

        DocumentReference docRef = firebaseFirestore.collection("StationNames").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                this.station =   document.get("StationName").toString();
                initListener();
            }
        });
    }

    private void initListener() {
        listener = requestDocRef.addSnapshotListener((value, error) -> {
            ArrayList<String> requestList = new ArrayList<>();
            if (value.get("requests")!=null){
                requestList  = (ArrayList<String>) value.get("requests");
            }

            boolean varMi = false;

            for (String requests: requestList){
                int index = requests.indexOf("/");
                if (station.equals(requests.substring(0, index))){
                    varMi = true;
                }
            }
            if ( !varMi ) {
                requestAccepted();
            }
        });
    }

    private void requestAccepted() {
        Intent intent = new Intent(MissionActivity.this, OnWayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("sentStation", sentStation);
        startActivity(intent);
        listener.remove();
        finish();
    }


    public void forkliftCancel(View view){

        onBackPressed();

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MissionActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("İptal etmek istiyor musunuz ?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.remove();
                requestDocRef.update("requests", FieldValue.arrayRemove(station)).addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()){
                        finish();
                    }
                });
            }
        });

        alert.show();

    }
}
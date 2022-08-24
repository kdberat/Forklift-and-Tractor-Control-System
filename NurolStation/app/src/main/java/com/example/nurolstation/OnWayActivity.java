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
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OnWayActivity extends AppCompatActivity {
    Chronometer acceptedTimer;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    //CommunicationData userData = new CommunicationData();
    Boolean forkliftRequest;
    private String sentStation;
    private String forklift = "";
    private String station;
    private CollectionReference forkliftRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_way);

        Bundle b = getIntent().getExtras();
        sentStation = b.getString("sentStation");

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        acceptedTimer = (Chronometer) findViewById(R.id.timerAccepted);
        acceptedTimer.setFormat("İstek Gönderildi: - %s");
        acceptedTimer.start();

        getStationName();

    }

    private void getStationName(){
        String uid = auth.getCurrentUser().getUid();

        DocumentReference docRef = firebaseFirestore.collection("StationNames").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                this.station =   document.get("StationName").toString();
                initForklift();
            }
        });
    }


    private void initForklift() {

        forkliftRef = firebaseFirestore.collection("Forklifts");
        forkliftRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    for (DocumentSnapshot item : documents){
                        String forkliftStationName = item.get("station").toString();
                        if (forkliftStationName.equals(station)){
                            forklift = item.getId();
                            ((TextView) findViewById(R.id.acceptedtextView)).setText("İstek kabul edildi, " + forklift + " yola çıktı");
                        }
                    }
                }
            }
        });
    }

    private void doNothing() {
    }

    public void missionCompleted(View view){

        Intent intentToCallActivity = new Intent(OnWayActivity.this,CallActivity.class);
        startActivity(intentToCallActivity);
        finish();

//        AlertDialog.Builder alert = new AlertDialog.Builder(OnWayActivity.this);
//        alert.setTitle("Görev Kontrol");
//        alert.setMessage("İş Tamamlandı mı?");
//        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                //ikinci istasyon işi tamamlayacağı için commentlendi, fmt yazılımına eklendi.
//                //forkliftRef.document(forklift).update("station", "");
//                firebaseFirestore.collection("Forklifts").document(forklift).update("sentStation", sentStation);
//                firebaseFirestore.collection("Forklifts").document(forklift).update("station", "");
//                Intent intentToCallActivity = new Intent(OnWayActivity.this,CallActivity.class);
//                startActivity(intentToCallActivity);
//                finish();
//            }
//        });
//        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(OnWayActivity.this, "İstek Geri Çekildi", Toast.LENGTH_SHORT).show();
//            }
//        });
//        alert.show();
    }
}
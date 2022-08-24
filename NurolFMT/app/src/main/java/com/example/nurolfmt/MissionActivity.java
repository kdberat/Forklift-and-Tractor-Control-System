package com.example.nurolfmt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MissionActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String forkliftName = "";
    private String workinStation = "";
    private String sentStation;
    private Boolean isLoaded = false;
    private String forklift = "";
    private String station;
    private CollectionReference forkliftRef;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();

        firestore.collection("ForkliftNames").document(uid).get().addOnCompleteListener(task->{
            if(task.isSuccessful()){
                forkliftName = task.getResult().get("name").toString();
                initForklift();
            }
        });


    }




    private void initForklift(){
        DocumentReference docRef = firestore.collection("Forklifts").document(forkliftName);
        docRef.addSnapshotListener((value, error) -> {
            sentStation = value.get("sentStation").toString();
            workinStation = value.get("station").toString();
            if(!isLoaded){
                ((TextView)findViewById(R.id.stationName)).setText(workinStation);
            }
            else if(isLoaded && !sentStation.equals("")){
                ((TextView)findViewById(R.id.stationName)).setText(sentStation);
            } else if (workinStation.equals("") && sentStation.equals("")){
                finish();
            }
        });
    }

    public void missioncomplete(View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(MissionActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("İş Tamamlandı mı?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DocumentReference docRef = firestore.collection("Forklifts").document(forkliftName);
                docRef.update("sentStation", "").addOnCompleteListener(task -> {
                    Intent intent = new Intent(MissionActivity.this, FeedActivity.class);
                    startActivity(intent);
                });
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //userdata.setRequestStatus(false);
                //forkliftRequest = userdata.getRequestStatus();
                Toast.makeText(MissionActivity.this, "Tamamlanmadı", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();


    }
    public void forkliftloaded(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(MissionActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("İş Tamamlandı mı?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isLoaded = true;
                DocumentReference docRef = firestore.collection("Forklifts").document(forkliftName);
                docRef.update("station", "").addOnCompleteListener(task -> {
                view.findViewById(R.id.button).setVisibility(View.INVISIBLE);
                });
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //userdata.setRequestStatus(false);
                //forkliftRequest = userdata.getRequestStatus();
                Toast.makeText(MissionActivity.this, "Tamamlanmadı", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();


    }
}
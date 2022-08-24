package com.example.nurolstation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AcceptanceStation extends AppCompatActivity implements GelenAraclarRecyclerViewAdapter.ItemClickListener {

    private String stationName ;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> aracListesi = new ArrayList<>();
    private GelenAraclarRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_station);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getStationName();
        initRefresh();

        adapter = new GelenAraclarRecyclerViewAdapter(this, aracListesi);
        RecyclerView recyclerView = findViewById(R.id.gelenler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    private void getStationName(){
        String uid = auth.getCurrentUser().getUid();

        DocumentReference docRef = firebaseFirestore.collection("StationNames").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                this.stationName =   document.get("StationName").toString();
                initForklift();
            }
        });
    }


    private void initForklift() {

        CollectionReference forkliftRef = firebaseFirestore.collection("Forklifts");
        forkliftRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    aracListesi.clear();
                    for (DocumentSnapshot item : documents){
                        String forkliftStationName = item.get("sentStation").toString();
                        if (forkliftStationName.equals(stationName)){
                            String forklift = item.getId();
                            aracListesi.add(forklift);
                        }
                    }
                    adapter.updateData(aracListesi);
                }
            }
        });
    }

    private void initRefresh(){
        ImageButton button = findViewById(R.id.refresh);
        button.setOnClickListener(view -> {
            initForklift();
        });
    }

    @Override
    public void onItemClick(View view, int position, String stationName) {

        dialog(stationName);
    }



    public void dialog(String stationName){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("Mal kabulu gerçekleşti mi?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                DocumentReference docRefTow = firebaseFirestore.collection("Forklifts").document(stationName);
                docRefTow.update("sentStation", "").addOnCompleteListener(task ->
                {
                    initForklift();
                });

            }
        });

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                }
        });
        alert.show();


    }

}
package com.example.nurolfmt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> requestListData = new ArrayList<String>();
    private ArrayList<String> forkliftRequests = new ArrayList<>();
    private ArrayList<String> towTruckRequests = new ArrayList<>();
    private int type = 0;
    private String collectionName;

    private String forkliftName = "";
    private MyRecyclerViewAdapter rvAdapter;
    private RecyclerView recyclerView;
    TextView forkliftView;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rvAdapter = new MyRecyclerViewAdapter(this, requestListData);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter.setClickListener(this);
        recyclerView.setAdapter(rvAdapter);

        initForkliftName();
        forkliftView = findViewById(R.id.textView2);
        forkliftView.setText(forkliftName);
//        initRequestObserver();


    }

    private void initForkliftName() {
        firebaseFirestore.collection("ForkliftNames").document(auth.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                forkliftName = task.getResult().get("name").toString();
                forkliftView = findViewById(R.id.textView2);
                forkliftView.setText(forkliftName);

                initForkliftType();
            }
        });
    }


    private void initForkliftType(){
        firebaseFirestore.collection("Forklifts").document(forkliftName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                type = (int) ( (long) task.getResult().get("BenKimim"));
            }

            initRequestObserver(type);
        });

    }


    private void initRequestObserver(int type) {
        switch (type){
            case 0 : {
                observeForklift();
                collectionName = "ForkliftRequest";
                break;
            }
            case 1 : {
                observeTowTruck();
                collectionName = "TowTruckRequest";
                break;
            }
        }
//        observeTowTruck();
    }

    private void observeForklift(){

        DocumentReference docRef = firebaseFirestore.collection("Requests").document("ForkliftRequest");
        docRef.addSnapshotListener((value, error) -> {
            if (value.get("requests") != null) {
                forkliftRequests = (ArrayList<String>) value.get("requests");
            }

            updateRequestList();
        });

    }

    private void observeTowTruck(){

        DocumentReference docRef = firebaseFirestore.collection("Requests").document("TowTruckRequest");
        docRef.addSnapshotListener((value, error) -> {
            if (value.get("requests") != null) {
                towTruckRequests = (ArrayList<String>) value.get("requests");
            }

            updateRequestList();
        });

    }


    private void updateRequestList(){
        requestListData.clear();
        requestListData.addAll(forkliftRequests);
        requestListData.addAll(towTruckRequests);

        rvAdapter.updateData(requestListData, forkliftRequests.size());
        recyclerView.invalidate();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post) {
            //upload
//            Intent intentToUpload = new Intent(FeedActivity.this, MissionActivity.class);
//            startActivity(intentToUpload);

        } else if (item.getItemId() == R.id.sign_out) {
            //signout
            firebaseFirestore.terminate();
            auth.signOut();

            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();

        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemClick(View view, int position, String stationName) {

        AlertDialog.Builder alert = new AlertDialog.Builder(FeedActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("İstasyondan gelen isteği kabul etmek istiyor musunuz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int index = stationName.indexOf("/");

                docRef = firebaseFirestore.collection("Forklifts").document(forkliftName);
                docRef.update("station", stationName.substring(0, index)).addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()){
                        DocumentReference reqRef = firebaseFirestore.collection("Requests").document(collectionName);
                        reqRef.update("requests", FieldValue.arrayRemove(stationName)).addOnCompleteListener(task1 -> {
                            docRef.update("sentStation", stationName.substring(index+1)).addOnCompleteListener(task2 ->{
                                if (task2.isSuccessful()){
                                    Intent intent = new Intent(FeedActivity.this, MissionActivity.class);
                                    startActivity(intent);
                                }
                            });
                        });
                    }
                    else {
                        Toast.makeText(FeedActivity.this, "Çağıralamadı", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(FeedActivity.this, "İş Kabul edilmedi", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();

    }



}





package com.example.nurolstation;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> requestListData = new ArrayList<String>();
    private String stationName = "";
    private MyRecyclerViewAdapter rvAdapter;
    private RecyclerView recyclerView;
    private int type = 0;
    TextView forkliftView;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Bundle bundle = getIntent().getExtras();

        type = bundle.getInt("type");

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rvAdapter = new MyRecyclerViewAdapter(this, requestListData);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter.setClickListener(this);
        recyclerView.setAdapter(rvAdapter);

//        forkliftView = findViewById(R.id.textSelection);
//        forkliftView.setText(forkliftName);
        initRequestObserver();
        getStationName();
    }

    private void getStationName(){
        String uid = auth.getCurrentUser().getUid();

        DocumentReference docRef = firebaseFirestore.collection("StationNames").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                this.stationName =   document.get("StationName").toString();
            }
        });
    }

    private void initRequestObserver() {
        CollectionReference docRef = firebaseFirestore.collection("StationNames");
        docRef.addSnapshotListener((value, error) -> {
            ArrayList<String> stationList = new ArrayList<>();

            for (DocumentSnapshot doc : value.getDocuments()){
                stationList.add(doc.get("StationName").toString());
            }

            rvAdapter.updateData(stationList);
            recyclerView.invalidate();
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         if (item.getItemId() == R.id.add_post) {
             //upload
             Intent intentToUpload = new Intent(SelectionActivity.this, MissionActivity.class);
             startActivity(intentToUpload);

         } else if (item.getItemId() == R.id.sign_out) {
             //signout
             auth.signOut();
             Intent intentToMain = new Intent(SelectionActivity.this, MainActivity.class);
             startActivity(intentToMain);
             finish();

         }


         return super.onOptionsItemSelected(item);
     }



    @Override
    public void onItemClick(View view, int position, String sentStationName) {

        AlertDialog.Builder alert = new AlertDialog.Builder(SelectionActivity.this);

        alert.setTitle("İstek Kontrol");
        alert.setMessage("Gönderilecek istasyondan emin misiniz?");

        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                docRef = firebaseFirestore.collection("Requests").document(TypeClass.getCollectionName(type));
                docRef.update("requests", FieldValue.arrayUnion(stationName+"/"+sentStationName)).addOnCompleteListener(task ->

                {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(SelectionActivity.this, MissionActivity.class);
                        intent.putExtra("sentStation", sentStationName);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SelectionActivity.this, "Çağıralamadı", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(SelectionActivity.this, "İş Kabul edilmedi", Toast.LENGTH_SHORT).show();
            }
        });
        alert.show();

    }



}




/*

 */


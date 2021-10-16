package com.example.rssreader.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rssreader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class UrlListActivity extends AppCompatActivity {
    private ListView listView;
    private Toolbar toolbar;
    private ArrayList<String> arrayList;
    private ArrayAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_list);

        listView = findViewById(R.id.list);
        toolbar = findViewById(R.id.toolbar);
        arrayList = new ArrayList<>();

        //Toolbar
        setSupportActionBar(toolbar);
        setActionBar();

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        getUser(intent.getStringExtra("email"));
    }
    private void getAllUser(){
                //Get All User
                db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("AllData",String.valueOf(document.getData()));
                            }
                        } else {
                            Log.d("AllData",String.valueOf(task.getException()));
                        }
                    }
                });
    }
    private void getUser(String email){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> group = (ArrayList<String>) document.get("url");
                        arrayList.addAll(group);
                        Log.d("ArrayList",arrayList.toString());
                        adapter = new ArrayAdapter<String>(UrlListActivity.this, android.R.layout.simple_list_item_1, arrayList);
                        listView.setAdapter(adapter);
                        Log.d("DocumentSnapshot data: ", String.valueOf(document.getData()));
                    } else {
                        Log.d("DocumentSnapshot data: ","No such document");
                    }
                } else {
                    Log.d(TAG, "Get Failed With ", task.getException());
                }
            }
        });
    }
    private void setActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
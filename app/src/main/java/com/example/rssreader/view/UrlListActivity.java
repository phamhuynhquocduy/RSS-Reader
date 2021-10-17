package com.example.rssreader.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.adapter.UrlListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class UrlListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ArrayList<String> arrayList;
    private UrlListAdapter adapter;
    private FirebaseFirestore db;
    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_list);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.tvUrl);
        imageView=findViewById(R.id.imageUrl);
        arrayList = new ArrayList<>();

        //Toolbar
        setSupportActionBar(toolbar);
        setActionBar();

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        // Get link url of user
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
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        Log.d("ArrayList",arrayList.toString());
                        adapter = new UrlListAdapter(arrayList,UrlListActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(UrlListActivity.this));
                        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                        adapter.notifyDataSetChanged();
                        Log.d("DocumentSnapshot data: ", String.valueOf(document.getData()));
                    } else {
                        Log.d("DocumentSnapshot data: ","No such document");
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
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

    private void deleteUser(String email){
        db.collection("users").document(email)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DeleteUser", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DeleteUser", e.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
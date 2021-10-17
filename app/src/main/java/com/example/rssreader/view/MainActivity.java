package com.example.rssreader.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rssreader.R;
import com.example.rssreader.adapter.RssFeedListAdapter;
import com.example.rssreader.model.RssFeedModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private ArrayList<RssFeedModel> mFeedModelList;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView imagePhoto,imageFeed;
    private TextView tvName,tvFeed;
    private FirebaseFirestore db;
    private String name,image,email;
    private ArrayList<String> arrayList;
    private static final int REQUEST_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        imageFeed =findViewById(R.id.imageFeed);
        tvFeed =findViewById(R.id.tvFeed);

        View header = navigationView.getHeaderView(0);
        tvName = header.findViewById(R.id.main_fullname);
        imagePhoto =header.findViewById(R.id.main_profile_image);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedModelList = new ArrayList<RssFeedModel>();

        //Init array list
        arrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        //Setting toolbar
        setSupportActionBar(toolbar);
        setToolbar();
        //Set up navigation drawer
        setUpNavDrawer();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        image = intent.getStringExtra("image");
        email = intent.getStringExtra("email");
        Log.d("email",email);
        //Update profile user
        tvName.setText("Xin chào "+ name);
        if(intent.getStringExtra("image")!=null){
            Uri myUri = Uri.parse(image);
            Picasso.get().load(myUri)
                    .placeholder(R.drawable.placeholder_user)
                    .error(R.drawable.error)
                    .into(imagePhoto);
        }
    }
    private void doStuff(String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //do your work
                String urlLink = url;
                try {
                    //if not enter http or https it will be added
                    if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                        urlLink = "http://" + urlLink;

                    URL url = new URL(urlLink);
                    InputStream inputStream = url.openConnection().getInputStream();
                    //Read url
                    read(inputStream);
                } catch (IOException e) {
                    Log.e("Error", e.getMessage());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Do your work after the job is finished
                        //Fill recyclerView
                        Log.d("list",mFeedModelList.toString());

                        // If list feed more than 0
                        if(mFeedModelList.size()>0){
                            tvFeed.setVisibility(View.GONE);
                            imageFeed.setVisibility(View.GONE);
                        }else{
                            tvFeed.setVisibility(View.VISIBLE);
                            imageFeed.setVisibility(View.VISIBLE);
                        }
                        recyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
                        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    }
                });
            }
        }).start();
    }

    void read(InputStream in){
        mFeedModelList.clear();
        try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbfactory.newDocumentBuilder();
            Document doc = builder.parse(in);

            Element root = doc.getDocumentElement();
            NodeList nodeList  = root.getElementsByTagName("item");

            for( int i=0;i<nodeList.getLength();i++) {
                Element element = (Element) nodeList.item(i);
                String title = getValue(element, "title");
                String link = getValue(element, "link");
                String date =getValue(element,"pubDate");
                mFeedModelList.add(new RssFeedModel(title,link,date));
            }
        }
            catch (Exception e){
        }
    }

    private String getValue(Element element, String name ){
        NodeList nodeList = element.getElementsByTagName(name);
        return this.getTextNodeValue(nodeList.item(0));
    }

    private  final  String getTextNodeValue(Node elem){
        Node child;
        if(elem!=null){
            for(child = elem.getFirstChild();child!=null;child= child.getNextSibling()){
                if(child.getNodeType()==Node.TEXT_NODE){
                    return child.getNodeValue();
                }else {
                    CharacterData cd = (CharacterData) child;
                    return cd.getData();
                }
            }
        }
        return "";
    }

    private void setUpNavDrawer() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                final int menuItemId = menuItem.getItemId();
                switch (menuItemId) {
                    case R.id.navigation_add:
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.layout_dialog_add);
                        Button button = dialog.findViewById(R.id.buttonSubmit);
                        ImageButton imageButton = dialog.findViewById(R.id.imgBtnDismiss);
                        EditText editText = dialog.findViewById(R.id.edtURL);
                        CheckBox checkBox = dialog.findViewById(R.id.checkBox);

                        //Custom width dialog
                        int width = (int)(getResources().getDisplayMetrics().widthPixels);
                        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doStuff(editText.getText().toString());
                                if(checkBox.isChecked()){
                                    getDocument(email,editText.getText().toString());
                                }
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return false;
                    case R.id.navigation_save:
                        Intent intentUrl = new Intent(MainActivity.this, UrlListActivity.class);
                        intentUrl.putExtra("email",email);
                        intentUrl.putExtra("name",name);
                        intentUrl.putExtra("image",image);
                        startActivityIfNeeded(intentUrl,REQUEST_CODE);
                        return false;
                    case R.id.navigation_log_out:
                        Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intentLogout);
                        saveLoginState(null);
                        finish();
                        return false;
                    default: {
                        return false;
                    }
                }
            }
        });
    }

    public void setToolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //Save URL
    private void saveURL(String url){
        arrayList.add(url);

        Map<String, Object> user = new HashMap<>();
        user.put("url",arrayList);
        db.collection("users").document(String.valueOf(email))
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AddData", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error writing document", e.getMessage());
                    }
                });
    }

    private void saveLoginState(String id_token){
        SharedPreferences sharedpreferences = getSharedPreferences("Status login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Google", id_token);
        editor.commit();

    }

    private void getDocument(String document,String url){

            arrayList.clear();

            DocumentReference docRef = db.collection("users").document(document);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> group = (ArrayList<String>) document.get("url");
                            arrayList.addAll(group);
                            saveURL(url);
                            Log.d("DocumentSnapshot data: ", String.valueOf(document.getData()));
                        } else {
                            Log.d("DocumentSnapshot data: ","No such document");
                            saveURL(url);
                        }
                    } else {
                        Log.d(TAG, "Get Failed With ", task.getException());
                    }
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    doStuff(data.getStringExtra("result"));
            }
        }
    }
}
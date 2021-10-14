package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.ContentValues.TAG;
import static java.nio.charset.StandardCharsets.*;

public class MainActivity extends AppCompatActivity {

    private ArrayList<RssFeedModel> mFeedModelList;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView imagePhoto;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        View header = navigationView.getHeaderView(0);
        tvName = header.findViewById(R.id.main_fullname);
        imagePhoto =header.findViewById(R.id.main_profile_image);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedModelList = new ArrayList<RssFeedModel>();

        //Setting toolbar
        setSupportActionBar(toolbar);
        setToolbar();
        //Set up navigation drawer
        setUpNavDrawer();

        Intent intent = getIntent();
        //Update profile user
        tvName.setText("Xin chào "+ intent.getStringExtra("name"));
        if(intent.getStringExtra("image")!=null){
            Uri myUri = Uri.parse(intent.getStringExtra("image"));
            Picasso.get().load(myUri)
                    .placeholder(R.drawable.placeholder_user)
                    .error(R.drawable.error)
                    .into(imagePhoto);
        }
    }
    private void doStuff(EditText editText){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //do your work
                String urlLink = editText.getText().toString();
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
                        recyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
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
            NodeList nodeListDescription =root.getElementsByTagName("description");

            for( int i=0;i<nodeList.getLength();i++) {
                String CData= nodeListDescription.item(i+1).getTextContent();
                Log.d("CData",CData);
                // Get link image
                Pattern p = Pattern.compile("<img src=\"([^\"]+)");
                Matcher matcher = p.matcher (CData);
                String image = "";
                if (matcher.find()) {
                    image = matcher.group (1);
                }
                Element element = (Element) nodeList.item(i);
                String title = getValue(element, "title");
                String link = getValue(element, "link");
                mFeedModelList.add(new RssFeedModel(title,link,null,image));
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
                menuItem.setChecked(true);
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
                                doStuff(editText);
                                if(checkBox.isChecked()){
                                    saveURL();
                                }
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return false;
                    case R.id.navigation_save:
                        Intent intentUrl = new Intent(MainActivity.this,UrlListActivity.class);
                        startActivity(intentUrl);
                        return false;
                    case R.id.navigation_log_out:
                        Intent intentLogout = new Intent(MainActivity.this,LoginActivity.class);
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

    private void saveURL(){

    }

    private void saveLoginState(String id_token){
        SharedPreferences sharedpreferences = getSharedPreferences("Status login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Google", id_token);
        editor.commit();

    }
}
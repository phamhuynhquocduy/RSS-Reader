package com.example.rssreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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

    private Button btnFetchFeed;
    private EditText edtLink;
    private ArrayList<RssFeedModel> mFeedModelList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFetchFeed = findViewById(R.id.btnFetchFeed);
        edtLink = findViewById(R.id.edtLink);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFeedModelList = new ArrayList<RssFeedModel>();

        btnFetchFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStuff();
            }
        });
    }
    private void doStuff(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //do your work
                String urlLink = edtLink.getText().toString();
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
                        //do your work after the job is finished
                        //fill recyclerView
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
}
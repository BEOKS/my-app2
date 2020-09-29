package com.mycompany.app;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class App{
    static ArrayList<String> urlArrayList=new ArrayList<String>();
    static DatabaseReference ref=null;
    public static void main(String[] args) {
	//init Firebase
        initFirebase();
	// write your code here
        try {
            while (true) {
                if(urlArrayList.size()!=0){
                    for(int i=0;i<urlArrayList.size();i++){
                        String url=urlArrayList.get(i);
                        Elements elements=null;
                        if((elements=getTableElements(getDocument(url)))==null){
                            System.err.println("Fail : cannot connet to "+url);
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference().child("URL_LIST/"+(i+1)+"/DATA").setValueAsync(elements.toString());
                            System.out.println("Success : connet to "+url);
                        }
                    }
                }
                else{
                    System.out.println("urlArrayList is null");
                }
                Thread.sleep(3000);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public static Document getDocument(String url){
        Document document=null;
        try{
            document=Jsoup.connect(url).get();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return document;
    }
    public static Elements getTableElements(Document document){
        if(document==null){
            return null;
        }
        else{
            return document.getElementsByTag("table");
        }
    }
    private static void initFirebase(){
        FileInputStream refreshToken = null;
        try {
            refreshToken = new FileInputStream("src/main/java/com/mycompany/app/noticealarm-122b4-firebase-adminsdk-ji4uu-da709949be.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .setDatabaseUrl("https://noticealarm-122b4.firebaseio.com/")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(options);

        ref = FirebaseDatabase.getInstance().getReference().child("URL_LIST");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String url=(String)dataSnapshot.child("URL").getValue();
                System.out.println("onChildAdded : "+url);
                urlArrayList.add(url);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String url=(String)dataSnapshot.child("URL").getValue();
                System.out.println("onChildAdded : "+url);
                urlArrayList.remove(url);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

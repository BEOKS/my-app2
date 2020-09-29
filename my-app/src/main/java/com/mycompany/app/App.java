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
import java.util.HashMap;
import java.util.Map;

public class App{
    static String url=null;
    public static void main(String[] args) {
	//init Firebase
        initFirebase();
	// write your code here
        try {
            while (true) {
                if(url!=null){
                    Elements elements=null;
                    if((elements=getTableElements(getDocument(url)))==null){
                        System.err.println("Fail : cannot connet to "+url);
                    }
                    else{
                        System.out.println("Success : connet to "+url);
                    }
                }
                else{
                    System.out.println("url is null");
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
            refreshToken = new FileInputStream("/Users/slwnskq/Desktop/GIT/my-app/src/main/java/com/mycompany/app/noticealarm-122b4-firebase-adminsdk-ji4uu-da709949be.json");
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("URL_LIST/1/URL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url=(String)dataSnapshot.getValue();
                System.out.println("on Data Changed");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference usersRef = ref.child("users");

        Map<String, String> users = new HashMap<>();
        users.put("alanisawesome", "Alan Turing");
        users.put("gracehop", "Grace Hopper");

        usersRef.setValueAsync(users);

    }
}

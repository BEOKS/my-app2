package com.mycompany.app;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class App{
    static ArrayList<String> urlArrayList=new ArrayList<String>();
    static HashMap<String,Elements> elementMap=new HashMap<String, Elements>();
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
                        Elements elements=getTableElements(getDocument(url));
                        if(elements==null){
                            System.err.println("Fail : cannot connet to "+url);
                        }
                        else{
                            if(!equalElements(elements,elementMap.get(url))){
                                final String temp_url=parseURLtoDatabaseKey(urlArrayList.get(i));
                                FirebaseDatabase.getInstance().getReference(temp_url).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Long data=(Long)dataSnapshot.getValue();
                                        FirebaseDatabase.getInstance().getReference(temp_url).setValueAsync(-1);
                                        FirebaseDatabase.getInstance().getReference(temp_url).setValueAsync(data);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            System.out.println("Success : connet to "+url);
                        }
                    }
                }
                else{
                    System.out.println("urlArrayList is null");
                }
                Thread.sleep(5000);
            }
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    public static boolean equalElements(Elements updatedElements, Elements pastElements){
        int maxIndex=1;
        String maxString="";
        Elements tr_updatedElements=updatedElements.get(0).getElementsByTag("tr");
        Elements elements1=tr_updatedElements.get(1).select("th,td");
        for(int i=0;i<elements1.size();i++){
            String string=elements1.get(i).text();
            if(string.length()>maxString.length()){
                try {
                    Integer.parseInt(string);
                }
                catch (NumberFormatException e){
                    maxString=string;
                    maxIndex=i;
                }
            }
        }
        //compare
        Elements tr_pastElements=pastElements.get(0).getElementsByTag("tr");
        for(int i=1;i<tr_updatedElements.size()&&i<tr_pastElements.size();i++){
            String a=tr_updatedElements.get(i).select("th,td").get(maxIndex).text(),
                    b=tr_pastElements.get(i).select("th,td").get(maxIndex).text();
            if(!a.equals(b)){
                pastElements=updatedElements;
                return false;
            }
        }
        return true;
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
        /**
         * handling handshake_failure
         */
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            System.setProperty("https.protocols", "TLSv1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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

        ref = FirebaseDatabase.getInstance().getReference();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String url=(String)dataSnapshot.getKey();
                url=parseDatabaseKeyToURL(url);
                System.out.println("onChildAdded : "+url);
                urlArrayList.add(url);
                elementMap.put(url,getTableElements(getDocument(url)));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.getKey()+": onDataChanged! value:"+(Long)dataSnapshot.getValue());
                if((Long)dataSnapshot.getValue()==0){
                    FirebaseDatabase.getInstance().getReference(dataSnapshot.getKey()).removeValue(null);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String url=(String)dataSnapshot.getKey();
                url=parseDatabaseKeyToURL(url);
                System.out.println("onChildRemoved : "+url);
                urlArrayList.remove(url);
                elementMap.remove(url);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public static String parseURLtoDatabaseKey(String urlAddress){
        return urlAddress.replace('.','-').replace('/','\\');
    }
    public static String parseDatabaseKeyToURL(String urlAddress){
        return urlAddress.replace('-','.').replace('\\','/');
    }
}
class Data{
    public ArrayList<ArrayList<String>> arrayLists=new ArrayList<ArrayList<String>>();
    public Data(Element tableHtml){
        Elements elements=tableHtml.getElementsByTag("tr");
        for(int i=0;i<elements.size();i++){
            Elements elements1=elements.get(i).select("th,td");
            ArrayList<String> arrayList=new ArrayList<String>();
            for(int j=0;j<elements1.size();j++){
                arrayList.add(elements1.get(j).text());
            }
            this.arrayLists.add(arrayList);
        }
        int d=0;
    }
}
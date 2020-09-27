import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Main{
    public static void main(String[] args) {
	// write your code here

    }
    public Document getDocument(String url){
        Document document=null;
        try{
            document=Jsoup.connect(url).get();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return document;
    }
}

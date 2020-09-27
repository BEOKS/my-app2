import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main{
    public static void main(String[] args) {
	// write your code here
        String url="http://computer.knu.ac.kr/06_sub/02_sub.html";
        try {
            while (true) {
                Elements elements=null;
                if((elements=getTableElements(getDocument(url)))==null){
                    System.err.println("Fail : cannot connet to "+url);
                }
                else{
                    System.out.println("Success : connet to "+url);
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
}

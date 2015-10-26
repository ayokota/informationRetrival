package CS172_Info_Ret.webCrawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseHTML {
	
	/**
	 * Takes in document and returns all URL's as a List of URL's
	 * @param doc The document to be parsed
	 * @return List of URL's found
	 */
	public List<URL> parseHTML(Document doc) {
		List<URL> linkList = new ArrayList<URL>();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			try{
				String url = link.attr("abs:href").replace("#main","");
				// "Parse only http links (avoid ftp, https or any other protocol)" 
				if (url.length() >= 7 && url.substring(0,7).equals("http://")) {
					linkList.add(new URL(url));
				}
			} catch(MalformedURLException ex) {
				ex.printStackTrace();
			}
		}
		return linkList;
	}
	
//	public static void main(String[] args){
//        String url = "http://www.cs.ucr.edu";
//        try{
//	        Document doc = Jsoup.connect(url).get();
//	        List<URL> linkList = parseHTML(doc);
//	        for(int i = 0; i < linkList.size(); ++i){
//	        	System.out.println(linkList.get(i).toString());
//	        }
//        } catch(IOException ex) {
//        	ex.printStackTrace();
//        }
//    }
}

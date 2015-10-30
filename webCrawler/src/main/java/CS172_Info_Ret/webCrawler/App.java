package CS172_Info_Ret.webCrawler;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import CS172_Info_Ret.webCrawler.Objects.NormalizedUrl;

/**
 * Hello world!
 *
 */
public class App 
{
	private static Queue<Pair> urlQueue = new LinkedList<Pair>();
	private static HashMap<String,Boolean> alreadyFound = new HashMap<String,Boolean>();
	private static Long numPages = 1500L;
	private static Long numHops = 1L;
	private static String filePathStore;
	private static String filePathSeeds;
	
	public static void parseUrl (String url, Long depth) {
		Crawler c = new Crawler (url);
    	
    	c.ParseRobots();
    	c.linkExtraction();
    	
		//print them out
		for(NormalizedUrl Nurl : c.getNormalizedUrlList()) {
			//Nurl.print();
			if(!alreadyFound.containsKey(Nurl.generateCleanUrl()) /*or excluded by robots.txt*/ ){
				if(depth+1 <= numHops) {
					urlQueue.add(new Pair(Nurl.generateCleanUrl(),depth+1) );
					alreadyFound.put(Nurl.generateCleanUrl(), true);
				}
			}
		}
	}
	
    public static void main( String[] args ) throws MalformedURLException
    {
    	
    	try {
    		
    		if(args.length == 4) {
    			numHops = Long.parseLong(args[0]);
    			numPages = Long.parseLong(args[1]);
    			filePathStore = args[2];
    			filePathSeeds = args[3];
    		}
    		
    		//Add seed to queue
        	String url = "http://www.about.com/";
        	urlQueue.add(new Pair(url,0L));
        	alreadyFound.put(url,true);
        	
			Long numPagesCounter = 0L;
			
			System.out.println("count\tdepth\turl");
			
			while(!urlQueue.isEmpty() && numPagesCounter < numPages) {
				numPagesCounter++;
				Pair next = urlQueue.remove();
				url = next.getURL();
				System.out.print(numPagesCounter);
				System.out.print("\t");
				System.out.print(next.getDepth());
				System.out.println("\t" + url);
				parseUrl(url,next.getDepth());
			}
        	
		} catch (Exception e) {			
			e.printStackTrace();
		}
    	
    }
}

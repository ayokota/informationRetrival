package CS172_Info_Ret.webCrawler;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import CS172_Info_Ret.webCrawler.Objects.NormalizedUrl;
import CS172_Info_Ret.webCrawler.Objects.Pair;

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
	
	
	/* for testing */
//	public static void parseUrl (String url, Integer depth) {
//		Crawler c = new Crawler (url);
//    	
//    	//c.ParseRobots();
//    	c.linkExtraction();
//    	
//		//print them out
//		for(NormalizedUrl Nurl : c.getNormalizedUrlList()) {
//			//Nurl.print();
//			if(!alreadyFound.containsKey(Nurl.generateCleanUrl()) /*or excluded by robots.txt*/ ){
//				if(depth+1 <= numHops) {
//					urlQueue.add(new Pair(Nurl.generateCleanUrl(),depth+1) );
//					alreadyFound.put(Nurl.generateCleanUrl(), true);
//				}
//			}
//		}
//	}
	
    public static void main( String[] args ) throws MalformedURLException
    {
    	if (args.length < 4) {
    		System.out.println("Too few arguments.\n" +
					"Usage:\n"+
					"1. Number of hops\n" +
					"2. Number of pages\n" +
					"3. Location to store crawled data\n" +
					"4. Location of the seeds");
    	}
    	
    	Crawler crawler = new Crawler();
    	crawler.crawl(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3]);
    	//CrawlerUtilities utility = new CrawlerUtilities();
    	//utility.writeFile("./temp", "hello world");
    	//System.out.println(utility.readFile("./temp"));
//    	for(String s : utility.ListSegments("./seeds")) {
//    		System.out.println(s);
//    	}
    	/*
    	 *
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
			
			System.out.println("Done!");
        	
		} catch (Exception e) {			
			e.printStackTrace();
		}
		*/
    	
    }
}

package CS172_Info_Ret.webCrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import CS172_Info_Ret.webCrawler.Objects.META;
import CS172_Info_Ret.webCrawler.Objects.NormalizedUrl;
import CS172_Info_Ret.webCrawler.Objects.Pair;
import CS172_Info_Ret.webCrawler.Objects.robot;

public class Crawler {
	
	private String historyPath = "./history/urlHistory.txt";
	private String mappingPath = "./history/mapping.txt";
	private String historyFolder = "./history";
	
	private List<String> history;
	private Queue<Pair> urlQueue;
	private CrawlerUtilities utility;
	private int numPageAlreadyOwn;
	
	/**
	 * Constructor for Crawler
	 */
	public Crawler () {
		this.utility = new CrawlerUtilities();
		this.history = loadHistory();
		this.numPageAlreadyOwn = this.history.size();
		this.urlQueue = new LinkedList<Pair>();
	}

	/**
	 * Loads history from urlHistory.txt file
	 * Also create any folder structure that's not already been created
	 * that includes historyFolder, historyPath, mappingPath
	 * 
	 * return a list of urls thats already been crawled
	 */
	private List<String> loadHistory () {
		List<String> urlHistory = new LinkedList<String> ();

		try {
			if(!utility.fileExists(historyFolder))
				new File(historyFolder).mkdirs();
			if(!utility.fileExists(mappingPath))
				new File(mappingPath).createNewFile();
			File history = new File(historyPath);
			if(!history.exists()) {
			    history.createNewFile();
			} else {
				urlHistory = utility.TokenizeByNewLine(utility.readFile(historyPath));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlHistory;
	}
 	
	/**
	 * record a url into urlHistory.txt
	 */
	private void recordHistory(String url) {
		history.add(url);
		utility.appendToFile(historyPath, url);
	}
	
	/**
	 * Parse out all links from the webpage, and put these links into NormalizedUrl object
	 * return a list of extracted Urls
	 */
	public List<NormalizedUrl> urlExtraction (String url) {
		List <NormalizedUrl> NormalizedList = new LinkedList<NormalizedUrl> ();
		try {
			List<String> urlList = new LinkedList<String> ();
			Document doc = Jsoup.connect(url).get();
			urlList = new ParseHTML().parseHTML(doc);
			
			for (String u : urlList) {
				NormalizedUrl nUrl = new NormalizedUrl();
				
				nUrl.UrlNormalization(new URL(u));
				NormalizedList.add(nUrl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return NormalizedList;
	}
	
	private boolean validateUrl (String url) {
		try {
			URL urlObj = new URL(url);
			if (urlObj.openStream()!=null){
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Download a webcontent the primative way
	 * @param url : the url you wish to download
	 * @return content of the page
	 */
	private  String fetchPageToMemory(String url) throws IOException, MalformedURLException {
		try {
			URL urlObj = new URL(url);
			StringBuilder content = new StringBuilder();
			if (!validateUrl(url))
				return null;
			BufferedReader x = new BufferedReader (new InputStreamReader(urlObj.openConnection().getInputStream()));
			
			while(x.ready()) {
				content.append(x.readLine());
				content.append("\n");
			}
			
			x.close();
			
			return content.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Check if robots.txt exists for the given url
	 * return true if exists
	 * return false if it doesn't exist
	 */
	public boolean robotExists(NormalizedUrl nUrl) {
		try {
			String robotsUrl = nUrl.getProtocol() + "://" + nUrl.getHost() + "/robots.txt";
			if(validateUrl(robotsUrl)) 
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Download the robots.txt for the given url
	 * parse the robots.txt
	 * return a list of robots
	 */
	public List<robot> ParseRobots(NormalizedUrl nUrl) {
		List <robot> robotsList = new LinkedList<robot> ();
		try {
			String robotsUrl = nUrl.getProtocol() + "://" + nUrl.getHost() + "/robots.txt";

			String robots = fetchPageToMemory(robotsUrl);
						
			if(robots==null){
				return null;
			}
			StringTokenizer st = new StringTokenizer(robots, "\n");
			
			robot agent = null;
			
			while(st.hasMoreElements()) {
				String token = st.nextElement().toString();
				if(token.startsWith("#"))
					continue;
				
				StringTokenizer split = new StringTokenizer(token, ": ");
				String type = split.nextElement().toString();
				if(type.equals("User-agent")) {
					if(agent != null)
						robotsList.add(agent);
					agent = new robot();
					if(split.hasMoreElements())
						agent.setUserAgent(split.nextElement().toString());
				} else if(type.equals("Allow")) {
					if(split.hasMoreElements()){
						String subUrl = split.nextElement().toString();
						META metaContent = null;
						if(!agent.getMetaContents().containsKey(subUrl)) {
							metaContent = new META();
						} else {
							metaContent = agent.getMetaContents().get(subUrl);
						}
						metaContent.setFollow(true);
						agent.getMetaContents().put(subUrl, metaContent);
					}
				} else if(type.equals("Disallow")) {
					if(split.hasMoreElements()){
						String subUrl = split.nextElement().toString();
						META metaContent = null;
						if(!agent.getMetaContents().containsKey(subUrl)) {
							metaContent = new META();
						} else {
							metaContent = agent.getMetaContents().get(subUrl);
						}
						metaContent.setNofollow(true);
						agent.getMetaContents().put(subUrl, metaContent);
					}
				} else if(type.equals("Index")) {
					if(split.hasMoreElements()){
						String subUrl = split.nextElement().toString();
						META metaContent = null;
						if(!agent.getMetaContents().containsKey(subUrl)) {
							metaContent = new META();
						} else {
							metaContent = agent.getMetaContents().get(subUrl);
						}
						metaContent.setIndex(true);
						agent.getMetaContents().put(subUrl, metaContent);
					}
				} else if(type.equals("Noindex")) {
					if(split.hasMoreElements()){
						String subUrl = split.nextElement().toString();
						META metaContent = null;
						if(!agent.getMetaContents().containsKey(subUrl)) {
							metaContent = new META();
						} else {
							metaContent = agent.getMetaContents().get(subUrl);
						}
						metaContent.setNoindex(true);
						agent.getMetaContents().put(subUrl, metaContent);
					}
				} else if(type.equals("Crawl-Delay")) {
					if(split.hasMoreElements())
						agent.setCrawl_Delay(Integer.parseInt(split.nextElement().toString()));
				} else {
					
				}
			}
			
			robotsList.add(agent);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return robotsList;
	}
		
	/**
	 * Check if the given url has already been crawled
	 * return true if it's being crawled
	 * return false if it didn't
	 */
	public boolean isDup(String url) {
		for (String s : history) {
			if (s.equals(url)) 
				return true;
		}
		return false;
	}
	
	/**
	 * Extract the robot with agent *
	 * return the robot with the agent *
	 */
	private robot extractGenericUser(List<robot> robots) {
		for (robot r : robots) {
			if (r.getUserAgent().equals("*"))
				return r;
		}
		return null;
	}
	
	/**
	 * See if the targetUrl has rules in the robots.txt with subUrl and baseUrl passed in
	 * return true if robots.txt has rules for targetUrl
	 * return false if it doesn't
	 * 
	 * May need to redesign this function to use regex matching mechanic
	 * 
	 */
	private boolean urlMatches (String targetUrl, String subUrl, String baseUrl) {	
		//normalize the base url by deleting the last "/"
		if(baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length()-1);
		}
		
		//this is one way to handle the regex
		if(subUrl.startsWith("*")) {
			if(subUrl.endsWith("*")) {
				if(targetUrl.startsWith(baseUrl) && targetUrl.contains(subUrl.replace("*", ""))) 
					return true;
			}
			if(targetUrl.startsWith(baseUrl) && targetUrl.endsWith(subUrl.replace("*", ""))) {
				return true;
			}
		} else if (subUrl.endsWith("*")) {
			String firstHalf = targetUrl + subUrl.replace("*",  "") ;
			if(targetUrl.startsWith(firstHalf)) 
				return true;
		} else {
			if(targetUrl.equals(baseUrl + subUrl)) {
				return true;
			}
		}
		//System.out.println("targetUrl: " + targetUrl + "\tsubUrl: " + subUrl + "\tbaseUrl: " + baseUrl);
		return false;
	}
	
	/**
	 * Filter a list of url, the crawlable urls will be added to the urlQueue
	 */
	public void enqueueUrls(List<NormalizedUrl> fullList, List<robot> robots, Pair url) {
		robot genericUser = extractGenericUser(robots);
		if (genericUser==null) 
			return;
		
		for(NormalizedUrl nUrl : fullList) {
			if(nUrl.getProtocol().equals("http")) {
				for(String subUrl : genericUser.getMetaContents().keySet()) {
					if(urlMatches(nUrl.generateCleanUrl(), subUrl, url.getURL())) {
						if (genericUser.getMetaContents().get(subUrl).isFollow()) {
							urlQueue.add(new Pair(nUrl.generateCleanUrl(), url.getDepth()+1));
						}
					} else {
						//if it's not in the robots.txt
						urlQueue.add(new Pair(nUrl.generateCleanUrl(), url.getDepth()+1));
					}
				}
			}
		}
	}
	
	/**
	 * check the history and remove the urls that's already been crawled
	 * return a filtered list of url
	 */
	private List<NormalizedUrl> removeDups (List<NormalizedUrl> urlList) {
		List<NormalizedUrl> nonDups = new LinkedList<NormalizedUrl> ();
		for(NormalizedUrl n : urlList) {
			if(!history.contains(n.generateCleanUrl()))
				nonDups.add(n);
		}
		return nonDups;
	}
	
	/**
	 * Check if a give url will give return status of 200
	 * return true if 200
	 * return false if not 200
	 */
	private boolean checkConnection (String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)u.openConnection();
			connection.setConnectTimeout(5000);
			connection.connect();
	
			 if(connection.getResponseCode()==200) 
				 return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Check if a give url is a HTML page
	 * return true if it is HTML
	 * return false if it is not HTML
	 */
	private boolean isHTML(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection urlc = (HttpURLConnection)u.openConnection();
			urlc.setAllowUserInteraction( false );
			urlc.setDoInput( true );
			urlc.setDoOutput( false );
			urlc.setUseCaches( true );
			urlc.setRequestMethod("HEAD");
			urlc.connect();
			if(urlc.getContentType().contains("text/html")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Check if a give url is crawlable
	 * conditions are:
	 * depth must be smaller than hop
	 * connection must return 200
	 * page must be HTML
	 * the url must not be a duplicate
	 */
	private boolean isCrawlable (String url, Integer depth, Integer numHops){
		//check if the hop is too far
		if(depth > numHops)
			return false;
		//check if response code is 200
		if(!checkConnection(url)) 
			return false;
		//check if page is html
		if(!isHTML(url))
			return false;
		//check if dup
		if(isDup(url)) 
			return false;
		return true;
		
	}
	
	/**
	 * This is the main crawling method
	 */
	public void crawl(Integer numHops, Integer numPages, String filePathStore, String filePathSeeds) {
		try {
			//load seeds into the queue
			urlQueue = utility.loadSeeds(filePathSeeds);

			while(!urlQueue.isEmpty() && (history.size()-numPageAlreadyOwn)<numPages) {

				NormalizedUrl nUrl = new NormalizedUrl ();
				
				//pop top of the queue
				Pair urlPair = urlQueue.remove();
				//check if it's safe to crawl this page
				if(!isCrawlable(urlPair.getURL(), urlPair.getDepth(), numHops))
					continue;
				//put this in history to prevent from crawling again
				recordHistory(urlPair.getURL());
				//download page ---> storage
				String htmlFile = filePathStore + history.size() +".html";
				utility.writeFile(htmlFile, fetchPageToMemory(urlPair.getURL()));
				utility.appendToFile(mappingPath, urlPair.getURL() + ":" + htmlFile);
					
				nUrl.UrlNormalization(new URL(urlPair.getURL()));
				List<robot> robots = null;
				if(checkConnection(nUrl.toString())) {
					robots = ParseRobots(nUrl);
				}
				//extract link
				List<NormalizedUrl> nUrlList = removeDups(urlExtraction(urlPair.getURL()));
				//valid url -> queue
				if(robots!=null)
					enqueueUrls(nUrlList, robots, urlPair);
				else {
					for(NormalizedUrl n : nUrlList) {
						if(nUrl.getProtocol().equals("http")) {
							urlQueue.add(new Pair(n.generateCleanUrl(), urlPair.getDepth()));
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*--- all testing function goes here ---*/
	/*for testing*/
	private void printRobots(List<robot> robots) {
		for (robot r : robots) {

			System.out.println("Agent : " + r.getUserAgent());
			System.out.println("Craw Delay : " + r.getCrawl_Delay());
			for (String key : r.getMetaContents().keySet()) {
			    META value = r.getMetaContents().get(key);
			    System.out.println(key );
			    System.out.print("Follow: " + value.isFollow()+ "\t");
			    System.out.print("NoFollow: " + value.isNofollow()+ "\t");
			    System.out.print("Index: " + value.isIndex()+ "\t");
			    System.out.println("NoIndex: " + value.isNoindex());
			}
		}
	}
}

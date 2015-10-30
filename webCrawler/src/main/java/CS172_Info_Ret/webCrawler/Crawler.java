package CS172_Info_Ret.webCrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import CS172_Info_Ret.webCrawler.Objects.NormalizedUrl;
import CS172_Info_Ret.webCrawler.Objects.robot;

public class Crawler {
	
	private List <NormalizedUrl> normalizedUrlList;
	private List <robot> robots;
	private Document doc;
	private String url;
	private NormalizedUrl normalizedUrl;
	
	/**
	 * Constructor
	 */
	public Crawler (String url) {
		try {
			this.normalizedUrlList = new LinkedList<NormalizedUrl> ();
			this.robots = new LinkedList<robot> ();

			this.url = url;
			try {
				this.doc = Jsoup.connect(url).get();
			} catch (SocketTimeoutException e) {
				this.doc = new Document("");
				System.out.println("Timed out\t" + url);
			}
			
			NormalizedUrl n = new NormalizedUrl();
			n.UrlNormalization(new URL(url));
			this.normalizedUrl = n;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse out all links from the webpage, and put these links into NormalizedUrl object
	 */
	public void linkExtraction () {
		try {
			List<URL> urlList = new LinkedList<URL> ();
			urlList = new ParseHTML().parseHTML(doc);
			
			for (URL u : urlList) {
				NormalizedUrl nUrl = new NormalizedUrl();
				
				nUrl.UrlNormalization(u);
				this.normalizedUrlList.add(nUrl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Download a webcontent the primative way
	 * @param path : the path of the folder
	 * @param fileName : name of the file that saves all the web content
	 * @param url : the url you wish to download
	 */
	public  void downloadPage(String path, String fileName, String url) throws IOException, MalformedURLException {
		URL urlObj = new URL(url);

		BufferedReader x = new BufferedReader (new InputStreamReader(urlObj.openConnection().getInputStream()));
		
		BufferedWriter fos = new BufferedWriter(new FileWriter( path + fileName));
		
		while(x.ready()) {
			String line = x.readLine();
			fos.write(line);
			fos.write("\n");
		}
		
		x.close();
		fos.close();
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
			return null;
		}
	}
	
	private void printRobots() {
		for (robot r : this.robots) {
			System.out.println(r.getUserAgent());
			System.out.println(r.getCrawl_Delay());
			for(String s: r.getAllowList()) {
				System.out.println(s);
			}
			for(String s: r.getDisallowList()) {
				System.out.println(s);
			}
		}
	}
	
	public void ParseRobots() {
		try {
			String robotsUrl = normalizedUrl.getProtocol() + "://" + normalizedUrl.getHost() + "/robots.txt";
			String robots = fetchPageToMemory(robotsUrl);
			if(robots==null){
				return;
			}
			StringTokenizer st = new StringTokenizer(robots, "\n");
			
			robot agent = null;
			
			while(st.hasMoreElements()) {
				String token = st.nextElement().toString();
				StringTokenizer split = new StringTokenizer(token, ": ");
				String type = split.nextElement().toString();
				if(type.equals("User-agent")) {
					if(agent != null)
						this.robots.add(agent);
					agent = new robot();
					if(split.hasMoreElements())
						agent.setUserAgent(split.nextElement().toString());
				} else if(type.equals("Allow")) {
					if(split.hasMoreElements())
						agent.getAllowList().add(split.nextElement().toString());
				} else if(type.equals("Disallow")) {
					if(split.hasMoreElements())
						agent.getDisallowList().add(split.nextElement().toString());
				} else if(type.equals("Crawl-Delay")) {
					if(split.hasMoreElements())
						agent.setCrawl_Delay(Integer.parseInt(split.nextElement().toString()));
				} else {
					
				}
				
			}
			this.robots.add(agent);
			
			//printRobots();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<NormalizedUrl> getNormalizedUrlList() {
		return normalizedUrlList;
	}



	public void setNormalizedUrlList(List<NormalizedUrl> normalizedUrlList) {
		this.normalizedUrlList = normalizedUrlList;
	}
}

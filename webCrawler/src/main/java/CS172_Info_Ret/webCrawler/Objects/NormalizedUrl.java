package CS172_Info_Ret.webCrawler.Objects;

import java.net.MalformedURLException;
import java.net.URL;

public class NormalizedUrl {
	private String protocol;
	private int port ;
	private String host ;
	private String path ;
	private String query ;
	private String bookmark ;
	private String url ;
	
	public NormalizedUrl() {
		this.protocol="";
		this.port=0;
		this.host="";
		this.path="";
		this.query="";
		this.bookmark="";
		this.url="";
	}
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getBookmark() {
		return bookmark;
	}

	public void setBookmark(String bookmark) {
		this.bookmark = bookmark;
	}
	
	public void setURL(String urlString) {
		this.url = urlString;
	}
	
	public String getUrl() {
		return this.url;
	}

	/**
	 * Takes in whole url as a string, analyze and normalize it into NormalizedUrl object
	 * @param whole url as a String
	 * @return NormalizedUlr object
	 */
	public  void UrlNormalization (URL url) throws MalformedURLException {
		if (url.equals("") || url==null) {
			System.err.println("Invalid url");
		}
		this.protocol = url.getProtocol();
		this.port= url.getPort();
		this.host=url.getHost();
		this.path=url.getPath();
		this.query=url.getQuery();
		this.bookmark=url.getRef();
		this.url = url.toString();
	}
	
	public void print() {
		System.out.println("Protocol is : " + this.protocol );
		System.out.println("Port is : " + this.port );
		System.out.println("Host is : " + this.host );
		System.out.println("Path is : " + this.path );
		System.out.println("Query is : " + this.query );
		System.out.println("Bookmark is : " + this.bookmark );
	}
	
	/**
	 * Generate a url in String
	 */
	public String generateCleanUrl() {
		String cleanUrl = this.protocol 
				+ "://"
				+ this.host 
				+ (this.port >= 0 ? "" + this.port: "") 
				+ (this.path == null ? "" : this.path)
				+ (this.query == null ? "" : this.query);
		return cleanUrl;
	}
}

package CS172_Info_Ret.webCrawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

import CS172_Info_Ret.webCrawler.Objects.Pair;

public class CrawlerUtilities {
	public String readFile(String filename) {
		StringBuilder content = new StringBuilder();
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(filename));
	    	String line;
	    	while((line = br.readLine())!=null) 
	    		content.append(line).append("\n");
	    	
	    	br.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return content.toString();
	}
	
	public void writeFile(String filename, String content) {
		try {
			BufferedWriter fos = new BufferedWriter(new FileWriter( filename));
			fos.write(content);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void appendToFile(String filename, String content) {
		try {
			content = content + "\n";
			Files.write(Paths.get(filename), content.getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*perform ls non-recursively, and return a list of files only with the given directory */
	public List<String> ListSegments (String directory) {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		List<String> files = new LinkedList<String> ();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) 
		        files.add(listOfFiles[i].getName());
		}
		
		return files;
	}
	
	
	public Queue<Pair> loadSeeds (String directory) {
		Queue<Pair> urlQueue = new LinkedList<Pair> ();
		
		
		
		for ( String file : ListSegments(directory)) {
			for ( String url : TokenizeByNewLine(readFile(directory + file))) {
				urlQueue.add(new Pair(url, 0));
			}
		}
		
		return urlQueue;
	}
	
	public List<String> TokenizeByNewLine(String content) {
		List <String> tokens = new LinkedList<String> ();
		
		StringTokenizer st = new StringTokenizer(content, "\n");
		
		while(st.hasMoreElements()) {
			tokens.add(st.nextElement().toString());
		}
		return tokens;
	}
	
	public boolean fileExists(String directory) {
		File f = new File(directory);
		if (f.exists() ) {
		   return true;
		}
		return false;
	}
}

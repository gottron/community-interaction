package de.unikoblenz.west.reveal.roles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserAnalysisFileReader {

	private String nextline = null;
	
	private String community = "N/A";
	
	private BufferedReader reader = null;
	
	public UserAnalysisFileReader(File in, String community) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF8"));
		this.nextline = "";
		this.community = community;
		// Read twice to skip header line
		this.fetchNextLine();
		this.fetchNextLine();
	}
	
	public boolean hasNext() {
		return (nextline != null);
	}
	
	public static void main(String[] args) throws IOException {
		UserAnalysisFileReader uafr = new UserAnalysisFileReader(new File("data-out/Math-u-1.csv"), "Math (StackExchange)");
		while(uafr.hasNext()) {
			System.out.println(uafr.nextAsUserWithRole());
		}
	}
	
	public UserWithRole nextAsUserWithRole() throws IOException {
		if (this.nextline == null) {
			return null;
		} else {
			UserWithRole result = new UserWithRole();
			String[] entries = this.nextline.split("\\t");
			/*
			 *  0 -> accountName
			 *  1 -> id
			 *  2 -> posts
			 *  3 -> questions 
			 *  4 -> answers 
			 *  5 -> comments 
			 *  6 -> discussions 
			 *  7 -> replyingUsers 
			 *  8 -> neighbours 
			 *  9 -> inDegreeRatio 
			 * 10 -> postReplyRatio 
			 * 11 -> threadInitiationRatio 
			 * 12 -> biDirPostsRatio
			 * 13 -> biDirNeighbourRatio
			 * 14 -> avgPostsPerThread 
			 * 15 -> stddevPostsPerThread 
			 * 16 -> avgAnswersPerQuestion 
			 * 17 -> avgCommentsPerPost 
			 * ... (Rest not of interest)
			 */
			result.username = entries[0];
			result.id = Long.parseLong(entries[1]);
			result.contributions = Integer.parseInt(entries[2]);
			result.community = this.community;
			result.avgPostPerThread = Double.parseDouble(entries[14]);
			result.bidirNeighbourRatio = Double.parseDouble(entries[13]);
			result.bidirThreadRatio = Double.parseDouble(entries[12]);
			result.inDegreeRatio = Double.parseDouble(entries[9]);
			result.postsReplyRatio = Double.parseDouble(entries[10]);
			result.stddevPostsPerThread = Double.parseDouble(entries[15]);
			result.threadInitiationRatio = Double.parseDouble(entries[11]);
					
			this.fetchNextLine();
			return result;
		}
	}
	
	private void fetchNextLine() throws IOException {
		if (this.nextline != null) {
			this.nextline = this.reader.readLine();
		}
	}
	
}

package de.unikoblenz.west.reveal.roles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class UserAnalysisFileReader {

	private String nextline = null;
	
	private String community = "N/A";
	
	private BufferedReader reader = null;
	
	public UserAnalysisFileReader(File in, String community) throws IOException {
		this.reader = new BufferedReader(new FileReader(in));
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
			 *  1 -> posts
			 *  2 -> questions 
			 *  3 -> answers 
			 *  4 -> comments 
			 *  5 -> discussions 
			 *  6 -> replyingUsers 
			 *  7 -> neighbours 
			 *  8 -> inDegreeRatio 
			 *  9 -> postReplyRatio 
			 * 10 -> threadInitiationRatio 
			 * 11 -> biDirPostsRatio
			 * 12 -> biDirNeighbourRatio
			 * 13 -> avgPostsPerThread 
			 * 14 -> stddevPostsPerThread 
			 * 15 -> avgAnswersPerQuestion 
			 * 16 -> avgCommentsPerPost 
			 * ... (Rest not of interest)
			 */
			result.username = entries[0];
			result.contributions = Integer.parseInt(entries[1]);
			result.community = this.community;
			result.avgPostPerThread = Double.parseDouble(entries[13]);
			result.bidirNeighbourRatio = Double.parseDouble(entries[12]);
			result.bidirThreadRatio = Double.parseDouble(entries[11]);
			result.inDegreeRatio = Double.parseDouble(entries[8]);
			result.postsReplyRatio = Double.parseDouble(entries[9]);
			result.stddevPostsPerThread = Double.parseDouble(entries[14]);
			result.threadInitiationRatio = Double.parseDouble(entries[10]);
					
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

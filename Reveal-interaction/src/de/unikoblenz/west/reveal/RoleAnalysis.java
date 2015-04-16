package de.unikoblenz.west.reveal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;

import de.unikoblenz.west.reveal.analytics.CommunityAnalysis;
import de.unikoblenz.west.reveal.roles.RoleAssociation;
import de.unikoblenz.west.reveal.roles.UserWithFeatures;
import de.unikoblenz.west.reveal.roles.UserWithRole;
import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.twitter.snow.SnowCommunityFactory;

public class RoleAnalysis {

	public static void main(String[] args) throws IOException {
		// Setup for reading tweets in JSON format from input files 
		String path = "/Users/gottron/Documents/Data/SNOW/snow14_testset_tweets/";
		File inDir = new File(path);
		String[] jsonFiles = inDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("tweets.json.");
			}
		});
		for (int i = 0; i< jsonFiles.length; i++) {
			jsonFiles[i] = path + jsonFiles[i];
		}

		// Calling the factory methods to create the internal community structures from the tweets in the files. 
		System.out.println("Constructing community ...");
		Community seCommunity = SnowCommunityFactory.parseCommunity("snow", jsonFiles);
		int minLimit = 1;
		System.out.println("Community Analysis, using minlimit: "+minLimit);
		HashSet<UserWithFeatures> uwf = CommunityAnalysis.analyseUserFeatures(seCommunity, minLimit);
		
		System.out.println("Converting users ...");
		// Convert into UserWithRole objects suitable for Role analysis
		HashSet<UserWithRole> users = new HashSet<UserWithRole>();
		for (UserWithFeatures userFeatures : uwf) {
			UserWithRole u = userFeatures.convertToUserWithRole();
			users.add(u);
		}
		
		// Actual role analysis
		System.out.println("Processing users (Role Analysis) ...");
		RoleAssociation ra = new RoleAssociation();
		ra.process(users);
		
		// Flushing results to simple log file
		System.out.println("Writing results ...");
		PrintStream out = new PrintStream(new File("out.log"), "UTF8");
		for (UserWithRole uwr : users) {
			out.println(uwr.id+"\t"+ uwr.username+"\t"+uwr.role);
		}
		out.close();
	}

}

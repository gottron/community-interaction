package de.unikoblenz.west.reveal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.unikoblenz.west.reveal.analytics.CommunityAnalysis;
import de.unikoblenz.west.reveal.stackexchange.StackExchangeCommunityFactory;
import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.twitter.snow.SnowCommunityFactory;

public class SnowAnalysis {

	public static void main(String[] args) throws IOException {
		String path = "/Users/gottron/Documents/Data/SNOW/snow14_testset_tweets/";
		File inDir = new File(path);
		String[] jsonFiles = inDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("tweets.json.");
			}
		});
		for (int i = 0; i< jsonFiles.length; i++) {
			jsonFiles[i] = path + jsonFiles[i];
		}
		long init = System.currentTimeMillis();

		Community seCommunity = SnowCommunityFactory.parseCommunity("snow", jsonFiles);
		CommunityAnalysis.communityToCsv(seCommunity, new File("data-out/snow-ncsrd-1.csv"));
		for (int minLimit = 1; minLimit < 3; minLimit++) {
			System.out.println("Using minlimit: "+minLimit);
			CommunityAnalysis.analyseDiscussionTrees(seCommunity, new File("data-out/snow-dt-"+minLimit+".csv"), new File("data-out/snow-dt-header-"+minLimit+".txt"),minLimit);
			CommunityAnalysis.analyseUsers(seCommunity, new File("data-out/snow-u-"+minLimit+".csv"), new File("data-out/snow-u-header-"+minLimit+".txt"),minLimit);
		}
		long tick = System.currentTimeMillis();
		System.out.println("Time: "+(tick-init)+" ms");

	}

}

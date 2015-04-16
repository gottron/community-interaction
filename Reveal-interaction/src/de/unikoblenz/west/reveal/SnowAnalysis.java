package de.unikoblenz.west.reveal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import de.unikoblenz.west.reveal.analytics.CommunityAnalysis;
import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.twitter.snow.SnowCommunityFactory;

/**
 * Community Analysis for the SNOW dataset (Twitter). Generates community
 * objects and flushes the results of an analysis of discussion trees and user
 * to intermediate files for a more detailed analysis (e.g. role analysis)
 * 
 * @author Thomas Gottron
 * 
 */
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
		for (int i = 0; i < jsonFiles.length; i++) {
			jsonFiles[i] = path + jsonFiles[i];
		}
		long init = System.currentTimeMillis();

		Community seCommunity = SnowCommunityFactory.parseCommunity("snow",
				jsonFiles);
		CommunityAnalysis.communityToCsv(seCommunity, new File(
				"data-out/snow-ncsrd-1.csv"));
		for (int minLimit = 1; minLimit < 3; minLimit++) {
			System.out.println("Using minlimit: " + minLimit);
			CommunityAnalysis.analyseDiscussionTrees(seCommunity, new File(
					"data-out/snow-dt-" + minLimit + ".csv"), new File(
					"data-out/snow-dt-header-" + minLimit + ".txt"), minLimit);
			CommunityAnalysis.analyseUsers(seCommunity, new File(
					"data-out/snow-u-" + minLimit + ".csv"), new File(
					"data-out/snow-u-header-" + minLimit + ".txt"), minLimit);
		}
		long tick = System.currentTimeMillis();
		System.out.println("Time: " + (tick - init) + " ms");

	}

}

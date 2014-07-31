package de.unikoblenz.west.reveal;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.unikoblenz.west.reveal.analytics.CommunityAnalysis;
import de.unikoblenz.west.reveal.stackexchange.StackExchangeCommunityFactory;
import de.unikoblenz.west.reveal.structures.Community;

public class StackExchangeAnalysis {

	private final static String COMMUNITY_NAME_OPTION = "n";
	private final static String USER_FILE_OPTION = "u";
	private final static String POST_FILE_OPTION = "p";
	private final static String COMMENT_FILE_OPTION = "c";
	
	public static void main(String[] args) {
		Options options = new Options();

		options.addOption(COMMUNITY_NAME_OPTION, true, "Name for the community");
		options.addOption(USER_FILE_OPTION, true, "users file (StackExchange XML dump format)");
		options.addOption(POST_FILE_OPTION, true, "posts file (StackExchange XML dump format)");
		options.addOption(COMMENT_FILE_OPTION, true, "comments file (StackExchange XML dump format)");
		
		CommandLineParser parser = new BasicParser();
		try {
			String name = "(no name)";
			String userFile = null;
			String postFile = null;
			String commentFile = null;

			CommandLine cmd = parser.parse( options, args);
			if (cmd.hasOption(COMMUNITY_NAME_OPTION)) {
				name = cmd.getOptionValue(COMMUNITY_NAME_OPTION);
			}
			if (cmd.hasOption(USER_FILE_OPTION)) {
				userFile = cmd.getOptionValue(USER_FILE_OPTION);
			}
			if (cmd.hasOption(POST_FILE_OPTION)) {
				postFile = cmd.getOptionValue(POST_FILE_OPTION);
			}
			if (cmd.hasOption(COMMENT_FILE_OPTION)) {
				commentFile = cmd.getOptionValue(COMMENT_FILE_OPTION);
			}

			if ( (userFile == null) || (userFile == null) || (userFile == null) ) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "StackExchangeAnalysis", options );
			} else {
				Community seCommunity = StackExchangeCommunityFactory.parseCommunity(name, userFile, postFile, commentFile);
				for (int minLimit = 0; minLimit < 3; minLimit++) {
					System.out.println("Using minlimit: "+minLimit);
					CommunityAnalysis.analyseDiscussionTrees(seCommunity, new File("data-out/"+name+"-dt-"+minLimit+".csv"), new File("data-out/"+name+"-dt-header-"+minLimit+".txt"),minLimit);
					CommunityAnalysis.analyseUsers(seCommunity, new File("data-out/"+name+"-u-"+minLimit+".csv"), new File("data-out/"+name+"-u-header-"+minLimit+".txt"),minLimit);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
}

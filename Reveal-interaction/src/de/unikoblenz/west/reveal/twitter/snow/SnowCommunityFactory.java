package de.unikoblenz.west.reveal.twitter.snow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.User;

/**
 * Factory class to generate Community objects from Twitter data in JSON format,
 * specifically for the SNOW dataset
 * 
 * @author Thomas Gottron
 * 
 */
public class SnowCommunityFactory {

	/**
	 * Generates a Community object from a list of JSON files providing tweets
	 * in native JSON format. The tweets are parsed and used to derive the list
	 * of all users and all appearing interaction, i.e. discussion trees.
	 * 
	 * @param name
	 *            Name for the community (for displaying purposes)
	 * @param jsonFiles
	 *            list of files containing tweets in JSON format
	 * @return Community object representing the interaction on Twitter captured
	 *         in the input files.
	 * @throws IOException
	 */
	public static Community parseCommunity(String name, String[] jsonFiles)
			throws IOException {
		// resulting community object to be filled with data.
		Community result = new Community(name);
		for (String file : jsonFiles) {
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF8"));
			String line = null;
			while ((line = bin.readLine()) != null) {
				SnowRawPost raw = SnowRawPost.fromJSON(line);

				User user = null;
				// Check if the author of the tweet is already captured in the
				// community -- if yes: retrieve, if no: create
				if (!result.existsUser(raw.authorId)) {
					user = result.createUser(raw.authorId);
					user.accountName = SnowCommunityFactory
							.cleanUserName(raw.authorName);
				} else {
					user = result.getUser(raw.authorId);

				}
				// Check if the message is already in the community (can happen
				// if we have seen a reply to this tweet before the actual
				// tweet) -- if yes: retrieve, if no: create
				DiscussionNode node = null;
				if (result.existsDiscussionNode(raw.postId)) {
					node = result.getDiscussionNode(raw.postId);
				} else {
					node = result.createDiscussionNode(raw.postId);
				}
				node.setUser(user);

				if (raw.inReplyToUser > 0) {
					// Raw tweet contains info about this tweet having been in
					// reply to another user.
					if (raw.inReplyTo > 0) {
						// Raw tweet contains info about this tweet having been
						// in reply to another tweet.
						User replyToUser = null;
						// Check if author of the replied-to tweet is already
						// captured in the community -- if yes: retrieve, if no:
						// create
						if (!result.existsUser(raw.inReplyToUser)) {
							replyToUser = result.createUser(raw.inReplyToUser);
							replyToUser.accountName = SnowCommunityFactory
									.cleanUserName(raw.replyToUserName);
						} else {
							replyToUser = result.getUser(raw.inReplyToUser);
						}
						// Check if the replied-to tweet is already containd in
						// the community -- if yes: retrieve, if no: create
						DiscussionNode replyToNode = null;
						if (result.existsDiscussionNode(raw.inReplyTo)) {
							replyToNode = result
									.getDiscussionNode(raw.inReplyTo);
						} else {
							replyToNode = result
									.createDiscussionNode(raw.inReplyTo);
							replyToNode.setUser(replyToUser);
						}
						// add the current tweet as a discussion child node to
						// replied-to tweet
						replyToNode.addChild(node);
					}
				}

				// System.out.println(user.getId()+"\t"+raw.postId+"\t"+raw.inReplyTo+"\t"+raw.authorId+"\t"+raw.authorName);
			}
			bin.close();
		}
		return result;
	}

	/**
	 * Internal method to harmonise representation of user names by replacing
	 * all white spaces into blanks.
	 * 
	 * @param name
	 *            raw user name (original account name)
	 * @return cleaned user name which only contains blanks as white spaces.
	 */
	public static String cleanUserName(String name) {
		return name.replaceAll("\\s", " ");
	}

}

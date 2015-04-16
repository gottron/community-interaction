package de.unikoblenz.west.reveal.twitter.snow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.User;

public class SnowCommunityFactory {

	public static Community parseCommunity(String name, String[] jsonFiles) throws IOException {
		Community result = new Community(name);
		int cnt = 0;
		for (String file :jsonFiles) {
			BufferedReader bin = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String line = null;
			while((line= bin.readLine()) != null) {
				cnt++;
				SnowRawPost raw = SnowRawPost.fromJSON(line);

				User user = null;
		    	if (! result.existsUser(raw.authorId)) {
			    	user = result.createUser(raw.authorId);
			    	user.accountName = SnowCommunityFactory.cleanUserName(raw.authorName);
		    	} else {
		    		user = result.getUser(raw.authorId);
		    	
		    	}
		    	DiscussionNode node = null;
		    	if (result.existsDiscussionNode(raw.postId)) {
		    		node = result.getDiscussionNode(raw.postId);
		    	} else {
		    		node = result.createDiscussionNode(raw.postId);
		    	}
		    	node.setUser(user);

				if (raw.inReplyToUser > 0) {
					if (raw.inReplyTo > 0) {
						User replyToUser = null;
				    	if (! result.existsUser(raw.inReplyToUser)) {
				    		replyToUser = result.createUser(raw.inReplyToUser);
				    		replyToUser.accountName = SnowCommunityFactory.cleanUserName(raw.replyToUserName);
				    	} else {
				    		replyToUser = result.getUser(raw.inReplyToUser);
				    	}
				    	DiscussionNode replyToNode = null;
				    	if (result.existsDiscussionNode(raw.inReplyTo)) {
				    		replyToNode = result.getDiscussionNode(raw.inReplyTo);
				    	} else {
				    		replyToNode = result.createDiscussionNode(raw.inReplyTo);
					    	replyToNode.setUser(replyToUser);
				    	}
				    	replyToNode.addChild(node);
					}
				}
				
				System.out.println(cnt+"\t"+user.getId()+"\t"+raw.postId+"\t"+raw.inReplyTo+"\t"+raw.authorId+"\t"+raw.authorName);
			}
			bin.close();
		}
		return result;
	}
	
	public static String cleanUserName(String name) {
		return name.replaceAll("\\s", " ");
	}
	
}

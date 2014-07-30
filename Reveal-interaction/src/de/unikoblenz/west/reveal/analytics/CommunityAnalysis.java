package de.unikoblenz.west.reveal.analytics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.DiscussionTree;
import de.unikoblenz.west.reveal.structures.User;

public class CommunityAnalysis {

	
	public static void analyseDiscussionTrees(Community community, File fDataOut, File fHeaderOut) {
	    System.out.println("Writing out DiscussionTrees... ");
	    try{
		    PrintStream dataOut = new PrintStream(fDataOut);
		    PrintStream headerOut = new PrintStream(fHeaderOut);
		    TreeSet<DiscussionTree> discussions = community.getDiscussionTrees();
	
		    headerOut.println(community.getName()+" ("+discussions.size()+" discussion trees)");
		    
	    	dataOut.println("#  size \t depth \t  userSize");
		    for (DiscussionTree  tree : discussions) {
		    	int size = tree.size();
		    	int depth = tree.depth();
		    	int userSize = tree.users().size();
		    	dataOut.println(size +"\t"+ depth +"\t"+  userSize);
		    }
		    dataOut.close();
		    headerOut.close();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	}

	public static void analyseUsers(Community community, File fDataOut, File fHeaderOut) {
	    System.out.println("Writing out Users... ");
	    try{
		    PrintStream dataOut = new PrintStream(fDataOut);
		    PrintStream headerOut = new PrintStream(fHeaderOut);
	    	TreeSet<Long> userIds = community.getUserIds();
	
		    headerOut.println(community.getName()+" ("+userIds.size()+" users)");
		    
	    	
	    	dataOut.println("#  posts "
	    			+ "\t questions "
	    			+ "\t answers "
	    			+ "\t comments "
	    			+ "\t discussions "
	    			+ "\t  replyingUsers "
	    			+ "\t inDegreeRatio "
	    			+ "\t postReplyRatio "
	    			+ "\t threadInitiationRatio "
	    			+ "\t avgPostsPerThread "
	    			+ "\t avgAnswersPerQuestion "
	    			+ "\t avgCommentsPerPost ");
	    	
	    	for (long id : userIds) {
	    		User user = community.getUser(id);
	    		TreeSet<DiscussionNode> contributions = community.getUserContributions(user);
	    		int postCount = contributions.size();
	    		int questionCount = 0;
	    		int answerCount = 0;
	    		int commentCount = 0;
	    		int postWithReplyCount = 0;
	    		int postCommentsCount = 0;
	    		int answersForUserQuestions = 0;
	    		TreeSet<User> replyingUsers = new TreeSet<User>();
	    		for (DiscussionNode node : contributions) {
	    			switch (node.type) {
	    			case DiscussionNode.TYPE_QUESTION : 
	    				questionCount++;
	    				break;
	    			case DiscussionNode.TYPE_ANSWER : 
	    				answerCount++;
	    				break;
	    			case DiscussionNode.TYPE_COMMENT : 
	    				commentCount++;
	    				break;
	    			}
	    			TreeSet<DiscussionNode> replies = node.directChildren(); 
	    			if (replies.size()>0) {
	    				postWithReplyCount++;
	    				for (DiscussionNode reply : replies) {
	    					replyingUsers.add(reply.getUser());
	    					if (reply.type == DiscussionNode.TYPE_COMMENT) {
	    						postCommentsCount++;
	    					} else if (reply.type == DiscussionNode.TYPE_ANSWER) {
	    						answersForUserQuestions++;
	    					}
	    				}
	    			}
	    		}
	    		int replyingUserCount = replyingUsers.size();
	    		double inDegreeRatio = ((double) replyingUserCount)/(userIds.size());
	    		double postReplyRatio = postCount > 0?((double) postWithReplyCount)/postCount:0;
	    		double threadInitiationRatio = postCount > 0?((double) questionCount)/postCount:0;
	    		double avgCommentsPerPost =  postCount > 0?((double) postCommentsCount)/postCount:0;
	    		double avgRepliesPerQuestion =  questionCount > 0?((double) answersForUserQuestions)/questionCount:0;
	    		
	    		TreeSet<DiscussionTree> discussions = community.getUserDiscussionTrees(user);
	    		int discussionCount = discussions.size();
	    		int discussionSizeTotal = 0;
	    		for (DiscussionTree discussion : discussions) {
	    			discussionSizeTotal += discussion.size();
	    		}
	    		double avgPostPerThread = discussionCount>0?((double) discussionSizeTotal)/discussionCount:0;
	    		
	    		
	    		dataOut.println(postCount +
	    				"\t"+ questionCount +
	    				"\t"+ answerCount +
	    				"\t"+ commentCount +
	    				"\t"+ discussionCount +
	    				"\t"+replyingUserCount+
	    				"\t"+inDegreeRatio+
	    				"\t"+postReplyRatio+
	    				"\t"+threadInitiationRatio+
	    				"\t"+avgPostPerThread+
	    				"\t"+avgRepliesPerQuestion+
	    				"\t"+avgCommentsPerPost);
	    	}
		    
		    dataOut.close();
		    headerOut.close();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	}

}

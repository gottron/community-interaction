package de.unikoblenz.west.reveal.analytics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.DiscussionTree;
import de.unikoblenz.west.reveal.structures.User;
import de.unikoblenz.west.reveal.structures.annotations.Annotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangeDiscussionNodeAnnotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangePostAnnotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangeUserAnnotation;

public class CommunityAnalysis {

	public static final byte BASE_MODE = 1;
	public static final byte STACK_EXCHANGE_MODE = 2;
	
	public static void analyseDiscussionTrees(Community community, File fDataOut, File fHeaderOut, int minSize) {
	    System.out.println("Writing out DiscussionTrees... ");
	    byte mode = BASE_MODE;
	    int entryCnt = 0;
	    try{
		    PrintStream dataOut = new PrintStream(fDataOut);
		    PrintStream headerOut = new PrintStream(fHeaderOut);
		    TreeSet<DiscussionTree> discussions = community.getDiscussionTrees();
		    
		    DiscussionTree sample = discussions.iterator().next();
		    if (sample.root.annotation == null) {
		    	mode = BASE_MODE;
		    } else if (sample.root.annotation instanceof StackExchangeDiscussionNodeAnnotation) {
		    	mode = STACK_EXCHANGE_MODE;
		    }
	
		    
		    String header = "# size \t depth \t  userCount \t isBiDir";
		    switch (mode) {
		    case STACK_EXCHANGE_MODE:
		    	header += "\t length"
		    			+ "\t avgLength"
		    			+ "\t views"
		    			+ "\t favorites";
		    	break;
		    }
		    
	    	dataOut.println(header);
		    for (DiscussionTree  tree : discussions) {
		    	int size = tree.size();
		    	int depth = tree.depth();
		    	int userSize = tree.users().size();
		    	int isBiDir = tree.isBidirectional()?1:0; 
		    	String stats = size +"\t"+ depth +"\t"+  userSize +"\t"+isBiDir;
			    switch (mode) {
			    case STACK_EXCHANGE_MODE:
			    	TreeSet<DiscussionNode> nodes = tree.allContributions();
			    	int totLength = 0;
			    	int totViews = 0;
			    	int totFavorites = 0;
			    	for (DiscussionNode node : nodes) {
			    		totLength += ((StackExchangeDiscussionNodeAnnotation) (node.annotation)).bodyContentLength;
			    		if (node.annotation instanceof StackExchangePostAnnotation) {
			    			int views = ((StackExchangePostAnnotation) (node.annotation)).views;
			    			int favorites = ((StackExchangePostAnnotation) (node.annotation)).favoriteCount;
				    		totViews += views;
				    		totFavorites += favorites;
			    		}
			    	}
			    	
			    	double avgLength = nodes.size()>0?(((double) totLength)/nodes.size()):0;
			    	stats += "\t"+totLength
		    			+"\t" + avgLength
			    		+"\t" + totViews
			    		+"\t" + totFavorites;
			    	break;
			    }
			    if (size >= minSize) {
			    	dataOut.println(stats);
			    	entryCnt++;
			    }
		    }
		    
		    headerOut.println(community.getName()+" ("+entryCnt+" discussions"+(minSize>0?(" with at least "+minSize+" contributions"):"")+")");

		    dataOut.close();
		    headerOut.close();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	}

	public static void analyseUsers(Community community, File fDataOut, File fHeaderOut, int minContribs) {
	    System.out.println("Writing out Users... ");
	    byte mode = BASE_MODE;
	    
	    int entryCnt = 0;
	    try{
		    PrintStream dataOut = new PrintStream(fDataOut);
		    PrintStream headerOut = new PrintStream(fHeaderOut);
	    	TreeSet<Long> userIds = community.getUserIds();
	
		    User sample = community.getUser(userIds.iterator().next());
		    if (sample.annotation == null) {
		    	mode = BASE_MODE;
		    } else if (sample.annotation instanceof StackExchangeUserAnnotation) {
		    	mode = STACK_EXCHANGE_MODE;
		    }

	    	
		    
	    	
	    	String header = "#  posts "
	    			+ "\t questions "
	    			+ "\t answers "
	    			+ "\t comments "
	    			+ "\t discussions "
	    			+ "\t replyingUsers "
	    			+ "\t neighbours "
	    			+ "\t inDegreeRatio "
	    			+ "\t postReplyRatio "
	    			+ "\t threadInitiationRatio "
	    			+ "\t biDirPostsRatio "
	    			+ "\t biDirNeighbourRatio "
	    			+ "\t avgPostsPerThread "
	    			+ "\t avgAnswersPerQuestion "
	    			+ "\t avgCommentsPerPost ";
		    switch (mode) {
		    case STACK_EXCHANGE_MODE:
		    	header += "\t age "
		    	    + "\t avgFavoritesPerPost"
		    	    + "\t avgPostLength"
		    	    + "\t avgViewsPerPost"
		    	    + "\t correctAnswerRate"
		    	    + "\t accountAge"
		    	    + "\t downVotes"
		    	    + "\t hasAboutMe"
		    	    + "\t hasLocation"
		    	    + "\t hasWebsite"
		    	    + "\t lastAccess"
		    	    + "\t reputation"
		    	    + "\t upvotes"
		    	    + "\t views";
		    	break;
		    }
	    	dataOut.println(header);

	    	
	    	for (long id : userIds) {
	    		User user = community.getUser(id);
	    		TreeSet<DiscussionNode> contributions = community.getUserContributions(user);
	    		int postCount = contributions.size();
	    		int totalContributions = 0;
	    		int questionCount = 0;
	    		int answerCount = 0;
	    		int commentCount = 0;
	    		int postWithReplyCount = 0;
	    		int postCommentsCount = 0;
	    		int answersForUserQuestions = 0;
	    		int biDirThreadCount = 0;
	    		
	    		/*
	    		 * Vars only needed for StackExchange Mode
	    		 */
	    		int favoriteCount = 0;
	    		int totContributionLength = 0;
	    		int correctAnswerCount = 0;
	    		int postViews = 0;
	    		
	    		TreeSet<User> replyingUsers = new TreeSet<User>();
	    		TreeSet<User> neighbourUsers = new TreeSet<User>();
	    		TreeSet<User> biDirectionalUsers = new TreeSet<User>();
	    		for (DiscussionNode node : contributions) {
	    			totalContributions++;
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
				    switch (mode) {
				    case STACK_EXCHANGE_MODE:
				    	Annotation annotation = node.annotation;
				    	if (annotation instanceof StackExchangeDiscussionNodeAnnotation) {
				    		totContributionLength += ((StackExchangeDiscussionNodeAnnotation) annotation).bodyContentLength;
				    	}
				    	if (annotation instanceof StackExchangePostAnnotation) {
				    		favoriteCount += ((StackExchangePostAnnotation) annotation).favoriteCount;
				    		postViews += ((StackExchangePostAnnotation) annotation).views;
				    		correctAnswerCount += ((StackExchangePostAnnotation) annotation).isCorrectAnswer?1:0;
				    	}
				    }
	    			
	    			TreeSet<DiscussionNode> replies = node.directChildren(); 
	    			if (replies.size()>0) {
	    				postWithReplyCount++;
	    				int biDirThreadInc = 0;
	    				User biDirCandidate = null;
	    				if (node.getParent() != null) {
	    					biDirCandidate = node.getParent().getUser();
	    					neighbourUsers.add(biDirCandidate);
	    				}
	    				for (DiscussionNode reply : replies) {
	    					User replier=reply.getUser();
	    					replyingUsers.add(replier);
	    					neighbourUsers.add(replier);
	    					if (replier == biDirCandidate) {
	    						biDirectionalUsers.add(biDirCandidate);
	    	    				biDirThreadInc = 1;
	    					}
	    					if (reply.type == DiscussionNode.TYPE_COMMENT) {
	    						postCommentsCount++;
	    					} else if (reply.type == DiscussionNode.TYPE_ANSWER) {
	    						answersForUserQuestions++;
	    					}
	    				}
						biDirThreadCount += biDirThreadInc;
	    			}
	    		}
	    		int neighbourCount = neighbourUsers.size();
	    		int replyingUserCount = replyingUsers.size();
	    		int biDirUserCount = biDirectionalUsers.size();
	    		double inDegreeRatio = ((double) replyingUserCount)/(userIds.size());
	    		double postReplyRatio = postCount > 0?((double) postWithReplyCount)/postCount:0;
	    		double threadInitiationRatio = postCount > 0?((double) questionCount)/postCount:0;
	    		double avgCommentsPerPost =  postCount > 0?((double) postCommentsCount)/postCount:0;
	    		double avgRepliesPerQuestion =  questionCount > 0?((double) answersForUserQuestions)/questionCount:0;
	    		double biDirNeighbourRatio =  neighbourCount > 0?((double) biDirUserCount)/neighbourCount:0;
	    		/*
	    		 * TODO: Update required! Here actually bidir Posts -- not threads (not the same if multiple posts in same discussion thread)
	    		 */
	    		double biDirThreadRatio =  postCount > 0?((double) biDirThreadCount)/postCount:0;
	    		
	    		TreeSet<DiscussionTree> discussions = community.getUserDiscussionTrees(user);
	    		int discussionCount = discussions.size();
	    		int discussionSizeTotal = 0;
	    		for (DiscussionTree discussion : discussions) {
	    			discussionSizeTotal += discussion.size();
	    		}
	    		double avgPostPerThread = discussionCount>0?((double) discussionSizeTotal)/discussionCount:0;
	    		
	    		
	    		String stats = postCount +
	    				"\t"+ questionCount +
	    				"\t"+ answerCount +
	    				"\t"+ commentCount +
	    				"\t"+ discussionCount +
	    				"\t"+replyingUserCount+
	    				"\t"+neighbourCount+
	    				"\t"+inDegreeRatio+
	    				"\t"+postReplyRatio+
	    				"\t"+threadInitiationRatio+
	    				"\t"+biDirThreadRatio+
	    				"\t"+biDirNeighbourRatio+
	    				"\t"+avgPostPerThread+
	    				"\t"+avgRepliesPerQuestion+
	    				"\t"+avgCommentsPerPost;
			    switch (mode) {
			    case STACK_EXCHANGE_MODE:
			    	double avgFavsPerPost = postCount>0?((double)favoriteCount)/postCount:0;
			    	double avgPostLength = postCount>0?((double)totContributionLength)/postCount:0;
			    	double avgViewsPerPost = postCount>0?((double)postViews)/postCount:0;
			    	double correctAnswerRate = answerCount>0?((double)correctAnswerCount)/answerCount:0;

			    	StackExchangeUserAnnotation annotation = (StackExchangeUserAnnotation) user.annotation;
			    	if (annotation == null) {
			    		annotation = new StackExchangeUserAnnotation();
			    	}
			    	stats += "\t"+annotation.age
			    	    + "\t"+avgFavsPerPost
			    	    + "\t"+avgPostLength
			    	    + "\t"+avgViewsPerPost
			    	    + "\t"+correctAnswerRate
			    	    + "\t"+annotation.accountAge
			    	    + "\t"+annotation.downVotes
			    	    + "\t"+(annotation.hasAboutMe?1:0)
			    	    + "\t"+(annotation.hasLocation?1:0)
			    	    + "\t"+(annotation.hasWebSite?1:0)
			    	    + "\t"+annotation.lastAccess
			    	    + "\t"+annotation.reputation
			    	    + "\t"+annotation.upVotes
			    	    + "\t"+annotation.views;
			    	break;
			    }
			    if ( (totalContributions >= minContribs) ) {
			    	dataOut.println(stats);
			    	entryCnt++;
			    }
	    	}
		    headerOut.println(community.getName()+" ("+entryCnt+" users"+(minContribs>0?(" with at least "+minContribs+" contributions"):"")+")");
		    
		    dataOut.close();
		    headerOut.close();
	    } catch (IOException ioe) {
	    	ioe.printStackTrace();
	    }
	}

}

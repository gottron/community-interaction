package de.unikoblenz.west.reveal.analytics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import de.unikoblenz.west.reveal.roles.UserWithFeatures;
import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.DiscussionTree;
import de.unikoblenz.west.reveal.structures.User;
import de.unikoblenz.west.reveal.structures.annotations.Annotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangeDiscussionNodeAnnotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangePostAnnotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangeUserAnnotation;

/**
 * Various static methods for analysing Community objects. The results of the
 * analytics are either directly flushed to simple file based persistence layer
 * (CSV files) or returned in the form of feature vectors.
 * 
 * @author Thomas Gottron
 * 
 */
public class CommunityAnalysis {

	/**
	 * Internal flag value for running a analysis on a generic community
	 */
	public static final byte BASE_MODE = 1;
	/**
	 * Internal flag value for running a analysis on a StackExchange community
	 * (more data, e.g. contents, votes, etc.)
	 */
	public static final byte STACK_EXCHANGE_MODE = 2;

	/**
	 * This method aggregates all text ever written by each author. This
	 * provides an aggregate view on all texts of each individual contributor.
	 * Might serve for text based analytics of the people.
	 * 
	 * Results are stored in CSV file with two columns: AuthorId, Text
	 * 
	 * @param community Community to analyse
	 * @param fDataOut File to write the results to.
	 */
	public static void authorText(Community community, File fDataOut) {
		try {
			// prepare output file
			PrintStream out = new PrintStream(fDataOut);
			out.println("# AuthorId\t Aggr. Text");
			// iterate over all users
			for (long uid : community.getUserIds()) {
				User u = community.getUser(uid);
				StringBuilder buffer = new StringBuilder();
				// iterate over all contributions
				for (DiscussionNode n : community.getUserContributions(u)) {
					buffer.append(((StackExchangeDiscussionNodeAnnotation) n.annotation).bodyContent
							+ " ");
				}
				String aggr = buffer.toString().replaceAll("\\s", " ").trim();
				if (aggr.length() > 0) {
					out.println(u.getId() + "\t" + aggr);
				}
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * 
	 * @param community
	 * @param fDataOut
	 */
	public static void replyNetwork(Community community, File fDataOut) {
		System.out.println("Writing out Reply Network... ");
		TreeMap<Long, TreeSet<Long>> adjacency = new TreeMap<Long, TreeSet<Long>>();
		for (DiscussionTree tree : community.getDiscussionTrees()) {
			CommunityAnalysis.discussionNetwork(tree.root, adjacency);
		}
		try {
			PrintStream out = new PrintStream(fDataOut);
			out.println("# ReplierId\tAuthorId");
			for (long responderId : adjacency.keySet()) {
				for (long authorId : adjacency.get(responderId)) {
					out.println(responderId + "\t" + authorId);
				}
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void discussionNetwork(DiscussionNode node,
			TreeMap<Long, TreeSet<Long>> adjacency) {
		long authorId = node.getUser().getId();
		for (DiscussionNode child : node.directChildren()) {
			long responderId = child.getUser().getId();
			if (!adjacency.containsKey(responderId)) {
				TreeSet<Long> list = new TreeSet<Long>();
				adjacency.put(responderId, list);
			}
			adjacency.get(responderId).add(authorId);
			CommunityAnalysis.discussionNetwork(child, adjacency);
		}
	}

	public static void analyseDiscussionTrees(Community community,
			File fDataOut, File fHeaderOut, int minSize) {
		System.out.println("Writing out DiscussionTrees... ");
		byte mode = BASE_MODE;
		int entryCnt = 0;
		try {
			PrintStream dataOut = new PrintStream(fDataOut);
			PrintStream headerOut = new PrintStream(fHeaderOut);
			TreeSet<DiscussionTree> discussions = community
					.getDiscussionTrees();

			DiscussionTree sample = discussions.iterator().next();
			if (sample.root.annotation == null) {
				mode = BASE_MODE;
			} else if (sample.root.annotation instanceof StackExchangeDiscussionNodeAnnotation) {
				mode = STACK_EXCHANGE_MODE;
			}

			String header = "# size \t depth \t  userCount \t isBiDir";
			switch (mode) {
			case STACK_EXCHANGE_MODE:
				header += "\t length" + "\t avgLength" + "\t views"
						+ "\t favorites";
				break;
			}

			dataOut.println(header);
			for (DiscussionTree tree : discussions) {
				int size = tree.size();
				int depth = tree.depth();
				int userSize = tree.users().size();
				int isBiDir = tree.isBidirectional() ? 1 : 0;
				String stats = size + "\t" + depth + "\t" + userSize + "\t"
						+ isBiDir;
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

					double avgLength = nodes.size() > 0 ? (((double) totLength) / nodes
							.size()) : 0;
					stats += "\t" + totLength + "\t" + avgLength + "\t"
							+ totViews + "\t" + totFavorites;
					break;
				}
				if (size >= minSize) {
					dataOut.println(stats);
					entryCnt++;
				}
			}

			headerOut
					.println(community.getName()
							+ " ("
							+ entryCnt
							+ " discussions"
							+ (minSize > 0 ? (" with at least " + minSize + " contributions")
									: "") + ")");

			dataOut.close();
			headerOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void communityToCsv(Community com, File fout) {
		System.out.println("Writing out NCSR'D csv format... ");
		try {
			PrintStream out = new PrintStream(fout, "UTF8");
			out.print("PostId" + "\t");
			out.print("ParentId" + "\t");
			out.print("UserId" + "\t");
			out.print("Timestamp" + "\t");
			out.print("Content" + "\n");
			for (DiscussionTree tree : com.getDiscussionTrees()) {
				for (DiscussionNode node : tree.allContributions()) {
					out.print(node.getId() + "\t");
					out.print((node.getParent() != null ? node.getParent()
							.getId() : CommunityAnalysis.csvText("NA")) + "\t");
					out.print(node.getUser().getId() + "\t");
					out.print(((node.annotation instanceof StackExchangeDiscussionNodeAnnotation) ? ((StackExchangeDiscussionNodeAnnotation) node.annotation).timestamp
							: CommunityAnalysis.csvText("NA"))
							+ "\t");
					out.print(CommunityAnalysis
							.csvText((node.annotation instanceof StackExchangeDiscussionNodeAnnotation) ? ((StackExchangeDiscussionNodeAnnotation) node.annotation).bodyContent
									: "NA")
							+ "\n");
				}
			}
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static String csvText(String in) {
		String mask = in.replaceAll("\\s", " ").replaceAll("\\\"", "\\\\\"");
		String result = "\"" + mask + "\"";
		return result;
	}

	/**
	 * Generate a feature vector representation for users in a Community.
	 * Computes all features necessary for a role analysis. Does not have any
	 * specific requests for extended community objects with specific
	 * annotations but works with the basic data and information provided by
	 * basic community objects from package
	 * de.unikoblenz.west.reveal.structures.
	 * 
	 * @param community
	 *            Community object to analyse.
	 * @param minContribs
	 *            Filter criterion to remove all users which did not provide at
	 *            least a certain number of contributions to the community.
	 * @return
	 */
	public static HashSet<UserWithFeatures> analyseUserFeatures(
			Community community, int minContribs) {
		// HashSet to contain the final objects representing the feature vectors
		// for users.
		HashSet<UserWithFeatures> result = new HashSet<UserWithFeatures>();
		// Obtain set of all users (community objects), to operate on and
		// iterate over for computing the vector representation.
		TreeSet<Long> userIds = community.getUserIds();
		for (long id : userIds) {
			// initialise user feature vector with value for the user Id.
			UserWithFeatures userFeatures = new UserWithFeatures();
			userFeatures.userId = id;

			User user = community.getUser(id);
			userFeatures.accountName = user.accountName;

			TreeSet<DiscussionNode> contributions = community
					.getUserContributions(user);
			userFeatures.postCount = contributions.size();
			int postWithReplyCount = 0;
			int postCommentsCount = 0;
			int answersForUserQuestions = 0;
			int biDirThreadCount = 0;

			TreeSet<User> replyingUsers = new TreeSet<User>();
			TreeSet<User> neighbourUsers = new TreeSet<User>();
			TreeSet<User> biDirectionalUsers = new TreeSet<User>();

			for (DiscussionNode node : contributions) {
				userFeatures.totalContributions++;
				switch (node.type) {
				case DiscussionNode.TYPE_QUESTION:
					userFeatures.questionCount++;
					break;
				case DiscussionNode.TYPE_ANSWER:
					userFeatures.answerCount++;
					break;
				case DiscussionNode.TYPE_COMMENT:
					userFeatures.commentCount++;
					break;
				}
				TreeSet<DiscussionNode> replies = node.directChildren();
				if (replies.size() > 0) {
					postWithReplyCount++;
					int biDirThreadInc = 0;
					User biDirCandidate = null;
					if (node.getParent() != null) {
						biDirCandidate = node.getParent().getUser();
						neighbourUsers.add(biDirCandidate);
					}
					for (DiscussionNode reply : replies) {
						User replier = reply.getUser();
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

			userFeatures.neighbourCount = neighbourUsers.size();
			userFeatures.replyingUserCount = replyingUsers.size();
			int biDirUserCount = biDirectionalUsers.size();
			userFeatures.inDegreeRatio = ((double) userFeatures.replyingUserCount)
					/ (userIds.size());
			userFeatures.postsReplyRatio = userFeatures.postCount > 0 ? ((double) postWithReplyCount)
					/ userFeatures.postCount
					: 0;
			userFeatures.threadInitiationRatio = userFeatures.postCount > 0 ? ((double) userFeatures.questionCount)
					/ userFeatures.postCount
					: 0;
			userFeatures.avgCommentsPerPost = userFeatures.postCount > 0 ? ((double) postCommentsCount)
					/ userFeatures.postCount
					: 0;
			userFeatures.avgRepliesPerQuestion = userFeatures.questionCount > 0 ? ((double) answersForUserQuestions)
					/ userFeatures.questionCount
					: 0;
			userFeatures.biDirNeighbourRatio = userFeatures.neighbourCount > 0 ? ((double) biDirUserCount)
					/ userFeatures.neighbourCount
					: 0;
			/*
			 * TODO: Update required! Here actually bidir Posts -- not threads
			 * (not the same if multiple posts in same discussion thread)
			 */
			userFeatures.biDirThreadRatio = userFeatures.postCount > 0 ? ((double) biDirThreadCount)
					/ userFeatures.postCount
					: 0;

			TreeSet<DiscussionTree> discussions = community
					.getUserDiscussionTrees(user);
			int discussionCount = discussions.size();
			int discussionSizeTotal = 0;
			double s1 = 0;
			double s2 = 0;
			int N = 0;

			for (DiscussionTree discussion : discussions) {
				int size = discussion.size();
				discussionSizeTotal += size;
				N++;
				s1 += size;
				s2 += Math.pow(size, 2);
			}
			userFeatures.avgPostPerThread = discussionCount > 0 ? ((double) discussionSizeTotal)
					/ discussionCount
					: 0;
			// Fast computation of stddev according to wikipedia
			userFeatures.stddevPostsPerThread = 0;
			if (N >= 2) {
				userFeatures.stddevPostsPerThread = Math.sqrt((N * s2 - Math
						.pow(s1, 2)) / (N * (N - 1)));
			}

			if ((userFeatures.totalContributions >= minContribs)) {
				result.add(userFeatures);
			}
		}
		return result;
	}

	public static void analyseUsers(Community community, File fDataOut,
			File fHeaderOut, int minContribs) {
		System.out.println("Writing out Users... ");
		byte mode = BASE_MODE;

		int entryCnt = 0;
		try {
			PrintStream dataOut = new PrintStream(fDataOut, "UTF8");
			PrintStream headerOut = new PrintStream(fHeaderOut, "UTF8");
			TreeSet<Long> userIds = community.getUserIds();

			User sample = community.getUser(userIds.iterator().next());
			if (sample.annotation == null) {
				mode = BASE_MODE;
			} else if (sample.annotation instanceof StackExchangeUserAnnotation) {
				mode = STACK_EXCHANGE_MODE;
			}

			String header = "#  accountName" + "\t id " + "\t posts "
					+ "\t questions " + "\t answers " + "\t comments "
					+ "\t discussions " + "\t replyingUsers "
					+ "\t neighbours " + "\t inDegreeRatio "
					+ "\t postReplyRatio " + "\t threadInitiationRatio "
					+ "\t biDirPostsRatio " + "\t biDirNeighbourRatio "
					+ "\t avgPostsPerThread " + "\t stddevPostsPerThread "
					+ "\t avgAnswersPerQuestion " + "\t avgCommentsPerPost ";
			switch (mode) {
			case STACK_EXCHANGE_MODE:
				header += "\t age " + "\t avgFavoritesPerPost"
						+ "\t avgPostLength" + "\t avgViewsPerPost"
						+ "\t correctAnswerRate" + "\t accountAge"
						+ "\t downVotes" + "\t hasAboutMe" + "\t hasLocation"
						+ "\t hasWebsite" + "\t lastAccess" + "\t reputation"
						+ "\t upvotes" + "\t views";
				break;
			}
			dataOut.println(header);

			for (long id : userIds) {
				User user = community.getUser(id);
				TreeSet<DiscussionNode> contributions = community
						.getUserContributions(user);
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
					case DiscussionNode.TYPE_QUESTION:
						questionCount++;
						break;
					case DiscussionNode.TYPE_ANSWER:
						answerCount++;
						break;
					case DiscussionNode.TYPE_COMMENT:
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
							correctAnswerCount += ((StackExchangePostAnnotation) annotation).isCorrectAnswer ? 1
									: 0;
						}
					}

					TreeSet<DiscussionNode> replies = node.directChildren();
					if (replies.size() > 0) {
						postWithReplyCount++;
						int biDirThreadInc = 0;
						User biDirCandidate = null;
						if (node.getParent() != null) {
							biDirCandidate = node.getParent().getUser();
							neighbourUsers.add(biDirCandidate);
						}
						for (DiscussionNode reply : replies) {
							User replier = reply.getUser();
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
				double inDegreeRatio = ((double) replyingUserCount)
						/ (userIds.size());
				double postReplyRatio = postCount > 0 ? ((double) postWithReplyCount)
						/ postCount
						: 0;
				double threadInitiationRatio = postCount > 0 ? ((double) questionCount)
						/ postCount
						: 0;
				double avgCommentsPerPost = postCount > 0 ? ((double) postCommentsCount)
						/ postCount
						: 0;
				double avgRepliesPerQuestion = questionCount > 0 ? ((double) answersForUserQuestions)
						/ questionCount
						: 0;
				double biDirNeighbourRatio = neighbourCount > 0 ? ((double) biDirUserCount)
						/ neighbourCount
						: 0;
				/*
				 * TODO: Update required! Here actually bidir Posts -- not
				 * threads (not the same if multiple posts in same discussion
				 * thread)
				 */
				double biDirThreadRatio = postCount > 0 ? ((double) biDirThreadCount)
						/ postCount
						: 0;

				TreeSet<DiscussionTree> discussions = community
						.getUserDiscussionTrees(user);
				int discussionCount = discussions.size();
				int discussionSizeTotal = 0;
				double s1 = 0;
				double s2 = 0;
				int N = 0;

				for (DiscussionTree discussion : discussions) {
					int size = discussion.size();
					discussionSizeTotal += size;
					N++;
					s1 += size;
					s2 += Math.pow(size, 2);
				}
				double avgPostPerThread = discussionCount > 0 ? ((double) discussionSizeTotal)
						/ discussionCount
						: 0;
				// Fast computation of stddev according to wikipedia
				double stddevPostPerThread = 0;
				if (N >= 2) {
					stddevPostPerThread = Math.sqrt((N * s2 - Math.pow(s1, 2))
							/ (N * (N - 1)));
				}

				String stats = user.accountName + "\t" + user.getId() + "\t"
						+ postCount + "\t" + questionCount + "\t" + answerCount
						+ "\t" + commentCount + "\t" + discussionCount + "\t"
						+ replyingUserCount + "\t" + neighbourCount + "\t"
						+ inDegreeRatio + "\t" + postReplyRatio + "\t"
						+ threadInitiationRatio + "\t" + biDirThreadRatio
						+ "\t" + biDirNeighbourRatio + "\t" + avgPostPerThread
						+ "\t" + stddevPostPerThread + "\t"
						+ avgRepliesPerQuestion + "\t" + avgCommentsPerPost;
				switch (mode) {
				case STACK_EXCHANGE_MODE:
					double avgFavsPerPost = postCount > 0 ? ((double) favoriteCount)
							/ postCount
							: 0;
					double avgPostLength = postCount > 0 ? ((double) totContributionLength)
							/ postCount
							: 0;
					double avgViewsPerPost = postCount > 0 ? ((double) postViews)
							/ postCount
							: 0;
					double correctAnswerRate = answerCount > 0 ? ((double) correctAnswerCount)
							/ answerCount
							: 0;

					StackExchangeUserAnnotation annotation = (StackExchangeUserAnnotation) user.annotation;
					if (annotation == null) {
						annotation = new StackExchangeUserAnnotation();
					}
					stats += "\t" + annotation.age + "\t" + avgFavsPerPost
							+ "\t" + avgPostLength + "\t" + avgViewsPerPost
							+ "\t" + correctAnswerRate + "\t"
							+ annotation.accountAge + "\t"
							+ annotation.downVotes + "\t"
							+ (annotation.hasAboutMe ? 1 : 0) + "\t"
							+ (annotation.hasLocation ? 1 : 0) + "\t"
							+ (annotation.hasWebSite ? 1 : 0) + "\t"
							+ annotation.lastAccess + "\t"
							+ annotation.reputation + "\t" + annotation.upVotes
							+ "\t" + annotation.views;
					break;
				}
				if ((totalContributions >= minContribs)) {
					dataOut.println(stats);
					entryCnt++;
				}
			}
			headerOut
					.println(community.getName()
							+ " ("
							+ entryCnt
							+ " users"
							+ (minContribs > 0 ? (" with at least "
									+ minContribs + " contributions") : "")
							+ ")");

			dataOut.close();
			headerOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}

package de.unikoblenz.west.reveal.roles;

public class UserWithFeatures {

	public String accountName = null;
	public long userId = -1;
	public int totalContributions = 0;
	public int postCount = 0;
	public int questionCount = 0;
	public int answerCount = 0;
	public int commentCount = 0;
	public int discussionCount = 0;
	public int replyingUserCount = 0;
	public int neighbourCount = 0;
	
	public double inDegreeRatio = 0;
	public double postsReplyRatio = 0;
	public double threadInitiationRatio = 0;
	public double biDirThreadRatio = 0;
	public double biDirNeighbourRatio = 0;
	public double avgPostPerThread = 0;
	public double stddevPostsPerThread = 0;
	
	public double avgRepliesPerQuestion = 0;
	public double avgCommentsPerPost = 0;


	public UserWithRole convertToUserWithRole() {
		UserWithRole result = new UserWithRole();
		result.id = this.userId;
		result.username = this.accountName;
		result.avgPostPerThread = this.avgPostPerThread;
		result.bidirNeighbourRatio = this.biDirNeighbourRatio;
		result.bidirThreadRatio = this.biDirThreadRatio;
		result.contributions = this.totalContributions;
		result.inDegreeRatio = this.inDegreeRatio;
		result.postsReplyRatio = this.postsReplyRatio;
		result.stddevPostsPerThread = this.stddevPostsPerThread;
		result.threadInitiationRatio = this.threadInitiationRatio;
		return result;
	}
}

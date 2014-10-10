package de.unikoblenz.west.reveal.roles;

public class UserWithRole {

	public static final String ROLE_ELITIST = "Elitist";
	public static final String ROLE_GRUNT = "Grunt";
	public static final String ROLE_JOINING_CONVERSATIONALIST = "Joining Conversationalist";
	public static final String ROLE_POPULAR_INITIATOR = "Popular Initiator";
	public static final String ROLE_POPULAR_PARTICIPANT = "Popular Participant";
	public static final String ROLE_SUPPORTER = "Supporter";
	public static final String ROLE_TACITURN = "Taciturn";
	public static final String ROLE_IGNORED = "Ignored";
	public static final String ROLE_NONE = "<None>";
	
	public String role = ROLE_NONE;
	
	public static final String LEVEL_UNDEF = "undef";
	public static final String LEVEL_LOW = "low";
	public static final String LEVEL_MED = "med";
	public static final String LEVEL_HIGH = "high";
	
	public String username = "";
	public String community = "";

	public int contributions = 0;

	public double inDegreeRatio = 0;
	public String lvl_inDegreeRatio = LEVEL_UNDEF;
	
	public double postsReplyRatio = 0;
	public String lvl_postsReplyRatio = LEVEL_UNDEF;
	
	public double threadInitiationRatio = 0;
	public String lvl_threadInitiationRatio = LEVEL_UNDEF;
	
	public double bidirThreadRatio = 0;
	public String lvl_bidirThreadRatio = LEVEL_UNDEF;
	
	public double bidirNeighbourRatio = 0;
	public String lvl_bidirNeighbourRatio = LEVEL_UNDEF;
	
	public double avgPostPerThread = 0;
	public String lvl_avgPostPerThread = LEVEL_UNDEF;

	public double stddevPostsPerThread = 0;
	public String lvl_stddevPostsPerThread = LEVEL_UNDEF;
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(this.username);
		result.append(" in "+this.community);
		result.append(" ("+role+"):");
		result.append(" idr="+this.inDegreeRatio);
		result.append(" prr="+this.postsReplyRatio);
		result.append(" tir="+this.threadInitiationRatio);
		result.append(" btr="+this.bidirThreadRatio);
		result.append(" bnr="+this.bidirNeighbourRatio);
		result.append(" appt="+this.avgPostPerThread);
		result.append(" sppt="+this.stddevPostsPerThread);
		return result.toString();
	}
	
}

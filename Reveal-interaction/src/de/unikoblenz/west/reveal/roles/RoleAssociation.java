package de.unikoblenz.west.reveal.roles;

import java.util.ArrayList;
import java.util.Set;

public class RoleAssociation {

	public void process(Set<UserWithRole> users) {
		
		ArrayList<Double> list_avgPostPerThread = new ArrayList<Double>();
		ArrayList<Double> list_bidirNeighbourRatio = new ArrayList<Double>();
		ArrayList<Double> list_bidirThreadRatio = new ArrayList<Double>();
		ArrayList<Double> list_inDegreeRatio = new ArrayList<Double>();
		ArrayList<Double> list_postsReplyRatio = new ArrayList<Double>();
		ArrayList<Double> list_stddevPostsPerThread = new ArrayList<Double>();
		ArrayList<Double> list_threadInitiationRatio = new ArrayList<Double>();
		
		for (UserWithRole user : users) {
				list_avgPostPerThread.add(user.avgPostPerThread);
				list_bidirNeighbourRatio.add(user.bidirNeighbourRatio);
				list_bidirThreadRatio.add(user.bidirThreadRatio);
				list_inDegreeRatio.add(user.inDegreeRatio);
				list_postsReplyRatio.add(user.postsReplyRatio);
				list_stddevPostsPerThread.add(user.stddevPostsPerThread);
				list_threadInitiationRatio.add(user.threadInitiationRatio);
		}
		
		Boundaries apptBoundary = new Boundaries(list_avgPostPerThread);
		Boundaries bnrBoundary = new Boundaries(list_bidirNeighbourRatio);
		Boundaries btrBoundary = new Boundaries(list_bidirThreadRatio);
		Boundaries idrBoundary = new Boundaries(list_inDegreeRatio);
		Boundaries prrBoundary = new Boundaries(list_postsReplyRatio);
		Boundaries spptBoundary = new Boundaries(list_stddevPostsPerThread);
		Boundaries tirBoundary = new Boundaries(list_threadInitiationRatio);

		for (UserWithRole user : users) {
			user.lvl_avgPostPerThread = apptBoundary.getLevel(user.avgPostPerThread);
			user.lvl_bidirNeighbourRatio = bnrBoundary.getLevel(user.bidirNeighbourRatio);
			user.lvl_bidirThreadRatio = btrBoundary.getLevel(user.bidirThreadRatio);
			user.lvl_inDegreeRatio = idrBoundary.getLevel(user.inDegreeRatio);
			user.lvl_postsReplyRatio = prrBoundary.getLevel(user.postsReplyRatio);
			user.lvl_stddevPostsPerThread = spptBoundary.getLevel(user.stddevPostsPerThread);
			user.lvl_threadInitiationRatio = tirBoundary.getLevel(user.threadInitiationRatio);
			this.assignRole(user);
		}
		
	}
	
	private String assignRole(UserWithRole u) {
		
		if ( 
				u.lvl_inDegreeRatio.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_bidirThreadRatio.equals(UserWithRole.LEVEL_HIGH)
				&& u.lvl_bidirNeighbourRatio.equals(UserWithRole.LEVEL_LOW)
				) {
			u.role = UserWithRole.ROLE_ELITIST;
		} else if (
				u.lvl_bidirThreadRatio.equals(UserWithRole.LEVEL_MED)
				&& u.lvl_bidirNeighbourRatio.equals(UserWithRole.LEVEL_MED)
				&& u.lvl_avgPostPerThread.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_stddevPostsPerThread.equals(UserWithRole.LEVEL_LOW)
				) {
			u.role = UserWithRole.ROLE_GRUNT;
		} else if (
				u.lvl_threadInitiationRatio.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_avgPostPerThread.equals(UserWithRole.LEVEL_HIGH)
				&& u.lvl_stddevPostsPerThread.equals(UserWithRole.LEVEL_HIGH)
				) {
			u.role = UserWithRole.ROLE_JOINING_CONVERSATIONALIST;
		} else if (
				u.lvl_inDegreeRatio.equals(UserWithRole.LEVEL_HIGH)
				&& u.lvl_avgPostPerThread.equals(UserWithRole.LEVEL_HIGH)
				) {
			u.role = UserWithRole.ROLE_POPULAR_INITIATOR;
		} else if (
				u.lvl_inDegreeRatio.equals(UserWithRole.LEVEL_HIGH)
				&& u.lvl_threadInitiationRatio.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_avgPostPerThread.equals(UserWithRole.LEVEL_MED)
				&& u.lvl_stddevPostsPerThread.equals(UserWithRole.LEVEL_MED)
				) {
			u.role = UserWithRole.ROLE_POPULAR_PARTICIPANT;
		} else if (
				u.lvl_inDegreeRatio.equals(UserWithRole.LEVEL_MED)
				&& u.lvl_bidirThreadRatio.equals(UserWithRole.LEVEL_MED)
				&& u.lvl_bidirNeighbourRatio.equals(UserWithRole.LEVEL_MED)
				) {
			u.role = UserWithRole.ROLE_SUPPORTER;
		} else if (
				u.lvl_bidirThreadRatio.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_bidirNeighbourRatio.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_avgPostPerThread.equals(UserWithRole.LEVEL_LOW)
				&& u.lvl_stddevPostsPerThread.equals(UserWithRole.LEVEL_LOW)
				) {
			u.role = UserWithRole.ROLE_TACITURN;
		} else if (
				u.lvl_postsReplyRatio.equals(UserWithRole.LEVEL_LOW)
				) {
			u.role = UserWithRole.ROLE_IGNORED;
		} else {
			u.role = UserWithRole.ROLE_NONE;
		}

		return u.role;
	}
	
	
}

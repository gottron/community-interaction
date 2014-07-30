package de.unikoblenz.west.reveal.structures;

import java.util.TreeMap;
import java.util.TreeSet;

public class Community {

	public final static String ANONYMOUS_ACCOUNT = "(anonymous)";
	
	private String name = null;
	
	/**
	 * Map User Ids to User objects
	 */
	private TreeMap<Long,User> users = new TreeMap<Long, User>();
	
	/**
	 * Map discussionNodeIds to discussion trees
	 */
	private TreeMap<Long, DiscussionTree> discussions = new TreeMap<Long, DiscussionTree> ();

	/**
	 * Map discussionNodeIds to DiscussionNodes
	 */
	private TreeMap<Long, DiscussionNode> discussionEntries = new TreeMap<Long, DiscussionNode> ();
	
	/**
	 * Map user Ids to discussioNode Ids.
	 */
	private TreeMap<Long,TreeSet<Long>> contributions= new TreeMap<Long, TreeSet<Long>>();

	/**
	 * Initializes the community object with a given name
	 * @param name
	 */
	public Community (String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name given for this community
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Obtain the User Object for a User Id.
	 * @param id
	 * @return
	 */
	public User getUser(long id) {
		User result = null;
		if (this.existsUser(id)) {
			result = users.get(id);
		}
		return result;
	}
	
	/**
	 * Returns all User Ids.
	 * @return
	 */
	public TreeSet<Long> getUserIds() {
		TreeSet<Long> result = new TreeSet<Long>();
		result.addAll(this.users.keySet());
		return result;
	}
	
	/**
	 * Retrieve all contributions of a user
	 * @param user
	 * @return
	 */
	public TreeSet<DiscussionNode> getUserContributions(User user) {
		TreeSet<DiscussionNode> result = new TreeSet<DiscussionNode>();
		if (this.contributions.containsKey(user.getId()) ) {
			TreeSet<Long> contributionIds = this.contributions.get(user.getId());
			for (long discussionNodeId: contributionIds) {
				if (discussionEntries.containsKey(discussionNodeId)) {
					result.add(this.discussionEntries.get(discussionNodeId));
				}
			}
		}
		return result;
	}
	
	/**
	 * Retrieve all Discussion Trees a user is involved in.
	 * @param user
	 * @return
	 */
	public TreeSet<DiscussionTree> getUserDiscussionTrees(User user) {
		TreeSet<DiscussionTree> result =  new TreeSet<DiscussionTree>();
		if (this.contributions.containsKey(user.getId()) ) {
			TreeSet<Long> contributionIds = this.contributions.get(user.getId());
			for (long discussionNodeId: contributionIds) {
				if (discussions.containsKey(discussionNodeId)) {
					result.add(this.discussions.get(discussionNodeId));
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns all DiscussionTrees in the community
	 * 
	 * @return
	 */
	public TreeSet<DiscussionTree> getDiscussionTrees() {
		TreeSet<DiscussionTree> result = new TreeSet<DiscussionTree>();
		result.addAll(this.discussions.values());
		return result;
	}
	
	/**
	 * Adds the user to the list of users. If a user with the same id exists it is overwritten.
	 *  
	 * @param user
	 */
	public User createUser(long id) {
		User user = new User(id, this);
		this.users.put(user.getId(), user);
		return user;
	}
	
	
	/**
	 * Checks if a user ID exists and is known.
	 * @param id
	 * @return
	 */
	public boolean existsUser(long id) {
		return this.users.containsKey(id);
	}
	
	/**
	 * Checks if a discussion node  ID exists and is known.
	 * @param id
	 * @return
	 */
	public boolean existsDiscussionNode(long id) {
		return this.discussionEntries.containsKey(id);
	}
	
	/**
	 * Returns the DiscussionNode for a specific id
	 * @param id
	 * @return
	 */
	public DiscussionNode getDiscussionNode(long id) {
		DiscussionNode result = null;
		if (discussionEntries.containsKey(id)) {
			result = discussionEntries.get(id);
		}
		return result;
	}
	
	/**
	 * Creates a new discussion entry and registers it in the community
	 * @param id
	 * @return
	 */
	public DiscussionNode createDiscussionNode(long id) {
		DiscussionNode result = new DiscussionNode(id, this);
		this.discussionEntries.put(id, result);
		DiscussionTree dt = new DiscussionTree();
		dt.root = result;
		this.discussions.put(id, dt);
		return result;
	}
	
	/**
	 * Returns the discussion tree in which a node is contained
	 * @param nodeId
	 * @return
	 */
	public DiscussionTree getDiscussion(long nodeId) {
		DiscussionTree result = null;
		if (this.discussions.containsKey(nodeId)) {
			result = this.discussions.get(nodeId);
		}
		return result;
	}
	
	/**
	 * Updates the index if a DiscussionNode is moved to another DiscussionTree
	 * @param nodeId
	 * @param newTree
	 */
	protected void shiftDiscussionNode(long nodeId, DiscussionTree newTree) {
		this.discussions.put(nodeId, newTree);
	}
	
	/**
	 * Updates the references of users to contributions when the user of a node has been changed.
	 * @param nodeId
	 * @param oldUser
	 * @param newUser
	 */
	protected void updateUserContributions(long nodeId, User oldUser, User newUser) {
		if (oldUser != null) {
			long oldId = oldUser.getId();
			TreeSet<Long> oldContributions = this.contributions.get(oldId);
			if (oldContributions != null) {
				oldContributions.remove(nodeId);
			}
		}
		long newId = newUser.getId();
		if (! contributions.containsKey(newId)) {
			this.contributions.put(newId, new TreeSet<Long>());
		}
		contributions.get(newId).add(nodeId);
	}
	
}

package de.unikoblenz.west.reveal.structures;

import java.util.Date;
import java.util.TreeSet;

public class DiscussionNode extends CommunityObject {

	public static final int TYPE_CONTENT_ITEM = 0;
	public static final int TYPE_QUESTION = 1;
	public static final int TYPE_ANSWER = 2;
	public static final int TYPE_COMMENT = 3;

	/**
	 * The user who created the discussion item
	 */
	private User user = null;

	/**
	 * Type of the node (Comment, question, answer, etc.)
	 */
	public int type = TYPE_CONTENT_ITEM;

	/**
	 * List of direct responses to the discussion item
	 */
	private TreeSet<DiscussionNode> children = new TreeSet<DiscussionNode>();

	/**
	 * Parent node (if available)
	 */
	private DiscussionNode parent = null;

	/**
	 * Date when the entry was created
	 */
	public Date creationDate = null;

	protected DiscussionNode(long id, Community community) {
		super(id, community);
	}

	/**
	 * Returns the user who is owner / a uthor of the discussion node
	 * 
	 * @return
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * Sets the user who is owner / a uthor of the discussion node
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		User oldUser = this.user;
		this.user = user;
		if (oldUser != user) {
			this.getCommunity().updateUserContributions(this.getId(), oldUser,
					user);
		}
	}

	/**
	 * Compute the depth of the discussion, i.e. the depth in the discussion
	 * tree structure assuming this to be the root node of the analysis.
	 * 
	 * @return
	 */
	public int depth() {
		int depth = 0;
		if (children.size() > 0) {
			for (DiscussionNode child : children) {
				depth = Math.max(depth, child.depth());
			}
		}
		depth++;
		return depth;
	}

	/**
	 * Returns the parent node of this node (or null if it is a root node in the
	 * discussion).
	 * 
	 * @return
	 */
	public DiscussionNode getParent() {
		return this.parent;
	}

	/**
	 * Compute the size of the discussion, i.e. the number of all contributions
	 * under this discussion node
	 * 
	 * @return
	 */
	public int size() {
		int size = 0;
		if (children.size() > 0) {
			for (DiscussionNode child : children) {
				size += child.size();
			}
		}
		size++;
		return size;
	}

	/**
	 * The set of users who have contributed to the discussion under this entry,
	 * i.e. user who generate children in this discussion tree (not necessarily
	 * direct children)
	 * 
	 * @return
	 */
	public TreeSet<User> users() {
		TreeSet<User> result = new TreeSet<User>();
		if (children.size() > 0) {
			for (DiscussionNode child : children) {
				result.addAll(child.users());
			}
		}
		if (this.user != null) {
			result.add(this.user);
		}
		return result;
	}

	/**
	 * The set of Date objects related to all children contained in this
	 * discussion structure. Gives an idea of when activity occurred.
	 * 
	 * @return
	 */
	public TreeSet<Date> activity() {
		TreeSet<Date> result = new TreeSet<Date>();
		if (children.size() > 0) {
			for (DiscussionNode child : children) {
				result.addAll(child.activity());
			}
		}
		result.add(this.creationDate);
		return result;
	}

	/**
	 * Returns the set of all direct children elements in the discussion
	 * structure
	 * 
	 * @return
	 */
	public TreeSet<DiscussionNode> directChildren() {
		TreeSet<DiscussionNode> result = new TreeSet<DiscussionNode>();
		result.addAll(children);
		return result;
	}

	/**
	 * Returns the set of all direct and indirect children elements in the
	 * discussion structure
	 * 
	 * @return
	 */
	public TreeSet<DiscussionNode> recursiveContributions() {
		TreeSet<DiscussionNode> result = new TreeSet<DiscussionNode>();
		result.add(this);
		for (DiscussionNode child : children) {
			result.addAll(child.recursiveContributions());
		}
		return result;
	}

	/**
	 * Add a discussion node as child to this object. All references to children
	 * and parents are updated automatically. Also the discussion tree structure
	 * in the community object is adapted.
	 */
	public void addChild(DiscussionNode node) {
		if (node == this) {
			throw new IllegalArgumentException(
					"Node cannot have itself as child");
		}
		DiscussionNode oldParent = node.getParent();
		if (oldParent != null) {
			oldParent.children.remove(node);
		}
		this.children.add(node);
		node.parent = this;
		// update references to discussionTrees in the community (if necessary)
		DiscussionTree oldTree = this.getCommunity()
				.getDiscussion(node.getId());
		DiscussionTree newTree = this.getCommunity()
				.getDiscussion(this.getId());
		if (!oldTree.equals(newTree)) {
			this.getCommunity().shiftDiscussionNode(node.getId(), newTree);
		}
	}

	/**
	 * Checks if anyone in a set of users has replied to this node.
	 * 
	 * @param users
	 * @return
	 */
	public boolean containsReplyFrom(TreeSet<User> users) {
		boolean result = false;
		for (DiscussionNode child : this.children) {
			if (users.contains(child.getUser())) {
				result = true;
			} else {
				result = child.containsReplyFrom(users);
			}
			if (result) {
				break;
			}
		}
		return result;
	}

	/**
	 * Gives the set of users to which this node is a reply, i.e. the authors of users or higher nodes in the discussiontree.
	 * @return
	 */
	public TreeSet<User> repliesToUsers() {
		TreeSet<User> result = new TreeSet<User>();
		if (this.parent != null) {
			result.add(this.parent.getUser());
			result.addAll(this.parent.repliesToUsers());
		}
		return result;
	}

	/**
	 * Checks if this discussionNode is element in a bidirectional conversion,
	 * i.e. it replies to a node X (directly or indirectly) and the user of node
	 * X has replied to this node.
	 * 
	 * @return
	 */
	public boolean isBidirectionalContribution() {
		boolean result = false;
		TreeSet<User> forefatherUsers = this.repliesToUsers();
		if (forefatherUsers.size() > 0) {
			result = this.containsReplyFrom(forefatherUsers);
		}
		return result;
	}
}

package de.unikoblenz.west.reveal.structures;

import java.util.Date;
import java.util.TreeSet;

/**
 * This class represents a single discussion tree. Basically it consists of a
 * single root DiscussionNode and capsulates access to some selected methods of
 * this root node.
 * 
 * @author Thomas Gottron
 * 
 */
public class DiscussionTree implements Comparable<DiscussionTree> {

	/**
	 * The root node of the discussion tree, i.e. the contribution which started
	 * the discussion
	 */
	public DiscussionNode root = null;

	/**
	 * Determine the depth of the discussion tree (i.e. distance to the leave
	 * node which is farthest away from the root node)
	 * 
	 * @return
	 */
	public int depth() {
		return this.root.depth();
	}

	/**
	 * Count the number of all contributions in this discussion tree
	 * 
	 * @return
	 */
	public int size() {
		return this.root.size();
	}

	/**
	 * Return a set of all users who were involved in this discussion
	 * 
	 * @return
	 */
	public TreeSet<User> users() {
		return this.root.users();
	}

	/**
	 * Retrieve a set of all contributions that are part of this discussion
	 * tree.
	 * 
	 * @return
	 */
	public TreeSet<DiscussionNode> allContributions() {
		return this.root.recursiveContributions();
	}

	/**
	 * Set of all dates of contributions in this tree.
	 * 
	 * @return
	 */
	public TreeSet<Date> activity() {
		return this.root.activity();
	}

	@Override
	public int compareTo(DiscussionTree o) {
		return this.root.compareTo(o.root);
	}

	/**
	 * Checks if any of the nodes in this tree is part of a birectional
	 * conversation.
	 * 
	 * @return
	 */
	public boolean isBidirectional() {
		boolean result = false;
		TreeSet<DiscussionNode> nodes = this.allContributions();
		for (DiscussionNode node : nodes) {
			if (node.isBidirectionalContribution()) {
				return true;
			}
		}
		return result;
	}

}

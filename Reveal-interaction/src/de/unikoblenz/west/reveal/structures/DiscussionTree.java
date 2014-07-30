package de.unikoblenz.west.reveal.structures;

import java.util.Date;
import java.util.TreeSet;

public class DiscussionTree implements Comparable<DiscussionTree>{

	public DiscussionNode root = null;
	
	public int depth() {
		return this.root.depth();
	}
	
	public int size() {
		return this.root.size();
	}
	
	public TreeSet<User> users() {
		return this.root.users();
	}
	
	public TreeSet<DiscussionNode> allContributions() {
		return this.root.recursiveContributions();
	}
	
	public TreeSet<Date> activity() {
		return this.root.activity();
	}

	@Override
	public int compareTo(DiscussionTree o) {
		// TODO Auto-generated method stub
		return this.root.compareTo(o.root);
	}
	
}

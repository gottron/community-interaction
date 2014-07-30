package de.unikoblenz.west.reveal.structures;

import de.unikoblenz.west.reveal.structures.annotations.Annotation;

/**
 * Abstract community object class. Manages references to the community objects and the ids.
 * 
 * @author Thomas Gottron
 *
 */
public abstract class CommunityObject implements Comparable<CommunityObject> {

	/**
	 * Annotation object to store community specific information
	 */
	public Annotation annotation = null;
	
	/**
	 * ID of the object. Must be unique across all objects of the same type in the community.
	 */
	private long id = 0;

	/**
	 * Reference to the community in which this object is contained
	 */
	private Community community = null;
	
	/**
	 * Protected object instantiation to force the use the Community factory methods to generate the objects
	 * @param id
	 * @param community
	 */
	protected CommunityObject(long id, Community community) {
		this.community = community;
		this.id = id;
	}
	
	@Override
	public int compareTo(CommunityObject o) {
		return (int) Math.signum(this.id - o.id);
	}
	
	/**
	 * Returns the object ID
	 * @return
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Returns a reference to the community in which this object is registered
	 * @return
	 */
	public Community getCommunity() {
		return this.community;
	}
	
}

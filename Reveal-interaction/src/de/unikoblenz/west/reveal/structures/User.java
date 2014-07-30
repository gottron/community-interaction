package de.unikoblenz.west.reveal.structures;

/**
 * Simple user object -- only contains id and an account name string  
 * @author Thomas Gottron
 *
 */
public class User extends CommunityObject {

	/**
	 * The name (account name) of the user
	 */
	public String accountName = null;

	protected User(long id, Community community) {
		super(id,community);
	}
	
}

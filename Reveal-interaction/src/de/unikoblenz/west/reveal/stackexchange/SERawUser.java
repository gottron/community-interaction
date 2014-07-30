package de.unikoblenz.west.reveal.stackexchange;

import java.util.Date;

/**
 * A User object for StackExchange as parsed from an input file. Contains all raw data.
 * 
 * @author Thomas Gottron
 *
 */
public class SERawUser {
	protected int id = 0;	
	protected int reputation = 0;
	protected Date creationDate = null;
	protected String displayName = null;
	protected String emailHash = null;
	protected Date lastAccessDate = null;
	protected String websiteUrl = null;
	protected String location = null;
	protected int age = 0;
	protected String aboutMe = null;
	protected int views = 0;
	protected int upVotes = 0;
	protected int downVotes = 0;
	protected int accountId;
}

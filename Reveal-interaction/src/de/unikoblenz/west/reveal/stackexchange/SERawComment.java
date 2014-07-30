package de.unikoblenz.west.reveal.stackexchange;

import java.util.Date;

/**
 * A StackExchange comment as parsed from the dump files. Contains all raw data.
 * @author Thomas Gottron
 *
 */
public class SERawComment {

	public int id = 0;
	public int postId = 0;
	public int score = 0;
	public String text = null;
	public Date creationDate = null;
	public int userId = 0;
	
}

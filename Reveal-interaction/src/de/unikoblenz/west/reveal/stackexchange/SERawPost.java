package de.unikoblenz.west.reveal.stackexchange;

import java.util.Date;

/**
 * A StackExchange Post as extracted from the dump files. Contains all raw data.
 * 
 * @author Thomas Gottron
 * 
 */
public class SERawPost {

	public final static int QUESTION_ID = 1;
	public final static int ANSWER_ID = 2;

	public int id = 0;
	public int postTypeId = 0;
	public int parentId = 0;
	public int acceptedAnswerId = 0;
	public Date creationDate = null;
	public int score = 0;
	public int viewCount = 0;
	public String body = null;
	public int ownerUserId = 0;
	public int lastEditorUserId = 0;
	public String lastEditorDisplayName = null;
	public Date lastEditDate = null;
	public Date lastActivityDate = null;
	public Date communityOwnedDate = null;
	public Date closedDate = null;
	public String title = null;
	public String tags = null;
	public int answerCount = 0;
	public int commentCount = 0;
	public int favoriteCount = 0;

}

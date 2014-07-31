package de.unikoblenz.west.reveal.stackexchange;

import java.util.ArrayList;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StackPostsParser extends DefaultHandler {

	public static final String ELT_ROW = "row";
	public static final String ATT_ID = "Id";
	public static final String ATT_POSTTYPE = "PostTypeId";
	public static final String ATT_PARENT_ID = "ParentId";
	public static final String ATT_ACCEPTED_ANSWER = "AcceptedAnswerId";
	public static final String ATT_CREATION = "CreationDate";
	public static final String ATT_SCORE = "Score";
	public static final String ATT_VIEWCOUNT = "ViewCount";
	public static final String ATT_BODY = "Body";
	public static final String ATT_OWNER_USER_ID = "OwnerUserId";
	public static final String ATT_LAST_EDITOR_ID = "LastEditorUserId";
	public static final String ATT_LAST_EDITOR_DISPLAY_NAME = "LastEditorDisplayName";
	public static final String ATT_LAST_EDIT_DATE = "LastEditDate";
	public static final String ATT_LAST_ACTIVITY_DATE = "LastActivityDate";
	public static final String ATT_COMMUNITY_OWNED_DATE = "CommunityOwnedDate";
	public static final String ATT_CLOSED_DATE = "ClosedDate";
	public static final String ATT_TITLE = "Title";
	public static final String ATT_TAGS = "Tags";
	public static final String ATT_ANSWER_COUNT = "AnswerCount";
	public static final String ATT_COMMENT_COUNT = "CommentCount";
	public static final String ATT_FAVORITE_COUNT = "FavoriteCount";

	

	
	
	public ArrayList<SERawPost> posts = new ArrayList<SERawPost>();
	
	@Override
	public void startDocument() throws SAXException {
		this.posts = new ArrayList<SERawPost>();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals(ELT_ROW)) {
			//   <row Id="10" Reputation="412" CreationDate="2013-02-19T21:06:55.953" 
			// DisplayName="charles" LastAccessDate="2014-05-02T21:27:29.540" Views="3" 
			// UpVotes="30" DownVotes="0" AccountId="1895162" />
			
			
			
			SERawPost post = new SERawPost();
			if (attributes.getIndex(ATT_ID)>=0) {
				post.id = Integer.parseInt(attributes.getValue(ATT_ID));
			}
			if (attributes.getIndex(ATT_POSTTYPE)>=0) {
				post.postTypeId = Integer.parseInt(attributes.getValue(ATT_POSTTYPE));
			}
			if (attributes.getIndex(ATT_PARENT_ID)>=0) {
				post.parentId = Integer.parseInt(attributes.getValue(ATT_PARENT_ID));
			}
			if (attributes.getIndex(ATT_ACCEPTED_ANSWER)>=0) {
				post.acceptedAnswerId = Integer.parseInt(attributes.getValue(ATT_ACCEPTED_ANSWER));
			}
			if (attributes.getIndex(ATT_CREATION)>=0) {
				post.creationDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_CREATION));
			}
			if (attributes.getIndex(ATT_SCORE)>=0) {
				post.score = Integer.parseInt(attributes.getValue(ATT_SCORE));
			}
			if (attributes.getIndex(ATT_VIEWCOUNT)>=0) {
				post.viewCount = Integer.parseInt(attributes.getValue(ATT_VIEWCOUNT));
			}
			if (attributes.getIndex(ATT_BODY)>=0) {
				post.body = attributes.getValue(ATT_BODY);
			}
			if (attributes.getIndex(ATT_OWNER_USER_ID)>=0) {
				post.ownerUserId = Integer.parseInt(attributes.getValue(ATT_OWNER_USER_ID));
			}
			if (attributes.getIndex(ATT_LAST_EDITOR_ID)>=0) {
				post.lastEditorUserId = Integer.parseInt(attributes.getValue(ATT_LAST_EDITOR_ID));
			}
			if (attributes.getIndex(ATT_LAST_EDITOR_DISPLAY_NAME)>=0) {
				post.lastEditorDisplayName = attributes.getValue(ATT_LAST_EDITOR_DISPLAY_NAME);
			}
			if (attributes.getIndex(ATT_LAST_EDIT_DATE)>=0) {
				post.lastEditDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_LAST_EDIT_DATE));
			}
			if (attributes.getIndex(ATT_LAST_ACTIVITY_DATE)>=0) {
				post.lastActivityDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_LAST_ACTIVITY_DATE));
			}
			if (attributes.getIndex(ATT_COMMUNITY_OWNED_DATE)>=0) {
				post.communityOwnedDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_COMMUNITY_OWNED_DATE));
			}
			if (attributes.getIndex(ATT_CLOSED_DATE)>=0) {
				post.closedDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_CLOSED_DATE));
			}
			if (attributes.getIndex(ATT_TITLE)>=0) {
				post.title = attributes.getValue(ATT_TITLE);
			}
			if (attributes.getIndex(ATT_TAGS)>=0) {
				String sTags= attributes.getValue(ATT_TAGS);
				if (sTags.startsWith("<")) {
					sTags = sTags.substring(1);
				}
				if (sTags.endsWith(">")) {
					sTags = sTags.substring(0,sTags.length()-1);
				}
				post.tags = sTags.split("><");
			}
			if (attributes.getIndex(ATT_ANSWER_COUNT)>=0) {
				post.answerCount = Integer.parseInt(attributes.getValue(ATT_ANSWER_COUNT));
			}
			if (attributes.getIndex(ATT_COMMENT_COUNT)>=0) {
				post.commentCount = Integer.parseInt(attributes.getValue(ATT_COMMENT_COUNT));
			}
			if (attributes.getIndex(ATT_FAVORITE_COUNT)>=0) {
				post.favoriteCount = Integer.parseInt(attributes.getValue(ATT_FAVORITE_COUNT));
			}
			
			this.posts.add(post);
		}
	}


}

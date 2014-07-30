package de.unikoblenz.west.reveal.stackexchange;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StackCommentsParser extends DefaultHandler {

		public static final String ELT_ROW = "row";
		public static final String ATT_ID = "Id";
		public static final String ATT_POST_ID = "PostId";
		public static final String ATT_SCORE = "Score";
		public static final String ATT_TEXT = "Text";
		public static final String ATT_CREATION = "CreationDate";
		public static final String ATT_USER_ID = "UserId";		
		
		
		public ArrayList<SERawComment> comments = new ArrayList<SERawComment>();
		
		@Override
		public void startDocument() throws SAXException {
			this.comments = new ArrayList<SERawComment>();
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
				
				
				
				SERawComment comment = new SERawComment();
				if (attributes.getIndex(ATT_ID)>=0) {
					comment.id = Integer.parseInt(attributes.getValue(ATT_ID));
				}
				if (attributes.getIndex(ATT_POST_ID)>=0) {
					comment.postId = Integer.parseInt(attributes.getValue(ATT_POST_ID));
				}
				if (attributes.getIndex(ATT_SCORE)>=0) {
					comment.score = Integer.parseInt(attributes.getValue(ATT_SCORE));
				}
				if (attributes.getIndex(ATT_TEXT)>=0) {
					comment.text = attributes.getValue(ATT_TEXT);
				}
				if (attributes.getIndex(ATT_CREATION)>=0) {
					comment.creationDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_CREATION));
				}
				if (attributes.getIndex(ATT_USER_ID)>=0) {
					comment.userId = Integer.parseInt(attributes.getValue(ATT_USER_ID));
				}
				this.comments.add(comment);
			}
		}



}

package de.unikoblenz.west.reveal.stackexchange;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StackUserParser extends DefaultHandler {

	public static final String ELT_ROW = "row";
	public static final String ATT_ID = "Id";
	public static final String ATT_REPUT = "Reputation";
	public static final String ATT_CREATION = "CreationDate";
	public static final String ATT_DISP_NAME = "DisplayName";
	public static final String ATT_LAST_ACCESS = "LastAccessDate";
	public static final String ATT_VIEWS = "Views";
	public static final String ATT_UP_VOTES = "UpVotes";
	public static final String ATT_DOWN_VOTES = "DownVotes";
	public static final String ATT_ACCOUNT_ID = "AccountId";
	public static final String ATT_LOCATION = "Location";
	public static final String ATT_AGE = "Age";
	public static final String ATT_ABOUT = "AboutMe";
	public static final String ATT_WEBSITE = "WebsiteUrl";
	

	
	
	public ArrayList<SERawUser> users = new ArrayList<SERawUser>();
	
	@Override
	public void startDocument() throws SAXException {
		this.users = new ArrayList<SERawUser>();
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
			
			
			
			SERawUser user = new SERawUser();
			if (attributes.getIndex(ATT_ID)>=0) {
				user.id = Integer.parseInt(attributes.getValue(ATT_ID));
			}
			if (attributes.getIndex(ATT_REPUT)>=0) {
				user.reputation = Integer.parseInt(attributes.getValue(ATT_REPUT));
			}
			if (attributes.getIndex(ATT_CREATION)>=0) {
				user.creationDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_CREATION));
			}
			if (attributes.getIndex(ATT_DISP_NAME)>=0) {
				user.displayName = attributes.getValue(ATT_DISP_NAME);
			}
			if (attributes.getIndex(ATT_LAST_ACCESS)>=0) {
				user.lastAccessDate = StackExchangeCommunityFactory.parseDate(attributes.getValue(ATT_LAST_ACCESS));
			}
			if (attributes.getIndex(ATT_VIEWS)>=0) {
				user.views = Integer.parseInt(attributes.getValue(ATT_VIEWS));
			}
			if (attributes.getIndex(ATT_UP_VOTES)>=0) {
				user.upVotes = Integer.parseInt(attributes.getValue(ATT_UP_VOTES));
			}
			if (attributes.getIndex(ATT_DOWN_VOTES)>=0) {
				user.downVotes = Integer.parseInt(attributes.getValue(ATT_DOWN_VOTES));
			}
			if (attributes.getIndex(ATT_ACCOUNT_ID)>=0) {
				user.accountId = Integer.parseInt(attributes.getValue(ATT_ACCOUNT_ID));
			}
			if (attributes.getIndex(ATT_LOCATION)>=0) {
				user.location = attributes.getValue(ATT_LOCATION);
			}
			if (attributes.getIndex(ATT_AGE)>=0) {
				user.age = Integer.parseInt(attributes.getValue(ATT_AGE));
			}
			if (attributes.getIndex(ATT_ABOUT)>=0) {
				user.aboutMe = attributes.getValue(ATT_ABOUT);
			}
			if (attributes.getIndex(ATT_WEBSITE)>=0) {
				user.websiteUrl = attributes.getValue(ATT_WEBSITE);
			}			
			
			this.users.add(user);
		}
	}

}
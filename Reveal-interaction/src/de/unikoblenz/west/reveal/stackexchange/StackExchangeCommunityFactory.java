package de.unikoblenz.west.reveal.stackexchange;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.joda.time.Instant;
import org.joda.time.Interval;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.User;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangeDiscussionNodeAnnotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangePostAnnotation;
import de.unikoblenz.west.reveal.structures.annotations.StackExchangeUserAnnotation;

public class StackExchangeCommunityFactory {
	
	public static final long COMMENT_ID_OFFSET = 1000000000l;

	public static Community parseCommunity(String name, String userFilename, String postFilename, String commentFilename) {
		Community result = null;
		try {
		    SAXParserFactory spf = SAXParserFactory.newInstance();
		    spf.setNamespaceAware(true);
		    SAXParser saxParser = spf.newSAXParser();
		    XMLReader xmlReader = saxParser.getXMLReader();
		    
		    System.out.println("Parsing users ... ");
			StackUserParser userParser = new StackUserParser();
		    xmlReader.setContentHandler(userParser);
		    xmlReader.parse(convertToFileURL(userFilename));
		    
		    System.out.println("Parsing posts ... ");
			StackPostsParser postParser = new StackPostsParser();
		    xmlReader.setContentHandler(postParser);
		    xmlReader.parse(convertToFileURL(postFilename));
		    
		    System.out.println("Parsing comments ... ");
			StackCommentsParser commentParser = new StackCommentsParser();
		    xmlReader.setContentHandler(commentParser);
		    xmlReader.parse(convertToFileURL(commentFilename));
		    
		    System.out.println("Constructing community object... ");
		    result = new Community("StackExchange Community "+name);
		    
		    Date now = new Date();

		    for (SERawUser seUser : userParser.users) {
		    	User user = result.createUser(seUser.id);
		    	user.accountName = seUser.displayName;
		    	StackExchangeUserAnnotation annotation = new StackExchangeUserAnnotation();
		    	annotation.accountAge = dateToAge(seUser.creationDate, now);
		    	annotation.age = seUser.age;
		    	annotation.downVotes = seUser.downVotes;
		    	annotation.hasAboutMe = seUser.aboutMe != null;
		    	annotation.hasLocation = seUser.location != null;
		    	annotation.hasWebSite = seUser.websiteUrl != null;
		    	annotation.lastAccess = dateToAge(seUser.lastAccessDate, now);
		    	annotation.reputation = seUser.reputation;
		    	annotation.upVotes = seUser.upVotes;
		    	annotation.views = seUser.views;
		    	user.annotation = annotation;
		    }

		    /*
		     * First round: collect all ids of correct answers
		     */
		    TreeSet<Long> correctAnswerIds = new TreeSet<Long>();
		    for (SERawPost sePost : postParser.posts) {
		    	if (sePost.postTypeId == SERawPost.QUESTION_ID) {
		    		correctAnswerIds.add((long) sePost.acceptedAnswerId);
		    	}
		    }
		    
		    for (SERawPost sePost : postParser.posts) {

		    	DiscussionNode node = null;
		    	if (result.existsDiscussionNode(sePost.id)) {
		    		node = result.getDiscussionNode(sePost.id);
		    	} else {
		    		node = result.createDiscussionNode(sePost.id);
		    	}

		    	User author = null;
		    	if ( result.existsUser(sePost.ownerUserId)) {
		    		author = result.getUser(sePost.ownerUserId);
		    	} else {
			    	author = result.createUser(sePost.ownerUserId);
			    	author.accountName = Community.ANONYMOUS_ACCOUNT;
		    	}
		    	node.setUser(author);

		    	StackExchangePostAnnotation annotation = new StackExchangePostAnnotation();
		    	annotation.acceptedAnswerId = sePost.acceptedAnswerId;
		    	annotation.bodyContentLength = sePost.title!=null?sePost.body.length():0;
		    	annotation.favoriteCount = sePost.favoriteCount;
		    	annotation.score = sePost.score;
		    	annotation.tagCount = sePost.tags.length;
		    	annotation.titleContentLength = sePost.title!=null?sePost.title.length():0;
		    	annotation.views = sePost.viewCount;
		    	if (correctAnswerIds.contains(node.getId())) {
		    		annotation.isCorrectAnswer = true;
		    	}
		    	node.annotation = annotation;
		    	
		    	switch (sePost.postTypeId) {
	    		case SERawPost.QUESTION_ID :
	    			node.type = DiscussionNode.TYPE_QUESTION;
	    			break;
	    		case SERawPost.ANSWER_ID :
	    			node.type = DiscussionNode.TYPE_ANSWER;
			    	
	    			DiscussionNode parent = null;
			    	if (result.existsDiscussionNode(sePost.parentId)) {
			    		parent = result.getDiscussionNode(sePost.parentId);
			    	} else {
			    		parent = result.createDiscussionNode(sePost.parentId);
			    	}
			    	parent.addChild(node);

			    	break;
		    	}
		    	
		    	
		    }
		    
		    for (SERawComment seComment : commentParser.comments) {
		    	
		    	DiscussionNode node = null;
		    	long commentId = seComment.id + COMMENT_ID_OFFSET;
		    	if (result.existsDiscussionNode(commentId)) {
		    		node = result.getDiscussionNode(commentId);
		    	} else {
		    		node = result.createDiscussionNode(commentId);
		    	}
		    	
		    	User author = null;
		    	if ( result.existsUser(seComment.userId)) {
		    		author = result.getUser(seComment.userId);
		    	} else {
			    	author = result.createUser(seComment.userId);
			    	author.accountName = Community.ANONYMOUS_ACCOUNT;
		    	}
		    	node.setUser(author);

		    	StackExchangeDiscussionNodeAnnotation annotation = new StackExchangeDiscussionNodeAnnotation();
		    	annotation.bodyContentLength = seComment.text!=null?seComment.text.length():0;
		    	annotation.score = seComment.score;
		    	node.annotation = annotation;
		    	
		    	DiscussionNode parent = null;
		    	if (result.existsDiscussionNode(seComment.postId)) {
		    		parent = result.getDiscussionNode(seComment.postId);
		    	} else {
		    		parent = result.createDiscussionNode(seComment.postId);
		    	}
		    	
    			node.type = DiscussionNode.TYPE_COMMENT;
		    	
		    	parent.addChild(node);
			    	
		    }
		    

		} catch (SAXException saxe) {
			saxe.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }


	public static Date parseDate(String date) {
		Date result = null;
		// CreationDate="2013-02-19T21:06:55.953"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		try {
			result = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}


	public static double dateToAge(Date date, Date ref) {
		double age = 0;
		Instant oldTime  = new Instant(date.getTime());
		Instant newTime = new Instant(ref.getTime());
		Interval interval = new Interval(oldTime, newTime);
		age = interval.toDurationMillis();
		return age;
	}


	
}

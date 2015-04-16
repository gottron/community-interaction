package de.unikoblenz.west.reveal.twitter.snow;

import org.json.JSONObject;

/**
 * Class for scraping relevant information from JSON encoded tweets and store
 * them in a simple object format.
 * 
 * @author Thomas Gottron
 * 
 */
public class SnowRawPost {

	/**
	 * JSON field for the id of the tweet (post/message)
	 */
	public static final String BASE_POST_ID_KEY = "id";
	/**
	 * JSON field for the user info about the author (itself a JSON object)
	 */
	public static final String BASE_USER_KEY = "user";
	/**
	 * JSON field for the user id (within the user object)
	 */
	public static final String USER_ID_KEY = "id";
	/**
	 * JSON field for the id of the replied-to tweet (if any)
	 */
	public static final String BASE_REPLY_TO_ID_KEY = "in_reply_to_status_id";
	/**
	 * JSON field for the author id of the replied-to tweet (if any)
	 */
	public static final String BASE_REPLY_TO_USER_ID_KEY = "in_reply_to_user_id";
	/**
	 * JSON field for the author name of the replied-to tweet (if any)
	 */
	public static final String BASE_REPLY_TO_USER_NAME_KEY = "in_reply_to_screen_name";
	/**
	 * JSON field for the name of the author (within the user object)
	 */
	public static final String USER_NAME_KEY = "name";
	/**
	 * JSON field for the actual text content of the tweet
	 */
	public static final String BASE_CONTENT_KEY = "text";

	public long postId = -1;
	public long authorId = -1;
	public long inReplyTo = -1;
	public long inReplyToUser = -1;
	public String authorName = null;
	public String content = null;
	public String replyToUserName = null;

	public static SnowRawPost fromJSON(String json) {
		SnowRawPost result = new SnowRawPost();
		JSONObject jO = new JSONObject(json);
		if (jO.has(BASE_POST_ID_KEY)) {
			result.postId = jO.getLong(BASE_POST_ID_KEY);
		}
		if (jO.has(BASE_REPLY_TO_ID_KEY)) {
			if (!jO.isNull(BASE_REPLY_TO_ID_KEY)) {
				result.inReplyTo = jO.getLong(BASE_REPLY_TO_ID_KEY);
			}
		}
		if (jO.has(BASE_REPLY_TO_USER_ID_KEY)) {
			if (!jO.isNull(BASE_REPLY_TO_USER_ID_KEY)) {
				result.inReplyToUser = jO.getLong(BASE_REPLY_TO_USER_ID_KEY);
			}
			if (jO.has(BASE_REPLY_TO_USER_NAME_KEY)) {
				if (!jO.isNull(BASE_REPLY_TO_USER_NAME_KEY)) {
					result.replyToUserName = jO
							.getString(BASE_REPLY_TO_USER_NAME_KEY);
				}
			}
		}
		if (jO.has(BASE_CONTENT_KEY)) {
			result.content = jO.getString(BASE_CONTENT_KEY);
		}
		if (jO.has(BASE_USER_KEY)) {
			JSONObject jUser = jO.getJSONObject(BASE_USER_KEY);
			if (jUser.has(USER_NAME_KEY)) {
				result.authorName = jUser.getString(USER_NAME_KEY);
			}
			if (jUser.has(USER_ID_KEY)) {
				result.authorId = jUser.getLong(USER_ID_KEY);
			}
		}
		return result;
	}

}

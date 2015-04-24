package de.unikoblenz.west.reveal.twitter.storm;

import de.unikoblenz.west.reveal.structures.Community;
import de.unikoblenz.west.reveal.structures.DiscussionNode;
import de.unikoblenz.west.reveal.structures.User;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Factory class to generate Community objects from Twitter data in JSON format,
 * specifically for the SNOW dataset
 *
 * @author Thomas Gottron
 *
 */
public class StormCommunityFactory {

    /**
     * Generates a Community object from a list of JSON files providing tweets
     * in native JSON format. The tweets are parsed and used to derive the list
     * of all users and all appearing interaction, i.e. discussion trees.
     *
     * @param name Name for the community (for displaying purposes)
     * @param discussionTreeBoltOutput output of the discussionTreeBolt
     * @return Community object representing the interaction on Twitter captured
     * in the input files.
     * @throws IOException
     */
    public static Community parseCommunity(String name, Map<String, Object> discussionTreeBoltOutput) {

        List<Map<String, Object>> tweets = (List<Map<String, Object>>) discussionTreeBoltOutput.get("result");

        // resulting community object to be filled with data.
        Community result = new Community(name);

        for (Map<String, Object> tweet : tweets) {
            addPostToCommunity(result, null, tweet);
        }

        return result;
    }

    private static void addPostToCommunity(Community result, Map<String, Object> parentTweet, Map<String, Object> tweet) {
        User user;
        String userIdString = (String) tweet.get("author_id");
        if (userIdString == null) {
            return;
        }
        long userId = Long.parseLong(userIdString);
        String screenName = (String) tweet.get("author_screen_name");
        long postId = Long.parseLong((String) tweet.get("tweet_id"));
        List<Map<String, Object>> replies = (List<Map<String, Object>>) tweet.get("replies");

        // Check if the author of the tweet is already captured in the
        // community -- if yes: retrieve, if no: create
        if (!result.existsUser(userId)) {
            user = result.createUser(userId);
            user.accountName = cleanUserName(screenName);
        } else {
            user = result.getUser(userId);
        }

        DiscussionNode node = result.createDiscussionNode(postId);
        node.setUser(user);

        if (parentTweet != null) {
            DiscussionNode parentNode = result.getDiscussionNode(Long.parseLong((String) parentTweet.get("tweet_id")));
            parentNode.addChild(node);
        }

        // recursively add all nodes in the tree
        for (Map<String, Object> reply : replies) {
            addPostToCommunity(result, tweet, reply);
        }
    }

    /**
     * Internal method to harmonise representation of user names by replacing
     * all white spaces into blanks.
     *
     * @param name raw user name (original account name)
     * @return cleaned user name which only contains blanks as white spaces.
     */
    public static String cleanUserName(String name) {
        return name.replaceAll("\\s", " ");
    }

}

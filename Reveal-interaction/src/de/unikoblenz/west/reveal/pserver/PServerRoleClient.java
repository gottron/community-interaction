package de.unikoblenz.west.reveal.pserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import de.unikoblenz.west.reveal.roles.UserWithRole;

public class PServerRoleClient {

	public static final String ATTR_COMMUNITY = "Community";
	
	public static final String FEAT_CONTRIBUTIONS = "Contributions";
	public static final String FEAT_ROLE_ELITIST = "Role.Elitist";
	public static final String FEAT_ROLE_GRUNT = "Role.Grunt";
	public static final String FEAT_ROLE_IGNORED = "Role.Ignored";
	public static final String FEAT_ROLE_JOINING_CONVERSATIONALIST = "Role.Joining_Conversationalist";
	public static final String FEAT_ROLE_NONE = "Role.None";
	public static final String FEAT_ROLE_POPULAR_INITIATOR = "Role.Popular_Initiator";
	public static final String FEAT_ROLE_POPULAR_PARTICIPANT = "Role.Popular_Participant";
	public static final String FEAT_ROLE_SUPPORTER = "Role.Supporter";
	public static final String FEAT_ROLE_TACITURN = "Role.Taciturn";
	public static final String FEAT_IN_DEGREE_RATIO = "InDegreeRatio";
	public static final String FEAT_POST_REPLIED_RATIO = "PostRepliedRatio";
	public static final String FEAT_THREAD_INIT_RATIO = "ThreadInitiationRatio";
	public static final String FEAT_BI_DIR_THREAD_RATIO = "BiDirectionalThreadRatio";
	public static final String FEAT_BI_DIR_NEIGHBOUR_RATIO = "BiDirectionalNeighboursRatio";
	public static final String FEAT_AVG_POST_PER_THREAD = "AvergaePostsPerThread";
	public static final String FEAT_STD_DEV_POST_PER_THREAD = "StdDevPostsPerThread";

	
	private PServerConfiguration config = new PServerConfiguration();
	
	private static Logger LOGGER = Logger.getLogger("InfoLogging");

	public PServerRoleClient(PServerConfiguration conf) {
		this.config = conf;
	}
	
	public void initializePserverModel() {
		//Users - Initial Model
        //set init value for each attribute: attr=null
		List<String> attrib = new ArrayList<String>();
		attrib.add(ATTR_COMMUNITY);
		
		List<String> init_attrib = set_init_values(attrib);
		String attributes = join(init_attrib, "&");
		String attributes_req = constructRequest("addattr", attributes);
		
		List<String> ftrs = new ArrayList<String>();
		ftrs.add(FEAT_CONTRIBUTIONS);
		ftrs.add(FEAT_ROLE_ELITIST);
		ftrs.add(FEAT_ROLE_GRUNT);
		ftrs.add(FEAT_ROLE_IGNORED);
		ftrs.add(FEAT_ROLE_JOINING_CONVERSATIONALIST);
		ftrs.add(FEAT_ROLE_NONE);
		ftrs.add(FEAT_ROLE_POPULAR_INITIATOR);
		ftrs.add(FEAT_ROLE_POPULAR_PARTICIPANT);
		ftrs.add(FEAT_ROLE_SUPPORTER);
		ftrs.add(FEAT_ROLE_TACITURN);
		ftrs.add(FEAT_IN_DEGREE_RATIO);
		ftrs.add(FEAT_POST_REPLIED_RATIO);
		ftrs.add(FEAT_THREAD_INIT_RATIO);
		ftrs.add(FEAT_BI_DIR_THREAD_RATIO);
		ftrs.add(FEAT_BI_DIR_NEIGHBOUR_RATIO);
		ftrs.add(FEAT_AVG_POST_PER_THREAD);
		ftrs.add(FEAT_STD_DEV_POST_PER_THREAD);
		
		List<String> init_ftrs = set_init_values(ftrs);
		String features = join(init_ftrs, "&");
		String features_req = constructRequest("addftr", features);
		
		System.out.println("Sending Initial Requests to Pserver ....\n");
		sendRequest(attributes_req);
		sendRequest(features_req);
		
	}

	public ArrayList<String>  listUsers() {
		LOGGER.info("Loading user names from server.... \n");
		String list_req = constructRequest("getusrs", "whr=*");
		JSONObject json = XML.toJSONObject(this.sendResponseRequest(list_req));
		return jsonToUserList(json);
	}
	
	public ArrayList<String>  listUsers(String role) {
		LOGGER.info("Loading user names from server for role "+role+" ... \n");
		
		String ftr_name = PServerRoleClient.FEAT_ROLE_NONE;
		if (role.equals(UserWithRole.ROLE_ELITIST)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_ELITIST;
		} else if (role.equals(UserWithRole.ROLE_GRUNT)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_GRUNT;
		} else if (role.equals(UserWithRole.ROLE_IGNORED)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_IGNORED;
		} else if (role.equals(UserWithRole.ROLE_JOINING_CONVERSATIONALIST)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_JOINING_CONVERSATIONALIST;
		} else if (role.equals(UserWithRole.ROLE_NONE)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_NONE;
		} else if (role.equals(UserWithRole.ROLE_POPULAR_INITIATOR)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_POPULAR_INITIATOR;
		} else if (role.equals(UserWithRole.ROLE_POPULAR_PARTICIPANT)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_POPULAR_PARTICIPANT;
		} else if (role.equals(UserWithRole.ROLE_SUPPORTER)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_SUPPORTER;
		} else if (role.equals(UserWithRole.ROLE_TACITURN)) {
			ftr_name = PServerRoleClient.FEAT_ROLE_TACITURN;
		}
		String list_req = constructRequest("sqlftrusr", "whr=up_numvalue:1|and|up_feature:'"+ftr_name+"'");
		JSONObject json = XML.toJSONObject(this.sendResponseRequest(list_req));
		return jsonToUserList(json);
	}
	
	private ArrayList<String> jsonToUserList(JSONObject json) {
		ArrayList<String> result = new ArrayList<String>();
		if (! json.isNull("result")) {
			Object o = json.get("result");
			if (! (o instanceof String)) {
				JSONObject resultObject = json.getJSONObject("result"); 
				JSONArray array = resultObject.getJSONArray("row");
				for (int i = 0; i < array.length(); i++) {
					JSONObject row = array.getJSONObject(i);
					if (! row.isNull("usr")) {
						result.add(array.getJSONObject(i).getString("usr"));
					}
				}
			}
		}
		return result;
	}
	
	public void addUser(UserWithRole user) {
		LOGGER.info("Sending data about one user to PServer..... \n");
		
		List<String> attrib = new ArrayList<String>();
		attrib.add(ATTR_COMMUNITY);
		List<String> fixed_attrs = setPrefix(attrib, "attr");
		
		List<String> ftrs = new ArrayList<String>();
		ftrs.add(FEAT_CONTRIBUTIONS);
		ftrs.add(FEAT_ROLE_ELITIST);
		ftrs.add(FEAT_ROLE_GRUNT);
		ftrs.add(FEAT_ROLE_IGNORED);
		ftrs.add(FEAT_ROLE_JOINING_CONVERSATIONALIST);
		ftrs.add(FEAT_ROLE_NONE);
		ftrs.add(FEAT_ROLE_POPULAR_INITIATOR);
		ftrs.add(FEAT_ROLE_POPULAR_PARTICIPANT);
		ftrs.add(FEAT_ROLE_SUPPORTER);
		ftrs.add(FEAT_ROLE_TACITURN);
		ftrs.add(FEAT_IN_DEGREE_RATIO);
		ftrs.add(FEAT_POST_REPLIED_RATIO);
		ftrs.add(FEAT_THREAD_INIT_RATIO);
		ftrs.add(FEAT_BI_DIR_THREAD_RATIO);
		ftrs.add(FEAT_BI_DIR_NEIGHBOUR_RATIO);
		ftrs.add(FEAT_AVG_POST_PER_THREAD);
		ftrs.add(FEAT_STD_DEV_POST_PER_THREAD);
		List<String> fixed_ftrs = setPrefix(ftrs, "ftr");

		List<String> values = new ArrayList<String>();
		values.add(fixed_attrs.get(0)+"="+this.encodeParam(user.community));
		values.add(fixed_ftrs.get(0)+"="+user.contributions);
		values.add(fixed_ftrs.get(1)+"="+(user.role.equals(UserWithRole.ROLE_ELITIST)?1:0));
		values.add(fixed_ftrs.get(2)+"="+(user.role.equals(UserWithRole.ROLE_GRUNT)?1:0));
		values.add(fixed_ftrs.get(3)+"="+(user.role.equals(UserWithRole.ROLE_IGNORED)?1:0));
		values.add(fixed_ftrs.get(4)+"="+(user.role.equals(UserWithRole.ROLE_JOINING_CONVERSATIONALIST)?1:0));
		values.add(fixed_ftrs.get(5)+"="+(user.role.equals(UserWithRole.ROLE_NONE)?1:0));
		values.add(fixed_ftrs.get(6)+"="+(user.role.equals(UserWithRole.ROLE_POPULAR_INITIATOR)?1:0));
		values.add(fixed_ftrs.get(7)+"="+(user.role.equals(UserWithRole.ROLE_POPULAR_PARTICIPANT)?1:0));
		values.add(fixed_ftrs.get(8)+"="+(user.role.equals(UserWithRole.ROLE_SUPPORTER)?1:0));
		values.add(fixed_ftrs.get(9)+"="+(user.role.equals(UserWithRole.ROLE_TACITURN)?1:0));
		values.add(fixed_ftrs.get(10)+"="+user.inDegreeRatio);
		values.add(fixed_ftrs.get(11)+"="+user.postsReplyRatio);
		values.add(fixed_ftrs.get(12)+"="+user.threadInitiationRatio);
		values.add(fixed_ftrs.get(13)+"="+user.bidirThreadRatio);
		values.add(fixed_ftrs.get(14)+"="+user.bidirNeighbourRatio);
		values.add(fixed_ftrs.get(15)+"="+user.avgPostPerThread);
		values.add(fixed_ftrs.get(16)+"="+user.stddevPostsPerThread);
		
		String joinedValues = join(values, "&");
		//System.out.println(joinedValues);
		String completeValues = "usr="+this.encodeParam(user.username)+"&"+joinedValues;
		sendRequest(constructRequest("setusr", completeValues));
		
	}
	
	/**
	 * Send the request to Pserver
	 */
	private void sendRequest(String request){
		try{
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			
			int retCode = connection.getResponseCode(); 
			if (retCode == HttpURLConnection.HTTP_OK) {
		        // OK
				LOGGER.info("[SUCCESS] Request was successful! \n");
		    } else {
		        // Server returned HTTP error code.
		    	LOGGER.warning(" Request was NOT successful! \n");

		    }
			
		} catch (MalformedURLException e) {
			LOGGER.info("[ERROR] Error while sending request \n");
		} catch (IOException e) {
			LOGGER.warning("[ERROR] Error while sending request \n");
		}
		
	}
	/**
	 * Send the request to Pserver
	 */
	private String sendResponseRequest(String request){
		StringBuffer buffer = new StringBuffer();
		try{
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			
			int retCode = connection.getResponseCode(); 
			if (retCode == HttpURLConnection.HTTP_OK) {
		        // OK
				LOGGER.info("[SUCCESS] Request was successful! \n");
		    } else {
		        // Server returned HTTP error code.
		    	LOGGER.warning(" Request was NOT successful! \n");

		    }
			
			BufferedReader bin = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			while ( (line=bin.readLine()) != null) {
				buffer.append(line);
			}
		} catch (MalformedURLException e) {
			LOGGER.info("[ERROR] Error while sending request \n");
		} catch (IOException e) {
			LOGGER.warning("[ERROR] Error while sending request \n");
		}
		return buffer.toString();

				
	}
	
	private static String join(List<String> list, String delim) {

	    StringBuilder sb = new StringBuilder();

	    String loopDelim = "";

	    for(String s : list) {

	        sb.append(loopDelim);
	        sb.append(s);            

	        loopDelim = delim;
	    }

	    return sb.toString();
	}

	private static List<String> set_init_values(List<String> list){
		List<String> init_list = new ArrayList<String>();
		for(String item : list ){
			//System.out.println(item + "=null");
			init_list.add(item + "=null");
			}
		return init_list;
	}
	
	/**
	 * Construct the request url as a string
	 */
	private String constructRequest(String command, String values){
		String host = this.config.getHost();
		String modelType = this.config.getMode();
		String clientName = this.config.getClientName();
		String clientPass = this.config.getClientPass();
		
		String request = host+modelType+"?clnt="+clientName+"|"+clientPass+"&com="+command+"&"+values;
		
		return request;	
	}

	private static List<String> setPrefix(List<String> list, String prefix){
		List<String> fixed_list = new ArrayList<String>();
		for(String item : list ){
			//System.out.println(item + "=null");
			fixed_list.add(prefix+"_"+item);
			}
		return fixed_list;
	} 
	

	private String encodeParam(String parameter) {
		try {
			return URLEncoder.encode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parameter;
	}
	
}

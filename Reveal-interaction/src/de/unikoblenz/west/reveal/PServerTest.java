package de.unikoblenz.west.reveal;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

import de.unikoblenz.west.reveal.pserver.PServerConfiguration;
import de.unikoblenz.west.reveal.pserver.PServerRoleClient;
import de.unikoblenz.west.reveal.roles.RoleAssociation;
import de.unikoblenz.west.reveal.roles.UserAnalysisFileReader;
import de.unikoblenz.west.reveal.roles.UserWithRole;

public class PServerTest {

	public static void main(String[] args) throws IOException {
		PServerConfiguration config = new PServerConfiguration();
//		config.setHost("http://idefix.iit.demokritos.gr:1111/");
		PServerRoleClient pservRoleClient = new PServerRoleClient(config);
//		pservRoleClient.initializePserverModel();
		PServerTest.roleAnalysis(pservRoleClient);
//		PServerTest.retrieveData(pservRoleClient);

		
	}
	
	public static void retrieveData(PServerRoleClient pClient) throws IOException {
		ArrayList<String> lines = pClient.listUsers();
		ArrayList<String> roleElitist = pClient.listUsers(UserWithRole.ROLE_ELITIST);
		ArrayList<String> roleGrunt = pClient.listUsers(UserWithRole.ROLE_GRUNT);
		ArrayList<String> roleIgnored = pClient.listUsers(UserWithRole.ROLE_IGNORED);
		ArrayList<String> roleJoining = pClient.listUsers(UserWithRole.ROLE_JOINING_CONVERSATIONALIST);
		ArrayList<String> roleNone = pClient.listUsers(UserWithRole.ROLE_NONE);
		ArrayList<String> rolePopInit = pClient.listUsers(UserWithRole.ROLE_POPULAR_INITIATOR);
		ArrayList<String> rolePopPart = pClient.listUsers(UserWithRole.ROLE_POPULAR_PARTICIPANT);
		ArrayList<String> roleSupporter = pClient.listUsers(UserWithRole.ROLE_SUPPORTER);
		ArrayList<String> roleTaciturn = pClient.listUsers(UserWithRole.ROLE_TACITURN);
		System.out.println("Total users: "+lines.size());
		System.out.println("Elitist: "+roleElitist.size());
		System.out.println("Grunt: "+roleGrunt.size());
		System.out.println("Ignored: "+roleIgnored.size());
		System.out.println("Joining Conversationalist: "+roleJoining.size());
		System.out.println("Popular Initiator: "+rolePopInit.size());
		System.out.println("Popular Participant: "+rolePopPart.size());
		System.out.println("Supporter: "+roleSupporter.size());
		System.out.println("Taciturn: "+roleTaciturn.size());
		System.out.println("None: "+roleNone.size());
	}
	
	public static void roleAnalysis(PServerRoleClient pClient) throws IOException {
//		UserAnalysisFileReader uafr = new UserAnalysisFileReader(new File("data-out/Travel-u-1.csv"), "Travel (StackExchange), contribs >= 1");
//		UserAnalysisFileReader uafr = new UserAnalysisFileReader(new File("data-out/Apple-u-1.csv"), "Apple (StackExchange), contribs >= 1");
		UserAnalysisFileReader uafr = new UserAnalysisFileReader(new File("data-out/Math-u-1.csv"), "Math (StackExchange), contribs >= 1");
		HashSet<UserWithRole> users = new HashSet<UserWithRole>();
		
		long init = System.currentTimeMillis();
		System.out.println("Reading users ...");
		while(uafr.hasNext()) {
			UserWithRole u = uafr.nextAsUserWithRole();
//			System.out.println(u);
			users.add(u);
		}
		long tick1 = System.currentTimeMillis();
		RoleAssociation ra = new RoleAssociation();
		System.out.println("Processing users ...");
		ra.process(users);
		long tick2 = System.currentTimeMillis();
		System.out.println("Storing users in PServer ...");
		int elitist = 0;
		int grunt = 0;
		int ignored = 0;
		int joining = 0;
		int popInit = 0;
		int popPart = 0;
		int supporter = 0;
		int taciturn = 0;
		int none = 0;
		PrintStream csvOut = new PrintStream("roles-classification.csv");
		csvOut.print("Role\t");
		csvOut.print("Username\t");
		csvOut.print("AvgPostPerThread\t");
		csvOut.print("BidirNeighbourRatio\t");
		csvOut.print("BidirThreadRatio\t");
//		out.print("Contributions\t");
		csvOut.print("InDegreeRatio\t");
		csvOut.print("PostsReplyRatio\t");
		csvOut.print("StddevPostsPerThread\t");
		csvOut.print("ThreadInitiationRatio\n");
		for (UserWithRole u : users) {
			PServerTest.writeUser(u, csvOut);
//			System.out.println(u);
//			pClient.addUser(u);
			if (u.role.equals(UserWithRole.ROLE_ELITIST)) {
				elitist++;
			} else if (u.role.equals(UserWithRole.ROLE_GRUNT)) {
				grunt++;
			} else if (u.role.equals(UserWithRole.ROLE_IGNORED)) {
				ignored++;
			} else if (u.role.equals(UserWithRole.ROLE_JOINING_CONVERSATIONALIST)) {
				joining++;
			} else if (u.role.equals(UserWithRole.ROLE_POPULAR_INITIATOR)) {
				popInit++;
			} else if (u.role.equals(UserWithRole.ROLE_POPULAR_PARTICIPANT)) {
				popPart++;
			} else if (u.role.equals(UserWithRole.ROLE_SUPPORTER)) {
				supporter++;
			} else if (u.role.equals(UserWithRole.ROLE_TACITURN)) {
				taciturn++;
			} else if (u.role.equals(UserWithRole.ROLE_NONE)) {
				none++;
			} 
		}
		csvOut.close();
		long tick3 = System.currentTimeMillis();
		System.out.println("Total users: "+users.size()+" users");
		System.out.println("Reading: "+(tick1-init)+" ms");
		System.out.println("Processing: "+(tick2-tick1)+" ms");
		System.out.println("PServer: "+(tick3-tick2)+" ms");
		System.out.println("*** Role dist ***");
		System.out.println("Elitist: "+elitist);
		System.out.println("Grunt: "+grunt);
		System.out.println("Ignored: "+ignored);
		System.out.println("Joining Conversationalist: "+joining);
		System.out.println("Popular Initiator: "+popInit);
		System.out.println("Popular Participant: "+popPart);
		System.out.println("Supporter: "+supporter);
		System.out.println("Taciturn: "+taciturn);
		System.out.println("None: "+none);

	}
	
	public static void writeUser(UserWithRole user, PrintStream out) {
		out.print("\""+user.role+"\"\t");
		out.print("\""+user.username+"\"\t");
		out.print(user.avgPostPerThread+"\t");
		out.print(user.bidirNeighbourRatio+"\t");
		out.print(user.bidirThreadRatio+"\t");
//		out.print(user.contributions+"\t");
		out.print(user.inDegreeRatio+"\t");
		out.print(user.postsReplyRatio+"\t");
		out.print(user.stddevPostsPerThread+"\t");
		out.print(user.threadInitiationRatio+"\n");
	}
	
}

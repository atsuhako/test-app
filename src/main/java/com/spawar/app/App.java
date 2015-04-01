package com.spawar.app;

//import edu.jhuapl.tinkerpop.*;
//import org.apache.commons.configuration.*;
//import com.tinkerpop.gremlin.structure.*;
//import com.tinkerpop.gremlin.structure.util.*;
// 	import com.tinkerpop.gremlin.structure.Graph;
// 	import com.tinkerpop.gremlin.structure.util.GraphFactory;

	import com.tinkerpop.blueprints.Graph;
	import com.tinkerpop.blueprints.GraphFactory;

	import org.apache.commons.configuration.Configuration;

	import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration;
	import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration.InstanceType;

	import java.util.regex.Matcher;
	import java.util.regex.Pattern;

	import org.neo4j.graphdb.GraphDatabaseService;
	import org.neo4j.graphdb.factory.GraphDatabaseFactory;
	import org.neo4j.remote.RemoteGraphDatabase;

/**
 * Hello world!
 * https://github.com/neo4j-contrib/java-rest-binding
 */
public class App {
	public static void main(String[] args) {
//		testAccumuloGraph();
		testNeo4j();
		System.out.print("Press enter: ");
		System.console().readLine();
		System.out.println("Hello World!");
	} //end of main

	private static void testAccumuloGraph() {
		Configuration cfg1 = new AccumuloGraphConfiguration().setInstanceType(InstanceType.Mock).setGraphName("graph");

		Configuration cfg2 = new AccumuloGraphConfiguration()
			.setInstanceType(InstanceType.Distributed)
			.setZooKeeperHosts("192.168.227.133:2181")
			.setInstanceName("acc1")
			.setUser("acc1_user").setPassword("password")
			.setGraphName("graph")
			.setCreate(true);

		//return GraphFactory.open(cfg);

		GraphFactory.open(cfg2);
	} //end of testAccumuloGraph

	private static void testNeo4j() {
		//GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/Anthony/Documents/Neo4j/default.graphdb");
		GraphDatabaseService graphDb = RemoteGraphDatabase("localhost:3131");
		//registerShutdownHook(graphDb);
	} //end of testNeo4j

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	} //end of registerShutdownHook
} //end of class
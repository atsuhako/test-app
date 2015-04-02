package com.spawar.app;

import edu.jhuapl.tinkerpop.AccumuloGraph;
import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration;
import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration.InstanceType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * Hello world!
 * https://github.com/neo4j-contrib/java-rest-binding
 */
public class App {
	public static void main(String[] args) {
		testAccumuloGraph();
//		testNeo4j();

		System.out.println("Hello World!");
	} //end of main

	private static void testAccumuloGraph() {
		AccumuloGraphConfiguration cfg1 = new AccumuloGraphConfiguration().setInstanceType(InstanceType.Mock).setGraphName("graph");

		AccumuloGraphConfiguration cfg2 = new AccumuloGraphConfiguration()
			.setInstanceType(InstanceType.Distributed)
			.setZooKeeperHosts("192.168.227.133:2181")
			.setInstanceName("acc1")
			.setUser("acc1_user")
			.setPassword("password")
			.setGraphName("graph")
			.setCreate(true);

		//return GraphFactory.open(cfg);

		//Graph graph = GraphFactory.open(cfg2);
		AccumuloGraph graph = new AccumuloGraph(cfg2);
	} //end of testAccumuloGraph

	private static void testNeo4j() {
		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/Anthony/Documents/Neo4j/default.graphdb");
		registerShutdownHook(graphDb);

		System.out.print("Press enter to continue: ");
		System.console().readLine();
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
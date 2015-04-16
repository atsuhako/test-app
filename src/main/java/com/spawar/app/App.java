package com.spawar.app;

//import edu.jhuapl.tinkerpop.AccumuloGraph;
//import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration;
//import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration.InstanceType;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Result;

import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.Path;

/**
 * Hello world!
 * https://github.com/neo4j-contrib/java-rest-binding
 *
 */
public class App {
	private enum MyLabels implements Label { ROOT, KEY; }
	private enum MyRels implements RelationshipType { PARENT_OF }

	public static void main(String[] args) {
		//testAccumuloGraph();
		//testNeo4jAdapter();
		testNeo4jRestAdapter();
	} //end of main

/*

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
*/
	private static void testNeo4jRestAdapter() {
		Neo4jAdapter neo4jDb = new Neo4jAdapter();
		NumberFormat nf = new DecimalFormat("###,###,##0");

		long startTime = System.nanoTime();

		System.out.println("Rest Nodes (STARTUP): " + neo4jDb.getRestNodeCnt() );
		System.out.println("Unprocessed Nodes (STARTUP): " + neo4jDb.getRestUnprocessedCnt() );

		neo4jDb.restCreateTree();
//		neo4jDb.restTraverseTree();

		System.out.println("\n" + "Unprocessed Nodes (SHUTDOWN): " + neo4jDb.getRestUnprocessedCnt() );
		System.out.println("Rest Nodes (SHUTDOWN): " + neo4jDb.getRestNodeCnt());

		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + nf.format(elapsedTime/(long)1000000000) + " seconds");

	} //end of testNeo4jRestAdapter

	private static void testNeo4jAdapter() {
		Neo4jAdapter neo4jDb = new Neo4jAdapter("C:/db_neo4j/keytree.graphdb");
		NumberFormat nf = new DecimalFormat("###,###,##0");

		//System.out.println("Total nodes at startup: " + nf.format(neo4jDb.getAllNodeCnt()));
		System.out.println("Total nodes PROCESSED at startup: " + nf.format(neo4jDb.getProcessedCnt()));

//		long startTime2 = System.nanoTime();
//		System.out.println("Total nodes PROCESSED at startup: " + nf.format(neo4jDb.getProcessedCnt2()));
//		long elapsedTime2 = System.nanoTime() - startTime2;
//		System.out.println("Elapsed Time: " + nf.format(elapsedTime2/(long)1000000000) + " seconds");

		// operations on the graph
		long startTime = System.nanoTime();

//		Node root = neo4jDb.findOrCreateNode(MyLabels.ROOT, "name", "root");
//		long rootId = neo4jDb.findOrCreateNodeId(MyLabels.ROOT, "name", "root");

		// Create child records up to depth x
		//neo4jDb.createChildRecords(rootId, 3);
		neo4jDb.createKeyTree(5);

		// Traverse all nodes
//		neo4jDb.traverseAll();

		long elapsedTime = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + nf.format(elapsedTime/(long)1000000000) + " seconds");


		System.out.println("Total nodes PROCESSED at shutdown: " + nf.format(neo4jDb.getProcessedCnt()));
		//System.out.println("Total nodes at shutdown: " + nf.format(neo4jDb.getAllNodeCnt()));
	} //end of testNeo4jAdapter
} //end of class
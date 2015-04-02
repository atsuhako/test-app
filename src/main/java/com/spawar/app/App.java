package com.spawar.app;

import edu.jhuapl.tinkerpop.AccumuloGraph;
import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration;
import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration.InstanceType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.graphdb.Result;
import java.text.NumberFormat;
import java.text.DecimalFormat;

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
		testNeo4j();

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
		NumberFormat nf = new DecimalFormat("###,###,##0");

		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/db_neo4j/btree2.graphdb");
		registerShutdownHook(graphDb);

		System.out.println("Total nodes at startup: " + getAllNodeCnt(graphDb));
		System.out.println("Total relationships at startup: " + getAllRelCnt(graphDb));

		long startTime = System.nanoTime();

		// operations on the graph

/*
		long startTime1 = System.nanoTime();
		Node root = findOrCreateNode(graphDb, MyLabels.ROOT, "name", "k4");
		createChildRecords(graphDb, root, 3);
		System.out.println();
		long estimatedTime1 = System.nanoTime() - startTime1;
		System.out.println("Elapsed Time (1): " + nf.format(estimatedTime1/(long)1000000000) + " seconds");
*/

/*
		long startTime2 = System.nanoTime();
		Node root = findOrCreateNode(graphDb, MyLabels.ROOT, "name", "k4");
		createChildRecords2(graphDb, root, 2);
		System.out.println();
		long estimatedTime2 = System.nanoTime() - startTime2;
		System.out.println("Elapsed Time (2): " + nf.format(estimatedTime2/(long)1000000000) + " seconds");
*/

		long startTime3 = System.nanoTime();
		long rootId = findOrCreateNodeId(graphDb, MyLabels.ROOT, "name", "k4");
		createChildRecords3(graphDb, rootId, "", 3);
		System.out.println();
		long estimatedTime3 = System.nanoTime() - startTime3;
		System.out.println("Elapsed Time (3): " + nf.format(estimatedTime3/(long)1000000000) + " seconds");

		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Elapsed Time: " + nf.format(estimatedTime/(long)1000000000) + " seconds");

		System.out.println("Total nodes at shutdown: " + getAllNodeCnt(graphDb));
		System.out.println("Total relationships at shutdown: " + getAllRelCnt(graphDb));

		System.out.print("Press enter to continue: ");
		System.console().readLine();
	} //end of testNeo4j

	private static int getAllNodeCnt(GraphDatabaseService graphDb) {
		int nodeCnt = 0;

		try (Transaction tx = graphDb.beginTx()) {
			GlobalGraphOperations ggops = GlobalGraphOperations.at(graphDb);

			for (Node n : ggops.getAllNodes()) {
				nodeCnt++;
			} //end of for
			tx.success();

		} catch (Exception e) {
			e.printStackTrace();
		} //end of catch

		return nodeCnt;
	} //end of getAllNodeCnt

	private static int getAllRelCnt(GraphDatabaseService graphDb) {
		int relCnt = 0;

		try (Transaction tx = graphDb.beginTx()) {
			GlobalGraphOperations ggops = GlobalGraphOperations.at(graphDb);

			for (Relationship n : ggops.getAllRelationships()) {
				relCnt++;
			} //end of for

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} //end of catch

		return relCnt;
	} //end of getAllRelCnt

	private static Node findOrCreateNode(GraphDatabaseService graphDb, Label label, String key, String value) {
		Node returnNode = null;

		try (Transaction tx = graphDb.beginTx()) {
			returnNode = graphDb.findNode(label, key, value);
			if (returnNode == null) {
				returnNode = graphDb.createNode(label);
				returnNode.setProperty(key, value);
			} //end of else

			tx.success();
		} //end of try

		return returnNode;
	} //end of findOrCreateNode

	private static long findOrCreateNodeId(GraphDatabaseService graphDb, Label label, String key, String value) {
		long nodeId = -1;

		try (Transaction tx = graphDb.beginTx()) {
			Node returnNode = graphDb.findNode(label, key, value);
			if (returnNode == null) {
				returnNode = graphDb.createNode(label);
				returnNode.setProperty(key, value);
			} //end of else

			nodeId = returnNode.getId();
			tx.success();
		} //end of try

		return nodeId;
	} //end of findOrCreateNodeId

	private static void createChildRecords(GraphDatabaseService graphDb, Node parentNode, int maxDepth) {
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			Node childNode = null;
			Iterator<Relationship> iterator = null;
			String key = null;

			try (Transaction tx = graphDb.beginTx()) {
				key = (parentNode.hasLabel(MyLabels.ROOT) ? "" : parentNode.getProperty("key")) + String.valueOf(c);

				iterator = parentNode.getRelationships(Direction.OUTGOING).iterator();

				while (iterator.hasNext() && childNode == null) {
					Node currNode = iterator.next().getEndNode();
					if (currNode.getProperty("key").equals(key)) {
						childNode = currNode;
					} //end of if
				} //end of while

				if (childNode == null) {
					childNode = graphDb.createNode(MyLabels.KEY);
					childNode.setProperty("key", key);
					childNode.setProperty("processed", false);
					parentNode.createRelationshipTo(childNode, MyRels.PARENT_OF);
				} //end of if

				tx.success();
			} //end of try

			// Recursively create children til max depth
			if (key.length() < maxDepth) createChildRecords(graphDb, childNode, maxDepth);
			//if (key.length() == 1) System.out.print(key);
		} //end of for
	} //end of createChildRecords

	private static void createChildRecords2(GraphDatabaseService graphDb, Node parentNode, int maxDepth) {
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			Node childNode = null;
			String key = null;

			try (Transaction tx = graphDb.beginTx()) {
				key = (parentNode.hasLabel(MyLabels.ROOT) ? "" : parentNode.getProperty("key")) + String.valueOf(c);
				childNode = graphDb.findNode(MyLabels.KEY, "key", key);

				if (childNode == null) {
					childNode = graphDb.createNode(MyLabels.KEY);
					childNode.setProperty("key", key);
					childNode.setProperty("processed", false);
					parentNode.createRelationshipTo(childNode, MyRels.PARENT_OF);
				} //end of else

				tx.success();
			} //end of try

			// Recursively create children til max depth
			if (key.length() < maxDepth) createChildRecords2(graphDb, childNode, maxDepth);
		} //end of for
	} //end of createChildRecords2

	private static void createChildRecords3(GraphDatabaseService graphDb, long parentId, String parentKey, int maxDepth) {
		StringBuffer cqlStmt = new StringBuffer("MATCH (p) WHERE id(p) = " + parentId);
		ArrayList<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			String key = parentKey + String.valueOf(c);
			cqlStmt.append(" CREATE UNIQUE (p)-[:PARENT_OF]->(:KEY{ name:'" + key + "'})");
		} //end of for
		cqlStmt.append(" WITH p");
		cqlStmt.append(" MATCH (p)-[:PARENT_OF]->(c) RETURN id(c), c.name");

		try ( Transaction tx = graphDb.beginTx();Result result = graphDb.execute(cqlStmt.toString()) ) {
     	while ( result.hasNext() ){
				resultList.add(result.next());
     	} //end of while
     	tx.success();
		} //end of try

		if (parentKey.length() + 1 < maxDepth) {
			for (Map<String, Object> row : resultList) {
				createChildRecords3(graphDb, Long.parseLong(String.valueOf(row.get("id(c)"))), String.valueOf(row.get("c.name")), maxDepth);
			} //end of for
		} //end of if
	} //end of createChildRecords3

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			} //end of run
		});
	} //end of registerShutdownHook
} //end of class
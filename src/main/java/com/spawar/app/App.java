package com.spawar.app;

import edu.jhuapl.tinkerpop.AccumuloGraph;
import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration;
import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration.InstanceType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

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
		testNeo4jBatchInsert(5);

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
		NumberFormat nf = new DecimalFormat("###,###,##0.00");

		GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/Anthony/Documents/Neo4j/default.graphdb");
		registerShutdownHook(graphDb);

		System.out.println("Total nodes at startup: " + getAllNodeCnt(graphDb));
		System.out.println("Total relationships at startup: " + getAllRelCnt(graphDb));

		long startTime = System.nanoTime();

		//find root
		Node root = findOrCreateNode(graphDb, MyLabels.ROOT, "name", "k4");

		// operations on the graph
		long startTime1 = System.nanoTime();
		createChildRecords(graphDb, root, 3);
		System.out.println();
		long estimatedTime1 = System.nanoTime() - startTime1;
		System.out.println("Elapsed Time (1): " + nf.format(estimatedTime1/(long)1000000000) + " nanoseconds");

		long startTime2 = System.nanoTime();
		createChildRecords2(graphDb, root, 2);
		System.out.println();
		long estimatedTime2 = System.nanoTime() - startTime2;
		System.out.println("Elapsed Time (2): " + nf.format(estimatedTime2/(long)1000000000) + " nanoseconds");


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
			} catch (Exception e) {
			e.printStackTrace();
		} //end of catch

		return relCnt;
	} //end of getAllRelCnt

	private static Node findOrCreateNode(GraphDatabaseService graphDb, Label label, String key, String value) {
		Node returnNode = null;

		try (Transaction tx = graphDb.beginTx(); ResourceIterator<Node> iterator = graphDb.findNodesByLabelAndProperty(label, key, value).iterator()) {
			if (iterator.hasNext()) {
				returnNode = iterator.next();
			} else {
				returnNode = graphDb.createNode(label);
				returnNode.setProperty(key, value);
			} //end of else

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} //end of finally

		return returnNode;
	} //end of findOrCreateNode

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
//						System.out.print("e");
					} //end of if
				} //end of while

				if (childNode == null) {
					childNode = graphDb.createNode(MyLabels.KEY);
					childNode.setProperty("key", key);
					childNode.setProperty("processed", false);
					parentNode.createRelationshipTo(childNode, MyRels.PARENT_OF);
					System.out.print(".");
				} //end of if

				tx.success();
			} catch (Exception e) {
				e.printStackTrace();
			} //end of catch

			// Recursively create children til max depth
			if (key.length() < maxDepth) createChildRecords(graphDb, childNode, maxDepth);
		} //end of for
	} //end of createChildRecords

	private static void testNeo4jBatchInsert(int maxDepth) {
		BatchInserter inserter = null;
		try {
			inserter = BatchInserters.inserter("C:/db_neo4j/testbatch.graphdb");
			Map<String, Object> properties = new HashMap<>();

			properties.put("key", "aaa");
			long mattiasNode = inserter.createNode(properties, MyLabels.KEY);

			properties.put("key", "bbb");
			long chrisNode = inserter.createNode(properties, MyLabels.KEY);

			inserter.createRelationship(mattiasNode, chrisNode, MyRels.PARENT_OF, null);
		} finally {
			if (inserter != null) {
				inserter.shutdown();
			} //end of if
		} //end of finally
	} //end of testNeo4jBatchInsert

	private static void createChildRecords2(GraphDatabaseService graphDb, Node parentNode, int maxDepth) {
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			Node childNode = null;
			ResourceIterator<Node> iterator = null;
			String key = null;


			try (Transaction tx = graphDb.beginTx()) {
				key = (parentNode.hasLabel(MyLabels.ROOT) ? "" : parentNode.getProperty("key")) + String.valueOf(c);
				iterator = graphDb.findNodesByLabelAndProperty(MyLabels.KEY, "key", key).iterator();

				if (iterator.hasNext()) {
//					System.out.print("g");
					childNode = iterator.next();
				} else {
					childNode = graphDb.createNode(MyLabels.KEY);
					childNode.setProperty("key", key);
					childNode.setProperty("processed", false);
					parentNode.createRelationshipTo(childNode, MyRels.PARENT_OF);

					System.out.print(".");
				} //end of else

				tx.success();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iterator.close();
			} //end of finally

			// Recursively create children til max depth
			if (key.length() < maxDepth) createChildRecords2(graphDb, childNode, maxDepth);
		} //end of for
	} //end of createChildRecords2

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
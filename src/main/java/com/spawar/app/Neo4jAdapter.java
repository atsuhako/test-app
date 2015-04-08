package com.spawar.app;

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
public class Neo4jAdapter {
	private GraphDatabaseService graphDb;
	private enum MyLabels implements Label { ROOT, KEY, UNPROCESSED, PROCESSED; }
	private enum MyRels implements RelationshipType { PARENT_OF }
	private int createdCnt = 0;

	public Neo4jAdapter(String filePath) {
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(filePath);
		registerShutdownHook(graphDb);
	} //end of Neo4jAdapter

	public Neo4jAdapter(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
		registerShutdownHook(graphDb);
	} //end of Neo4jAdapter

	public long traverseAll(Node startNode) {
		long nodeCnt = 0;

		System.out.print("Traversing (v1 logic)...");

		long traversedCnt = 0;
		while ((traversedCnt = traverseBatch(startNode)) > 0) {
			nodeCnt += traversedCnt;
			System.out.println(nodeCnt + " nodes traversed");
		} //end of while

		System.out.println(" Done!");

		return nodeCnt;
	} //end of traverseAll

	public long traverseBatch(Node startNode) {
		long traversedCnt = 0;
		ResourceIterator<Node> iterator = null;

		try (Transaction tx = this.graphDb.beginTx()) {
			TraversalDescription unprocessedTraversal = this.graphDb.traversalDescription()
						.depthFirst()
						.evaluator(Evaluators.fromDepth(1))
						.relationships(MyRels.PARENT_OF, Direction.OUTGOING)
						.evaluator( new Evaluator() {
							@Override
							public Evaluation evaluate(Path path) {
								boolean endNodeIsProcessed = path.endNode().hasLabel(MyLabels.PROCESSED);
								return endNodeIsProcessed ? Evaluation.EXCLUDE_AND_CONTINUE : Evaluation.INCLUDE_AND_CONTINUE;
							}
						});

			iterator = unprocessedTraversal.traverse(startNode).nodes().iterator();

			while (iterator.hasNext() && traversedCnt < 1000) {
				Node currNode = iterator.next();
				currNode.addLabel(MyLabels.PROCESSED);
//				currNode.setProperty("processed", true);
				traversedCnt++;
			} //end of while

			tx.success();
			System.gc();
		} finally {
			iterator.close();
		} //end of try

		return traversedCnt;
	} //end of traverseBatch

	public long traverseAllWithCypher(long rootId) {
		long nodeCnt = 0;

		System.out.print("Traversing (using Cypher)...");

		long traversedCnt = 0;
		while ((traversedCnt = traverseBatchWithCypher(rootId)) > 0) {
			nodeCnt += traversedCnt;
			System.out.println(nodeCnt + " nodes cyphered");
		} //end of while

		System.out.println(" Done!");

		return nodeCnt;
	} //end of traverseAllWithCypher

	private long traverseBatchWithCypher(long rootId) {
		long nodeCnt = 0;

		try ( Transaction tx = this.graphDb.beginTx();Result result = this.graphDb.execute("MATCH (r:ROOT)-[:PARENT_OF*1..10]-(c) WHERE id(r) = " + rootId + " AND (c.processed = false OR c.processed IS NULL) RETURN c LIMIT 1000") ) {
     	while (result.hasNext()) {
				Map<String, Object> row = result.next();
				Node n = (Node)row.get("c");
				n.setProperty("processed", true);
				nodeCnt++;
     	} //end of while
     	tx.success();
			System.gc();
		} //end of try
		System.out.println("returning: " + nodeCnt);

		return nodeCnt;
	} //end of traverseBatchWithCypher

	public long getAllNodeCnt() {
		long nodeCnt = 0;

		try (Transaction tx = this.graphDb.beginTx()) {
			GlobalGraphOperations ggops = GlobalGraphOperations.at(this.graphDb);

			for (Node n : ggops.getAllNodes()) {
				nodeCnt++;
			} //end of for
			tx.success();
		} //end of try

		return nodeCnt;
	} //end of getAllNodeCnt

	public long getProcessedCnt() {
		long nodeCnt = 0;
		ResourceIterator<Node> iterator = null;

		try (Transaction tx = this.graphDb.beginTx()) {
			iterator = graphDb.findNodes(MyLabels.PROCESSED);
			while (iterator.hasNext()) {
				iterator.next();
				nodeCnt++;
			} //end of while
			tx.success();
		} finally {
			iterator.close();
		} //end of try

		return nodeCnt;
	} //end of getProcessedCnt

	public long getProcessedCnt2() {
		long nodeCnt = 0;

		try ( Transaction tx = this.graphDb.beginTx();Result result = this.graphDb.execute("MATCH (c:PROCESSED) RETURN c") ) {
     	while (result.hasNext()) {
				result.next();
				nodeCnt++;
				if (nodeCnt % 1000 == 0) System.gc(); // Run Garbage Collector every 1000 nodes
     	} //end of while
     	tx.success();
		} //end of try

		return nodeCnt;
	} //end of getProcessedCnt2

	@Deprecated public long getUnprocessedCnt(long rootId) {
		long nodeCnt = 0;

		try ( Transaction tx = this.graphDb.beginTx();Result result = this.graphDb.execute("MATCH (r:ROOT)-[:PARENT_OF*1..99]-(c) WHERE id(r) = " + rootId + " AND (c.processed = false OR c.processed IS NULL) RETURN c") ) {
     	while (result.hasNext()) {
				result.next();
				nodeCnt++;
				if (nodeCnt % 1000 == 0) System.gc(); // Run Garbage Collector every 1000 nodes
     	} //end of while
     	tx.success();
		} //end of try

		return nodeCnt;
	} //end of getUnprocessedCnt

	public Node findOrCreateNode(Label label, String key, String value) {
		Node returnNode = null;

		try (Transaction tx = this.graphDb.beginTx()) {
			returnNode = this.graphDb.findNode(label, key, value);
			if (returnNode == null) {
				returnNode = this.graphDb.createNode(label);
				returnNode.setProperty(key, value);
			} //end of else

			tx.success();
		} //end of try

		return returnNode;
	} //end of findOrCreateNode

	public long findOrCreateNodeId(Label label, String key, String value) {
		long nodeId = -1;

		try (Transaction tx = this.graphDb.beginTx()) {
			Node returnNode = this.graphDb.findNode(label, key, value);
			if (returnNode == null) {
				returnNode = this.graphDb.createNode(label);
				returnNode.setProperty(key, value);
			} //end of else

			nodeId = returnNode.getId();
			tx.success();
		} //end of try

		return nodeId;
	} //end of findOrCreateNodeId

	public void createAlphaTree(Node parentNode, int maxDepth) {
		//defghijklmnopqrstuvwxyz
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			Node childNode = null;
			Iterator<Relationship> iterator = null;
			String key = null;

			try (Transaction tx = this.graphDb.beginTx()) {
				key = (parentNode.hasLabel(MyLabels.ROOT) ? "" : parentNode.getProperty("key")) + String.valueOf(c);

				iterator = parentNode.getRelationships(Direction.OUTGOING).iterator();

				while (iterator.hasNext() && childNode == null) {
					Node currNode = iterator.next().getEndNode();
					if (currNode.getProperty("key").equals(key)) {
						childNode = currNode;
					} //end of if
				} //end of while

				if (childNode == null) {
					childNode = this.graphDb.createNode(MyLabels.KEY);
					childNode.setProperty("key", key);
					childNode.setProperty("processed", false);
					parentNode.createRelationshipTo(childNode, MyRels.PARENT_OF);
					createdCnt++;
					if (createdCnt % 1000 == 0) System.out.println(createdCnt + " nodes created");
				} //end of if

				tx.success();
				System.gc();
			} //end of try

			// Recursively create children til max depth
			if (key.length() < maxDepth) createAlphaTree(childNode, maxDepth);
			//if (key.length() == 1) System.out.print(key);
		} //end of for
	} //end of createAlphaTree

	public void createChildRecords(long parentId, int maxDepth) {
		createChildRecords(parentId, "", maxDepth);
	} //end of createChildRecords

	private void createChildRecords(long parentId, String parentKey, int maxDepth) {
		StringBuffer cqlStmt = new StringBuffer("MATCH (p) WHERE id(p) = " + parentId);
		ArrayList<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			String key = parentKey + String.valueOf(c);
			cqlStmt.append(" CREATE UNIQUE (p)-[:PARENT_OF]->(:KEY{ key:'" + key + "'})");
		} //end of for
		cqlStmt.append(" WITH p");
		cqlStmt.append(" MATCH (p)-[:PARENT_OF]->(c) RETURN id(c), c.key");

		try ( Transaction tx = this.graphDb.beginTx();Result result = this.graphDb.execute(cqlStmt.toString()) ) {
     	while ( result.hasNext() ){
				resultList.add(result.next());
     	} //end of while
     	tx.success();
     	System.gc();
		} //end of try

		if (parentKey.length() + 1 < maxDepth) {
			for (Map<String, Object> row : resultList) {
				createChildRecords(Long.parseLong(String.valueOf(row.get("id(c)"))), String.valueOf(row.get("c.key")), maxDepth);
			} //end of for
		} //end of if
	} //end of createChildRecords

	private void registerShutdownHook(final GraphDatabaseService graphDb) {
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

	@Deprecated private int getAllRelCnt() {
		int relCnt = 0;

		try (Transaction tx = this.graphDb.beginTx()) {
			GlobalGraphOperations ggops = GlobalGraphOperations.at(this.graphDb);

			for (Relationship n : ggops.getAllRelationships()) {
				relCnt++;
			} //end of for

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} //end of catch

		return relCnt;
	} //end of getAllRelCnt

	@Deprecated private int getAllRelCnt2() {
		int relCnt = 0;

		try (Transaction tx = this.graphDb.beginTx()) {
			GlobalGraphOperations ggops = GlobalGraphOperations.at(this.graphDb);

			for (Relationship n : ggops.getAllRelationships()) {
				relCnt++;
			} //end of for

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} //end of catch

		return relCnt;
	} //end of getAllRelCnt2

	@Deprecated private void createChildRecords_universal_search(Node parentNode, int maxDepth) {
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
			Node childNode = null;
			String key = null;

			try (Transaction tx = this.graphDb.beginTx()) {
				key = (parentNode.hasLabel(MyLabels.ROOT) ? "" : parentNode.getProperty("key")) + String.valueOf(c);
				childNode = this.graphDb.findNode(MyLabels.KEY, "key", key);

				if (childNode == null) {
					childNode = this.graphDb.createNode(MyLabels.KEY);
					childNode.setProperty("key", key);
					childNode.setProperty("processed", false);
					parentNode.createRelationshipTo(childNode, MyRels.PARENT_OF);
				} //end of else

				tx.success();
			} //end of try

			// Recursively create children til max depth
			if (key.length() < maxDepth) createChildRecords_universal_search(childNode, maxDepth);
		} //end of for
	} //end of createChildRecords_universal_search
} //end of class
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

import java.util.Arrays;

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
		//testNeo4jRestAdapter();

		System.out.println("ABC: " + invert("Abcd Efgh"));

		System.out.println("encipher Vigenere: " + encipherVigenere("defend the east wall of the castle", "fortification"));
		System.out.println("decipher Vigenere: " + decipherVigenere("ISWXVI BJE XIGG BOCE WK BJE VIGGQS", "fortification"));
		System.out.println();

		System.out.println("encipher Beaufort: " + encipherBeaufort("defend the east wall of the castle", "fortification"));
		System.out.println("decipher Beaufort: " + decipherBeaufort("CKMPVC PVW PIWU JOGI UA PVW RIWUUK", "fortification"));

		System.out.println("decipher Beaufort via Vigenere: " + encipherVigenere(invert("CKMPVC PVW PIWU JOGI UA PVW RIWUUK"), "fortification"));
		System.out.println("decipher Beaufort via Vigenere: " + decipherVigenere(invert("CKMPVC PVW PIWU JOGI UA PVW RIWUUK"), "fortification"));
		System.out.println();

		System.out.println("encipher K1: " + encipherKryptos("BETWEEN SUBTLE SHADING AND THE ABSENCE OF LIGHT LIES THE NUANCE OF IQLUSION", "Palimpsest"));
		System.out.println("decipher K1: " + decipherKryptos("EMUFPHZ LRFAXY USDJKZL DKR NSH GNFIVJY QT QUXQB QVYU VLL TREVJY QT MKYRDMFD", "Palimpsest"));
		System.out.println();

		System.out.println("encipher K2: " + encipherKryptos("IT WAS TOTALLY INVISIBLE HOWS THAT POSSIBLE ? THEY USED THE EARTHS MAGNETIC FIELD X THE INFORMATION WAS GATHERED AND TRANSMITTED UNDERGRUUND TO AN UNKNOWN LOCATION X DOES LANGLEY KNOW ABOUT THIS ? THEY SHOULD ITS BURIED OUT THERE SOMEWHERE X WHO KNOWS THE EXACT LOCATION ? ONLY WW THIS WAS HIS LAST MESSAGE X THIRTY EIGHT DEGREES FIFTY SEVEN MINUTES SIX POINT FIVE SECONDS NORTH SEVENTY SEVEN DEGREES EIGHT MINUTES FORTY FOUR SECONDS WEST X LAYER TWO", "Abscissa"));
		System.out.println("decipher K2: " + decipherKryptos("VFPJUDEEHZWETZYVGWHKKQETGFQJNCEGGWHKK?DQMCPFQZDQMMIAGPFXHQRLGTIMVMZJANQLVKQEDAGDVFRPJUNGEUNAQZGZLECGYUXUEENJTBJLBQCRTBJDFHRRYIZETKZEMVDUFKSJHKFWHKUWQLSZFTIHHDDDUVH?DWKBFUFPWNTDFIYCUQZEREEVLDKFEZMOQQJLTTUGSYQPFEUNLAVIDXFLGGTEZ?FKZBSFDQVGOGIPUFXHHDRKFFHQNTGPUAECNUVPDJMQCLQUMUNEDFQELZZVRRGKFFVOEEXBDMVPNFQXEZLGREDNQFMPNZGLFLPMRJQYALMGNUVPDXVKPDQUMEBEDMHDAFMJGZNUPLGEWJLLAETG", "Abscissa"));
		System.out.println();


		System.out.println("K-4 Alphabet Distribution: " + alphaDistribMap(k4CipherText));
	} //end of main


	private static final String invert(String str) {
		StringBuffer invertedStr = new StringBuffer();
		char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

		for (char c : str.toCharArray()) {
			if (c >= 65 && c <= 90) {
				invertedStr.append((char)(90 - c + 65));
			} else if (c >= 97 && c <= 122) {
				invertedStr.append((char)(122 - c + 97));
			} else {
				invertedStr.append(c);
			} //end of else
		} //end of for

//		for (int i = 0; i < str.length(); i++) {
//			invertedStr[i] = alphabet[25 - Arrays.binarySearch(alphabet, Character.toUpperCase(str.charAt(i)))];
//		}

		return invertedStr.toString();
	} //end of invert


	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String tabulaRecta = "KRYPTOSABCDEFGHIJLMNQUVWXZ";
	private static final String keySquare = "KRYPT" +
																					"OSABC" +
																					"DEFGH" +
																					"ILMNQ" +
																					"UVWXZ";

	private static final String k4CipherText = "OBKRUOXOGHULBSOLIFBBWFLRVQQPRNGKSSOTWTQSJQSSEKZZWATJKLUDIAWINFBNYPVTTMZFPKWGDKZXTJCDIGKUHUAUEKCAR";

	protected static final HashMap<Character, Integer> alphaDistribMap(String str) {
		HashMap<Character, Integer> resultMap = new HashMap<Character, Integer>();

		// Initialize the map
		for (char c : alphabet.toCharArray()) {
			resultMap.put(c, 0);
		} //end of for

		// Populate the map
		for (char c : str.toUpperCase().toCharArray()) {
			resultMap.put(c, 1+resultMap.get(c));
		} //end of for

		return resultMap;
	} //end of alphaDistribMap

	protected static final String encipherKryptos(String plainText, String key) {
		StringBuffer cipherText = new StringBuffer();
		key = key.toUpperCase();

		int i = 0;
		for (char c : plainText.toUpperCase().toCharArray()) {
			if (tabulaRecta.indexOf(c) >= 0) {
				cipherText.append(tabulaRecta.charAt((tabulaRecta.indexOf(c) + tabulaRecta.indexOf(key.charAt(i % key.length()))) % 26));
				i++;
			} else {
				cipherText.append(c);
			} //end of else
		} // end of for

		return cipherText.toString();
	} //end of encipherKryptos

	protected static final String decipherKryptos(String cipherText, String key) {
		StringBuffer plainText = new StringBuffer();

		key = key.toUpperCase();

		int i = 0;
		for (char c : cipherText.toUpperCase().toCharArray()) {
			if (tabulaRecta.indexOf(c) >= 0) {
				plainText.append(tabulaRecta.charAt((26 + tabulaRecta.indexOf(c) - tabulaRecta.indexOf(key.charAt(i % key.length()))) % 26));
				i++;
			} else {
				plainText.append(c);
			} //end of else
		} //end of for

		return plainText.toString();
	} //end of decipherKryptos


	protected static final String encipherVigenere(String plainText, String key) {
		StringBuffer cipherText = new StringBuffer();
		key = key.toUpperCase();

		int i = 0;
		for (char c : plainText.toUpperCase().toCharArray()) {
			if (alphabet.indexOf(c) >= 0) {
				cipherText.append(alphabet.charAt((alphabet.indexOf(c) + alphabet.indexOf(key.charAt(i % key.length()))) % 26));
				i++;
			} else {
				cipherText.append(c);
			} //end of else
		} // end of for

		return cipherText.toString();
	} //end of encipherVigenere

	protected static final String decipherVigenere(String cipherText, String key) {
		StringBuffer plainText = new StringBuffer();

		key = key.toUpperCase();

		int i = 0;
		for (char c : cipherText.toUpperCase().toCharArray()) {
			if (alphabet.indexOf(c) >= 0) {
				plainText.append(alphabet.charAt((26 + alphabet.indexOf(c) - alphabet.indexOf(key.charAt(i % key.length()))) % 26));
				i++;
			} else {
				plainText.append(c);
			} //end of else
		} //end of for

		return plainText.toString();
	} //end of decipherVigenere


	protected static final String encipherBeaufort(String plainText, String key) {
		return reciprocalCipher(plainText, key);
	} //end of encipherBeaufort

	protected static final String decipherBeaufort(String cipherText, String key) {
		return reciprocalCipher(cipherText, key);
	} //end of decipherBeaufort

	private static final String reciprocalCipher(String origText, String key) {
		StringBuffer transformedText = new StringBuffer();
		key = key.toUpperCase();

		int i = 0;
		for (char c : origText.toUpperCase().toCharArray()) {
			if (alphabet.indexOf(c) >= 0) {
				transformedText.append(alphabet.charAt((26 + alphabet.indexOf(key.charAt(i % key.length())) - alphabet.indexOf(c)) % 26));
				i++;
			} else {
				transformedText.append(c);
			} //end of else
		} // end of for

		return transformedText.toString();
	} //end of reciprocalCipher



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
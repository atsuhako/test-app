package com.spawar.app;

//import edu.jhuapl.tinkerpop.*;
//import org.apache.commons.configuration.*;
//import com.tinkerpop.gremlin.structure.*;
//import com.tinkerpop.gremlin.structure.util.*;
	import com.tinkerpop.blueprints.Graph;
	import com.tinkerpop.blueprints.GraphFactory;

// 	import com.tinkerpop.gremlin.structure.Graph;
// 	import com.tinkerpop.gremlin.structure.util.GraphFactory;

	import org.apache.commons.configuration.Configuration;

	import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration;
	import edu.jhuapl.tinkerpop.AccumuloGraphConfiguration.InstanceType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
//		testAccumuloGraph();

		try {
			testRegex();
		} catch (Exception e) {
			e.printStackTrace();
		} //end of catch

		System.out.println("Hello World!");
		}

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

	private static void testRegex() throws Exception {
		System.out.println("newPath: " + localizeFilepath("\\\\cci-scanctr\\Release\\Navy\\Backfile\\1149\\SSMinnow\\Backfile\\2007\\122375\\Fuel1149_12345678901234_10.pdf"));

		System.out.println("newPath: " + localizeFilepath("\\\\cci-scanctr\\Release\\Navy\\Backfile\\Food\\SSMinnow\\Backfile\\2007\\122375\\FoodReceipt_987654_35400795.pdf"));

		System.out.println("newPath: " + localizeFilepath("\\\\cci-scanctr\\Release\\Navy\\Backfile\\PCards\\SSMinnow\\Backfile\\2007\\122375\\PCard_Purchase Files_12345_201310_5.pdf"));

		System.out.println("newPath: " + localizeFilepath("\\\\cci-scanctr\\Release\\Navy\\Backfile\\Misc\\SSMinnow\\Backfile\\2007\\122375\\FoodReceipt_VJ56852338907E_373.pdf"));
	} //end of testRegex

	private static String localizeFilepath(String networkFilepath) throws Exception {
		String localFilepath;

		try {
			String[] pathArr = networkFilepath.split("Backfile");
			localFilepath = "C:\\whatever\\" + pathArr[1].split("\\\\")[1] + pathArr[2];
		} catch (Exception e) {
			throw new Exception("Invalid filepath format <" + networkFilepath + ">");
		} //end of catch

		return localFilepath;
	} //end of localizeFilepath
} //end of class
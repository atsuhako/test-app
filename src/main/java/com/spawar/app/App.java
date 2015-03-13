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

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
    	Configuration cfg1 = new AccumuloGraphConfiguration().setInstanceType(InstanceType.Mock).setGraphName("graph");

Configuration cfg2 = new AccumuloGraphConfiguration()
  .setInstanceType(InstanceType.Distributed)
  .setZooKeeperHosts("localhost:2181")
  .setInstanceName("acc1")
  .setUser("acc1_user").setPassword("password")
  .setGraphName("graph")
  .setCreate(true);

//return GraphFactory.open(cfg);

			GraphFactory.open(cfg2);

      System.out.println( "Hello World!" );
    }
}

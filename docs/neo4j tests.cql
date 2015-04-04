java -classpath "C:\Program Files (x86)\Neo4j Community\bin\neo4j-desktop-2.2.0.jar" org.neo4j.shell.StartClient

CREATE (root { name: 'root' }) return root;

MATCH (a:Node { name: 'k4' }), (b:Node { name: 'a' })
CREATE (a)-[r:PARENT_OF]->(b)
RETURN r;

# Delete all nodes
START n = node(*)
OPTIONAL MATCH n-[r]-()
WHERE (ID(n)>0 AND ID(n)<10000)
DELETE n, r;

CREATE (root { name: 'root' })
RETURN root;

MATCH (root {name: 'root'})
CREATE UNIQUE (root)-[:PARENT_OF]->({name:'a'})
CREATE UNIQUE (root)-[:PARENT_OF]->({name:'b'})
CREATE UNIQUE (root)-[:PARENT_OF]->({name:'c'})
CREATE UNIQUE (root)-[:PARENT_OF]->({name:'d'})
CREATE UNIQUE (root)-[:PARENT_OF]->({name:'e'})
WITH root
MATCH (root)-[:PARENT_OF]->(leaves)
RETURN leaves;

MATCH (n { name: "Anders" })--(m)
WITH m
ORDER BY m.name DESC LIMIT 1
MATCH (m)--(o)
RETURN o.name


CREATE (root:Node { name: 'root' } )
CREATE (a:Node { name: 'a' } )
CREATE (b:Node { name: 'b' } )
CREATE (c:Node { name: 'c' } )
CREATE (aa:Node { name: 'aa' } )
CREATE (ab:Node { name: 'ab' } )
CREATE (ac:Node { name: 'ac' } )
CREATE (root)-[r1:PARENT_OF]->(a)
CREATE (root)-[r2:PARENT_OF]->(b)
CREATE (root)-[r3:PARENT_OF]->(c)
CREATE (a)-[r5:PARENT_OF]->(aa)
CREATE (a)-[r6:PARENT_OF]->(ab)
CREATE (a)-[r7:PARENT_OF]->(ac)
return root;

MATCH (n) return n;
MATCH (n { name: 'root' } ) return n;


MERGE (c1 { name: 'a' }) ON MATCH SET c1 += { processed: TRUE }
MERGE (c2 { name: 'c' }) ON MATCH SET c2 += { processed: TRUE }
WITH c1, c2

MATCH (n)
MERGE (c {name: 'c' }) WHERE ID(c) = 1 ON MATCH SET c += { processed: TRUE } RETURN c;


MATCH ({name: 'root'})-[:PARENT_OF]->(c) return c;

#find root
MATCH (r:ROOT{name: 'root'}) return r;
MATCH (r:ROOT{name: 'k4'}) return r;

#Get all level 1
MATCH (ROOT{name: 'root'})-[:PARENT_OF]->(c:KEY) return c;
MATCH (ROOT{name: 'k4'})-[:PARENT_OF]->(c:KEY)-[:PARENT_OF]->(gc:KEY) return gc;


MATCH (group { name: "root" })-[:contains]->(parent)-[:PARENT_OF]->(child)
WITH group, parent, child
MATCH (child)<-[:has_child]-(parent2)<-[:contains]-(group)
WHERE parent <> parent2
RETURN DISTINCT child

START n=node(0)
MATCH (n)-[r:PARENT_OF*0..2]-(m)
WITH m
RETURN m.key;

CREATE (n:ROOT { name: 'k6' } ) return n;
MATCH (n:ROOT{name: 'k5'}) return n;

MATCH (n:ROOT{name: 'k5'})
WITH n
MATCH (n)-[:PARENT_OF*0..2]-(c)
WITH c, 'a' + c.key AS key_str
RETURN c, key_str;

START n=node(1)
MATCH (n)-[:PARENT_OF*0..1]-(c)
WITH c
RETURN c;

#CREATE (n:ROOT { name: 'k7' } ) return n;
MATCH (r:ROOT{name: 'k4'})-[:PARENT_OF*1..2]->(c)
RETURN c;


CREATE (n:ROOT { name: 'n5' } ) return n;

MATCH (r:ROOT{name: 'n5'})
WITH r
CREATE UNIQUE (r)-[:PARENT_OF]->({key:'a'})
CREATE UNIQUE (r)-[:PARENT_OF]->({key:'b'})
CREATE UNIQUE (r)-[:PARENT_OF]->({key:'c'})
CREATE UNIQUE (r)-[:PARENT_OF]->({key:'d', processed:true})
WITH r
MATCH (r)-[:PARENT_OF*1..2]-(c)
RETURN c;


MATCH (r:ROOT{name: 'n5'}) RETURN r;
MATCH (r:id(178)) return r;
MERGE (c {name: 'c' }) WHERE ID(c) = 178 ON MATCH

MATCH (r:ROOT)-[:PARENT_OF*1..2]-(c)
WHERE id(r) = 178 AND (c.processed = false OR c.processed IS NULL)
RETURN c
LIMIT 2;

MATCH (r:ROOT)-[:PARENT_OF*1..99]-(c) WHERE id(r) = 178 AND (c.processed = false OR c.processed IS NULL) RETURN c
;
MATCH (:Key{processed: false}) RETURN count(1);
MATCH ({processed: false}) RETURN count(1);


 match (n:ROOT{name:"k4"})-[:PARENT_OF]-c return c

 CREATE INDEX ON :User(EmailAddress);
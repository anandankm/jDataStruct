jDataStruct
===========

Graphs:
Implementation to find friends and friend-of-a-friend (foaf) using Neo4j Java REST API.
Rest API is useful to load data into neo4j using hadoop and query neo4j from various
front facing servers.

* Download neo4j community version.
* Start neo4j server:  bin/neo4j start
* Load sample friends/foaf nodes into neo4j: mvn -Dtest=SampleNodeTest test
* Query sample friends/foaf nodes with Natural Ordering: mvn -Dtest=QueryProcessor test


Dependencies
============

Maven:
  * Maven version > 2.0

jUtils:
  * Clone git@github.com:anandankm/jUtils.git
     1. git clone git@github.com:anandankm/jUtils.git
  * Install jUtils:
     1. cd jUtils
     2. mvn install -DskipTests

Neo4j:
  * Neo4j version 1.9
  * Neo4j REST API version 1.9

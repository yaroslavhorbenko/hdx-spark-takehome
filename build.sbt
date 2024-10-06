name := "HdxTakeHomeProject"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.12.17"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.3"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.3"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "3.3.4"
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "13.1.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.23.1" % Runtime

run / fork := true

Compile/mainClass := Some("io.hydrolix.connectors.spark.TakeHomeSimpleApp")
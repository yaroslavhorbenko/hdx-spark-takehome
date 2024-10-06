package io.hydrolix.connectors.spark

import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SparkSession

object TakeHomeSimpleApp extends Logging {

  def main(args: Array[String]): Unit = {

    val SPARK_MASTER   = "local[*]"
    val SPARK_APP_NAME = "TakeHomeSimpleApp"
    val DATA_DIR       = "./data"

    logger.info("Configuring and running local Spark context")

    val spark = SparkSession
      .builder()
      .master(SPARK_MASTER)
      .appName(SPARK_APP_NAME)
      .config("spark.sql.catalog.hdx", "io.hydrolix.connectors.spark.HdxTakeHomeCatalog")
      .config("spark.sql.catalog.hdx.data.dir", DATA_DIR)
      .getOrCreate()

    val sc = spark.sparkContext
    sc.setLogLevel("WARN")

    logger.info("Fetching data via hdx.takehome.logs files connector...")

    spark.sql("SELECT * FROM hdx.takehome.logs where data LIKE '%partition2%'").show()
    spark.stop()
  }
}

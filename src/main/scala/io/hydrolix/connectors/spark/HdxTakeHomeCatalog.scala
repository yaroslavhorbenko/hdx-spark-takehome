package io.hydrolix.connectors.spark

import org.apache.spark.internal.Logging
import org.apache.spark.sql.connector.catalog._
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import java.util.Collections
import java.{util => ju}
import scala.collection.JavaConverters._
import io.hydrolix.connectors.spark.spark.nope

class HdxTakeHomeCatalog extends TableCatalog with SupportsNamespaces with Logging {

  private var tables: Map[Identifier, Table] = Map()

  override def initialize(name: String, options: CaseInsensitiveStringMap): Unit = {
    val logsIdentifier = Identifier.of(Array("takehome"), "logs")
    tables += (logsIdentifier -> HdxTakeHomeTable())
  }

  override def name(): String = "hdx"

  override def listTables(namespace: Array[String]): Array[Identifier] =
    tables.keys.toArray

  override def loadTable(ident: Identifier): Table =
    tables.getOrElse(ident, throw new IllegalArgumentException(s"Table ${ident.name()} not found."))

  override def listNamespaces(): Array[Array[String]] =
    Array(Array(name()))

  override def listNamespaces(namespace: Array[String]): Array[Array[String]] = Array()

  override def loadNamespaceMetadata(namespace: Array[String]): ju.Map[String, String] = Collections.emptyMap()

  //noinspection ScalaDeprecation
  override def createTable(
    ident: Identifier,
    schema: StructType,
    partitions: Array[Transform],
    properties: ju.Map[String, String]
  ): Table = nope()
  override def alterTable(ident: Identifier, changes: TableChange*): Table = nope()
  override def dropTable(ident: Identifier): Boolean = nope()
  override def renameTable(oldIdent: Identifier, newIdent: Identifier): Unit = nope()
  override def createNamespace(namespace: Array[String], metadata: ju.Map[String, String]): Unit = nope()
  override def alterNamespace(namespace: Array[String], changes: NamespaceChange*): Unit = nope()
  override def dropNamespace(namespace: Array[String], cascade: Boolean): Boolean = nope()
}

package object spark {
  def nope() = throw new UnsupportedOperationException("HydrolixTakeHome connector is read-only")
}

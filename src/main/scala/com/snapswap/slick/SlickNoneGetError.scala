package com.snapswap.slick

import com.typesafe.config.ConfigFactory
import slick.jdbc.H2Profile.api._
import slick.lifted.MappedProjection

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.control.NonFatal

object SlickNoneGetError extends App {

  case class ErrorInfo(code: String)

  case class Action(id: String, `type`: String, error: Option[ErrorInfo])

  class ActionsTable(tag: Tag) extends Table[Action](tag, "actions") {
    def id = column[String]("id", O.PrimaryKey)

    def `type` = column[String]("type")

    def errorCode = column[Option[String]]("error_code")

    def mappedError: MappedProjection[Option[ErrorInfo], Option[String]] = errorCode.<>({
      case Some(code) =>
        Some(ErrorInfo(code))
      case None =>
        None
    }, {
      case Some(ErrorInfo(code)) =>
        Some(Some(code))
      case _ =>
        None
    })

    def * = (id, `type`, mappedError) <> (Action.tupled, Action.unapply)
  }


  val config = ConfigFactory.parseString(
    s"""
       |h2mem1 = {
       |  url = "jdbc:h2:mem:test1"
       |  driver = org.h2.Driver
       |  connectionPool = disabled
       |  keepAliveConnection = true
       |}
     """.stripMargin)
  val db = Database.forConfig("h2mem1", config = config)
  val actions = TableQuery[ActionsTable]

  val a1 = Action("01", "something", Some(ErrorInfo("Boom!")))
  val a2 = Action("01", "something", None)

  val result = (for {
  // Create schema
    _ <- db.run(actions.schema.create)
    // Insert to table processed without error
    _ <- db.run(actions.insertOrUpdate(a1))
    // Insert to table failed with None.get error
    _ <- db.run(actions.insertOrUpdate(a2))
    // close connection
    _ = db.close()
  } yield ()).recover {
    case NonFatal(ex) =>
      println(ex)
      throw ex
  }


  Await.result(result, 10.seconds)
}
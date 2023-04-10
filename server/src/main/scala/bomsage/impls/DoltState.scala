package bomsage.impls

import net.liftweb.mapper.DB
import net.liftweb.db.StandardDBVendor
import net.liftweb.util.Props
import net.liftweb.http.LiftRules
import net.liftweb._
import mapper._
import bomsage.abstractions.State
import net.liftweb.common.Box
import bomsage.abstractions.{Operation => AOperation}
import bomsage.abstractions.OperationStates

object DoltState extends State[Long] {

  override def getOperation(operationId: String): Box[AOperation] = ???

   def putOperation(operationId: String, state: OperationStates.Value): Box[Long] = ???

  def setupDB(): Unit = {

    val vendor =
      new StandardDBVendor(
        Props.get("db.driver") openOr "org.h2.Driver",
        Props.get("db.url") openOr
          "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        Props.get("db.user"),
        Props.get("db.password")
      )

    LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

    DB.defineConnectionManager(mapper.DefaultConnectionIdentifier, vendor)

    Schemifier.schemify(true, Schemifier.infoF _, Operation)
  }
}

object Operation extends Operation with LongKeyedMetaMapper[Operation] {}

class Operation extends LongKeyedMapper[Operation] with IdPK with CreatedTrait with AOperation {

  override def operationId: String = hash.get

  override def operationState: OperationStates.Value = ???

  def getSingleton = Operation

  object hash extends MappedPoliteString(this, 1024) {
    override def dbIndexed_? : Boolean = true
  }

  object status extends MappedEnum(this, OperationStates) {
    override def defaultValue: OperationStates.Value = OperationStates.Running

  }

}

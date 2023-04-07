package bomsage.impls

import net.liftweb.mapper.DB
import net.liftweb.db.StandardDBVendor
import net.liftweb.util.Props
import net.liftweb.http.LiftRules
import net.liftweb._
import mapper._

object DoltState {
  def setupDB(): Unit = {

    val vendor =
      new StandardDBVendor(
        Props.get("db.driver") openOr "org.h2.Driver",
        Props.get("db.url") openOr
          "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        Props.get("db.user"),
        Props.get("db.password")
      )

      println(f"Vendor ${vendor}")
    LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

    DB.defineConnectionManager(mapper.DefaultConnectionIdentifier, vendor)

    Schemifier.schemify(true, Schemifier.infoF _, Submitted)
  }
}

object Submitted extends Submitted with LongKeyedMetaMapper[Submitted] {}

class Submitted extends LongKeyedMapper[Submitted] with IdPK with CreatedTrait {
  def getSingleton = Submitted

  object hash extends MappedPoliteString(this, 1024) {
    override def dbIndexed_? : Boolean = true
  }

}

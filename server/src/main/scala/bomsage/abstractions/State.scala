package bomsage.abstractions

import net.liftweb.common.Box

trait State {
    def getOperation(operationId: String): Box[Operation]
}

case class Operation(id: String, status: String ) {
    def running_? : Boolean = status == "running"
}
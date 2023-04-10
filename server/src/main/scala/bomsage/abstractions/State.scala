package bomsage.abstractions

import net.liftweb.common.Box

/**
  * Manage the state of Sage/SCITT "stuff"
  */
trait State[PrimaryKeyType] {

    /**
      * For a given `operationId` get the most recent operations
      *
      * @param operationId the operationId to fetch
      * @return the most recent operation
      */
    def getOperation(operationId: String): Box[Operation]

    def putOperation(operationId: String, state: OperationStates.Value): Box[PrimaryKeyType]
}


object OperationStates extends Enumeration {
    type OperationStates = Value 
    val Stored, Prevalidated, Submitted, Running, Validated, Rejected = Value
}

/**
  * An operation and it's status
  */
trait Operation {
    /**
      * The ID of the operation... typically the hash or IPFS locator associated
      * with the thing being operated on
      *
      * @return the identifier for the operation
      */
    def operationId: String

    /**
      * the status of the operation
      *
      * @return a string representing the status of the operation
      */
    def operationState: OperationStates.Value

    /**
      * Is the operation status "running"?
      *
      * @return true if the status is "running"
      */
    def running_? : Boolean = operationState == OperationStates.Submitted
}

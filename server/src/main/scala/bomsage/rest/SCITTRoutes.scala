package bomsage.rest

import net.liftweb.http.rest.RestHelper
import net.liftweb.http.Req
import net.liftweb.http.S
import net.liftweb.common.Full
import net.liftweb.http.JsonResponse
import net.liftweb.actor.LAFuture
import net.liftweb.common.{Box, Full, Empty}
import bomsage.abstractions.Storage
import bomsage.abstractions.State
import net.liftweb.http.LiftResponse
import bomsage.abstractions.StorageLocation
import net.liftweb.common.Failure
import net.liftweb.http.PlainTextResponse
import net.liftweb.http.InMemoryResponse
import bomsage.scitt.SCITTService

class SCITTRoutes[PrimaryKeyType](
    val storage: Storage,
    val state: State[PrimaryKeyType],
    val service: SCITTService[PrimaryKeyType]
) extends RestHelper {
  serve {
    case "entries" :: entryId :: "receipt" :: Nil Get _ => receiptFor(entryId)

    case "entries" :: entryId :: Nil Get _ => claimFor(entryId)

    case "entries" :: Nil Post body => submitClaim(body.contentType, body.body)

    case "operations" :: operationId :: Nil Get _ => ???
  }

  def receiptFor(entryId: String): LAFuture[Box[JsonResponse]] = ???

  def claimFor(entryId: String): LAFuture[Box[LiftResponse]] = {
    LAFuture.build {
      storage.retrieve(StorageLocation(entryId)) match {
        case Full(v) =>
          Full(
            InMemoryResponse(
              v.array,
              ("Content-Type" -> "application/octet-stream") :: ("Content-Length" -> v.array.length.toString) :: ("Content-Disposition" -> f"attachment; filename=\"${entryId}.cose\"") :: Nil,
              Nil,
              200
            )
          )
        case Empty =>
          Full(PlainTextResponse(f"Entry ID not found: ${entryId}", 404))
        case f: Failure => Full(PlainTextResponse(f.msg, 500))
      }
    }
  }

  def submitClaim(
      contentType: Box[String],
      body: Box[Array[Byte]]
  ): LAFuture[Box[JsonResponse]] = ???
}

package bomsage.cose

import java.io.File
import net.liftweb.common.Box
import net.liftweb.util.Helpers
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import java.io.FileOutputStream
import bomsage.util.SageUtil
import net.liftweb.common.Empty
import net.liftweb.common.Full
import COSE.{Message, Sign1Message, HeaderKeys}
import net.liftweb.common.Failure
import com.upokecenter.cbor.CBORType
import bomsage.abstractions.Storage
import bomsage.abstractions.{State, Operation}
import bomsage.abstractions.Validation
import bomsage.abstractions.Ledger

object SCITTServiceEmulator {
  // temporary claim header labels, see draft-birkholz-scitt-architecture
  val COSE_Headers_Issuer = 391
}

/** The SCITT Service emulator abstract class. Derived from
  * https://github.com/scitt-community/scitt-api-emulator/blob/main/scitt_emulator/scitt.py
  *
  * @param serviceParameterPath
  * @param storagePath
  */
abstract class SCITTServiceEmulator(
    val storage: Storage,
    val state: State,
    val validation: Validation,
    val ledger: Ledger
) {
 
  def initializeService(): Unit

  def createReceiptContents(countersignTbi: Array[Byte], entryId: String): Unit

  def verifyReceiptContents(
      receiptLontents: List[String],
      countersign_tbi: Array[Byte]
  ): Unit

  def getOperation(operationId: String): Box[Operation] = {
    for {
      operation <- state.getOperation(operationId)
      toReturn <- if (operation.running_?) finishOperation(operation) else Full(operation)
    } yield toReturn

  }

  protected def createEntry(claim: Array[Byte]): Box[JValue] = synchronized {
    Empty // FIXME
    // for {
    //   theStoragePath <- storagePath
    //   lastEntryPath = new File(theStoragePath, "last_entry_id.txt")
    //   lastEntryId = Helpers
    //     .tryo {
    //       val bytes = Helpers.readWholeFile(lastEntryPath)
    //       Integer.valueOf(new String(bytes)).intValue()
    //     }
    //     .openOr(0)
    //   entryId = f"${lastEntryId + 1}"
    //   _ <- createReceipt(claim, entryId)
    //   _ <- SageUtil.writeFile(lastEntryPath, entryId)
    //   _ <- SageUtil.writeFile(
    //     new File(theStoragePath, f"${entryId}.cose"),
    //     claim
    //   )
    // } yield ("entryId" -> entryId)

  }

  protected def createReceipt(claim: Array[Byte], entryId: String): Box[Unit] = {
    def validateMessage(in: Message): Box[Message] = {
      if (!in.isInstanceOf[Sign1Message])
        Failure("Claim is not a COSE_Sign1 message")
      else if (!in.getProtectedAttributes().ContainsKey(HeaderKeys.Algorithm))
        Failure("Claim does not have an algorithm header parameter")
      else if (
        !in.getProtectedAttributes().ContainsKey(HeaderKeys.CONTENT_TYPE)
      ) Failure("Claim does not have a content type header parameter")
      else if (
        !in
          .getProtectedAttributes()
          .ContainsKey(SCITTServiceEmulator.COSE_Headers_Issuer)
      ) Failure("Claim does not have an issuer header parameter")
      else if (
        ! {
          val theType = in
            .getProtectedAttributes()
            .get(SCITTServiceEmulator.COSE_Headers_Issuer)
            .getType()
          theType == CBORType.TextString || theType == CBORType.ByteString
        }
      ) Failure("Claim issuer is not a string")
      else Full(in)
    }

    for {
      message <- Helpers.tryo { Message.DecodeFromBytes(claim) }
      message <- validateMessage(message)
    } yield ()
  }

  protected def finishOperation(operation: Operation): Box[Operation] = {
    Empty
    //    for {
    //     JString(operationId) <- operation \ "operationId"
    //     operationPath = new File(operationsPath, f"${operationId}.json")
    //     claimSrcPath = new File(operationsPath, f"${operationId}.cose")
    //     claim <- Helpers.tryo(Helpers.readWholeFile(claimSrcPath))
    //     entry <- createEntry(claim)
    //     _ = claimSrcPath.delete()
    //    }
    // operation_id = operation["operationId"]
    // operation_path = self.operations_path / f"{operation_id}.json"
    // claim_src_path = self.operations_path / f"{operation_id}.cose"

    // claim = claim_src_path.read_bytes()
    // entry = self._create_entry(claim)
    // claim_src_path.unlink()

    // operation["status"] = "succeeded"
    // operation["entryId"] = entry["entryId"]

    // with open(operation_path, "w") as f:
    //     json.dump(operation, f)

    // return operation
  }
}

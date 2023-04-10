package bomsage.abstractions

import net.liftweb.common.Box
import net.liftweb.util.Helpers
import COSE.Message
import COSE.Sign1Message
import net.liftweb.common.Failure
import COSE.HeaderKeys
import com.upokecenter.cbor.CBORType
import net.liftweb.common.Full

/** A `trait` that performs SCITT validation and other COSE operations
  */
trait Validation {

  /** temporary claim header labels, see draft-birkholz-scitt-architecture
    *
    * @return
    *   391
    */
  def COSE_Headers_Issuer = 391;

  /** Take a raw pile of bytes an attempt to turn it into a COSE message
    *
    * @param raw
    *   the bytes
    * @return
    *   `Full` if there's a valid COSE message
    */
  def unpackMessage(raw: Array[Byte]): Box[Message] = Helpers.tryo {
    Message.DecodeFromBytes(raw)
  }

  /**
    * Performs some basic validation of the message:
    *        * Is it signed by a single signer
    *        * Does it specify the algorithm
    *        * Does it have a content type
    *        * 
    *
    * @param msg
    * @return
    */
  def basicValidation(msg: Message): Box[Message] = {
    val protectedAttributes = msg.getProtectedAttributes()

    if (!msg.isInstanceOf[Sign1Message])
      Failure("Claim is not a COSE_Sign1 message")
    else if (!protectedAttributes.ContainsKey(HeaderKeys.Algorithm))
      Failure("Claim does not have an algorithm header parameter")
    else if (!protectedAttributes.ContainsKey(HeaderKeys.CONTENT_TYPE))
      Failure("Claim does not have a content type header parameter")
    else if (
      !protectedAttributes
        .ContainsKey(COSE_Headers_Issuer)
    ) Failure("Claim does not have an issuer header parameter")
    else if (
      ! {
        val theType = protectedAttributes
          .get(COSE_Headers_Issuer)
          .getType()
        theType == CBORType.TextString || theType == CBORType.ByteString
      }
    ) Failure("Claim issuer is not a string")
    else Full(msg)

  }
}

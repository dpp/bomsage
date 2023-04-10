package bomsage.impls

import bomsage.abstractions.Cryptography
import net.liftweb.common.Box
import java.security.KeyPair
import net.liftweb.util.Helpers
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.security.SecureRandom

object CryptographHelper {

  /** Generate an EC/secp256r1 keypair
    *
    * @return
    *   the new keypair
    */
  def generateKeyPair(): Box[KeyPair] = {
    Helpers.tryo {
      val keyGen = KeyPairGenerator.getInstance("EC")
      keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom())
      val keyPair = keyGen.generateKeyPair()
      keyPair
    }
  }


}

class DevelopmentCryptography extends Cryptography {}

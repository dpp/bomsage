package bomsage.util
import io.ipfs.cid.Cid
import net.liftweb.util.Helpers

class SageUtilSpec extends munit.FunSuite {
  test("IPFS decode") {
   
    val cid = Cid.decode("QmPNfNBDeV5SLmbRjnpNjM9B6Upt9AakZ23aaJg8bbXA9x")
    assertEquals(cid.codec.`type`, 112L)
    assertEquals(cid.version, 0L)
   
  }
}

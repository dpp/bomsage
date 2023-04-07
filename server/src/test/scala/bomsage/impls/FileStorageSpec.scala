package bomsage.impls

import java.io.ByteArrayOutputStream
import net.liftweb.util.Helpers
import net.liftweb.common.{Box, Full, Empty, Failure}

class FileStorageSpec extends munit.FunSuite {
  val store = new FilesystemStore(Empty)

  DoltState.setupDB()
  
  test("Read and write a file") {
    val im = new ByteArrayOutputStream()
    for {
      _ <- 0 to (1000 + Helpers.randomInt(200))
    } im.write(Helpers.randomString(Helpers.randomInt(500) + 100).getBytes())
    im.close()
    var data = im.toByteArray()

    val res = store.store(data).openOrThrowException("This data should be returned")

    val otherData = store.retrieve(res).openOrThrowException("The file should be returned")
assertNotEquals(data, otherData)
  }
}

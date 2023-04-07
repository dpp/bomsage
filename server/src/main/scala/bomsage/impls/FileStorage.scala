package bomsage.impls

import bomsage.abstractions.{Storage, StorageLocation}
import net.liftweb.common.{Box, Full, Empty, Failure}
import java.io.File
import net.liftweb.util.{Props, Helpers}
import java.io.FileOutputStream


class FilesystemStore(val root: Box[File]) extends Storage {

  val rootDir = root.openOr({
    val f = new File(Props.get("storage.root", "/tmp/sage_store"))
    f.mkdirs()
    f
  })

  override def store(bytes: Array[Byte]): Box[StorageLocation] = {
    val hash = Helpers.hash256(bytes)
    val hex = Helpers.hexEncode(hash)
    val f = new File(rootDir, hex)
    
    Helpers.tryo {
      val fos = new FileOutputStream(f)
      fos.write(bytes)
      fos.close()
      StorageLocation(hex)
    }
  }

  override def retrieve(location: StorageLocation): Box[Array[Byte]] = {
    val f = new File(rootDir, location.hash)
    if (!f.exists()) Failure(f"File for hash ${location.hash} does not exist")
    Helpers.tryo {
      val bytes = Helpers.readWholeFile(f)
      bytes
    } match {
      case Full(bytes) =>
        if (Helpers.hexEncode(Helpers.hash256(bytes)) != location.hash)
          Failure(f"The file at ${location.hash} has been tampered with")
        else Full(bytes)
      case x => x
    }
  }

}

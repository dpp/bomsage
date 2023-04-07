package bomsage.abstractions

import net.liftweb.common.{Full, Empty, Failure, Box}
import java.io.File
import net.liftweb.util.Props
import net.liftweb.util.Helpers
import java.io.FileOutputStream

trait Storage {

  /** Store some bytes
    *
    * @param bytes
    *   the bytes to store
    * @return
    *   the location of the bytes
    */
  def store(bytes: Array[Byte]): Box[StorageLocation]

  /** Retrieve bytes based on location
    *
    * @param location
    * @return
    */
  def retrieve(location: StorageLocation): Box[Array[Byte]]
}

/** The location of a file
  *
  * @param hash
  *   the hash of the location. If it's IPFS-based the IPFS hash. If filesystem
  *   backed, the SHA256 of the file
  */
case class StorageLocation(hash: String)


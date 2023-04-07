package bomsage.util

import java.io.File
import net.liftweb.util.Helpers
import java.io.FileOutputStream
import net.liftweb.common.Box

/**
  * A bunch of helper methods
  */
object SageUtil {
    /**
      * Write data to a file
      *
      * @param path the `File` to write to (deleted before writing begins)
      * @param data the String to write
      * 
      * @return a `Box` which will be a `Failure` if there was an exception
      */
    def writeFile(path: File, data: String): Box[Unit] = {
        for {
            bytes <- Helpers.tryo(data.getBytes("UTF-8"))
            res <- writeFile(path, bytes)
        } yield res
    }

        /**
      * Write data to a file
      *
      * @param path the `File` to write to (deleted before writing begins)
      * @param data the String to write
      * 
      * @return a `Box` which will be a `Failure` if there was an exception
      */
    def writeFile(path: File, data: Array[Byte]): Box[Unit] = {
        Helpers.tryo{
            path.delete()
            val fos = new FileOutputStream(path)
            fos.write(data)
            fos.close()
        }
    }

    def sha256AsIPFS(bytes: Array[Byte]): String = {
      val rawHash = Helpers.hash256(bytes)
      "FIXME"
    }
}

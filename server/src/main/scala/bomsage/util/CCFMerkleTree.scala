package bomsage.util

import net.liftweb.util.Helpers
import java.util.concurrent.atomic.AtomicReference
import net.liftweb.common.Box
import net.liftweb.common.Empty
import net.liftweb.common.Full

/**
  * A CCF Merkle tree based on the implementation at
  * https://github.com/scitt-community/scitt-api-emulator/blob/main/scitt_emulator/ccf.py
  * 
  * Thread safe an non-blocking
  *
  */
class CCFMerkleTree {
    // keep the leaves in an atomic reference
  private val leaves: AtomicReference[Vector[Array[Byte]]] =
    new AtomicReference(Vector())

    // keep the levels and the leaves used to compute the levels
    // in an atomic reference
  private val levels: AtomicReference[
    (Box[Vector[Array[Byte]]], Vector[Vector[Array[Byte]]])
  ] = new AtomicReference((Empty, Vector()))

  /**
    * Add a node to the tree
    *
    * @param data the bytes to hash or directly add 
    * @param shouldHash_? true if the bytes should be hashed
    */
  def addLeaf(data: Array[Byte], shouldHash_? : Boolean): Unit = {
    val toAdd = if (shouldHash_?) Helpers.hash256(data) else data

    leaves.getAndUpdate(v => v :+ toAdd)
  }

  /**
    * Get a leaf hash
    *
    * @param index the index of the leaf
    * @return the hash value of the specific leaf
    */
  def getLeaf(index: Int): Array[Byte] = leaves.get()(index)

  /**
    * Get the number of leaves
    *
    * @return the number of leaves
    */
  def getLeafCount(): Int = leaves.get().length

  /**
    * Get the root hash of the Merkle tree
    *
    * @return the root hash of the Merkle tree
    */
  def getMerkleRoot(): Array[Byte] = {
    updateTree()
    levels.get()._2(0)(0)
  }

  /**
    * Given a set of hashed, compute the next level up in the tree
    *
    * @param in the Vector of the current level
    * @return a tree with the hash of the `in` items
    */
  private def calculateNextLevel(
      in: Vector[Vector[Array[Byte]]]
  ): Vector[Vector[Array[Byte]]] = {
    var soloLeaf: Box[Array[Byte]] = Empty
    val top = in(0)
    var numOnLevel = top.length
    if (numOnLevel == 1)
      throw new Exception(
        "Merkle tree failure... must have more than one leaf on every level"
      )

    if (numOnLevel % 2 == 1) {
      soloLeaf = Full(top.last)
      numOnLevel = numOnLevel - 1
    }

    var newLevel: Vector[Array[Byte]] = Vector()

    for { i <- 0 until numOnLevel by 2 } {
      newLevel = newLevel :+ Helpers.hash256(top(i) ++ top(i + 1))
    }

    soloLeaf match {
      case Full(v) => newLevel = newLevel :+ v
      case _       =>
    }

    newLevel +: in
  }

  /**
    * Update the tree. If the leaves have changed since the tree was last computed,
    * 
    */
  private def updateTree(): Unit = {
    val now = leaves.get()
    levels.get() match {
      case (Full(x), _)
          if x eq now => // do nothing... the leaves haven't changed since last computed
      case _ =>
        var base = Vector(now)

        while (base(0).length > 1)
          base = calculateNextLevel(base)

        levels.set((Full(now), base))
    }
  }

}

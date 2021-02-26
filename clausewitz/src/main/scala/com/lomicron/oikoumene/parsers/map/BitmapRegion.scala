package com.lomicron.oikoumene.parsers.map

/**
  * Represents a 2D region (blob, connected component) discovered
  * by BitmapGrouper. Encapsulates region id and its starting point
  * (top by y leftmost point - i.e. the first encountered during
  * labeling).
  *
  * @param id region id
  * @param x starting point x coordinate
  * @param y starting point y coordinate
  */
case class BitmapRegion(id: Int, x: Int, y: Int)

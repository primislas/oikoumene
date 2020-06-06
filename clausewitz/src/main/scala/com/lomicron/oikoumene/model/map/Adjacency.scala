package com.lomicron.oikoumene.model.map

case class Adjacency
(
  from: Int,
  to: Int,
  `type`: String,
  through: Int,
  startX: Int,
  startY: Int,
  stopX: Int,
  stopY: Int,
  comment: String
)

package com.lomicron.oikoumene.engine

import com.lomicron.utils.io.Identifier
import com.lomicron.utils.io.Tokenizer
import com.lomicron.utils.io.IO
import scala.io.Source
import scala.collection.SortedSet
import com.lomicron.utils.io.ProvinceHistoryParser
import com.lomicron.utils.io.Token
import com.lomicron.utils.io.Property
import com.lomicron.utils.io.Comment

object Oikoumene {
  def main(args: Array[String]) {
    println("Starting the known world...")
    println(System.getProperty("user.dir"))
    println("Loading provinces...")
    val map = MapLoader.loadMap("./resources/provinces.bmp")
    val tiles = map._1
    val routes = map._2
    println("Loaded " + tiles.size + " tiles, :" + tiles)
    val l: List[Int] = Nil
    
    val s = IO.readTextFile("./resources/151 - Constantinople.txt").toCharArray().toStream
    val tokens = Tokenizer.tokenize(s)
    tokens.foreach(println)
    //val uniqueIds = tokens.filter(_.isInstanceOf[Identifier]).map(_.toString).toSet
    //println(s"${uniqueIds.size} unique identifiers:")
    //(SortedSet[String]() ++ uniqueIds).foreach(println)
    
    val isComment: Token => Boolean = _.isInstanceOf[Comment]
    val lexTokens = ProvinceHistoryParser.parse(tokens.filter(!isComment(_)).toList)
    lexTokens.foreach(println)

    val isProperty: Token => Boolean = _.isInstanceOf[Property]
    val uniqueProps = SortedSet[String]() ++ lexTokens.filter(isProperty).map(_.toString)
    println(s"${uniqueProps.size} unique properties:")
    uniqueProps.foreach(println)

    val isIdentifier: Token => Boolean = _.isInstanceOf[Identifier]
    val uniqueIds = SortedSet[String]() ++ lexTokens.filter(isIdentifier).map(_.toString)
    println(s"${uniqueIds.size} unique values:")
    uniqueIds.foreach(println)
    println("Bye")
  }
  
  
}
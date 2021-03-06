package ru.dijkstra

import scala.collection.mutable.ListBuffer
import scalax.file.{PathMatcher, Path}
import scalax.file.PathMatcher.{GlobNameMatcher, GlobPathMatcher}

case class Options(masks: List[String], options: Map[String, String])

object UtfChecker {
  def parseArgs(args: List[String]) = {
    var options = Map[String, String]()
    var masks = ListBuffer[String]()
    args.foreach { arg =>
      arg match {
        case option if option.startsWith("-") => {
          if (option.contains("=")) {
            val key = option.substring(if (option.startsWith("--")) 2 else 1, option.indexOf("="));
            val value = option.substring(option.indexOf("=") + 1, option.length())
            if (key.length() == 0 || value.length() == 0)
              throw new Exception("Malformed parameter: " + option)
            options += (key -> value)
          } else {
            val key = option.substring(if (option.startsWith("--")) 2 else 1, option.length());
            options += (key -> "")
          }
        }
        case mask => masks += mask
      }
    }
    Options(masks.toList, options)
  }

  def showHelp () {
    println("Checks specified files for UTF8 compability and finds BOMs")
    println("Usage: UTFChecker [options] [masks]")
    println("e.g. UTFChecker --dir=\"/dev/prog number one\" *.h *.cpp *.java")
    println("     UTFChecker --dir=UtfChecker.scala")
    println("     UTFChecker *.sh")
    println("Options")
    println("  --help — show this message")
    println("  --dir  — specify file or directory to check. Default:'--dir=.'")
    println("Use DOS-like masks like '*.*', '*.h', 'p??o.txt'")
    println("Masks list is whitespace-separated. Subdirectories are checked recursively")
  }

  def matcher(masks: List[String]) = masks map { m => new GlobNameMatcher(m) : PathMatcher } reduceLeft(_ || _)

  def main(args: Array[String]) {
    import CheckerOperations.checkFile
    val opt = parseArgs(args.toList)
    if (opt.options.contains("help") || ((opt.options.size == 0) && (opt.masks.size == 0))) {
      showHelp()
      sys.exit()
    }
    val dir = opt.options.get("dir") match {
      case Some(x) => x
      case None => "."
    }
    val path = Path(dir)
    if (path.isFile) {
      val res = checkFile(path)
      if (res.isValid)
        println(path.path + " is valid")
      else
        println(path.path + " is invalid")
      if (res.isBOM)
        println(path.path + " has BOM")
    }
    else {
      var BOMs = 0
      var invalidFiles = 0
      var total = 0
      path.descendants(filter = matcher(opt.masks)).filter(_.isFile) foreach ( file => {
        val res = checkFile(file)
        if (res.isBOM) {
          BOMs += 1
          println(file.path + " has BOM")
        }
        if (!res.isValid) {
          invalidFiles += 1
          println(file.path + " is not utf8-correct")
        }
        total += 1
      })
      if (invalidFiles == 0)
        println("All specified files were valid")
      else println("Total invalid files: " + invalidFiles)
      if (BOMs == 0)
        println("No files with BOM found")
      else println("Total files with BOM: " + invalidFiles)
      if (total != 0) {
        println("Checked %d files".format(total))
      }
    }
  }
}

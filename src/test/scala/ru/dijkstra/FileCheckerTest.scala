package ru.dijkstra

import org.scalatest.FreeSpec
import org.scalatest.matchers.ShouldMatchers
import scalax.file.Path


class FileCheckerTest extends FreeSpec with ShouldMatchers {
  import CheckerOperations.checkFile
  "FileCheckerTest" - {
    "  Canonical mock" - {
      val file = Path("src\\test\\resources\\mock")
      val res = checkFile(file)
      " BOM" in {
        res.isBOM should be === true
      }
      " Valid " in {
        res.isValid should be === true
      }
    }
    "  12345 file" - {
      val file = Path("src\\test\\resources\\mock5k")
      val res = checkFile(file)
      " BOM" in {
        res.isBOM should be === false
      }
      " Valid " in {
        res.isValid should be === true
      }
    }
    "  Valid no BOM file" - {
      val file = Path("src\\test\\resources\\test_file")
      val res = checkFile(file)
      " BOM" in {
        res.isBOM should be === false
      }
      " Valid " in {
        res.isValid should be === true
      }
    }
    "  cp1251.txt" - {
      val file = Path("src\\test\\resources\\cp1251.txt")
      val res = checkFile(file)
      " BOM" in {
        res.isBOM should be === false
      }
      " Valid " in {
        res.isValid should be === false
      }
    }
    "  kanji.txt" - {
      val file = Path("src\\test\\resources\\漢字.txt")
      val res = checkFile(file)
      " BOM" in {
        res.isBOM should be === true
      }
      " Valid " in {
        res.isValid should be === true
      }
    }

    "   tekisuto.txt" - {
      val file = Path("src\\test\\resources\\tekisuto.txt")
      val res = checkFile(file)
      " BOM" in {
        res.isBOM should be === false
      }
       " Valid " in {
        res.isValid should be === true
      }
    }

  }

}

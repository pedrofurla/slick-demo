package slickdemo
package test

import Main._
import org.specs2.mutable.Specification
import slickdemo.dal.SlickSupport

class TemplateForASpec extends Specification {
    "someMethod " should { // TODO Still misses many tests

      "do something" in {
        def separator = { println; SlickSupport.printSpacer(); println }
        val empty = Array.empty[String]
        Main.main(empty)
        separator
        AdHocQueries.main(empty)
        separator
        SqlQueries.main(empty)

        true
      }

    }

}
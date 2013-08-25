
import org.scalatest.FunSuite

import reflect.runtime.universe._

import scalaz.Lens

case class Outer(name: String, inner: Inner)
case class Inner(member: String)



class LenzerTest extends FunSuite {

    ignore("learning compiler AST") {
        println (showRaw(reify{

            Lens.lensu(
                get = (p: Outer) => p.inner,
                set = (p: Outer, a: Inner) => p.copy(inner = a)
            )
        }.tree))
    }

    test("single field lens") {
        val t = Outer(name = "name", inner = Inner("a"))

        val innerL = Lenzer.forField[Outer, Inner]("inner")
        val nameL = Lenzer.forField[Outer, String]("name")

        assert(innerL.set(t, Inner("b")).inner.member === "b")
        assert(nameL.set(t, "other name").name === "other name")

        assert(nameL.mod(n => n + " mod", t).name === "name mod")
    }

}

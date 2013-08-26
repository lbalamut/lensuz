package lensuz


import org.scalatest.FunSuite

import reflect.runtime.universe._

import scalaz.Lens
import scalaz.LensFamily
import scala.language.reflectiveCalls

case class Outer(name: String, inner: Inner)

case class Inner(member: String)


class LenzerTest extends FunSuite {

    ignore("learning compiler AST") {
        println(showRaw(reify {
            new Object {
//                val inner = Lens.lensu(
//                    val get: (Outer) => Inner = (p: Outer) => p.inner
//                    val set: (Outer, Inner) => Outer = (p: Outer, a: Inner) => p.copy(inner = a)
//                    def nameGetF(p: lensuz.Outer): String = p.name
//                    def nameSetF(p: lensuz.Outer, i: lensuz.Inner): Outer = p.copy(inner = i)
                     def a: Lens[Outer, Inner] = null
//                )
            }
        }.tree))

    }

    test("single field lens") {
        val t = Outer(name = "name", inner = Inner("a"))

        val innerL = Lenzer.forField[Outer, Inner]("inner")
        val nameL: Lens[Outer, String] = Lenzer.forField[Outer, String]("name")

        assert(innerL.set(t, Inner("b")).inner.member === "b")
        assert(nameL.set(t, "other name").name === "other name")

        assert(nameL.mod(n => n + " mod", t).name === "name mod")
    }

    test("one big lenser") {
        val t = Outer(name = "name", inner = Inner("a"))

        val nameL = Lenzer.forAllFields[Outer].name

        assert(nameL.set(t, "other name").name === "other name")
        assert(nameL.mod(n => n + " mod", t).name === "name mod")

        val innerL = Lenzer.forAllFields[Outer].inner
        assert(innerL.set(t, Inner("b")).inner.member === "b")
    }

}



import language.experimental.macros

import scalaz.Lens

object Lenzer {
    def forField[S, T](fieldName: String): Lens[S, T] = macro LenzerMacros.forField[S, T]

}

object LenzerMacros {

    def forField[S: c.WeakTypeTag, T: c.WeakTypeTag]
            (c: scala.reflect.macros.Context)
            (fieldName: c.Expr[String]): c.Expr[scalaz.Lens[S, T]] = {

        import c.universe._

        def abort(reason: String) = c.abort(c.enclosingPosition, reason)

        val lens = (c.prefix.tree, fieldName.tree) match {
            case (x, Literal(Constant(name: String))) =>
                val sourceT = c.weakTypeOf[S]

                val mamber = sourceT.member(newTermName(name)) orElse {
                    abort(s"value $name is not a member of $sourceT")
                }

                val targetT = mamber.typeSignatureIn(sourceT) match {
                    case NullaryMethodType(tpe) => tpe
                    case _ => abort(s"member $name is not a field")
                }

                lensForMember(c)(name, sourceT, targetT)

            case x => abort(s"unexpected c.prefix tree: $x")
        }

        c.Expr[scalaz.Lens[S, T]](c.resetAllAttrs(lens))
    }

    private def lensForMember
            (c: scala.reflect.macros.Context)
            (name: String, sourceT: c.Type, targetT: c.Type): c.universe.Block = {

        import c.universe._

        val lensu = Select(Select(Ident(newTermName("scalaz")), newTermName("Lens")), newTermName("lensu"))

        Block(
            List(
                ValDef(Modifiers(), newTermName("x$1"), TypeTree(),
                    Function(
                        List(ValDef(Modifiers(Flag.PARAM), newTermName("p"), TypeTree(sourceT), EmptyTree)),
                        Select(Ident(newTermName("p")), newTermName(name))
                    )
                ),

                ValDef(Modifiers(), newTermName("x$2"), TypeTree(),
                    Function(
                        List(ValDef(Modifiers(Flag.PARAM), newTermName("p"), TypeTree(sourceT), EmptyTree),
                             ValDef(Modifiers(Flag.PARAM), newTermName("a"), TypeTree(targetT), EmptyTree)
                        ),
                        Apply(
                            Select(Ident(newTermName("p")), newTermName("copy")),
                            List(AssignOrNamedArg(Ident(newTermName(name)), Ident(newTermName("a"))))
                        )
                    )
                )
            ),

            Apply(
                lensu, List(Ident(newTermName("x$2")), Ident(newTermName("x$1")))
            )
        )
    }

}


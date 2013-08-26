package lensuz


import language.experimental.macros

import scalaz.Lens

object Lenzer {
    def forField[S, T](fieldName: String): Lens[S, T] = macro LenzerMacros.forField[S, T]

    def forAllFields[T]: Any = macro LenzerMacros.forAllFields[T]
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
        val lens = Select(Ident(newTermName("scalaz")), newTermName("Lens"))
        val lensu = Select(lens, newTermName("lensu"))

        Block(
            List(

                ValDef(Modifiers(), newTermName(name + "GetF"), TypeTree(),
                    Function(
                        List(ValDef(Modifiers(Flag.PARAM), newTermName("p"), TypeTree(sourceT), EmptyTree)),
                        Select(Ident(newTermName("p")), newTermName(name))
                    )
                ),

                ValDef(Modifiers(), newTermName(name + "SetF"), TypeTree(),
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

//                DefDef(Modifiers(), newTermName(name + "GetF"), List(),
//                    List(List(ValDef(Modifiers(Flag.PARAM), newTermName("p"), TypeTree(sourceT), EmptyTree))),
//                    TypeTree(targetT),
//                    Select(Ident(newTermName("p")), newTermName(name))
//                ),
//
//                DefDef(Modifiers(), newTermName(name + "SetF"), List(),
//                    List(List(ValDef(Modifiers(Flag.PARAM), newTermName("p"), TypeTree(sourceT), EmptyTree), ValDef(Modifiers(Flag.PARAM), newTermName("i"), TypeTree(targetT), EmptyTree))),
//                    TypeTree(sourceT),
//                    Apply(Select(Ident(newTermName("p")), newTermName("copy")),
//                        List(AssignOrNamedArg(Ident(newTermName(name)), Ident(newTermName("i"))))
//                    )
//                )
            ),

            Apply(
                 lensu, List(Ident(newTermName(name + "SetF")), Ident(newTermName(name + "GetF")))
            )
        )
    }

    def forAllFields[T: c.WeakTypeTag](c: scala.reflect.macros.Context) = {
        import c.universe._

        val sourceT = c.weakTypeOf[T]

        val valLensez:List[ValOrDefDef] = sourceT.declarations.collect {
            case m: MethodSymbol if m.isCaseAccessor =>
                val (name, targetT) = (m.name.toString, m.returnType)
                val lensTypeTree = AppliedTypeTree(Ident(c.mirror.staticClass("scalaz.LensFamily")), List(TypeTree(sourceT), TypeTree(sourceT), TypeTree(targetT), TypeTree(targetT)))

//                DefDef(Modifiers(Flag.PROTECTED), newTermName(name), List(), List(),
//                    lensTypeTree,
//                    lensForMember(c)(name, sourceT, targetT)
//                )

                ValDef(Modifiers(), newTermName(name),
                    lensTypeTree,
                    lensForMember(c)(name, sourceT, targetT)
                )

        }.toList

        val anon = newTypeName(c.fresh)
        val wrapper = newTypeName(c.fresh)

        val lenzerObject = Block(
            List(
                ClassDef(Modifiers(), anon, List(),
                    Template(Nil, emptyValDef,
                        (constructor(c) :: valLensez)
                    )
                ),
                ClassDef(
                    Modifiers(Flag.FINAL), wrapper, Nil,
                    Template(Ident(anon) :: Nil, emptyValDef, List(constructor(c)))
                )
            ),
            Apply(Select(New(Ident(wrapper)), nme.CONSTRUCTOR), Nil)
        )

//        println(lenzerObject)

        c.Expr(c.resetAllAttrs(lenzerObject))
    }

    private def constructor(c: scala.reflect.macros.Context) = {
        import c.universe._

        DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(), Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(()))))
    }
}


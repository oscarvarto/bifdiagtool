package umich

package object bidiatool {
  import shapeless._
  import umich.parser.Names._

  object hlistTypes {
    type LVLP = List[VarName] :: List[ParamName] :: HNil
    type MVMP = Map[String, VarName] :: Map[String, ParamName] :: HNil
    type MVDPD = Map[VarName, Double] :: Map[ParamName, Double] :: HNil
    val mvdpdEmpty: MVDPD = Map.empty[VarName, Double] :: Map.empty[ParamName, Double] :: HNil
    type ONON = Option[Name] :: Option[Name] :: HNil
  }

  object polyFuncs {
    object names extends Poly1 {
      implicit def caseList[T <: Name] = at[List[T]] { xs ⇒ xs.map(_.name) }
      implicit def caseMap[T <: Name] = at[Map[String, T]] { _.keys }
    }

    object keysVP extends Poly1 {
      implicit def caseMapValue[T <: Name] = at[Map[T, Double]] { _.keys.toList }
    }

    def addKV[T <: Name](m: hlistTypes.MVDPD)(k: T, v: Double): hlistTypes.MVDPD = {
      object add extends Poly1 {
        implicit def caseV = at[Map[VarName, Double]] { m ⇒
          k match {
            case _: VarName ⇒ m + (k.asInstanceOf[VarName] → v)
            case _          ⇒ m
          }
        }
        implicit def caseP = at[Map[ParamName, Double]] { m ⇒
          k match {
            case _: ParamName ⇒ m + (k.asInstanceOf[ParamName] → v)
            case _            ⇒ m
          }
        }
      }
      m map add
    }

    object deepZip extends Poly1 {
      implicit def caseTuple[T <: Name] = at[(List[String], List[T])] { t ⇒
        t._1 zip t._2
      }
    }

    object list2Map extends Poly1 {
      implicit def caseListTuple[T <: Name] = at[List[(String, T)]](
        ls ⇒ ls.toMap)
    }

    def lookup(m: hlistTypes.MVMP)(name: String): hlistTypes.ONON = {
      object get extends Poly1 {
        implicit def default[T <: Name] = at[Map[String, T]] { _.get(name) }
      }
      m.map(get)
    }

    object tagOption extends Poly1 {
      import scalaz.Tags
      implicit def caseOption[T <: Name] = at[Option[T]] { ov ⇒ Tags.First(ov) }
    }

    object difference extends Poly1 {
      implicit def caseListTuple[T <: Name] = at[(List[T], List[T])](t ⇒
        t._1 diff t._2)
    }

    object appendMap extends Poly2 {
      implicit def default[T <: Name] = at[HMap[NameMap], Map[T, Double]] { (hmap, map) ⇒
        implicit val TToNum = new NameMap[T, Double]
        map.toSeq.foldLeft(hmap) { (hetMap, kv) ⇒ hetMap + kv }
      }
    }

  }
}

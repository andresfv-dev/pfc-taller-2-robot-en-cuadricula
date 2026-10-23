package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

// Mis test propios del punto 5
@RunWith(classOf[JUnitRunner])
class RobotCuadriculaPunto5Test extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val estados = List(inicial, Estado(2, -5, Oeste), Estado(-4, 1, Este))

  test("simplificar: las órdenes básicas quedan iguales") {
    assert(simplificar(Avanzar(7)) == Avanzar(7))
    assert(simplificar(Girar(Izquierda)) == Girar(Izquierda))
  }

  test("simplificar: repetir un número negativo de veces no hace nada") {
    assert(simplificar(Repetir(-3, Girar(Derecha))) == Secuencia(Nil))
  }

  test("simplificar: aplana las secuencias internas y junta los avances que quedan juntos") {
    assert(simplificar(Secuencia(List(Avanzar(1), Secuencia(List(Avanzar(2), Girar(Izquierda)))))) ==
      Secuencia(List(Avanzar(3), Girar(Izquierda))))
  }

  test("simplificar: necesita varias pasadas para llegar a un solo avance") {
    assert(simplificar(Secuencia(List(Avanzar(1), Repetir(1, Secuencia(List(Avanzar(0), Avanzar(5))))))) ==
      Avanzar(6))
  }

  test("simplificar: repetir algo que no hace nada desaparece") {
    assert(simplificar(Repetir(5, Secuencia(List(Avanzar(3), Avanzar(-3))))) == Secuencia(Nil))
  }

  test("simplificar: deja el cuadrado como está") {
    val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))
    assert(simplificar(cuadrado) == cuadrado)
  }

  test("simplificar: no cambia el resultado con repeticiones y secuencias anidadas") {
    val ordenes = List(
      Secuencia(List(Girar(Derecha), Avanzar(0), Avanzar(4), Secuencia(List(Avanzar(-1))))),
      Repetir(2, Secuencia(List(Repetir(1, Avanzar(3)), Secuencia(Nil), Girar(Izquierda)))),
      Repetir(3, Repetir(0, Avanzar(8))))
    assert(ordenes.forall(o =>
      estados.forall(e => ejecutar(e, o) == ejecutar(e, simplificar(o)))))
  }

  test("simplificar: nunca deja más básicas de las que había") {
    val orden = Secuencia(List(Avanzar(2), Avanzar(2), Repetir(1, Girar(Derecha)), Avanzar(0)))
    assert(cuantasBasicas(simplificar(orden)) <= cuantasBasicas(orden))
  }
}

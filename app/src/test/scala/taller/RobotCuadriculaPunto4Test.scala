package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

// Mis test propios del punto 4
@RunWith(classOf[JUnitRunner])
class RobotCuadriculaPunto4Test extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))

  val alReves: Orden => Orden = {
    case Girar(Derecha) => Girar(Izquierda)
    case Girar(Izquierda) => Girar(Derecha)
    case otra => otra
  }

  val triple: Orden => Orden = {
    case Avanzar(n) => Avanzar(3 * n)
    case otra => otra
  }

  // componer ------------------------------------------------------------------

  test("componer: deja el giro primero y el avance después") {
    assert(componer(Girar(Izquierda), Avanzar(3)) ==
      Secuencia(List(Girar(Izquierda), Avanzar(3))))
  }

  test("componer: no aplana las secuencias que ya traen") {
    assert(componer(Secuencia(List(Avanzar(1))), Avanzar(2)) ==
      Secuencia(List(Secuencia(List(Avanzar(1))), Avanzar(2))))
  }

  test("componer: las básicas de la compuesta son la suma de las dos") {
    assert(cuantasBasicas(componer(cuadrado, Avanzar(7))) ==
      cuantasBasicas(cuadrado) + cuantasBasicas(Avanzar(7)))
  }

  test("componer: suma un nivel a la profundidad de la más honda") {
    assert(profundidad(componer(Avanzar(1), cuadrado)) == 4)
  }

  test("componer: girar y luego avanzar lleva al este") {
    assert(ejecutar(inicial, componer(Girar(Derecha), Avanzar(3))) == Estado(3, 0, Este))
  }

  // rutina --------------------------------------------------------------------

  test("rutina: arma la repetición de avanzar y girar") {
    assert(rutina(2)(Izquierda)(3) ==
      Repetir(3, Secuencia(List(Avanzar(2), Girar(Izquierda)))))
  }

  test("rutina: con cero pasos solo queda el giro dentro de la repetición") {
    assert(rutina(0)(Derecha)(2) == Repetir(2, Secuencia(List(Avanzar(0), Girar(Derecha)))))
  }

  test("rutina: fijados los pasos y el giro sirve para varias veces") {
    val izquierda3 = rutina(3)(Izquierda) _
    assert(izquierda3(1) == Repetir(1, Secuencia(List(Avanzar(3), Girar(Izquierda)))))
    assert(izquierda3(5) == Repetir(5, Secuencia(List(Avanzar(3), Girar(Izquierda)))))
  }

  test("rutina: ejecuta dos básicas por cada vez") {
    assert(cuantasBasicas(rutina(5)(Derecha)(6)) == 12)
  }

  test("rutina: el cuadrado de lado dos vuelve al origen") {
    assert(ejecutar(inicial, rutina(2)(Derecha)(4)) == Estado(0, 0, Norte))
  }

  test("rutina: cero veces deja el robot donde estaba") {
    assert(ejecutar(Estado(2, 3, Oeste), rutina(4)(Izquierda)(0)) == Estado(2, 3, Oeste))
  }

  // aplicarA ------------------------------------------------------------------

  test("aplicarA: un giro cambia solo la dirección de cada estado") {
    assert(aplicarA(List(Estado(1, 1, Este)), Girar(Izquierda)) == List(Estado(1, 1, Norte)))
  }

  test("aplicarA: conserva el orden de la lista") {
    val estados = List(Estado(0, 0, Norte), Estado(1, 1, Este), Estado(2, 2, Sur))
    assert(aplicarA(estados, Avanzar(1)) ==
      List(Estado(0, 1, Norte), Estado(2, 1, Este), Estado(2, 1, Sur)))
  }

  test("aplicarA: con el cuadrado cada estado queda como estaba") {
    val estados = List(inicial, Estado(4, -2, Oeste), Estado(-3, 7, Sur))
    assert(aplicarA(estados, cuadrado) == estados)
  }

  test("aplicarA: sobre un solo estado es ejecutar") {
    val orden = Secuencia(List(Avanzar(2), Girar(Derecha), Avanzar(5)))
    assert(aplicarA(List(inicial), orden) == List(ejecutar(inicial, orden)))
  }

  test("aplicarA: la secuencia vacía devuelve los mismos estados") {
    val estados = List(inicial, Estado(5, 5, Sur))
    assert(aplicarA(estados, Secuencia(Nil)) == estados)
  }

  // transformar ---------------------------------------------------------------

  test("transformar: cambia el sentido de los giros y deja los avances") {
    assert(transformar(Secuencia(List(Avanzar(1), Girar(Derecha))), alReves) ==
      Secuencia(List(Avanzar(1), Girar(Izquierda))))
  }

  test("transformar: sobre una básica es aplicarle f") {
    assert(transformar(Avanzar(3), triple) == Avanzar(9))
  }

  test("transformar: llega a las repeticiones anidadas") {
    assert(transformar(Repetir(2, Repetir(3, Avanzar(1))), triple) ==
      Repetir(2, Repetir(3, Avanzar(3))))
  }

  test("transformar: no cambia las veces ni la forma de las compuestas") {
    val orden = Secuencia(List(Repetir(2, Avanzar(1)), Girar(Derecha)))
    val nueva = transformar(orden, triple)
    assert(cuantasBasicas(nueva) == cuantasBasicas(orden))
    assert(profundidad(nueva) == profundidad(orden))
  }

  test("transformar: f se aplica una sola vez a cada básica") {
    assert(transformar(Avanzar(2), (o: Orden) => Repetir(2, o)) == Repetir(2, Avanzar(2)))
  }

  test("transformar: triplicar los avances triplica lo que avanza el robot") {
    assert(ejecutar(inicial, transformar(Secuencia(List(Avanzar(2), Avanzar(1))), triple)) ==
      Estado(0, 9, Norte))
  }
}

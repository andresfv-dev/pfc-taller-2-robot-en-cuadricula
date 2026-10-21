package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

// Mis test propios del punto 3
@RunWith(classOf[JUnitRunner])
class RobotCuadriculaPunto3Test extends AnyFunSuite {

  val r = new RobotCuadricula()
  import r._

  val cuadrado = Repetir(4, Secuencia(List(Avanzar(1), Girar(Derecha))))

  // cuantasBasicas ------------------------------------------------------------

  test("cuantasBasicas: un giro cuenta uno") {
    assert(cuantasBasicas(Girar(Izquierda)) == 1)
  }

  test("cuantasBasicas: repetir un avance tres veces cuenta tres") {
    assert(cuantasBasicas(Repetir(3, Avanzar(2))) == 3)
  }

  test("cuantasBasicas: las repeticiones anidadas se multiplican") {
    assert(cuantasBasicas(Repetir(2, Repetir(3, Girar(Derecha)))) == 6)
  }

  test("cuantasBasicas: una secuencia suma lo que ejecuta cada orden") {
    val orden = Secuencia(List(Avanzar(1),
      Repetir(2, Secuencia(List(Avanzar(1), Girar(Izquierda)))),
      Girar(Derecha)))
    assert(cuantasBasicas(orden) == 6)
  }

  test("cuantasBasicas: repetir con veces negativas no ejecuta nada") {
    assert(cuantasBasicas(Repetir(-2, Avanzar(1))) == 0)
  }

  test("cuantasBasicas: repetir n veces multiplica por n las básicas de la orden") {
    val veces = List(0, 1, 2, 5, 10)
    assert(veces.forall(n => cuantasBasicas(Repetir(n, cuadrado)) == n * cuantasBasicas(cuadrado)))
  }

  // profundidad ---------------------------------------------------------------

  test("profundidad: un giro tiene profundidad 1") {
    assert(profundidad(Girar(Izquierda)) == 1)
  }

  test("profundidad: repetir una básica tiene profundidad 2") {
    assert(profundidad(Repetir(5, Avanzar(1))) == 2)
  }

  test("profundidad: dos repeticiones anidadas dan profundidad 3") {
    assert(profundidad(Repetir(2, Repetir(3, Girar(Derecha)))) == 3)
  }

  test("profundidad: una secuencia vacía anidada también suma un nivel") {
    val orden = Secuencia(List(Avanzar(1), Secuencia(List(Girar(Izquierda), Secuencia(Nil)))))
    assert(profundidad(orden) == 3)
  }

  test("profundidad: cuenta los niveles de la rama más honda") {
    val orden = Repetir(2, Secuencia(List(Avanzar(1), Repetir(3, Girar(Derecha)))))
    assert(profundidad(orden) == 4)
  }

  test("profundidad: la repetición no cambia con las veces") {
    assert(List(0, 1, 7).forall(n => profundidad(Repetir(n, cuadrado)) == 4))
  }
}

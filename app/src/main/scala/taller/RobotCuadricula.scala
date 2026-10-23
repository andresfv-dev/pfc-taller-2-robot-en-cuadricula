package taller

/**
 * Taller 2 — órdenes para un robot en una cuadrícula infinita.
 *
 * El tipo `Orden` es recursivo: `Repetir` y `Secuencia` contienen otras
 * órdenes. Toda función que lo recorra lo es también, con un caso por cada
 * alternativa del tipo.
 *
 * Tal como está, el proyecto compila y las pruebas quedan en rojo.
 */
class RobotCuadricula {

  /** El robot empieza en el origen mirando al norte. */
  val inicial = Estado(0, 0, Norte)

  // Punto 1 -------------------------------------------------------------------

  /** La dirección que queda tras girar noventa grados hacia el sentido dado. */
  def girar(d: Direccion, s: Sentido): Direccion = (d,s) match{

    case (Norte,Derecha) => Este
    case (Norte, Izquierda)  =>Oeste
    case (Oeste,Derecha)  => Norte
    case (Oeste,Izquierda) => Sur
    case (Sur,Derecha) => Oeste
    case (Sur,Izquierda) => Este
    case (Este,Derecha) => Sur
    case (Este,Izquierda) => Norte
  }

  // Punto 2 -------------------------------------------------------------------

  /** El estado que resulta de aplicarle la orden al estado dado. */
  def ejecutar(e: Estado, o: Orden): Estado = (e,o) match {

    case(Estado(x,y,mira),(Girar(sentido)))=> Estado(x,y,girar(mira,sentido))
    case((Estado(x,y,Norte),(Avanzar(n)))) => Estado(x, y+n,Norte)
    case((Estado(x,y,Sur),(Avanzar(n)))) => Estado(x, y-n,Sur)
    case((Estado(x,y,Oeste),(Avanzar(n)))) => Estado(x-n, y,Oeste)
    case((Estado(x,y,Este),(Avanzar(n)))) => Estado(x+n, y,Este)

    case (x,Repetir(veces,orden))=> (veces)match {
      case n if n <=0=>  e
      case repetir => ejecutar(ejecutar(e,orden), Repetir(veces - 1, orden))
    }

    case(x,Secuencia(ordenes: List[Orden]) ) =>(ordenes:List[Orden]) match{

      case Nil => e
      case(primera::resto)=> (ejecutar((ejecutar(e,primera)),Secuencia(resto)))

    }

  }

  // Punto 3 -------------------------------------------------------------------

  /** Cuántos Avanzar y Girar llega a ejecutar la orden, contando repeticiones. */
  def cuantasBasicas(o: Orden): Int = o match {

    case Avanzar(_) => 1
    case Girar(_) => 1
    case Repetir(veces, orden) => math.max(veces, 0) * cuantasBasicas(orden)
    case Secuencia(ordenes) => ordenes.map(cuantasBasicas).sum
  }

  /** Niveles de anidamiento: una básica tiene 1, una compuesta uno más que la más honda. */
  def profundidad(o: Orden): Int = o match {

    case Avanzar(_) => 1
    case Girar(_) => 1
    case Repetir(_, orden) => 1 + profundidad(orden)
    case Secuencia(ordenes) => 1 + ordenes.map(profundidad).maxOption.getOrElse(0)
  }

  // Punto 4 -------------------------------------------------------------------

  /** La secuencia de las dos órdenes. */
  def componer(o1: Orden, o2: Orden): Orden = Secuencia(List(o1, o2))

  /** Currificada: fijados los pasos y el giro, queda una función de las veces. */
  def rutina(pasos: Int)(giro: Sentido)(veces: Int): Orden =
    Repetir(veces, Secuencia(List(Avanzar(pasos), Girar(giro))))

  /** La misma orden ejecutada sobre cada uno de los estados. */
  def aplicarA(estados: List[Estado], o: Orden): List[Estado] =
    estados.map(e => ejecutar(e, o))

  /** Reemplaza cada orden básica por f(orden), respetando la estructura de las compuestas. */
  def transformar(o: Orden, f: Orden => Orden): Orden = o match {

    case Avanzar(_) => f(o)
    case Girar(_) => f(o)
    case Repetir(veces, orden) => Repetir(veces, transformar(orden, f))
    case Secuencia(ordenes) => Secuencia(ordenes.map(x => transformar(x, f)))
  }

  // Punto 5 -------------------------------------------------------------------

  /** Una orden equivalente más corta, con las reglas del enunciado. */
  def simplificar(o: Orden): Orden = {

    // Reglas sobre la lista de una secuencia: quita los Avanzar(0), aplana las
    // secuencias internas y junta los avances contiguos
    def reducir(ordenes: List[Orden]): List[Orden] = ordenes match {

      case Nil => Nil
      case Avanzar(0) :: resto => reducir(resto)
      case Secuencia(internas) :: resto => internas ++ reducir(resto)
      case Avanzar(a) :: Avanzar(b) :: resto => reducir(Avanzar(a + b) :: resto)
      case primera :: resto => primera :: reducir(resto)
    }

    // Una pasada: aplica las reglas en el nodo y luego baja a lo que contiene
    def pasada(o: Orden): Orden = o match {

      case Avanzar(_) => o
      case Girar(_) => o
      case Repetir(veces, _) if veces <= 0 => Secuencia(Nil)
      case Repetir(1, orden) => orden
      case Repetir(_, Secuencia(Nil)) => Secuencia(Nil)
      case Repetir(veces, orden) => Repetir(veces, pasada(orden))
      case Secuencia(List(unica)) => unica
      case Secuencia(ordenes) => Secuencia(reducir(ordenes).map(pasada))
    }

    // Se repiten las pasadas hasta que una ya no cambie nada
    def hastaQueNoCambie(actual: Orden): Orden = {
      val siguiente = pasada(actual)
      if (siguiente == actual) actual else hastaQueNoCambie(siguiente)
    }

    hastaQueNoCambie(o)
  }
}

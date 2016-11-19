package services

import java.util.concurrent.atomic.AtomicLong
import javax.inject._

/**
 * This trait demonstrates how to create a component that is injected
 * into a controller. The trait represents a counter that returns a
 * incremented number each time it is called.
 */
trait Counter {
  def nextCount(): Long
}

/**
 * This class is a concrete implementation of the [[Counter]] trait.
 * It is configured for Guice dependency injection in the [[com.google.inject.Module]]
 * class.
 *
 * This class has a `Singleton` annotation because we need to make
 * sure we only use one counter per application. Without this
 * annotation we would get a new instance every time a [[Counter]] is
 * injected.
 */
@Singleton
class AtomicCounter extends Counter {  
  private val atomicCounter = new AtomicLong(101490) // magic
  override def nextCount(): Long = atomicCounter.getAndIncrement()
}

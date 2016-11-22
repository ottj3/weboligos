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

@Singleton
class AtomicCounter extends Counter {  
  private val atomicCounter = new AtomicLong(101490) // magic
  override def nextCount(): Long = atomicCounter.getAndIncrement()
}

case class OrderLine(quantity: Int, costs: Double)

val orderLines = List(OrderLine(2, 19.98), OrderLine(1, 1.99), OrderLine(3, 3.99))

/**
  * Lets say I want to add them all up (so total quantity and total costs)
  * You can loop over all the order lines and add up all quantities and totals.
  * However, I know that Int is a Monoid and Double is a Monoid so OrderLine is also a Monoid.
  * So all I need to do is define a pairwise action and add two things together and create a new one
  */
def pairWizeCombinator(line1: OrderLine, line2: OrderLine): OrderLine = {
  val newQty = line1.quantity + line2.quantity
  val newCosts = line1.costs + line2.costs
  OrderLine(newQty, newCosts)
}

/**
  * Once I have the pairwise operation I can use List.reduce and get my
  * totalizing function for free. Next to that, if the list is long I can also
  * do it in parallel.
  * Some extra explanation: reduce has the following signature
  * def reduce[A1 >: A](op: (A1, A1) => A1): A1
  * So it takes two inputs and return a new output. The totalization is done in the
  * pairWizeCombinator method.
  */
orderLines.reduce(pairWizeCombinator)
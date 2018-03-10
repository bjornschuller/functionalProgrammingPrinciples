def printRange() = {
  for(i <- 1 to 10) { // hard coded 1 to 10 YUCK!
    println(s"the number is $i")
  }
}

// parameterize the data input
def printRange(range: Range.Inclusive) = {
  for (i <- range) {
    println(s"the number is $i") //hard code the behavior YUCK!

  }
}

// parameterize the data input & the behavior
def printRange(range: Range.Inclusive, action: Int => Any) = {
  for(i <- range) {
    action(i)
  }
}

def printAction()(i: Int): Unit = {
  println(s"the number is $i")
}

def multiplyByTwoAction()(i: Int): Unit = {
  println(s"the number $i multiplied by two is = ${i * 2}")
}
printRange(1 to 10, printAction())
printRange(1 to 10, multiplyByTwoAction())
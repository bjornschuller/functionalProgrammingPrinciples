//normal two parameters
def addTwoNumbersA( x :Int, y :Int)  =  x + y

// partially applied function (returns a lambda)
val addTwoNumbers = (x: Int, y: Int) => x + y

//when you don't provide a value for the second parameter it returns a partially applied function
val res1 = addTwoNumbers(1, _: Int) //res1: Int => Int = $Lambda$1153/2079818695@74b865ae
res1(2)

//composing functions example:
val add1 = (value: Int) => value + 1
val multiplyBy100 = (value: Int) => value * 100

List(1,2,3,4).map(add1).map(multiplyBy100)

val add1ComposeMultiplyBy100 = multiplyBy100 compose add1
List(1,2,3,4).map(add1ComposeMultiplyBy100)
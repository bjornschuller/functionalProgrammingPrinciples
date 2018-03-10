<h3>Introduction</h3>
Whole document is based on this talk by Scott Wlaschin:
https://www.youtube.com/watch?v=E8I19uA-wGY&t=1002s

<h3>What are types?</h3>
Types are not classes. A function has a bunch of inputs and a bunch of outputs. A type is just a name given to a set of inputs or outputs for a function.
This can be primitive types, domain types or function types. Types don't have any behavior. So the behavior and the data are completely separated. 
Because types don't have behavior they can be composed, just like functions. Most functional languages have an 'algebraic types system', here you start with 
primitives and you create new types by glowing them together. There are basically two ways to glue them together:

1. Product types, example: `Set of people X Set of dates = Set(Alice, Jan 12th, Bob Dec 5th)`. 
   you can combine this together and you get the: `type Birthday = Person * Date`
   
2. Sum types/union types/choise types, example:
 `Set of cash values + Set of cheque values + Set of Creditcard values`
   you can combine this together and get the: `type PaymentMethod = Cash | Cheque of ChequeNumber| Card of CardType * CardNumber`  
TODO: BJORN DO THIS IN SCALA

<h3>Design principle: Strive for totality</h3>
Types fit in this principle. Totality means that for every input in a function there is a valid output. Example: 

```scala
def twelveDividedBy(input: Int): Int = {
	12 / input
}
```

This function says that it takes an Int and returns an Int. However, that types signature is a lie, because if we give a 0 as input it will throw a: 
`java.lang.ArithmeticException`. Alternatives: 

1. Constrain the inputs (do not except 0)
2. Constrain the output (return an Either or Option to handle failure -> types signature tells the truth) 

<h3>Functions as parameters</h3>
<h6>Guideline: Parameterize all the things. Try not to implement hard coded constants in your code instead give the values as a parameter.</h6>

Example: 

```scala
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
```
We have decoupled the behavior from the data. Any range, any print action!

<h3>Pattern: Partial Application</h3>
<h6>Bad news: Composition patterns only work for functions that have on parameter!</h6>
<h6>Good news: Every function is a one parameter function</h6>

Example:

```scala
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

```
When you call a function that has parameters, you are said to be applying the function to the parameters. 
In addition, when all the parameters are passed to the function,  you have fully applied the function to all of the parameters. 
But when you give only a subset of the parameters to the function, 
the result of the expression is a partially applied function (https://alvinalexander.com/scala/how-to-use-partially-applied-functions-in-scala-syntax-examples).

In a true functional language, every multi argument function is actually a function that
generates other functions all the way down until you pass the last argument in. 
This can be used by applying partial functions. 

<h3>Pattern: Use partial application to do dependency injection</h3>
Example:

```scala
case class Customer(name: String, age: Int, id: Int)

val customerDataInMemory = List(Customer("Piet", 20, 1),
                        Customer("Klaas", 30, 2),
                        Customer("John", 40, 3),
                        Customer("Freddy", 42, 4))

/**
* This function now requires customerData, could also be a db connection for instance
* However, it does not meets its interface because the function tells us getCustomerFromMemoryById, but it stills
* depends on the Memory Data (or database connection). So this dependency should be abstracted out..
*/
def getCustomerFromMemoryByIdV1(id: Int, customerData: List[Customer]) = {
  customerData.find(cust => cust.id == id)
}

/**
 * Partial application to do dependency injection example.
 * Here we abstracted out the Memory Data (or db connection) dependency.
 * And we can takes the getCustomerById function and pass it to anybody who need our the repository.
 */
val getCustomerFromMemoryById = (id: Int, customerData:  List[Customer]) =>  customerData.find(cust => cust.id == id)
val getCustomerById = getCustomerFromMemoryById(_: Int, customerDataInMemory) // partially apply, with the customerDataInMemory already baked in
getCustomerById(2) //res0: Option[Customer] = Some(Customer(Klaas,30,2))
getCustomerById(1)// res1: Option[Customer] = Some(Customer(Piet,20,1))
getCustomerById(3)// res2: Option[Customer] = Some(Customer(John,40,3))
```

<h3>Pattern: The Hollywood principle (continuations)</h3>
Continuations is just a fancy word about whats happens next. So this means that you
should have complete control over your functions (i.e., think about possible errors and handle them). 
In addition, as mentioned before, we also want to decouple the behavior from the data (i.e., passing in the continuations). 
Based on the above we don't like this:

```scala
def divide1(top: Int, bottom: Int) = {
  if(bottom == 0) throw new Exception("div by 0")
  else top/bottom
}
```

Instead we like this more:

```scala
def ifZero = Left("cannot divide by 0")
def ifSuccess = (top: Int, bottem: Int) => Right(top/bottem)

//complete control and nice and flexible
def divide2(top: Int, bottom: Int, ifZero: Any, ifSuccess: (Int, Int) => Any) = {
  if(bottom == 0) ifZero
  else ifSuccess(top, bottom)
}

//TEST => only many parameters
divide2(2, 0, ifZero, ifSuccess(_: Int, _: Int))

//bake in the behavior parameters, so you have less parameters during implementation
val divide = divide2(_: Int, _: Int, ifZero, ifSuccess(_: Int, _: Int))

//TEST => less parameters
val goodDivide = divide(2, 1)
val badDivide = divide(2, 0)

/**
  * passing in the continuations, keeps you control... Lets says I want other behavior
  */
def ifZeroOpt = None
def ifSuccessOpt = (top: Int, bottem: Int) => Some(top/bottem)
val divideOpt = divide2(_: Int, _: Int, ifZeroOpt, ifSuccessOpt(_: Int, _: Int))
val goodDivideOpt = divideOpt(2, 1)
val badDivideOpt = divideOpt(2, 0)
``` 

<h3>Monads</h3>
In short, Monads are really just chaining continuations together (it is more complex than this, but for now this is enough).
Above we created a function based on continuations. Meaning that in case, of failure we return a Left and in case of success
we return a Right. So we created a function where something goes in and the outcome depends on if returns a success (Right) 
or a failure (Left). In a code base we have a lot of these functions, where something goes in and it might or might not work. 
We connect them as follows, if the first thing works we connect it to the second thing and if the first thing doesn't work 
we just bypass the second thing.  

Example:

```scala
case class User(name: String, gender: String)
case class Address(street: String, postalCode: String, city: String, country: String)
type ErrorMessage = String
type City = String

def getUserByName(name: String): Either[ErrorMessage, User] ={
  println(s"getUserByName $name")
  if (name == "Bjorn") Right(User("Bjorn", "Male")) else Left(s"Unable to find user by name: $name")
}

def getAddressByUser(user: User): Either[ErrorMessage, Address]  = {
  println(s"getAddressByUser $user")
  if (user.name == "Bjorn") Right(Address("Bjorn", "StreetTest", "Amsterdam", "Netherlands"))
  else Left(s"Unable to find address of $user")
}

def getCity(username: String): Either[ErrorMessage, City] = {
  println(s"getCity $username")
  for{
    user <-  getUserByName(username)
    address <- getAddressByUser(user)
  } yield {
    println("FOUND ==> "+address.city)
    address.city
  }
}

getCity("Bjorn")  //the first function returns a Right so we connect it to the second function
getCity("Unknown") //getUserByName returns Left so we bypass the second function
```

In programming we have a world of normal things like: Int, Boolean and String. Next to this, we have a parallel
universe and that is for instance the world of Options. As a functional programmer you visit the World of Options. When you 
get a Some you have to go back to the world of normal values and do something with the value inside the Some. And you can go so one.
To illustrate see image below:

![alt text](resources/worlds1.png?raw=true "World of normal values")

This is annoying and you have to write ugly code for it. You should do it as follows: once you go up to the world of Options you want
to live in that world as long as you can, and maybe you have to go down in the very end. Lets demonstrate this with an image:

![alt text](resources/worlds2.png?raw=true "World of Options")

In Scala, we have a Map function and this function helps us to stay in the world of Options. In addition, we have flatMap to chain functions
that live in the world of Options. Please note that we use the Option wrapper just as an example,  we could use other wrapper types like Future,
Either and Lists etcetera.
<h6>Guideline: Most wrapped generic types have a 'map'. Use it!</h6>
<h6>A Functor is just a fancy word for a type that has a map (i.e., functor is a mappable type).</h6>


<h3>Monoids</h3>
The generalization:

- You start with a bunch of things, and some way of combining them two at a time. 

- <b>Rule 1 (Closure): </b> The result of combining two things is always another one of the things.
    * <b>Benefit:</b> converts pairwise operations into operations that works on list. Example:
   
    ```scala
    val totalInt = 1 + 2 + 3 + 4 // returns totalInt: Int = 10
    List(1, 2, 3, 4).reduce(_ + _) //collapses the list using modification, returns res0: Int = 10
    List(1, 2, 3, 4).reduce(_ * _) //collapses the list using modification, returns res1: Int = 24
    
    val totalString = "1" + "2" + "3" + "4" // returns totalString: String = 1234
    List("1", "2", "3", "4").reduce(_ + _) //collapses the list using modification, returns res2: String = 1234
    ```
- <b>Rule 2 (Associativity): </b> When combining more than two things, which pairwise combination you do
first doesn't matter (order doesn't matter).
    * <b>Benefit:</b> Divide and conquer, parallelization and incremental accumulation. Example:
    When I have: `1 + 2 + 3 + 4` I can do `(1+2)` on one core and on another core I can do `(3+4)` and combine the result.
    So when you have this Associativity property you get parallelization for free. You can spit the task among multiple CPU and cores
    and then combine the results. Incrementation means that when you have `1 + 2 + 3` and the next day you want to add `4` to it, 
    you don't have to recalculate all the elements you can just do `6 + 4`. This incremental accumulation is another nice thing
    of Monoids. 
- <b>Rule 3 (Identity element)</b>: There is a special thing called "zero" such that when you combine any thing
with "zero" you get the original thing back.
    * <b>Benefit:</b> Initial value for empty or missing data. For instance, if you want to apply a reduce or foldLeft method
    on a List you need to start with something. If zero is missing it is called a Semigroup (but we do not go into details about this).

<h3>Simplifying aggregation code with Monoids</h3>

```scala
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
```
     
<h3>Pattern: Convert non Monoid to Monoid</h3>
For instance you cannot add Customers together because a Customer is not a Monoid. However, you can create a Monoid
called Customer Stats, where everything inside the Customer Stats is a numeric field.  So you can map each Customer to a
Customer Stats. Once you have done that you can reduce them to a total Customer Stats. So there is a map followed by reduce. 
This is a very simplified version of the familiar: MAP/REDUCE PATTERN. 
The same approach you can apply for going to a very complex Monoid to a Monoid that is more efficient to work with. This is called
Monoid homomorphism. 

<h3>Pattern seeing monoids everywhere</h3>
<h6>Metrics guideline: Use counters rather than rates</h6>
<h6>Alternative Metric guideline: Make sure your metrics are Monoids (because they aggregate, they handle incremental stuff and missing data)</h6>

<h3>Is function composition a monoid</h3>
The example below uses two function and we are going to glue them together to create a new function. The problem is that the new function
is not the same type as the original function. So this is not a Monoid! It does not satisfy the Closure rule!

![alt text](resources/noMonoid.png?raw=true "No Monoid")

However, if I have a function that takes apples to apples and combine it with another function that takes apples to apples, I get a new
function that takes apples to apples. Functions like this are monoids!

![alt text](resources/monoid.png?raw=true "Monoid")

So functions where the input type and the output type are the same type are monoids. This is called: Endomorphisms. 
<h6>All Endomorphisms are Monoids.</h6>

An example of Endomorphisms:

```scala
def plus1(x: Int) = x + 1 //(val x: Int) => Int
def times2(x: Int) = x * 2 //(val x: Int) => Int
def substract42(x: Int) = x - 42 //(val x: Int) => Int

//Because they are all Endomorphisms I can reduce them and I get a new function like this:
def plus1ThenTimes2ThenSubstract42(x: Int) = substract42(times2(plus1(x))) //(val x: Int) => Int

```
This is just to show that you can create new functions from other functions. 

A more practical example is used in EVENT SOURCING, see the illustrative explanation below:

![alt text](resources/eventsourcing1.png?raw=true "")
![alt text](resources/eventsourcing2.png?raw=true "")


<h3>Monads vs Monoids</h3>
The monad laws are just the monoid definitions in diguise. They have <b>Closure, Associativity and Identity</b>.

    
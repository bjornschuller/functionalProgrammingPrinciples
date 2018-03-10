def ifZero = Left("cannot divide by 0")
def ifSuccess = (top: Int, bottem: Int) => Right(top/bottem)

def divide1(top: Int, bottom: Int) = {
  if(bottom == 0) throw new Exception("div by 0")
  else top/bottom
}

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
def twelveDividedBy(input: Int): Either[String, Int] = {
  if(input == 0) Left("Can't divide by 0")
  else Right(12 / input)
}

twelveDividedBy(0)
twelveDividedBy(2)
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
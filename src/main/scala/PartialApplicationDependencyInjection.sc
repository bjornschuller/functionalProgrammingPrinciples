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
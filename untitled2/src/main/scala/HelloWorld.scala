import scala.util.Random

object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, world!")
    val size=100000;
    var n=n_rands(size);
    elapsedTime(findprimes(100000));
  }
  def isprime(x:Int): Int =
  {
    val i=0
    for(i <- 2 until x)
      if (x % i == 0)  return 0
    1
  }
  def findprimes(x:Int) =
  {
    val i=0
    for(i <- 11 until x)
      if (isprime(i) != 0 && isprime(i - 6) != 0)
        println(i)
  }
  def n_rands(n : Int) = {
    val r = new scala.util.Random
    for (i <- (0 to 100000).toArray) yield r.nextInt(32767)
  }
  def elapsedTime[R](block: => R): R = {
    val s = System.currentTimeMillis
    val result = block    // call-by-name
    val e = System.currentTimeMillis
    println("[elapsedTime]: " + (e - s) + " ms")
    result
  }
}
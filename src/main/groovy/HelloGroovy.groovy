class HelloGroovy {
    static int add(a, b){
        a + b
    }
    static void main(String[] args) {
        println "Hello, Groovy!"
        var result = add(3, 5)
        println "The sum is: $result"
    }
}

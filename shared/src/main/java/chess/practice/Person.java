package chess.practice;

public class Person {
    private String name;

    //public static void main(String[] args) {
    //    new Person("Ryan Gosling");
    //}

    //more efficient version
    public static void main(String[] args) {
        var myGuy = new Person("Ryan Gosling");
        myGuy.sleep();
    }

    public Person(String name) {
        this.name = name;
    }

    public void sleep() {
        System.out.printf("%s is sleeping", name);
    }
}

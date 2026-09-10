package chess.practice;

import java.util.Arrays;

public class Hello_world {
    public static void main(String[] args) {
        var x = new GetSet();
        x.scores[1] = 50;
        System.out.println(Arrays.toString(x.scores));
    }
}
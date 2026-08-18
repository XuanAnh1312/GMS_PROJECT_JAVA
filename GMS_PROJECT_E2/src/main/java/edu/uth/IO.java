package edu.uth;

public class IO {
    public static void println(String s) {
        System.out.println(s);
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
}

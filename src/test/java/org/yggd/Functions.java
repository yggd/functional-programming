package org.yggd;

public class Functions {

    /**
     * ただ1足されたものを返す関数
     * @param i 1足される数
     * @return 1足された数
     */
    public static int g(int i) {
        return i + 1;
    }

    /**
     * int値を受け取って、"文字列:int値"という形式の文字列を返すだけの関数
     * @param i int値
     * @return 文字列:int値
     */
    public static String h(int i) {
        return String.format("文字列:%d", i);
    }

    /**
     * 引数で与えられたものをそのまま返す、何の面白みもない恒等関数
     * @param i　引数
     * @return i が返る。意味ねーけど何をもってファンクターやモナドとするか判定する上で非常に重要。
     */
    public static int id(int i) {
        return i;
    }
}

/**
 * This little program inputs all permutations of characters in a string
 * 30.08.2021
 */

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Permutation{
    public static void main(String[] args) {
        System.out.println("Enter string: ");
        Scanner sc = new Scanner(System.in);
        String userStr = sc.next();
        System.out.println("Permutations for " + userStr + " are: \n" + getPermutation(userStr));
    }

    public static Set<String> getPermutation(String str){
        Set<String> res = new HashSet<>();

        if(str == null){
            System.out.println("Empty string");
            return null;
        }
        else if(str.length() == 0){
            res.add("");
            return res;
        }

        char first = str.charAt(0);       // Первый символ
        String subStr = str.substring(1); // Остальная строка без первого символа
        Set<String> strings = getPermutation(subStr);

        for(String word: strings){
            for (int i = 0; i <= word.length(); i++) {
                res.add(insert(first, word, i));
            }
        }
        return res;
    }

    public static String insert(char c, String word, int k){
        String begin = word.substring(0, k);
        String end = word.substring(k);
        return begin + c + end;
    }
}

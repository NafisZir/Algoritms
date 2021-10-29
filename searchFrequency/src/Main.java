/**
 * Задание: написать программу, которая выводит 10 самых частых слов с строке по убыванию
 */

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args){
        String str = "Мама мыла-мыла-мыла раму!";
        String[] strArr = str.toLowerCase().split("[\\p{Blank}\\p{Punct}]+");


        Stream<String> stream = Stream.of(strArr);
        Map<String, Integer> map1 = stream.collect(HashMap::new, (map, value) -> {
            map.merge(value, 1, Integer::sum);
        }, HashMap::putAll);

        map1.entrySet()
                .stream()
                .sorted((e0, e1)-> e1.getValue().compareTo(e0.getValue()))
                .limit(10).
                forEach(entry-> System.out.println(entry.getKey() + " "));
    }
}

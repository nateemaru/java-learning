package stringbuilder;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtils {
    public static <K> Map<K, Integer> countOccurrences(K[] arr) {
        Map<K, Integer> result = new HashMap<>();

        for (K element : arr) {
            result.merge(element, 1, Integer::sum);
        }

        return result;
    }
}

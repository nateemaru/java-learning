import java.util.Arrays;
import java.util.function.Function;

public class CollectionMapping {
    public static <T> T[] arrayMapping(T[] arr, Function<? super T, ? extends T> mapper) {
        T[] result = Arrays.copyOf(arr, arr.length);

        for (int i = 0; i < result.length; i++) {
            result[i] = mapper.apply(result[i]);
        }

        return result;
    }
}

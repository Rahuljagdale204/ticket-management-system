package ticket.management.backend.util;

import java.util.Optional;
import java.util.function.Consumer;

public class EntityUtils {
    private EntityUtils() {}; // Prevents instanciation

    public static <T> void updateIfNotNull(Consumer<T> setter, T value) {
        Optional.ofNullable(value).ifPresent(setter);
    }
}

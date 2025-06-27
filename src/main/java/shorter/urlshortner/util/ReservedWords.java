package shorter.urlshortner.util;

import java.util.Set;

public class ReservedWords {
    public static final Set<String> RESERVED_CODES = Set.of(
        "admin", "login", "register", "logout", "api", "u", "user", "dashboard"
    );

    public static boolean isReserved(String code) {
        return RESERVED_CODES.contains(code.toLowerCase());
    }
}

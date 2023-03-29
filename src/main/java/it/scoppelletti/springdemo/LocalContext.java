package it.scoppelletti.springdemo;

import java.util.Arrays;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class LocalContext {

    @Getter
    private Locale locale = Locale.getDefault();

    @Getter
    private MessageSource messageSource;

    public String getMessage(String code, Object... args) {
        if (messageSource == null) {
            return new StringBuilder("code=")
                    .append(code)
                    .append(", args=")
                    .append(Arrays.deepToString(args))
                    .append(")").toString();
        }

        return messageSource.getMessage(code, args, locale);
    }
}

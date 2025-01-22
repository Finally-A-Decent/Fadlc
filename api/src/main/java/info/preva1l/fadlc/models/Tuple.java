package info.preva1l.fadlc.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class Tuple<F, S> {
    private final F first;
    private final S second;

    public static <F, S> Tuple<F, S> of(F first, S second) {
        return new Tuple<>(first, second);
    }
}

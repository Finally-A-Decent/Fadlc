package info.preva1l.fadlc.config.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceMode {
    MEMORY("Less Memory Usage"),
    TICK_TIME("Less CPU Time"),
    ;

    private final String pretty;
}

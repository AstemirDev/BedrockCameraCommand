package ru.astemir.cameracommand.common.camera;

import net.minecraft.util.Mth;
import java.util.function.Function;
import java.util.stream.Stream;

public enum EasingType {
    LINEAR((t)->t),
    SPRING((t)-> 1 - (Math.cos(t * Math.PI * 4) * Math.exp(-t * 6))),
    IN_QUAD((t) -> t * t),
    OUT_QUAD((t) -> -t * (t - 2)),
    IN_OUT_QUAD((t) -> t < 0.5 ? 2 * t * t : -2 * t * t + 4 * t - 1),
    IN_CUBIC((t) -> t * t * t),
    OUT_CUBIC((t)-> --t * t * t + 1),
    IN_OUT_CUBIC((t)->{
        t *= 2;
        if (t < 1) return 0.5 * t * t * t;
        t -= 2;
        return 0.5 * (t * t * t + 2);
    }),
    IN_QUART((t) -> t * t * t * t),
    OUT_QUART((t) -> 1 - Math.pow(1 - t, 4)),
    IN_OUT_QUART((t) -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2),
    IN_QUINT((t)-> t * t * t * t * t),
    OUT_QUINT((t) -> --t * t * t * t * t + 1),
    IN_OUT_QUINT((t)->{
        if((t *= 2) < 1) return 0.5 * t * t * t * t * t;
        return 0.5 * ((t -= 2) * t * t * t * t + 2);
    }),
    IN_SINE((t) -> 1 - Math.cos((t * Math.PI) / 2)),
    OUT_SINE((t) -> Math.sin((t * Math.PI) / 2)),
    IN_OUT_SINE((t) -> (-0.5 * (Math.cos(Math.PI * t) - 1))),
    IN_EXPO((t) -> (t == 0) ? 0 : Math.pow(2, 10 * (t - 1))),
    OUT_EXPO((t) -> (t == 1) ? 1 : 1 - Math.pow(2, -10 * t)),
    IN_OUT_EXPO((t) -> {
        if (t == 0) return 0.0;
        if (t == 1) return 1.0;
        if (t < 0.5) return 0.5 * Math.pow(2, (20 * t) - 10);
        return -0.5 * Math.pow(2, (-20 * t) + 10) + 1;
    }),
    IN_CIRC((t) -> 1 - Math.sqrt(1 - Math.pow(t, 2))),
    OUT_CIRC((t) -> Math.sqrt(1 - Math.pow(t - 1, 2))),
    IN_OUT_CIRC((t) -> t < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2),
    OUT_BOUNCE((t) -> {
        if (t < 4 / 11.0) {
            return (121 * t * t) / 16.0;
        } else if (t < 8 / 11.0) {
            return (363 / 40.0 * t * t) - (99 / 10.0 * t) + 17 / 5.0;
        } else if (t < 9 / 10.0) {
            return (4356 / 361.0 * t * t) - (35442 / 1805.0 * t) + 16061 / 1805.0;
        } else {
            return (54 / 5.0 * t * t) - (513 / 25.0 * t) + 268 / 25.0;
        }
    }),
    IN_BOUNCE((t) -> 1 - OUT_BOUNCE.ease(1 - t)),
    IN_OUT_BOUNCE((t) -> t < 0.5 ? 0.5 * IN_BOUNCE.ease(t * 2) : 0.5 * OUT_BOUNCE.ease(t * 2 - 1) + 0.5),
    IN_BACK((t)->t * t * ((1.70158 + 1) * t - 1.70158)),
    OUT_BACK((t)->--t * t * ((1.70158 + 1) * t + 1.70158) + 1),
    IN_OUT_BACK((t)->{
        double s = 1.70158 * 1.525;
        if((t *= 2) < 1) return 0.5 * (t * t * ((s + 1) * t - s));
        return 0.5 * ((t -= 2) * t * ((s + 1) * t + s) + 2);
    }),

    ELASTIC_IN((t) -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1 : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4);
    }),
    ELASTIC_OUT((t) -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1 : Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
    }),
    ELASTIC_IN_OUT((t) -> {
        double c5 = (2 * Math.PI) / 4.5;
        double sin = Math.sin((20 * t - 11.125) * c5);
        return t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ? -(Math.pow(2, 20 * t - 10) * sin) / 2 : (Math.pow(2, -20 * t + 10) * sin) / 2 + 1;
    });

    private Function<Double,Double> function;

    EasingType(Function<Double,Double> function) {
        this.function = function;
    }

    public double ease(double value){
        return function.apply(Mth.clamp(value,-1.0,1.0));
    }

    private String argumentName(){
        return toString().toLowerCase();
    }

    public static Stream<String> names(){
        return Stream.of(values()).map(EasingType::argumentName);
    }
}

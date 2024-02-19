package ru.astemir.cameracommand.client;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class TweenValue<T> {
    private T value;
    public TweenValue(T value) {
        this.value = value;
    }

    public abstract T tween(TweenValue<T> to, double t);

    public T getValue() {
        return value;
    }

    public static TweenValue<Vec3> vec3(Vec3 vec3){
        return new TweenValue<>(vec3) {
            @Override
            public Vec3 tween(TweenValue<Vec3> to, double t) {
                return new Vec3(Mth.lerp(t,getValue().x,to.getValue().x),Mth.lerp(t,getValue().y,to.getValue().y),Mth.lerp(t,getValue().z,to.getValue().z));
            }
        };
    }

    public static TweenValue<Vec2> vec2(Vec2 vec2){
        return new TweenValue<>(vec2) {
            @Override
            public Vec2 tween(TweenValue<Vec2> to, double t) {
                return new Vec2(Mth.rotLerp((float) t,getValue().x,to.getValue().x),Mth.rotLerp((float) t,getValue().y,to.getValue().y));
            }
        };
    }

}

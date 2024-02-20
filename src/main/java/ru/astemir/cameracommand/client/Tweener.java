package ru.astemir.cameracommand.client;


import com.mojang.blaze3d.Blaze3D;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import ru.astemir.cameracommand.common.camera.EasingType;

public class Tweener {
    private double startTime;
    private double duration;
    private TweenValue from;
    private TweenValue to;
    private EasingType easingType;
    private boolean enabled = false;

    public void tween(Vec3 from, Vec3 to, EasingType easingType,double duration){
        this.from = TweenValue.vec3(from);
        this.to = TweenValue.vec3(to);
        this.easingType = easingType;
        this.startTime = Blaze3D.getTime();
        this.duration = duration;
        this.enabled = true;
    }

    public void tween(Vec2 from, Vec2 to, EasingType easingType,double duration){
        this.from = TweenValue.vec2(from);
        this.to = TweenValue.vec2(to);
        this.easingType = easingType;
        this.startTime = Blaze3D.getTime();
        this.duration = duration;
        this.enabled = true;
    }

    public <T> T update(){
        if (this.duration < 0) {
            return (T) this.to.getValue();
        } else {
            double elapsed = Blaze3D.getTime() - this.startTime;
            if (elapsed < this.duration) {
                return (T) this.from.tween(this.to, this.easingType.ease(elapsed / this.duration));
            } else {
                this.enabled = false;
                return (T) this.to.getValue();
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

}

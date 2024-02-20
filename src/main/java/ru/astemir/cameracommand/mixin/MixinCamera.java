package ru.astemir.cameracommand.mixin;

import net.minecraft.client.Camera;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.astemir.cameracommand.client.CameraManager;
import ru.astemir.cameracommand.client.ExtendedCamera;
import ru.astemir.cameracommand.common.camera.CameraMode;

@Mixin(Camera.class)
public abstract class MixinCamera implements ExtendedCamera {
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow private float eyeHeight;
    @Shadow private float eyeHeightOld;
    @Shadow protected abstract void setRotation(float p_90573_, float p_90574_);
    @Shadow protected abstract void setPosition(Vec3 p_90582_);
    @Shadow protected abstract void move(double p_90569_, double p_90570_, double p_90571_);
    @Shadow protected abstract double getMaxZoom(double p_90567_);
    @Shadow private boolean initialized;
    @Shadow private BlockGetter level;
    @Shadow private Entity entity;
    @Shadow private boolean detached;
    @Shadow public abstract Vec3 getPosition();

    @Inject(method = "setup",at=@At(value = "HEAD"),cancellable = true)
    public void onSetup(BlockGetter level, Entity cameraEntity, boolean detached, boolean mirrored, float partialTick, CallbackInfo ci){
        CameraManager cameraManager = CameraManager.getInstance();
        if (cameraManager.isEnabled()) {
            CameraMode cameraMode = ExtendedCamera.getCameraMode();
            if (cameraManager.optionCameraMode.isEnabled()) {
                cameraManager.setup((Camera) (Object) this, level, cameraEntity, partialTick);
            }
            onCustomSetup(cameraManager,cameraMode,level, cameraEntity, partialTick);
            ci.cancel();
        }
    }

    @Inject(method = "move",at = @At("HEAD"),cancellable = true)
    public void onMove(double p_90569_, double p_90570_, double p_90571_, CallbackInfo ci){
        CameraManager cameraManager = CameraManager.getInstance();
        if (cameraManager.isEnabled()) {
            CameraMode cameraMode = ExtendedCamera.getCameraMode();
            if (cameraMode.isFree()){
                ci.cancel();
            }
        }
    }

    @Override
    public void onCustomSetup(CameraManager cameraManager, CameraMode cameraMode, BlockGetter level, Entity cameraEntity, float partialTick){
        this.initialized = true;
        this.level = level;
        this.entity = cameraEntity;
        this.detached = cameraMode.isDetached();
        Vec3 cameraPos = getPosition();
        Vec2 cameraRot = new Vec2(xRot,yRot);
        Vec3 cameraOffset = cameraMode.getOffset();
        if (!cameraMode.isFree()){
            if (cameraMode.getPosition() != null){
                cameraPos = cameraMode.getPosition();
            }else {
                cameraPos = new Vec3(Mth.lerp(partialTick, cameraEntity.xo, cameraEntity.getX()), Mth.lerp(partialTick, cameraEntity.yo, cameraEntity.getY()) + (double) Mth.lerp(partialTick, this.eyeHeightOld, this.eyeHeight), Mth.lerp(partialTick, cameraEntity.zo, cameraEntity.getZ()));
            }
            cameraRot = new Vec2(cameraEntity.getViewXRot(partialTick),cameraEntity.getViewYRot(partialTick));
        }
        if (cameraManager.optionCameraPosition.isEnabled()){
            cameraPos = cameraManager.getPosition();
        }
        if (cameraManager.optionCameraRotation.isEnabled() || cameraManager.optionCameraLookAt.isEnabled()){
            cameraRot = cameraManager.getGlobalRotation();
        }
        this.setRotation(new Vec2(Mth.wrapDegrees(cameraMode.getRotation().x+cameraRot.x),Mth.wrapDegrees(cameraMode.getRotation().y+cameraRot.y)),cameraMode.isMirrored());
        this.setPosition(cameraPos);
        this.move(cameraOffset.x,cameraOffset.y,cameraOffset.z);
        if (cameraMode.isDetached()) {
            this.move(-this.getMaxZoom(cameraMode.getZoomOut()), 0, 0);
        }
        if (!cameraMode.isDetached()){
            if (cameraEntity instanceof LivingEntity livingEntity){
                if (livingEntity.isSleeping()){
                    Direction direction = livingEntity.getBedOrientation();
                    if (direction != null) {
                        setRotation(new Vec2(0,direction.toYRot() - 180.0F),cameraMode.isMirrored());
                    }
                    this.move(0,0.3D, 0);
                }
            }
        }
    }

    @Override
    public void setRotation(Vec2 rotation,boolean mirrored){
        this.setRotation(rotation.y,rotation.x);
        if (mirrored){
            this.setRotation(this.yRot + 180.0F, -this.xRot);
        }
    }
}

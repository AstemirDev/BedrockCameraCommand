package ru.astemir.cameracommand.common.camera;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import ru.astemir.cameracommand.data.ServerCameraModeManager;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class CameraMode {
    public static final Supplier<CameraMode> FIRST_PERSON = Suppliers.memoize(CameraParent.FIRST_PERSON::cameraMode);
    public static final Supplier<CameraMode> THIRD_PERSON = Suppliers.memoize(CameraParent.THIRD_PERSON::cameraMode);
    public static final Supplier<CameraMode> THIRD_PERSON_BACK = Suppliers.memoize(CameraParent.THIRD_PERSON_BACK::cameraMode);
    public static final Supplier<CameraMode> FREE = Suppliers.memoize(CameraParent.FREE::cameraMode);
    private ResourceLocation location;
    private CameraParent parent;
    private boolean detached;
    private boolean mirrored;
    private boolean bobView;
    private boolean free;
    private Vec2 rotation;
    private Vec3 position;
    private Vec3 offset;
    private double zoomOut;

    public CameraMode(CameraParent parent,ResourceLocation location) {
        this.parent = parent;
        this.location = location;
    }

    public CameraParent getParent() {
        return parent;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public boolean isDetached() {
        return detached;
    }

    public void setDetached(boolean detached) {
        this.detached = detached;
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public boolean isBobView() {
        return bobView;
    }

    public void setBobView(boolean bobView) {
        this.bobView = bobView;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Vec2 getRotation() {
        return rotation;
    }

    public void setRotation(Vec2 rotation) {
        this.rotation = rotation;
    }

    public Vec3 getOffset() {
        return offset;
    }

    public void setOffset(Vec3 offset) {
        this.offset = offset;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public double getZoomOut() {
        return zoomOut;
    }

    public void setZoomOut(double zoomOut) {
        this.zoomOut = zoomOut;
    }
}
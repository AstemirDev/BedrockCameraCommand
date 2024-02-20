package ru.astemir.cameracommand.common.camera;

import net.minecraft.resources.ResourceLocation;
import ru.astemir.cameracommand.data.ServerCameraModeManager;

public enum CameraParent {
    FIRST_PERSON(new ResourceLocation("first_person")),
    THIRD_PERSON(new ResourceLocation("third_person")),
    THIRD_PERSON_BACK(new ResourceLocation("third_person_back")),
    FREE(new ResourceLocation("free"));
    private ResourceLocation resourceLocation;
    CameraParent(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public static CameraParent fromLocation(ResourceLocation resourceLocation){
        for (CameraParent parent : values()) {
            if (parent.resourceLocation.equals(resourceLocation)){
                return parent;
            }
        }
        throw new RuntimeException("Invalid parent: "+resourceLocation);
    }

    public CameraMode cameraMode(){
        return ServerCameraModeManager.getCameraMode(resourceLocation);
    }
}

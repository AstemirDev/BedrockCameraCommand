package ru.astemir.cameracommand.data;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.CameraParent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerCameraModeManager extends SimpleJsonResourceReloadListener {
    private static Set<CameraMode> cameraModes = new HashSet<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private static final String folder = "camera_modes";
    public ServerCameraModeManager() {
        super(GSON, folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        cameraModes = new HashSet<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            try {
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    String inheritsFrom = GsonHelper.getAsString(jsonObject,"inherits_from","minecraft:third_person");
                    boolean detached = GsonHelper.getAsBoolean(jsonObject,"detached",false);
                    boolean mirrored = GsonHelper.getAsBoolean(jsonObject,"mirrored",false);
                    boolean bobView = GsonHelper.getAsBoolean(jsonObject,"bobView",false);
                    boolean free = GsonHelper.getAsBoolean(jsonObject,"free",false);
                    double zoomOut = GsonHelper.getAsDouble(jsonObject,"zoomOut",4.0f);
                    Vec2 rotation = new Vec2(0,0);
                    Vec3 offset = new Vec3(0,0,0);
                    Vec3 position = null;
                    if (jsonObject.has("rotation")){
                        JsonArray jsonArray = jsonObject.getAsJsonArray("rotation");
                        rotation = new Vec2(jsonArray.get(0).getAsFloat(),jsonArray.get(1).getAsFloat());
                    }
                    if (jsonObject.has("offset")){
                        JsonArray jsonArray = jsonObject.getAsJsonArray("offset");
                        offset = new Vec3(jsonArray.get(0).getAsDouble(),jsonArray.get(1).getAsDouble(),jsonArray.get(2).getAsDouble());
                    }
                    if (jsonObject.has("position")){
                        JsonArray jsonArray = jsonObject.getAsJsonArray("position");
                        position = new Vec3(jsonArray.get(0).getAsDouble(),jsonArray.get(1).getAsDouble(),jsonArray.get(2).getAsDouble());
                    }
                    CameraMode cameraMode = new CameraMode(CameraParent.fromLocation(new ResourceLocation(inheritsFrom)), entry.getKey());
                    cameraMode.setDetached(detached);
                    cameraMode.setMirrored(mirrored);
                    cameraMode.setBobView(bobView);
                    cameraMode.setFree(free);
                    cameraMode.setRotation(rotation);
                    cameraMode.setPosition(position);
                    cameraMode.setOffset(offset);
                    cameraMode.setZoomOut(zoomOut);
                    cameraModes.add(cameraMode);
                }
            }catch (JsonParseException e){
                throw new RuntimeException("Invalid camera mode json: "+e);
            }
        }
    }

    public static CameraMode getCameraMode(ResourceLocation resourceLocation){
        for (CameraMode cameraMode : cameraModes) {
            if (cameraMode.getLocation().equals(resourceLocation)){
                return cameraMode;
            }
        }
        throw new RuntimeException("Invalid camera mode: "+resourceLocation);
    }

    public static Set<CameraMode> getCameraModes() {
        return cameraModes;
    }
}

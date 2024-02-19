package ru.astemir.cameracommand.common.camera;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CameraLookTarget {
    private Vec3 lookAtPos = null;
    private int lookAtEntityId = -1;
    public CameraLookTarget lookAtPos(Vec3 lookAtPos) {
        this.lookAtPos = lookAtPos;
        this.lookAtEntityId = -1;
        return this;
    }

    public CameraLookTarget lookAtEntity(int lookAtEntityId) {
        this.lookAtEntityId = lookAtEntityId;
        this.lookAtPos = null;
        return this;
    }

    public boolean isLookingAtEntity(){
        return lookAtEntityId != -1;
    }

    public boolean isLookingAtPos(){
        return lookAtPos != null;
    }

    public Vec3 getLookAtPos() {
        return lookAtPos;
    }

    public int getLookAtEntityId() {
        return lookAtEntityId;
    }

    public void encode(FriendlyByteBuf buffer){
        if (isLookingAtPos()) {
            buffer.writeInt(0);
            buffer.writeFloat((float) lookAtPos.x);
            buffer.writeFloat((float) lookAtPos.y);
            buffer.writeFloat((float) lookAtPos.z);
        }else
        if (isLookingAtEntity()){
            buffer.writeInt(1);
            buffer.writeInt(lookAtEntityId);
        }
    }


    public static CameraLookTarget decode(FriendlyByteBuf buffer){
        int id = buffer.readInt();
        if (id == 0){
            return new CameraLookTarget().lookAtPos(new Vec3(buffer.readFloat(),buffer.readFloat(),buffer.readFloat()));
        }else
        if (id == 1){
            return new CameraLookTarget().lookAtEntity(buffer.readInt());
        }
        return null;
    }
}

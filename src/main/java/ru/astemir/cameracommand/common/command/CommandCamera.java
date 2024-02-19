package ru.astemir.cameracommand.common.command;


import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.astemir.cameracommand.BedrockCameraCommand;
import ru.astemir.cameracommand.common.camera.CameraLookTarget;
import ru.astemir.cameracommand.common.camera.CameraMode;
import ru.astemir.cameracommand.common.camera.EasingType;
import ru.astemir.cameracommand.network.ClientCameraPacket;
import ru.astemir.cameracommand.network.NetworkUtils;

import java.util.Collection;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,modid = BedrockCameraCommand.MODID)
public class CommandCamera {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent e){
        e.getDispatcher().register(Commands.literal("camera")
                .requires((commandSourceStack)->commandSourceStack.hasPermission(2))
                .then(Commands.argument("targets",EntityArgument.players())
                        .then(Commands.literal("set")
                                .then(Commands.argument("mode", CameraModeArgument.mode())
                                        .executes((context)-> setCameraMode(context.getSource(), EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode")))
                                        .then(Commands.literal("ease")
                                                .then(Commands.argument("easeTime", FloatArgumentType.floatArg(0f,20000f))
                                                        .then(Commands.argument("easeType",EasingTypeArgument.easing())
                                                                .then(Commands.literal("default").executes((context)->setCameraModeEasingDefault(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),FloatArgumentType.getFloat(context,"easeTime"),EasingTypeArgument.getEasing(context,"easeType"))))
                                                                .then(Commands.literal("pos")
                                                                        .then(Commands.argument("position", Vec3Argument.vec3())
                                                                                .executes((context)-> setCameraModeEasingPosition(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),FloatArgumentType.getFloat(context,"easeTime"),EasingTypeArgument.getEasing(context,"easeType"),Vec3Argument.getVec3(context,"position")))
                                                                                .then(Commands.literal("rot").then(Commands.argument("rotation", RotationArgument.rotation()).executes((context)-> setCameraModeEasingPositionRot(context.getSource(),EntityArgument.getPlayers(context, "targets"),CameraModeArgument.getMode(context,"mode"),FloatArgumentType.getFloat(context,"easeTime"),EasingTypeArgument.getEasing(context,"easeType"),Vec3Argument.getVec3(context, "position"),RotationArgument.getRotation(context,"rotation")))))
                                                                        ))
                                                                .then(Commands.literal("rot")
                                                                        .then(Commands.argument("rotation",RotationArgument.rotation())
                                                                                .executes((context)->setCameraModeEasingRotation(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),FloatArgumentType.getFloat(context,"easeTime"),EasingTypeArgument.getEasing(context,"easeType"),RotationArgument.getRotation(context,"rotation")))))
                                                        )
                                                )
                                        )
                                        .then(Commands.literal("pos")
                                                .then(Commands.argument("position", Vec3Argument.vec3())
                                                        .executes((context)-> setCameraModePosition(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),Vec3Argument.getVec3(context,"position")))
                                                        .then(Commands.literal("rot").then(Commands.argument("rotation", RotationArgument.rotation()).executes((context)-> setCameraModePositionRot(context.getSource(),EntityArgument.getPlayers(context, "targets"),CameraModeArgument.getMode(context,"mode"),Vec3Argument.getVec3(context, "position"),RotationArgument.getRotation(context,"rotation")))))
                                                        .then(Commands.literal("facing")
                                                                .then(Commands.argument("lookAtEntity",EntityArgument.entity()).executes((context)->setCameraModePositionLookAt(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),Vec3Argument.getVec3(context,"position"),new CameraLookTarget().lookAtEntity(EntityArgument.getEntity(context,"lookAtEntity").getId()))))
                                                                .then(Commands.argument("lookAtPos",Vec3Argument.vec3()).executes((context)->setCameraModePositionLookAt(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),Vec3Argument.getVec3(context,"position"),new CameraLookTarget().lookAtPos(Vec3Argument.getVec3(context,"lookAtPos"))))))
                                                ))
                                        .then(Commands.literal("rot")
                                                .then(Commands.argument("rotation",RotationArgument.rotation())
                                                        .executes((context)->setCameraModeRotation(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),RotationArgument.getRotation(context,"rotation")))))
                                        .then(Commands.literal("facing")
                                                .then(Commands.argument("lookAtEntity",EntityArgument.entity()).executes((context)->setCameraModeLookAt(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),new CameraLookTarget().lookAtEntity(EntityArgument.getEntity(context,"lookAtEntity").getId()))))
                                                .then(Commands.argument("lookAtPos",Vec3Argument.vec3()).executes((context)->setCameraModeLookAt(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"),new CameraLookTarget().lookAtPos(Vec3Argument.getVec3(context,"lookAtPos")))))
                                        )
                                        .then(Commands.literal("default").executes((context)->setCameraModeDefault(context.getSource(),EntityArgument.getPlayers(context,"targets"),CameraModeArgument.getMode(context,"mode"))))
                                )
                        )
                        .then(Commands.literal("fade")
                                .then(Commands.literal("time")
                                        .then(Commands.argument("fadeInTime",TimeArgument.time()).then(Commands.argument("holdTime",TimeArgument.time()).then(Commands.argument("fadeOutTime", TimeArgument.time())
                                                .executes((context)->setCameraFadeTimings(context.getSource(),EntityArgument.getPlayers(context,"targets"),IntegerArgumentType.getInteger(context,"fadeInTime"),IntegerArgumentType.getInteger(context,"holdTime"),IntegerArgumentType.getInteger(context,"fadeOutTime")))
                                                .then(Commands.literal("color").then(Commands.argument("red",IntegerArgumentType.integer(0)).then(Commands.argument("green",IntegerArgumentType.integer(0)).then(Commands.argument("blue",IntegerArgumentType.integer(0))
                                                        .executes((context)->setCameraFadeTimingsColor(context.getSource(),EntityArgument.getPlayers(context,"targets"),IntegerArgumentType.getInteger(context,"fadeInTime"),IntegerArgumentType.getInteger(context,"holdTime"),IntegerArgumentType.getInteger(context,"fadeOutTime"),IntegerArgumentType.getInteger(context,"red"),IntegerArgumentType.getInteger(context,"green"),IntegerArgumentType.getInteger(context,"blue")))))))))))
                                .then(Commands.literal("color")
                                        .then(Commands.argument("red",IntegerArgumentType.integer(0)).then(Commands.argument("green",IntegerArgumentType.integer(0)).then(Commands.argument("blue",IntegerArgumentType.integer(0))
                                                .executes((context)->setCameraFadeColor(context.getSource(),EntityArgument.getPlayers(context,"targets"),IntegerArgumentType.getInteger(context,"red"),IntegerArgumentType.getInteger(context,"green"),IntegerArgumentType.getInteger(context,"blue")))))))
                        )
                        .then(Commands.literal("clear").executes((context)-> setCameraClear(context.getSource(),EntityArgument.getPlayers(context,"targets"))))
                )
        );
    }

    private static int setCameraFadeTimingsColor(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players,int fadeIn,int hold,int fadeOut,int r,int g,int b){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket().fadeTimings(fadeIn,hold,fadeOut).fadeColor(r,g,b));
        }
        return 0;
    }

    private static int setCameraFadeTimings(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players,int fadeIn,int hold,int fadeOut){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket().fadeTimings(fadeIn,hold,fadeOut));
        }
        return 0;
    }

    private static int setCameraFadeColor(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players,int r,int g,int b){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket().fadeColor(r,g,b));
        }
        return 0;
    }


    private static int setCameraModeEasingPositionRot(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode,float easeTime,EasingType easingType,Vec3 position, Coordinates rotation){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket()
                    .mode(mode)
                    .position(position)
                    .rotation(rotation.getRotation(commandSourceStack))
                    .easing(easingType,easeTime));
        }
        return 0;
    }

    private static int setCameraModeEasingPosition(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode,float easeTime,EasingType easingType,Vec3 position){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket()
                    .mode(mode)
                    .position(position)
                    .easing(easingType,easeTime));
        }
        return 0;
    }

    private static int setCameraModeEasingRotation(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode,float easeTime,EasingType easingType,Coordinates rotation){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket()
                    .mode(mode)
                    .rotation(rotation.getRotation(commandSourceStack))
                    .easing(easingType,easeTime));
        }
        return 0;
    }


    private static int setCameraModeEasingDefault(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players,CameraMode cameraMode,float easeTime,EasingType easingType){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().mode(cameraMode).defaultRotPos().easing(easingType,easeTime));
        }
        return 0;
    }


    private static int setCameraModePositionLookAt(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode, Vec3 position, CameraLookTarget lookTarget){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket()
                    .mode(mode)
                    .position(position)
                    .lookAt(lookTarget));
        }
        return 0;
    }

    private static int setCameraModePositionRot(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode, Vec3 position, Coordinates rotation){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player,new ClientCameraPacket()
                    .mode(mode)
                    .position(position)
                    .rotation(rotation.getRotation(commandSourceStack)));
        }
        return 0;
    }


    private static int setCameraModePosition(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode,Vec3 position){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().mode(mode).position(position));
        }
        return 0;
    }

    private static int setCameraModeRotation(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode mode,Coordinates rotation){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().mode(mode).rotation(rotation.getRotation(commandSourceStack)));
        }
        return 0;
    }

    private static int setCameraModeDefault(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players,CameraMode cameraMode){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().mode(cameraMode).defaultRotPos());
        }
        return 0;
    }

    private static int setCameraMode(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode cameraMode){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().mode(cameraMode));
        }
        return 0;
    }


    private static int setCameraModeLookAt(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players, CameraMode cameraMode, CameraLookTarget lookTarget){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().mode(cameraMode).lookAt(lookTarget));
        }
        return 0;
    }


    private static int setCameraClear(CommandSourceStack commandSourceStack, Collection<ServerPlayer> players){
        for (ServerPlayer player : players) {
            NetworkUtils.sendToPlayer(player, new ClientCameraPacket().clear());
        }
        return 0;
    }
}

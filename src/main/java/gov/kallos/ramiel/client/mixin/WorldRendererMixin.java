package gov.kallos.ramiel.client.mixin;

import gov.kallos.ramiel.client.RamielClient;
import gov.kallos.ramiel.client.config.RGBValue;
import gov.kallos.ramiel.client.manager.PlayerRegistry;
import gov.kallos.ramiel.client.model.Location;
import gov.kallos.ramiel.client.model.RamielPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static gov.kallos.ramiel.client.util.RenderUtil.drawWaypoint;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    /**
     * This class currently handles literally EVERYTHING for rendering player decorations.
     * It will eventually be massively shortened.
     */
    @Inject(method = {"render"}, at={@At(value="RETURN")})
    private void injectRender(MatrixStack matrixStack, float partialTicks, long timeSlice, boolean lookingAtBlock, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        final MinecraftClient game = MinecraftClient.getInstance();
        final ClientPlayerEntity gamePlayer = MinecraftClient.getInstance().player;

        if (gamePlayer == null)
            return;

        final Vec3d pos = MinecraftClient.getInstance().getEntityRenderDispatcher().camera.getPos();

        if (pos == null)
            return;

        final double posX = pos.getX();
        final double posY = pos.getY();
        final double posZ = pos.getZ();

        if(!RamielClient.getInstance().visualsEnabled()) {
            //Visuals are not enabled, halt.
            return;
        }

        List<String> playersOnRadar = new ArrayList<>();

        Iterator entityIterator = MinecraftClient.getInstance().world.getPlayers().iterator();
        //Iterates (Smoothly) Through all players on render distance.
        while (entityIterator.hasNext()){
                AbstractClientPlayerEntity playerEntity = (AbstractClientPlayerEntity) entityIterator.next();
                if(!playerEntity.equals(MinecraftClient.getInstance().player)) {
                    playersOnRadar.add(playerEntity.getDisplayName().getString());
                    //Batshit insane xyz differentiation for graphical shit.
                    double x = MathHelper.lerp((double) partialTicks, playerEntity.prevX,
                            playerEntity.getPos().getX()) - posX;
                    double y = MathHelper.lerp((double) partialTicks, playerEntity.prevY,
                            playerEntity.getPos().getY()) + 2.5 - posY;
                    double z = MathHelper.lerp((double) partialTicks, playerEntity.prevZ,
                            playerEntity.getPos().getZ()) - posZ;
                    double dist = Math.sqrt(x * x + y * y + z * z);

                    Vec3d subtickMove = playerEntity.getLerpedPos(partialTicks).subtract(playerEntity.getPos());

                    int maxWaypointDist = RamielClient.getInstance().getMaxWaypointDist();

                    if (maxWaypointDist > -1 && dist > (double) maxWaypointDist)
                        continue;

                    RamielPlayer rplayer = PlayerRegistry.getInstance().getOrCreatePlayer(playerEntity.getDisplayName().getString());
                    RGBValue standingRgb = RamielClient.getInstance().getConfig().getRgbByStanding(rplayer.getStanding());

                    //Once you're close enough waypoints shouldn't appear.
                    if (dist < 16) {
                        continue;
                    }

                    double viewDist = dist;
                    double maxDist = game.options.getViewDistance().getValue() * 16;
                    if (dist > maxDist) {
                        x = x / dist * maxDist;
                        y = y / dist * maxDist;
                        z = z / dist * maxDist;
                        viewDist = maxDist;
                    }
                    float scale = (float) (0.0025D * (viewDist + 4.0D));
                    String waypointText = rplayer.getUsername() + " (" + (int) dist + "m) ";
                    drawWaypoint(matrixStack,
                            game.textRenderer,
                            waypointText,
                            ColorHelper.Argb.getArgb(1, standingRgb.getR(), standingRgb.getG(), standingRgb.getB()),
                            (float) x, (float) y, (float) z,
                            scale,
                            game.getEntityRenderDispatcher().getRotation());
                    //TODO figure out how to get the actual world name if possible, otherwise base it entirely off of server. Not an issue for the moment, but later on
                    // it will be.
                    PlayerRegistry.getInstance().updateLocation(playerEntity, new Location(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                            "world"));
                }
		}
        Iterator cacheIt = PlayerRegistry.getInstance().fetchPlayersInCache().iterator();
        while (cacheIt.hasNext()){
            RamielPlayer cachedPlayer = (RamielPlayer) cacheIt.next();
            //If the player is on radar or it has been longer than 15 minutes nothing should happen.
            if(playersOnRadar.contains(cachedPlayer.getUsername()) ||
                    ((System.currentTimeMillis() - cachedPlayer.getLastUpdate()) > (RamielClient.getInstance().getConfig().getTimeToDisappear() * 60L) * 1000) ||
                    cachedPlayer.getLocation() == null) {
                //Do nothing
            } else {
                Location loc = cachedPlayer.getLocation();
                double x = loc.getX() - posX;
                double y = loc.getY() + 0.8D - posY;
                double z = loc.getZ() - posZ;
                double dist = Math.sqrt(x * x + y * y + z * z);

                int maxWaypointDist = RamielClient.getInstance().getMaxWaypointDist();

                if (maxWaypointDist > -1 && dist > (double) maxWaypointDist)
                    continue;

                double viewDist = dist;
                double maxDist = game.options.getViewDistance().getValue() * 16;
                if (dist > maxDist) {
                    x = x / dist * maxDist;
                    y = y / dist * maxDist;
                    z = z / dist * maxDist;
                    viewDist = maxDist;
                }
                float scale = (float) (0.0025D * (viewDist + 4.0D));
                String waypointText = cachedPlayer.getUsername() + "(" + (int) dist + "m) " + formatAge(cachedPlayer.getLastUpdate());
                RGBValue standingRgb = RamielClient.getInstance().getConfig().getRgbByStanding(cachedPlayer.getStanding());
                drawWaypoint(matrixStack,
                        game.textRenderer,
                        waypointText,
                        ColorHelper.Argb.getArgb(1, standingRgb.getR(), standingRgb.getG(), standingRgb.getB()),
                        (float) x, (float) y, (float) z,
                        scale,
                        game.getEntityRenderDispatcher().getRotation());
            }
        }
    }

    /**
     * Quickly formats the age according to timestamps.
     * @param timestamp the timestamp to grab the age for
     * @return A nice looking string for the timestamp.
     */
    private String formatAge(long timestamp) {
        final long age = System.currentTimeMillis() - timestamp;
        if (age < 0) {
            return "future";
        } else if (age < 10 * 1000) {
            return "now";
        } else if (age < 60 * 1000) {
            return "" + (age / 1000 / 10) * 10 + "s";
        } else if (age < 3600 * 1000) {
            return "" + age / 1000 / 60 + "min";
        } else if (age < 24 * 3600 * 1000) {
            return "" + age / 3600 / 1000 + "h" + (age / 1000 / 60) % 60 + "min";
        } else {
            return new SimpleDateFormat("MM/dd HH:mm").format(new Date(timestamp));
        }
    }
}

package floppaclient.module.impl.player

import floppaclient.FloppaClient.Companion.mc
import floppaclient.events.ReceivePacketEvent
import floppaclient.events.TeleportEventPre
import floppaclient.module.Category
import floppaclient.module.Module
import floppaclient.module.settings.impl.BooleanSetting
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S07PacketRespawn
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Cancels the rotation in teleport packets, but sends the correct response to the server.
 * @author Aton.
 */
object NoRotate : Module(
    "No Rotate",
    category = Category.PLAYER,
    description = "Prevents rotation on recieved teleport packets."
){
    /**
     * Option to toggle no rotate on packets with 0 pitch. Those are used for special teleports which in general should
     * rotate you.
     */
    private val pitch = BooleanSetting("0 Pitch", false, description = "Also prevents rotation of packets with 0 pitch, those are in general used for teleport which should rotate you..")

    private var doneLoadingTerrain = false

    init {
        this.addSettings(
            pitch
        )
    }

    /**
     * Intercepts the teleport packet and preforms custom handling of the packet if conditions are met.
     * When custom teleport handling is performed the event gets cancelled which prevents the vanilla mc processing the
     * packet.
     * This must not receive cancelled events, so that if different handling is implemented somewhere else this one can
     * be cancelled and only one will activate.
     */
    @SubscribeEvent
    fun onTeleportPacket(event: TeleportEventPre) {
        if (mc.thePlayer != null && ((event.packet).pitch != 0.0f || this.pitch.enabled)) {


            // At this point no rotate is active

            event.isCanceled = true


            val packetIn = event.packet
            val entityplayer: EntityPlayer = mc.thePlayer
            var d0: Double = packetIn.x
            var d1: Double = packetIn.y
            var d2: Double = packetIn.z
            var f: Float = packetIn.yaw
            var f1: Float = packetIn.pitch

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
                d0 += entityplayer.posX
            } else {
                entityplayer.motionX = 0.0
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
                d1 += entityplayer.posY
            } else {
                entityplayer.motionY = 0.0
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
                d2 += entityplayer.posZ
            } else {
                entityplayer.motionZ = 0.0
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
                f1 += entityplayer.rotationPitch
            }

            if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
                f += entityplayer.rotationYaw
            }

            entityplayer.setPosition(d0, d1, d2)
            val fakeYaw = f % 360.0f
            val fakePitch = f1 % 360.0f

            mc.netHandler.networkManager.sendPacket(
                C06PacketPlayerPosLook(
                    entityplayer.posX,
                    entityplayer.entityBoundingBox.minY,
                    entityplayer.posZ,
                    fakeYaw,
                    fakePitch,
                    false
                )
            )

            if (!this.doneLoadingTerrain) {
                mc.thePlayer.prevPosX = mc.thePlayer.posX
                mc.thePlayer.prevPosY = mc.thePlayer.posY
                mc.thePlayer.prevPosZ = mc.thePlayer.posZ
                this.doneLoadingTerrain = true
                mc.displayGuiScreen(null as GuiScreen?)
            }
        }
        doneLoadingTerrain = true
    }


    @SubscribeEvent(receiveCanceled = true)
    fun onRespawn(event: ReceivePacketEvent) {
        if (event.packet is S07PacketRespawn) {
            doneLoadingTerrain = false
        }
    }
}
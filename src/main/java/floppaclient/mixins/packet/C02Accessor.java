package floppaclient.mixins.packet;

import net.minecraft.network.play.client.C02PacketUseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({C02PacketUseEntity.class})
public interface C02Accessor {
    @Accessor
    void setEntityId(int paramInt);

    @Accessor
    void setAction(C02PacketUseEntity.Action paramAction);
}
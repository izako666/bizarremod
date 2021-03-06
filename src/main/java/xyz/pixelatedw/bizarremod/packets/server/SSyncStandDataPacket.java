package xyz.pixelatedw.bizarremod.packets.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.pixelatedw.bizarremod.ModMain;
import xyz.pixelatedw.bizarremod.data.entity.standdata.IStandData;
import xyz.pixelatedw.bizarremod.data.entity.standdata.StandDataCapability;

public class SSyncStandDataPacket
{
	private INBT data;

	public SSyncStandDataPacket() {}
	
	public SSyncStandDataPacket(IStandData props)
	{
		this.data = new CompoundNBT();
		this.data = StandDataCapability.INSTANCE.getStorage().writeNBT(StandDataCapability.INSTANCE, props, null);
	}

	public void encode(PacketBuffer buffer)
	{
		buffer.writeCompoundTag((CompoundNBT) data);
	}
	
	public static SSyncStandDataPacket decode(PacketBuffer buffer)
	{
		SSyncStandDataPacket msg = new SSyncStandDataPacket();
		msg.data = buffer.readCompoundTag();
		return msg;
	}

	public static void handle(SSyncStandDataPacket message, Supplier<NetworkEvent.Context> ctx)
	{
		if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
		{
			ctx.get().enqueueWork(() ->
			{
				PlayerEntity player = ModMain.PROXY.getPlayer();
				World world = player.world;
				IStandData props = StandDataCapability.get(player);
				
				StandDataCapability.INSTANCE.getStorage().readNBT(StandDataCapability.INSTANCE, props, null, message.data);
			});			
		}
		ctx.get().setPacketHandled(true);
	}
}

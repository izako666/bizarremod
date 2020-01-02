package xyz.pixelatedw.bizarremod.packets.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.pixelatedw.bizarremod.ModMain;
import xyz.pixelatedw.bizarremod.api.WyHelper;
import xyz.pixelatedw.bizarremod.capabilities.standdata.IStandData;
import xyz.pixelatedw.bizarremod.capabilities.standdata.StandDataCapability;
import xyz.pixelatedw.bizarremod.entities.stands.GenericStandEntity;
import xyz.pixelatedw.bizarremod.init.ModEntities;
import xyz.pixelatedw.bizarremod.init.ModNetwork;
import xyz.pixelatedw.bizarremod.init.ModParticleEffects;
import xyz.pixelatedw.bizarremod.packets.server.SStandExistencePacket;
import xyz.pixelatedw.bizarremod.packets.server.SSyncStandDataPacket;

public class CStandControlPacket
{
	private String standName = "";
	
	public CStandControlPacket() {}
	
	public CStandControlPacket(String standName)
	{
		this.standName = standName;
	}

	public void encode(PacketBuffer buffer)
	{
		buffer.writeString(this.standName);
	}
	
	public static CStandControlPacket decode(PacketBuffer buffer)
	{
		CStandControlPacket msg = new CStandControlPacket();
		msg.standName = buffer.readString();
		return msg;
	}

	public static void handle(CStandControlPacket message, final Supplier<NetworkEvent.Context> ctx)
	{
		if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
		{
			ctx.get().enqueueWork(() ->
			{
				PlayerEntity player = ctx.get().getSender();
				World world = player.world;
				IStandData props = StandDataCapability.get(player);

				if(!props.hasStandSummoned())
				{
					GenericStandEntity stand = ModEntities.getRegisteredStands().get(message.standName).getStandEntity(player);
					stand.setRotationYawHead(player.rotationYawHead);
					
					props.setStandSummoned(true);
					stand.onSummon(player);
					world.addEntity(stand);
					ModParticleEffects.SUMMON_STAND.spawn(world, stand.posX, stand.posY + 1.0, stand.posZ, 0, 0, 0);
					
					ModNetwork.sendToAll(new SStandExistencePacket(player.getUniqueID(), stand.getEntityId()));
				}
				else
				{
					try
					{
						GenericStandEntity target = WyHelper.<GenericStandEntity>getNearbyEntities(player.getPosition(), player.world, 5, GenericStandEntity.class).get(0);
						
						if(target.getOwner() == player)
						{
							props.setStandSummoned(false);
							target.onCancel(player);
							target.remove();
						}
					}
					catch(Exception e)
					{
						ModMain.LOGGER.warn("Error when summoning the Stand, canceling it.");
						props.setStandSummoned(false);
					}
				}
				
				ModNetwork.sendTo(new SSyncStandDataPacket(props), (ServerPlayerEntity)player);
			});			
		}
		ctx.get().setPacketHandled(true);
	}
}

package xyz.pixelatedw.bizarremod.entities.stands;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import xyz.pixelatedw.bizarremod.Consts;
import xyz.pixelatedw.bizarremod.abilities.greenday.MoldInfestationAbility;
import xyz.pixelatedw.bizarremod.api.StandInfo;
import xyz.pixelatedw.bizarremod.api.abilities.PunchProjectileAbility;
import xyz.pixelatedw.bizarremod.init.ModEntities;
import xyz.pixelatedw.wypi.abilities.Ability;

public class GreenDayEntity extends GenericStandEntity
{
	public GreenDayEntity(World world, PlayerEntity owner)
	{
		super(ModEntities.GREEN_DAY, world, owner);
	}
	
	public GreenDayEntity(World world)
	{
		super(ModEntities.GREEN_DAY, world);
	}

	@Override
	protected void registerAttributes()
	{
		super.registerAttributes();
	}
	
	@Override
	protected void registerData()
	{
		super.registerData();
		this.setDestructivePower('A');
		this.setSpeed('C');
		this.setRange('A');
		this.setPersistance('A');
		this.setPrecision('E');
		this.setDevelopmentPotential('A');
	}

	@Override
	public String getStandName()
	{
		return "Green Day";
	}
	
	@Override
	public void onSummon(PlayerEntity owner)
	{
		super.onSummon(owner);
	}

	@Override
	public void onCancel(PlayerEntity owner)
	{
		
	}
	
	public static class GreenDayStandInfo extends StandInfo
	{
		private Ability[] abilities = new Ability[] 
					{ 
						new PunchProjectileAbility(),
						new MoldInfestationAbility()
					};

		@Override
		public String getStandId()
		{
			return Consts.STAND_ID_GREEN_DAY;
		}
		
		@Override
		public GenericStandEntity getStandEntity(PlayerEntity owner)
		{
			GenericStandEntity stand = new GreenDayEntity(owner.world, owner);

			return stand;
		}

		@Override
		public Ability[] getAbilities()
		{
			return this.abilities;
		}

		@Override
		public JoJoPart getStandPart()
		{
			return JoJoPart.VENTO_AUREO;
		}
	}

}

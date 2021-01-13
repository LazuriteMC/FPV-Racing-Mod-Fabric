package dev.lazurite.fpvracing.server;

import dev.lazurite.fpvracing.network.packet.*;
import dev.lazurite.fpvracing.server.entity.FlyableEntity;
import dev.lazurite.fpvracing.server.item.ChannelWandItem;
import dev.lazurite.fpvracing.server.item.GogglesItem;
import dev.lazurite.fpvracing.server.item.QuadcopterItem;
import dev.lazurite.fpvracing.server.item.TransmitterItem;
import dev.lazurite.fpvracing.server.entity.flyable.QuadcopterEntity;
import dev.lazurite.rayon.api.registry.DynamicEntityRegistry;
import dev.lazurite.rayon.api.shape.provider.BoundingBoxShapeProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.UUID;

public class ServerInitializer implements ModInitializer {
	public static final String MODID = "fpvracing";
	public static final String VERSION = "1.0.0";
	public static final String URL = "https://github.com/LazuriteMC/FPV-Racing-Mod-Fabric/releases";

	/* Items */
	public static QuadcopterItem DRONE_SPAWNER_ITEM;
	public static GogglesItem GOGGLES_ITEM;
	public static TransmitterItem TRANSMITTER_ITEM;
	public static ChannelWandItem CHANNEL_WAND_ITEM;
	public static ItemGroup ITEM_GROUP;

	/* Entities */
	public static EntityType<QuadcopterEntity> QUADCOPTER_ENTITY;

	/* Keys */
	public static final HashMap<UUID, String[]> SERVER_PLAYER_KEYS = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerTick.register();

		NoClipC2S.register();
		GodModeC2S.register();
		PowerGogglesC2S.register();

		DRONE_SPAWNER_ITEM = Registry.register(Registry.ITEM, new Identifier(MODID, "drone_spawner_item"), new QuadcopterItem(new Item.Settings().maxCount(1)));
		GOGGLES_ITEM = Registry.register(Registry.ITEM, new Identifier(MODID, "goggles_item"), new GogglesItem(new Item.Settings().maxCount(1)));
		TRANSMITTER_ITEM = Registry.register(Registry.ITEM, new Identifier(MODID, "transmitter_item"), new TransmitterItem(new Item.Settings().maxCount(1)));
		CHANNEL_WAND_ITEM = Registry.register(Registry.ITEM, new Identifier(MODID, "channel_wand_item"), new ChannelWandItem(new Item.Settings().maxCount(1)));

		ITEM_GROUP = FabricItemGroupBuilder
				.create(new Identifier(MODID, "items"))
				.icon(() -> new ItemStack(GOGGLES_ITEM))
				.appendItems(stack -> {
					stack.add(new ItemStack(DRONE_SPAWNER_ITEM));
					stack.add(new ItemStack(GOGGLES_ITEM));
					stack.add(new ItemStack(TRANSMITTER_ITEM));
					stack.add(new ItemStack(CHANNEL_WAND_ITEM));
				})
				.build();

		QUADCOPTER_ENTITY = Registry.register(
				Registry.ENTITY_TYPE,
				new Identifier(MODID, "quadcopter_entity"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, QuadcopterEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.125F)).trackable(FlyableEntity.TRACKING_RANGE, 3, true).build()
		);

		DynamicEntityRegistry.INSTANCE.register(FlyableEntity.class, BoundingBoxShapeProvider::get, 1.0f, 0.05f);
	}
}

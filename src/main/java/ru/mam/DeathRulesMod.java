package ru.mam;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.mam.network.RulesPayload;
import ru.mam.rules.PlayerRuleEngine;

public class DeathRulesMod implements ModInitializer {
    public static final String MOD_ID = "mam";

    @Override
    public void onInitialize() {
        // Регистрируем пакет, которым сервер отправляет правила клиенту для оверлея.
        PayloadTypeRegistry.playS2C().register(RulesPayload.ID, RulesPayload.CODEC);

        // Новые правила выдаются при возрождении.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                PlayerRuleEngine.rollNew(newPlayer));

        // При смерти правила сбрасываются.
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayerEntity player) {
                PlayerRuleEngine.clear(player.getUuid());
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(PlayerRuleEngine::tickAll);

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                PlayerRuleEngine.onBlockBroken(serverPlayer, pos);
            }
        });
    }
}

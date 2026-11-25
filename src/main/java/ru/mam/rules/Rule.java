package ru.mam.rules;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/** Правило, которое действует на игрока до следующей смерти. */
public interface Rule {
    /** Уникальный идентификатор. */
    String id();

    /** Текст, который видит игрок. */
    String description();

    /** true — негативный эффект (красный), false — позитивный (зелёный). */
    boolean harmful();

    /** Вызывается один раз при возрождении. */
    default void apply(ServerPlayerEntity player) {
    }

    /** Вызывается каждый тик сервера, пока правило активно. */
    default void tick(ServerPlayerEntity player) {
    }

    /** Вызывается, когда игрок ломает блок. */
    default void onBlockBroken(ServerPlayerEntity player, BlockPos pos) {
    }
}

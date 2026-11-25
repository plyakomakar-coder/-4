package ru.mam.rules;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Все возможные правила. Weight — относительная частота выпадения.
 * Factory создаёт новый экземпляр каждый раз, потому что у событий есть состояние.
 */
public final class RuleRegistry {
    public record Template(int weight, Supplier<Rule> factory) {
    }

    public static final List<Template> ALL = new ArrayList<>();

    static {
        // ---- Позитивные эффекты ----
        effect("speed", "Скорость", false, StatusEffects.SPEED, 1, 3);
        effect("haste", "Ускоренная добыча", false, StatusEffects.HASTE, 0, 2);
        effect("strength", "Сила", false, StatusEffects.STRENGTH, 0, 2);
        effect("jump_boost", "Прыжок вверх", false, StatusEffects.JUMP_BOOST, 1, 3);
        effect("regeneration", "Регенерация", false, StatusEffects.REGENERATION, 0, 2);
        effect("resistance", "Сопротивление", false, StatusEffects.RESISTANCE, 0, 2);
        effect("fire_resistance", "Огнестойкость", false, StatusEffects.FIRE_RESISTANCE, 0, 0);
        effect("water_breathing", "Дыхание под водой", false, StatusEffects.WATER_BREATHING, 0, 0);
        effect("night_vision", "Ночное зрение", false, StatusEffects.NIGHT_VISION, 0, 0);
        effect("invisibility", "Невидимость", false, StatusEffects.INVISIBILITY, 0, 0);
        effect("absorption", "Поглощение урона", false, StatusEffects.ABSORPTION, 0, 3);
        effect("saturation", "Насыщение", false, StatusEffects.SATURATION, 0, 1);
        effect("luck", "Удача", false, StatusEffects.LUCK, 0, 1);
        effect("dolphins_grace", "Милость дельфина", false, StatusEffects.DOLPHINS_GRACE, 0, 0);
        effect("conduit_power", "Сила проводника", false, StatusEffects.CONDUIT_POWER, 0, 0);
        effect("health_boost", "Дополнительное здоровье", false, StatusEffects.HEALTH_BOOST, 0, 2);
        effect("slow_falling", "Плавное падение", false, StatusEffects.SLOW_FALLING, 0, 0);

        // ---- Негативные эффекты ----
        effect("slowness", "Медлительность", true, StatusEffects.SLOWNESS, 0, 3);
        effect("mining_fatigue", "Усталость при добыче", true, StatusEffects.MINING_FATIGUE, 0, 2);
        effect("weakness", "Слабость", true, StatusEffects.WEAKNESS, 0, 2);
        effect("blindness", "Слепота", true, StatusEffects.BLINDNESS, 0, 0);
        effect("nausea", "Тошнота", true, StatusEffects.NAUSEA, 0, 0);
        effect("darkness", "Тьма", true, StatusEffects.DARKNESS, 0, 0);
        effect("glowing", "Свечение (виден сквозь стены)", true, StatusEffects.GLOWING, 0, 0);
        effect("hunger", "Голод", true, StatusEffects.HUNGER, 0, 3);
        effect("poison", "Отравление", true, StatusEffects.POISON, 0, 1);
        effect("levitation", "Левитация (поднимает вверх)", true, StatusEffects.LEVITATION, 0, 0);

        // ---- Атрибуты и комбинированные правила ----
        ALL.add(new Template(2, () -> new CompositeRule("slow_fall", "Медленное падение без урона", false, List.of(
                new AttributeRule("slow_fall_gravity", "", false,
                        EntityAttributes.GRAVITY, -0.9, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                new AttributeRule("slow_fall_safe", "", false,
                        EntityAttributes.SAFE_FALL_DISTANCE, 1024.0, EntityAttributeModifier.Operation.ADD_VALUE)
        ))));

        ALL.add(new Template(2, () -> new CompositeRule("heavy_body", "Тяжёлое тело (гравитация x2)", true, List.of(
                new AttributeRule("heavy_gravity", "", true,
                        EntityAttributes.GRAVITY, 1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        ))));

        ALL.add(new Template(2, () -> new AttributeRule("high_jump", "Очень высокий прыжок", false,
                EntityAttributes.JUMP_STRENGTH, 2.0, EntityAttributeModifier.Operation.ADD_VALUE)));

        ALL.add(new Template(2, () -> new AttributeRule("low_jump", "Низкий прыжок", true,
                EntityAttributes.JUMP_STRENGTH, -0.3, EntityAttributeModifier.Operation.ADD_VALUE)));

        ALL.add(new Template(1, () -> new AttributeRule("no_move", "Нельзя ходить", true,
                EntityAttributes.MOVEMENT_SPEED, -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));

        // ---- События ----
        ALL.add(new Template(2, RuleEvents.Kit::new));
        ALL.add(new Template(1, RuleEvents.ArmorKit::new));
        ALL.add(new Template(2, RuleEvents.LightningOnRespawn::new));
        ALL.add(new Template(2, RuleEvents.PeriodicLightning::new));
        ALL.add(new Template(2, RuleEvents.TntBarrage::new));
        ALL.add(new Template(1, RuleEvents.BlastOnBreak::new));
        ALL.add(new Template(3, () -> new RuleEvents.Summon("summon_wither", "Визер рядом с тобой",
                EntityType.WITHER, 1, 1)));
        ALL.add(new Template(3, () -> new RuleEvents.Summon("summon_warden", "Варден рядом с тобой",
                EntityType.WARDEN, 1, 1)));
        ALL.add(new Template(2, () -> new RuleEvents.Summon("zombie_pack", "Стая зомби",
                EntityType.ZOMBIE, 3, 5)));
    }

    private static void effect(String id, String name, boolean harmful,
                               RegistryEntry<StatusEffect> entry, int min, int max) {
        ALL.add(new Template(3, () -> new EffectRule(id, name, harmful, entry,
                ThreadLocalRandom.current().nextInt(min, max + 1))));
    }

    private RuleRegistry() {
    }
}

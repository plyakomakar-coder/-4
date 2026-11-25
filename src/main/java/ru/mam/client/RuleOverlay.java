package ru.mam.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/** Оверлей с новыми правилами. Показывается 10 секунд после возрождения. */
public final class RuleOverlay {
    public record Line(String text, boolean harmful) {
    }

    private static final long SHOW_MILLIS = 10_000;
    private static volatile List<Line> lines = List.of();
    private static volatile long shownAt = 0;

    private RuleOverlay() {
    }

    public static void show(List<Line> newLines) {
        lines = newLines;
        shownAt = System.currentTimeMillis();
    }

    public static List<Line> parse(String text) {
        List<Line> result = new ArrayList<>();
        for (String raw : text.split("\n")) {
            if (raw.isEmpty()) {
                continue;
            }
            boolean harmful = raw.charAt(0) == '1';
            result.add(new Line(raw.substring(1), harmful));
        }
        return result;
    }

    public static void render(DrawContext context) {
        List<Line> current = lines;
        if (current.isEmpty() || System.currentTimeMillis() - shownAt > SHOW_MILLIS) {
            return;
        }

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        String title = "Новые правила";

        int maxWidth = font.getWidth(title);
        for (Line line : current) {
            maxWidth = Math.max(maxWidth, font.getWidth(line.text()));
        }

        int left = 8;
        int top = 8;
        int height = 16 + current.size() * 12;
        context.fill(left, top, left + maxWidth + 16, top + height, 0x90000000);

        context.drawText(font, title, left + 8, top + 6, 0xFFFFFFFF, true);
        int y = top + 20;
        for (Line line : current) {
            int color = line.harmful() ? 0xFFFF5555 : 0xFF55FF55;
            context.drawText(font, line.text(), left + 8, y, color, true);
            y += 12;
        }
    }
}

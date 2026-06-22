package com.dnd.creator.export;

import com.dnd.creator.model.CharacterModel;
import com.dnd.creator.model.Race;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Renders a {@link CharacterModel} to a printable, official-style D&D 5e
 * character sheet PDF. Pure rendering — no UI and no database access. All values
 * come from the supplied model; nothing is hardcoded game data.
 *
 * <p>The sheet is drawn programmatically with PDFBox (boxes, section titles and
 * text at computed coordinates) so no external template asset is required.</p>
 */
public class CharacterPdfExporter {

    // ─── Page geometry (A4, points) ────────────────────────────────────────
    private static final PDRectangle PAGE = PDRectangle.A4;
    private static final float MARGIN = 36f;
    private static final float COL_GAP = 12f;

    private static final PDType1Font FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_TITLE = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);

    // D&D colour scheme
    private static final float[] INK = {0.10f, 0.10f, 0.10f};
    private static final float[] DARK_RED = {0.42f, 0f, 0f};
    private static final float[] GOLD = {0.78f, 0.65f, 0.40f};

    /**
     * Render {@code character} to a PDF written to {@code target}.
     *
     * @throws IOException if the document cannot be created or written
     */
    public void export(CharacterModel character, File target) throws IOException {
        if (character == null) throw new IllegalArgumentException("character must not be null");
        if (target == null) throw new IllegalArgumentException("target must not be null");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PAGE);
            doc.addPage(page);

            float pageW = PAGE.getWidth();
            float pageH = PAGE.getHeight();
            float contentW = pageW - 2 * MARGIN;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float headerBottom = drawHeader(doc, cs, character, MARGIN, pageH - MARGIN, contentW);

                float colW = (contentW - 2 * COL_GAP) / 3f;
                float top = headerBottom - 14f;
                float bottom = MARGIN;

                float leftX = MARGIN;
                float midX = MARGIN + colW + COL_GAP;
                float rightX = MARGIN + 2 * (colW + COL_GAP);

                drawLeftColumn(cs, character, leftX, top, colW, bottom);
                drawMiddleColumn(cs, character, midX, top, colW, bottom);
                drawRightColumn(cs, character, rightX, top, colW, bottom);
            }

            doc.save(target);
        }
    }

    // ─── Header ─────────────────────────────────────────────────────────────

    private float drawHeader(PDDocument doc, PDPageContentStream cs, CharacterModel c,
                             float x, float topY, float width) throws IOException {
        float boxH = 96f;
        float y = topY - boxH;
        box(cs, x, y, width, boxH, GOLD, 1.5f);

        // Portrait box on the left
        float portW = 70f, portH = 86f;
        float portX = x + 5f, portY = y + (boxH - portH) / 2f;
        box(cs, portX, portY, portW, portH, GOLD, 1f);
        drawPortrait(doc, cs, c, portX, portY, portW, portH);

        float tx = portX + portW + 12f;
        // Name
        text(cs, FONT_TITLE, 22f, DARK_RED, tx, topY - 26f, safe(c.getName(), "Unnamed Character"));
        line(cs, tx, topY - 32f, x + width - 8f, topY - 32f, GOLD, 1f);

        // Class | Level, Race, Background, Alignment laid out in two label rows
        String cls = safe(c.getCharacterClass(), "—");
        if (c.getSubclassName() != null && !c.getSubclassName().isBlank()) {
            cls = cls + " (" + c.getSubclassName() + ")";
        }
        float r1 = topY - 52f;
        labelValue(cs, tx, r1, "CLASS & LEVEL", cls + "  |  Level " + c.getLevel());
        labelValue(cs, tx + 170f, r1, "RACE", c.getRace() != null ? safe(c.getRace().getName(), "—") : "—");

        float r2 = topY - 78f;
        labelValue(cs, tx, r2, "BACKGROUND", safe(c.getSelectedBackground(), "—"));
        labelValue(cs, tx + 170f, r2, "ALIGNMENT", safe(c.getAlignment(), "—"));

        return y;
    }

    private void drawPortrait(PDDocument doc, PDPageContentStream cs, CharacterModel c,
                              float x, float y, float w, float h) {
        String path = c.getImagePath();
        if (path == null || path.isBlank() || path.equals("placeholder.png")) return;
        try {
            File img = new File(path);
            if (!img.exists()) return;
            PDImageXObject pd = PDImageXObject.createFromFileByContent(img, doc);
            // Fit within the box, preserving aspect ratio.
            float scale = Math.min(w / pd.getWidth(), h / pd.getHeight());
            float dw = pd.getWidth() * scale, dh = pd.getHeight() * scale;
            cs.drawImage(pd, x + (w - dw) / 2f, y + (h - dh) / 2f, dw, dh);
        } catch (Exception ignored) {
            // A missing/unreadable portrait must never fail the whole export.
        }
    }

    // ─── Left column: abilities, saving throws, skills ─────────────────────

    private void drawLeftColumn(PDPageContentStream cs, CharacterModel c,
                                float x, float topY, float w, float bottom) throws IOException {
        Race race = c.getRace();
        int str = c.getStrength() + bonus(race, "STR");
        int dex = c.getDexterity() + bonus(race, "DEX");
        int con = c.getConstitution() + bonus(race, "CON");
        int intel = c.getIntelligence() + bonus(race, "INT");
        int wis = c.getWisdom() + bonus(race, "WIS");
        int cha = c.getCharisma() + bonus(race, "CHA");
        int prof = c.getProficiencyBonus();

        float y = topY;

        // Ability scores as a 2x3 grid of small boxes
        String[][] abilities = {
                {"STR", String.valueOf(str), modStr(mod(str))},
                {"DEX", String.valueOf(dex), modStr(mod(dex))},
                {"CON", String.valueOf(con), modStr(mod(con))},
                {"INT", String.valueOf(intel), modStr(mod(intel))},
                {"WIS", String.valueOf(wis), modStr(mod(wis))},
                {"CHA", String.valueOf(cha), modStr(mod(cha))},
        };
        float cellW = (w - 8f) / 3f, cellH = 40f;
        for (int i = 0; i < 6; i++) {
            float cx = x + (i % 3) * (cellW + 4f);
            float cy = y - (i / 3) * (cellH + 4f) - cellH;
            abilityCell(cs, cx, cy, cellW, cellH, abilities[i][0], abilities[i][1], abilities[i][2]);
        }
        y -= 2 * (cellH + 4f) + 10f;

        text(cs, FONT_BOLD, 9f, INK, x, y, "Proficiency Bonus: " + modStr(prof));
        y -= 16f;

        // Saving throws
        y = sectionTitle(cs, x, y, w, "SAVING THROWS");
        String[][] saves = {
                {"Strength", modStr(mod(str))}, {"Dexterity", modStr(mod(dex))},
                {"Constitution", modStr(mod(con))}, {"Intelligence", modStr(mod(intel))},
                {"Wisdom", modStr(mod(wis))}, {"Charisma", modStr(mod(cha))},
        };
        for (String[] s : saves) {
            text(cs, FONT, 9f, INK, x + 4f, y, s[1] + "   " + s[0]);
            y -= 13f;
        }
        y -= 8f;

        // Skills
        y = sectionTitle(cs, x, y, w, "SKILLS");
        String[][] skills = {
                {"Acrobatics", "DEX"}, {"Animal Handling", "WIS"}, {"Arcana", "INT"},
                {"Athletics", "STR"}, {"Deception", "CHA"}, {"History", "INT"},
                {"Insight", "WIS"}, {"Intimidation", "CHA"}, {"Investigation", "INT"},
                {"Medicine", "WIS"}, {"Nature", "INT"}, {"Perception", "WIS"},
                {"Performance", "CHA"}, {"Persuasion", "CHA"}, {"Religion", "INT"},
                {"Sleight of Hand", "DEX"}, {"Stealth", "DEX"}, {"Survival", "WIS"},
        };
        for (String[] sk : skills) {
            int abilityMod = switch (sk[1]) {
                case "STR" -> mod(str); case "DEX" -> mod(dex); case "CON" -> mod(con);
                case "INT" -> mod(intel); case "WIS" -> mod(wis); default -> mod(cha);
            };
            boolean proficient = isProficient(c, sk[0]);
            int total = abilityMod + (proficient ? prof : 0);
            String mark = proficient ? "●" : "○";
            text(cs, proficient ? FONT_BOLD : FONT, 8.5f, INK, x + 4f, y,
                    mark + " " + modStr(total) + "  " + sk[0] + " (" + sk[1] + ")");
            y -= 12f;
        }
    }

    // ─── Middle column: combat stats, attacks, spells ──────────────────────

    private void drawMiddleColumn(PDPageContentStream cs, CharacterModel c,
                                  float x, float topY, float w, float bottom) throws IOException {
        Race race = c.getRace();
        int dex = c.getDexterity() + bonus(race, "DEX");
        int con = c.getConstitution() + bonus(race, "CON");
        int ac = 10 + mod(dex);
        int speed = race != null ? race.getSpeed() : 30;
        int maxHp = resolveMaxHp(c, mod(con));

        float y = topY;
        float third = (w - 8f) / 3f;
        statCell(cs, x, y - 40f, third, 40f, "ARMOR CLASS", String.valueOf(ac));
        statCell(cs, x + third + 4f, y - 40f, third, 40f, "INITIATIVE", modStr(mod(dex)));
        statCell(cs, x + 2 * (third + 4f), y - 40f, third, 40f, "SPEED", speed + " ft");
        y -= 50f;

        int hitDie = c.getClassHitDie() == 0 ? 8 : c.getClassHitDie();
        statCell(cs, x, y - 36f, (w - 4f) / 2f, 36f, "HIT POINTS", String.valueOf(maxHp));
        statCell(cs, x + (w - 4f) / 2f + 4f, y - 36f, (w - 4f) / 2f, 36f,
                "HIT DICE", c.getLevel() + "d" + hitDie);
        y -= 48f;

        // Attacks
        y = sectionTitle(cs, x, y, w, "ATTACKS & SPELLCASTING");
        text(cs, FONT_BOLD, 8f, INK, x + 4f, y, "NAME            ATK      DAMAGE");
        y -= 12f;
        List<String[]> attacks = c.getWeaponAttacks();
        if (attacks != null && !attacks.isEmpty()) {
            for (String[] wpn : attacks) {
                text(cs, FONT, 8.5f, INK, x + 4f, y,
                        clip(wpn[0], 16) + "  " + wpn[1] + "  " + clip(wpn[2], 16));
                y -= 12f;
                if (y < bottom + 12f) return;
            }
        } else {
            int strMod = mod(c.getStrength() + bonus(race, "STR"));
            text(cs, FONT, 8.5f, INK, x + 4f, y, "Unarmed Strike  " + modStr(strMod) + "  1 Bludgeoning");
            y -= 12f;
        }
        y -= 8f;

        // Spells (only if the character has any)
        boolean hasCantrips = c.getSelectedCantrips() != null && !c.getSelectedCantrips().isEmpty();
        boolean hasSpells = c.getSelectedSpells() != null && !c.getSelectedSpells().isEmpty();
        if (hasCantrips || hasSpells) {
            y = sectionTitle(cs, x, y, w, "SPELLS");
            if (hasCantrips) {
                text(cs, FONT_BOLD, 8.5f, DARK_RED, x + 4f, y, "Cantrips");
                y -= 12f;
                for (String s : c.getSelectedCantrips()) {
                    text(cs, FONT, 8.5f, INK, x + 8f, y, "• " + clip(s, 28));
                    y -= 11f;
                    if (y < bottom + 11f) return;
                }
            }
            if (hasSpells) {
                text(cs, FONT_BOLD, 8.5f, DARK_RED, x + 4f, y, "Spells");
                y -= 12f;
                for (String s : c.getSelectedSpells()) {
                    text(cs, FONT, 8.5f, INK, x + 8f, y, "• " + clip(s, 28));
                    y -= 11f;
                    if (y < bottom + 11f) return;
                }
            }
        }
    }

    // ─── Right column: equipment, languages, personality ───────────────────

    private void drawRightColumn(PDPageContentStream cs, CharacterModel c,
                                 float x, float topY, float w, float bottom) throws IOException {
        float y = topY;

        y = sectionTitle(cs, x, y, w, "EQUIPMENT");
        List<String> equipment = c.getSelectedEquipment();
        if (equipment == null || equipment.isEmpty()) {
            text(cs, FONT, 8.5f, INK, x + 4f, y, "—");
            y -= 12f;
        } else {
            for (String item : equipment) {
                List<String> lines = wrap(item, 30);
                for (int i = 0; i < lines.size(); i++) {
                    String prefix = i == 0 ? "• " : "   ";
                    text(cs, FONT, 8.5f, INK, x + 4f, y, prefix + lines.get(i));
                    y -= 11f;
                    if (y < bottom + 11f) return;
                }
            }
        }
        y -= 8f;

        // Languages
        y = sectionTitle(cs, x, y, w, "LANGUAGES");
        Race race = c.getRace();
        if (race != null && race.getLanguages() != null && !race.getLanguages().isEmpty()) {
            for (String lang : race.getLanguages()) {
                String display = lang.isEmpty() ? lang
                        : Character.toUpperCase(lang.charAt(0)) + lang.substring(1);
                text(cs, FONT, 8.5f, INK, x + 4f, y, "• " + display);
                y -= 11f;
                if (y < bottom + 11f) return;
            }
        } else {
            text(cs, FONT, 8.5f, INK, x + 4f, y, "—");
            y -= 11f;
        }
        y -= 8f;

        // Feats (if any)
        List<String> feats = c.getFeats();
        if (feats != null && !feats.isEmpty()) {
            y = sectionTitle(cs, x, y, w, "FEATS");
            for (String f : feats) {
                text(cs, FONT, 8.5f, DARK_RED, x + 4f, y, "✦ " + clip(f, 28));
                y -= 11f;
                if (y < bottom + 11f) return;
            }
            y -= 8f;
        }

        // Personality / background free-text
        y = textBlock(cs, x, y, w, bottom, "PERSONALITY TRAITS", c.getPersonalityTraits());
        y = textBlock(cs, x, y, w, bottom, "IDEALS", c.getIdeals());
        y = textBlock(cs, x, y, w, bottom, "BONDS", c.getBonds());
        textBlock(cs, x, y, w, bottom, "FLAWS", c.getFlaws());
    }

    private float textBlock(PDPageContentStream cs, float x, float y, float w, float bottom,
                            String title, String value) throws IOException {
        if (y < bottom + 24f) return y;
        y = sectionTitle(cs, x, y, w, title);
        String v = (value == null || value.isBlank()) ? "—" : value;
        for (String ln : wrap(v, 34)) {
            if (y < bottom + 10f) return y;
            text(cs, FONT, 8f, INK, x + 4f, y, ln);
            y -= 10f;
        }
        return y - 6f;
    }

    // ─── Drawing primitives ────────────────────────────────────────────────

    private void abilityCell(PDPageContentStream cs, float x, float y, float w, float h,
                             String label, String score, String mod) throws IOException {
        box(cs, x, y, w, h, GOLD, 1.2f);
        textCentered(cs, FONT_BOLD, 8f, INK, x, w, y + h - 9f, label);
        textCentered(cs, FONT_BOLD, 16f, INK, x, w, y + 14f, score);
        textCentered(cs, FONT, 8f, INK, x, w, y + 3f, mod);
    }

    private void statCell(PDPageContentStream cs, float x, float y, float w, float h,
                          String label, String value) throws IOException {
        box(cs, x, y, w, h, INK, 1f);
        textCentered(cs, FONT_BOLD, 14f, INK, x, w, y + h - 18f, value);
        textCentered(cs, FONT_BOLD, 6.5f, INK, x, w, y + 4f, label);
    }

    private float sectionTitle(PDPageContentStream cs, float x, float y, float w, String title)
            throws IOException {
        text(cs, FONT_BOLD, 9.5f, DARK_RED, x, y, title);
        line(cs, x, y - 3f, x + w, y - 3f, GOLD, 0.8f);
        return y - 14f;
    }

    private void labelValue(PDPageContentStream cs, float x, float y, String label, String value)
            throws IOException {
        text(cs, FONT_BOLD, 6.5f, GOLD, x, y + 9f, label);
        text(cs, FONT_BOLD, 10f, INK, x, y, value);
    }

    private void box(PDPageContentStream cs, float x, float y, float w, float h,
                     float[] colour, float lineWidth) throws IOException {
        cs.setStrokingColor(colour[0], colour[1], colour[2]);
        cs.setLineWidth(lineWidth);
        cs.addRect(x, y, w, h);
        cs.stroke();
    }

    private void line(PDPageContentStream cs, float x1, float y1, float x2, float y2,
                      float[] colour, float lineWidth) throws IOException {
        cs.setStrokingColor(colour[0], colour[1], colour[2]);
        cs.setLineWidth(lineWidth);
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private void text(PDPageContentStream cs, PDType1Font font, float size, float[] colour,
                      float x, float y, String s) throws IOException {
        if (s == null) s = "";
        cs.beginText();
        cs.setFont(font, size);
        cs.setNonStrokingColor(colour[0], colour[1], colour[2]);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(s));
        cs.endText();
    }

    private void textCentered(PDPageContentStream cs, PDType1Font font, float size, float[] colour,
                              float boxX, float boxW, float y, String s) throws IOException {
        if (s == null) s = "";
        s = sanitize(s);
        float tw = font.getStringWidth(s) / 1000f * size;
        text(cs, font, size, colour, boxX + (boxW - tw) / 2f, y, s);
    }

    // ─── Helpers ───────────────────────────────────────────────────────────

    private int resolveMaxHp(CharacterModel c, int conMod) {
        if (c.getMaxHp() > 0) return c.getMaxHp();
        int hitDie = c.getClassHitDie() == 0 ? 8 : c.getClassHitDie();
        return hitDie + conMod + (c.getLevel() - 1) * ((hitDie / 2 + 1) + conMod);
    }

    private int bonus(Race race, String ability) {
        return race != null ? race.getAbilityBonuses().getOrDefault(ability, 0) : 0;
    }

    private int mod(int score) { return Math.floorDiv(score - 10, 2); }

    private String modStr(int m) { return (m >= 0 ? "+" : "") + m; }

    private boolean isProficient(CharacterModel c, String skill) {
        return c.getSelectedSkills() != null && c.getSelectedSkills().contains(skill);
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String clip(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    /** Wrap a string to lines of at most {@code max} characters on word boundaries. */
    private List<String> wrap(String s, int max) {
        List<String> out = new java.util.ArrayList<>();
        if (s == null || s.isBlank()) { out.add("—"); return out; }
        StringBuilder line = new StringBuilder();
        for (String word : s.trim().split("\\s+")) {
            if (line.length() == 0) {
                line.append(word);
            } else if (line.length() + 1 + word.length() <= max) {
                line.append(' ').append(word);
            } else {
                out.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        if (line.length() > 0) out.add(line.toString());
        return out;
    }

    /** Drop characters the Standard-14 WinAnsi fonts cannot encode (e.g. emoji). */
    private String sanitize(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '●' || ch == '○' || ch == '✦' || ch == '•' || ch == '…') {
                b.append(switch (ch) {
                    case '●' -> "[x]"; case '○' -> "[ ]"; case '✦' -> "*";
                    case '•' -> "-"; default -> "...";
                });
            } else if (ch >= 32 && ch < 256) {
                b.append(ch);
            } else {
                b.append('?');
            }
        }
        return b.toString();
    }
}

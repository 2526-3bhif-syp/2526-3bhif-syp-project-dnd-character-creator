package com.dnd.creator.data;

import com.dnd.creator.model.Race;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class DbManager {

    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:src/main/data/data.db";

    private static final Map<String, String> ABILITY_TO_SHORT = Map.of(
            "Strength", "STR", "Dexterity", "DEX", "Constitution", "CON",
            "Intelligence", "INT", "Wisdom", "WIS", "Charisma", "CHA"
    );

    private static final Map<String, Integer> CANTRIPS_KNOWN = Map.of(
            "Bard", 2, "Cleric", 3, "Druid", 2,
            "Sorcerer", 4, "Warlock", 2, "Wizard", 3
    );

    private static final Map<String, Integer> SPELLS_KNOWN = Map.of(
            "Bard", 4, "Sorcerer", 2, "Warlock", 2
            // Cleric/Druid/Wizard sind Prepared Casters, SpellSelectionView setzt Default 4
    );

    // ===== CONNECTION =====

    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection with Database successful!");
            if (!isDatabaseInitialized()) {
                initializeDatabase();
            }
            checkAndAddBackgroundColumns();
        } catch (SQLException e) {
            System.out.println("Connection with Database failed!");
        }
    }

    private boolean isDatabaseInitialized() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM race")) {
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private void checkAndAddBackgroundColumns() {
        String[] columns = {"personality_traits", "ideals", "bonds", "flaws"};
        try (Statement stmt = connection.createStatement()) {
            for (String col : columns) {
                try {
                    // Test if column exists by trying to select it
                    stmt.executeQuery("SELECT " + col + " FROM \"character\" LIMIT 1").close();
                } catch (SQLException e) {
                    // Column doesn't exist, add it
                    System.out.println("Adding column " + col + " to character table...");
                    stmt.executeUpdate("ALTER TABLE \"character\" ADD COLUMN " + col + " TEXT");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying background columns: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        System.out.println("Database empty — initializing from SQL script...");
        try {
            connection.setAutoCommit(false);
            runSqlScript("src/main/data/dnd5e.sql");
            connection.commit();
            connection.setAutoCommit(true);
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Initialization failed, rolling back: " + e.getMessage());
            try { connection.rollback(); connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private void runSqlScript(String scriptPath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(scriptPath)));
            List<String> statements = splitSqlStatements(content);
            try (Statement stmt = connection.createStatement()) {
                for (String sql : statements) {
                    stmt.executeUpdate(sql);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading SQL script '" + scriptPath + "': " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error executing SQL script '" + scriptPath + "': " + e.getMessage());
        }
    }

    private List<String> splitSqlStatements(String content) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (inString) {
                current.append(c);
                if (c == '\'') {
                    if (i + 1 < content.length() && content.charAt(i + 1) == '\'') {
                        current.append(content.charAt(i + 1));
                        i++;
                    } else {
                        inString = false;
                    }
                }
            } else {
                if (c == '\'') {
                    inString = true;
                    current.append(c);
                } else if (c == ';') {
                    String trimmed = current.toString().trim();
                    if (!trimmed.isEmpty()) {
                        statements.add(trimmed);
                    }
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }

        String trimmed = current.toString().trim();
        if (!trimmed.isEmpty()) {
            statements.add(trimmed);
        }

        return statements;
    }

    // ===== RACE METHODS =====

    public List<String> getAllRaces() {
        List<String> result = new ArrayList<>();

        String query = "SELECT name FROM race " +
                "WHERE parent_race IS NOT NULL " +
                "OR name NOT IN (SELECT parent_race FROM race WHERE parent_race IS NOT NULL) " +
                "ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Race getRaceByName(String raceName) {
        String query = "SELECT name, size, speed FROM race WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, raceName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Race race = new Race();
                race.setIndex(rs.getString("name"));
                race.setName(rs.getString("name"));
                race.setSpeed(rs.getInt("speed"));
                race.setSize(rs.getString("size"));
                loadAbilityBonuses(race);
                loadLanguages(race);
                loadTraits(race);
                return race;
            }
        } catch (SQLException e) {
            System.err.println("Error loading race: " + e.getMessage());
        }
        return null;
    }

    public Race getRaceByIndex(String raceName) {
        return getRaceByName(raceName);
    }

    private void loadAbilityBonuses(Race race) {
        String query = "SELECT ability, increment FROM race_ability_score_increment WHERE race_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, race.getName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String fullName = rs.getString("ability"); // "Strength", "Dexterity" etc.
                int increment = rs.getInt("increment");
                String shortName = ABILITY_TO_SHORT.getOrDefault(fullName, fullName);
                race.addAbilityBonus(shortName, increment);
            }
        } catch (SQLException e) {
            System.err.println("Error loading ability bonuses: " + e.getMessage());
        }
    }

    private void loadLanguages(Race race) {
        String query = "SELECT language FROM race_language WHERE race_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, race.getName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                race.addLanguage(rs.getString("language"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading languages: " + e.getMessage());
        }
    }

    private void loadTraits(Race race) {
        String query = "SELECT trait_name, description FROM race_trait WHERE race_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, race.getName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String traitName = rs.getString("trait_name");
                String desc = rs.getString("description");
                if (desc != null && desc.length() > 100) {
                    desc = desc.substring(0, 100) + "...";
                }
                race.addTrait(new Race.Trait(traitName, traitName, desc));
            }
        } catch (SQLException e) {
            System.err.println("Error loading traits: " + e.getMessage());
        }
    }

    // ===== CLASS METHODS =====

    public List<String> getAllClasses() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM class ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getClassByName(String className) {
        String query = "SELECT name, hit_die, primary_ability, spellcasting_ability FROM class WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> classData = new HashMap<>();
                String name = rs.getString("name");
                String spellAbility = rs.getString("spellcasting_ability");
                classData.put("index", name);
                classData.put("name", name);
                classData.put("hit_die", rs.getInt("hit_die"));
                classData.put("primary_ability", rs.getString("primary_ability"));
                classData.put("spellcasting_ability", spellAbility);
                classData.put("has_spells", spellAbility != null);
                classData.put("proficiencies", getClassProficiencies(name));
                classData.put("saving_throws", getClassSavingThrows(name));
                return classData;
            }
        } catch (SQLException e) {
            System.err.println("Error loading class: " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getClassByIndex(String className) {
        String query = "SELECT name, hit_die FROM class WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> classData = new HashMap<>();
                classData.put("name", rs.getString("name"));
                classData.put("hit_die", rs.getInt("hit_die"));
                return classData;
            }
        } catch (SQLException e) {
            System.err.println("Error loading class by index: " + e.getMessage());
        }
        return null;
    }

    private List<String> getClassProficiencies(String className) {
        List<String> result = new ArrayList<>();
        String query = "SELECT armour_type AS proficiency FROM class_armour_type_proficiency WHERE class_name = ? " +
                "UNION ALL " +
                "SELECT weapon_type FROM class_weapon_type_proficiency WHERE class_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setString(2, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("proficiency"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading class proficiencies: " + e.getMessage());
        }
        return result;
    }

    private List<String> getClassSavingThrows(String className) {
        List<String> result = new ArrayList<>();
        String query = "SELECT ability FROM class_saving_throw WHERE class_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("ability"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading class saving throws: " + e.getMessage());
        }
        return result;
    }

    public List<String> getClassStartingEquipment(String className) {
        return getStartingEquipment(className);
    }

    public List<String> getStartingEquipment(String className) {
        List<String> result = new ArrayList<>();
        String query = "SELECT mandatory_item FROM class_starting_equipment " +
                "WHERE class_name = ? AND is_mandatory = 1 AND mandatory_item IS NOT NULL " +
                "ORDER BY choice_order";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String item = rs.getString("mandatory_item");
                if (item != null && !item.trim().isEmpty()) {
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading starting equipment: " + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> getEquipmentOptions(String className) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = "SELECT id, choice_order, option_a, option_b, option_c " +
                "FROM class_starting_equipment " +
                "WHERE class_name = ? AND is_mandatory = 0 " +
                "ORDER BY choice_order";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> option = new HashMap<>();
                option.put("id", rs.getInt("id"));
                option.put("order_num", rs.getInt("choice_order"));
                option.put("choose", 1);

                String optA = rs.getString("option_a");
                String optB = rs.getString("option_b");
                String optC = rs.getString("option_c");
                List<String> parts = new ArrayList<>();
                if (optA != null && !optA.isBlank()) parts.add(optA);
                if (optB != null && !optB.isBlank()) parts.add(optB);
                if (optC != null && !optC.isBlank()) parts.add(optC);
                option.put("description", String.join(" or ", parts));

                result.add(option);
            }
        } catch (SQLException e) {
            System.err.println("Error loading equipment options: " + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> getClassSkillProficiencies(String className) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> config = getClassSkillSelectionConfig(className);
        Map<String, Object> entry = new HashMap<>();
        entry.put("description", "Choose " + config.get("choose") + " skills");
        entry.put("choose", config.get("choose"));
        result.add(entry);
        return result;
    }

    public Map<String, Object> getClassSkillSelectionConfig(String className) {
        Map<String, Object> config = new HashMap<>();
        List<String> options = new ArrayList<>();
        int choose = 2;

        String countQuery = "SELECT skill_count FROM class_skill_count WHERE class_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(countQuery)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) choose = rs.getInt("skill_count");
        } catch (SQLException e) {
            System.err.println("Error loading skill count: " + e.getMessage());
        }

        String skillQuery = "SELECT skill_name FROM class_skill_choice WHERE class_name = ? ORDER BY skill_name";
        try (PreparedStatement stmt = connection.prepareStatement(skillQuery)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) options.add(rs.getString("skill_name"));
        } catch (SQLException e) {
            System.err.println("Error loading skill choices: " + e.getMessage());
        }

        if (options.isEmpty()) options = getAllSkills();

        config.put("choose", choose);
        config.put("options", options);
        return config;
    }

    // ===== SPELL METHODS =====

    public List<Map<String, Object>> getSpellsByClass(String className, int maxLevel) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = "SELECT s.id, s.name, s.spell_level AS level, s.casting_time, s.range_area AS range, s.description " +
                "FROM spell s " +
                "JOIN class_spell cs ON s.id = cs.spell_id " +
                "WHERE cs.class_name = ? AND s.spell_level <= ? " +
                "ORDER BY s.spell_level, s.name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, maxLevel);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> spell = new HashMap<>();
                spell.put("index", String.valueOf(rs.getInt("id")));
                spell.put("name", rs.getString("name"));
                spell.put("level", rs.getInt("level"));
                spell.put("casting_time", rs.getString("casting_time"));
                spell.put("range", rs.getString("range"));
                String desc = rs.getString("description");
                if (desc != null && desc.length() > 150) desc = desc.substring(0, 150) + "...";
                spell.put("description", desc);
                result.add(spell);
            }
        } catch (SQLException e) {
            System.err.println("Error loading spells by class: " + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> getLevel1Spells(String className) {
        return getSpellsByClass(className, 1);
    }

    public Map<String, Object> getSpellDetails(String spellIndex) {
        try {
            int id = Integer.parseInt(spellIndex);
            String query = "SELECT * FROM spell WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Map<String, Object> spell = new HashMap<>();
                    spell.put("index", String.valueOf(rs.getInt("id")));
                    spell.put("name", rs.getString("name"));
                    spell.put("level", rs.getInt("spell_level"));
                    spell.put("casting_time", rs.getString("casting_time"));
                    spell.put("range", rs.getString("range_area"));
                    spell.put("duration", rs.getString("duration"));
                    spell.put("concentration", rs.getInt("is_concentration") > 0);
                    spell.put("ritual", rs.getInt("is_ritual") > 0);
                    spell.put("material", rs.getString("material_desc"));
                    spell.put("description", rs.getString("description"));
                    return spell;
                }
            }
        } catch (NumberFormatException | SQLException e) {
            System.err.println("Error loading spell details: " + e.getMessage());
        }
        return null;
    }

    public List<Map<String, String>> getSpellsForClass(String className, int level) {
        List<Map<String, String>> result = new ArrayList<>();
        String query = "SELECT s.name, s.spell_school, s.casting_time, s.range_area, s.duration " +
                "FROM spell s " +
                "JOIN class_spell cs ON s.id = cs.spell_id " +
                "WHERE cs.class_name = ? AND s.spell_level = ? " +
                "ORDER BY s.name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> spell = new HashMap<>();
                spell.put("name",         rs.getString("name"));
                spell.put("school",       rs.getString("spell_school"));
                spell.put("casting_time", rs.getString("casting_time"));
                spell.put("range",        rs.getString("range_area"));
                spell.put("duration",     rs.getString("duration"));
                result.add(spell);
            }
        } catch (SQLException e) {
            System.err.println("Error loading spells: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Integer> getSpellSlotsAtLevel(String className, int characterLevel) {
        Map<String, Integer> slots = new LinkedHashMap<>();
        slots.put("cantrips_known", CANTRIPS_KNOWN.getOrDefault(className, 0));
        slots.put("spells_known",   SPELLS_KNOWN.getOrDefault(className, 0));

        String query = "SELECT slots FROM class_spell_slots " +
                "WHERE class_name = ? AND character_level = ? AND slot_level = 1";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, characterLevel);
            ResultSet rs = stmt.executeQuery();
            slots.put("slots_level_1", rs.next() ? rs.getInt("slots") : 0);
        } catch (SQLException e) {
            System.err.println("Error loading spell slots: " + e.getMessage());
        }
        return slots;
    }

    // ===== BACKGROUND / SKILL =====

    public List<String> getAllBackgrounds() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM background ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading backgrounds: " + e.getMessage());
        }
        return result;
    }

    public List<String> getAllSkills() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM skill ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading skills: " + e.getMessage());
        }
        return result;
    }

    public List<String> getBackgroundSkills(String backgroundName) {
        List<String> result = new ArrayList<>();
        String query = "SELECT skill_name FROM background_skill WHERE background = ? ORDER BY skill_name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, backgroundName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("skill_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading background skills: " + e.getMessage());
        }
        return result;
    }

    public List<String> getAlignments() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name_x || ' ' || name_y AS name FROM alignment ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) result.add(rs.getString("name"));
        } catch (SQLException e) {
            System.err.println("Error loading alignments: " + e.getMessage());
        }
        return result;
    }

    // ===== CHARACTER PERSISTENCE =====

    private String copyPortraitToStorage(String srcPath) {
        if (srcPath == null || srcPath.equals("placeholder.png")) return srcPath;
        try {
            java.io.File src = new java.io.File(srcPath);
            if (!src.exists()) return srcPath;

            java.io.File dir = new java.io.File("src/main/data/portraits");
            dir.mkdirs();

            String ext = srcPath.contains(".") ? srcPath.substring(srcPath.lastIndexOf('.')) : ".png";
            String filename = "portrait_" + System.currentTimeMillis() + ext;
            java.io.File dest = new java.io.File(dir, filename);

            Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return dest.getPath();
        } catch (IOException e) {
            System.err.println("Could not copy portrait: " + e.getMessage());
            return srcPath;
        }
    }

    public boolean saveCharacter(com.dnd.creator.model.CharacterModel character) {
        if (character.getRace() == null || character.getCharacterClass() == null
                || character.getSelectedBackground() == null) {
            System.err.println("Cannot save incomplete character.");
            return false;
        }

        // Alignment ID auflösen
        Integer alignmentId = null;
        if (character.getAlignment() != null) {
            String alignQuery = "SELECT id FROM alignment WHERE name_x || ' ' || name_y = ?";
            try (PreparedStatement s = connection.prepareStatement(alignQuery)) {
                s.setString(1, character.getAlignment());
                ResultSet rs = s.executeQuery();
                if (rs.next()) alignmentId = rs.getInt("id");
            } catch (SQLException e) {
                System.err.println("Error resolving alignment: " + e.getMessage());
            }
        }

        String portraitPath = copyPortraitToStorage(character.getImagePath());

        if (character.getDbId() > 0) {
            return updateCharacter(character, portraitPath, alignmentId);
        }

        String insertChar = "INSERT INTO \"character\" " +
                "(character_name, race_name, class_name, subclass_name, background_name, alignment_id, level, character_picture, personality_traits, ideals, bonds, flaws) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertChar, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, character.getName());
            stmt.setString(2, character.getRace().getName());
            stmt.setString(3, character.getCharacterClass());
            if (character.getSubclassName() != null) stmt.setString(4, character.getSubclassName());
            else stmt.setNull(4, java.sql.Types.VARCHAR);
            stmt.setString(5, character.getSelectedBackground());
            if (alignmentId != null) stmt.setInt(6, alignmentId);
            else stmt.setNull(6, java.sql.Types.INTEGER);
            stmt.setInt(7, character.getLevel());
            stmt.setString(8, character.getImagePath());
            stmt.setString(9, character.getPersonalityTraits());
            stmt.setString(10, character.getIdeals());
            stmt.setString(11, character.getBonds());
            stmt.setString(12, character.getFlaws());


            if (stmt.executeUpdate() == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    saveCharacterStats(id, character);
                    saveCharacterSkills(id, character.getSelectedSkills());
                    saveCharacterEquipment(id, getAllSelectedEquipment(character));
                    saveCharacterSpells(id, getAllSelectedSpells(character));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving character: " + e.getMessage());
        }
        return false;
    }

    private boolean updateCharacter(com.dnd.creator.model.CharacterModel character, String portraitPath, Integer alignmentId) {
        long id = character.getDbId();
        String updateQuery = "UPDATE \"character\" SET character_name = ?, race_name = ?, class_name = ?, " +
                "background_name = ?, alignment_id = ?, character_picture = ?, personality_traits = ?, ideals = ?, bonds = ?, flaws = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setString(1, character.getName());
            stmt.setString(2, character.getRace().getName());
            stmt.setString(3, character.getCharacterClass());
            stmt.setString(4, character.getSelectedBackground());
            if (alignmentId != null) stmt.setInt(5, alignmentId);
            else stmt.setNull(5, java.sql.Types.INTEGER);
            stmt.setString(6, portraitPath);
            stmt.setString(7, character.getPersonalityTraits());
            stmt.setString(8, character.getIdeals());
            stmt.setString(9, character.getBonds());
            stmt.setString(10, character.getFlaws());
            stmt.setLong(11, id);

            if (stmt.executeUpdate() == 0) return false;

            // Delete old relationships
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM character_stats WHERE character_id = " + id);
                s.executeUpdate("DELETE FROM character_skill WHERE character_id = " + id);
                s.executeUpdate("DELETE FROM character_equipment WHERE character_id = " + id);
                s.executeUpdate("DELETE FROM character_spell WHERE character_id = " + id);
            }

            saveCharacterStats(id, character);
            saveCharacterSkills(id, character.getSelectedSkills());
            saveCharacterEquipment(id, getAllSelectedEquipment(character));
            saveCharacterSpells(id, getAllSelectedSpells(character));
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating character: " + e.getMessage());
            return false;
        }
    }

    private void saveCharacterStats(long characterId, com.dnd.creator.model.CharacterModel character) {
        String query = "INSERT INTO character_stats (character_id, strength, dexterity, constitution, intelligence, wisdom, charisma) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            stmt.setInt(2, character.getStrength());
            stmt.setInt(3, character.getDexterity());
            stmt.setInt(4, character.getConstitution());
            stmt.setInt(5, character.getIntelligence());
            stmt.setInt(6, character.getWisdom());
            stmt.setInt(7, character.getCharisma());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving character stats: " + e.getMessage());
        }
    }

    private void saveCharacterSkills(long characterId, List<String> skills) {
        if (skills == null) return;
        String query = "INSERT INTO character_skill (character_id, skill_name) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (String skill : skills) {
                stmt.setLong(1, characterId);
                stmt.setString(2, skill);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving character skills: " + e.getMessage());
        }
    }

    private void saveCharacterEquipment(long characterId, List<String> equipment) {
        if (equipment == null) return;
        String query = "INSERT INTO character_equipment (id, character_id, item_name) " +
                "VALUES ((SELECT COALESCE(MAX(id), 0) + 1 FROM character_equipment), ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (String item : equipment) {
                stmt.setLong(1, characterId);
                stmt.setString(2, item);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving character equipment: " + e.getMessage());
        }
    }

    private List<String> getAllSelectedEquipment(com.dnd.creator.model.CharacterModel character) {
        Set<String> result = new LinkedHashSet<>();
        if (character.getClassIndex() != null) result.addAll(getStartingEquipment(character.getClassIndex()));
        if (character.getSelectedEquipment() != null) {
            for (String item : character.getSelectedEquipment()) {
                String normalized = normalizeSavedEquipment(item);
                if (!normalized.isBlank()) result.add(normalized);
            }
        }
        return new ArrayList<>(result);
    }

    private String normalizeSavedEquipment(String item) {
        if (item == null) return "";
        return item.trim()
                .replaceFirst("^[A-Z]\\)\\s*", "")
                .replaceFirst("^\\([a-z]\\)\\s*", "")
                .replaceAll("\\s+", " ");
    }

    private void saveCharacterSpells(long characterId, List<String> spells) {
        if (spells == null || spells.isEmpty()) return;
        String lookupQuery = "SELECT id FROM spell WHERE name = ?";
        String insertQuery = "INSERT INTO character_spell (character_id, spell_id) VALUES (?, ?)";
        try (PreparedStatement lookup = connection.prepareStatement(lookupQuery);
             PreparedStatement insert = connection.prepareStatement(insertQuery)) {
            for (String spellName : spells) {
                lookup.setString(1, spellName);
                ResultSet rs = lookup.executeQuery();
                if (rs.next()) {
                    insert.setLong(1, characterId);
                    insert.setInt(2, rs.getInt("id"));
                    insert.addBatch();
                }
            }
            insert.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving character spells: " + e.getMessage());
        }
    }

    private List<String> getAllSelectedSpells(com.dnd.creator.model.CharacterModel character) {
        Set<String> result = new LinkedHashSet<>();
        if (character.getSelectedCantrips() != null) result.addAll(character.getSelectedCantrips());
        if (character.getSelectedSpells() != null) result.addAll(character.getSelectedSpells());
        return new ArrayList<>(result);
    }

    public List<com.dnd.creator.model.CharacterModel> getAllSavedCharacters() {
        List<com.dnd.creator.model.CharacterModel> characters = new ArrayList<>();
        String query = "SELECT c.id, c.character_name, c.race_name, c.class_name, c.subclass_name, " +
                "c.background_name, c.character_picture, c.level, c.personality_traits, c.ideals, c.bonds, c.flaws, a.name_x, a.name_y, " +
                "cs.strength, cs.dexterity, cs.constitution, cs.intelligence, cs.wisdom, cs.charisma, cs.max_hp " +
                "FROM \"character\" c " +
                "LEFT JOIN character_stats cs ON c.id = cs.character_id " +
                "LEFT JOIN alignment a ON c.alignment_id = a.id " +
                "ORDER BY c.id DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                com.dnd.creator.model.CharacterModel character = new com.dnd.creator.model.CharacterModel();
                long id = rs.getLong("id");
                character.setName(rs.getString("character_name"));
                character.setImagePath(rs.getString("character_picture"));
                character.setStrength(rs.getInt("strength"));
                character.setDexterity(rs.getInt("dexterity"));
                character.setConstitution(rs.getInt("constitution"));
                character.setIntelligence(rs.getInt("intelligence"));
                character.setWisdom(rs.getInt("wisdom"));
                character.setCharisma(rs.getInt("charisma"));

                String raceName = rs.getString("race_name");
                if (raceName != null) character.setRace(getRaceByName(raceName));

                String className = rs.getString("class_name");
                character.setClassIndex(className);
                if (className != null) {
                    Map<String, Object> classData = getClassByIndex(className);
                    if (classData != null) {
                        character.setCharacterClass((String) classData.get("name"));
                        character.setClassHitDie((Integer) classData.get("hit_die"));
                    }
                }

                String nameX = rs.getString("name_x");
                String nameY = rs.getString("name_y");
                if (nameX != null && nameY != null) character.setAlignment(nameX + " " + nameY);

                character.setDbId(id);
                character.setLevel(rs.getInt("level") > 0 ? rs.getInt("level") : 1);
                character.setSubclassName(rs.getString("subclass_name"));
                character.setMaxHp(rs.getInt("max_hp"));
                character.setSelectedBackground(rs.getString("background_name"));
                character.setPersonalityTraits(rs.getString("personality_traits"));
                character.setIdeals(rs.getString("ideals"));
                character.setBonds(rs.getString("bonds"));
                character.setFlaws(rs.getString("flaws"));
                character.setSelectedSkills(getCharacterSkills(id));
                character.setSelectedEquipment(getCharacterEquipment(id));
                character.setSelectedSpells(getCharacterSpells(id, false));
                character.setSelectedCantrips(getCharacterSpells(id, true));
                character.setWeaponAttacks(getCharacterWeaponAttacks(id, character));
                characters.add(character);
            }
        } catch (SQLException e) {
            System.err.println("Error loading characters: " + e.getMessage());
        }
        return characters;
    }

    private List<String> getCharacterSkills(long characterId) {
        List<String> result = new ArrayList<>();
        String query = "SELECT skill_name FROM character_skill WHERE character_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) result.add(rs.getString("skill_name"));
        } catch (SQLException e) {
            System.err.println("Error loading character skills: " + e.getMessage());
        }
        return result;
    }

    private List<String> getCharacterEquipment(long characterId) {
        List<String> result = new ArrayList<>();
        String query = "SELECT item_name FROM character_equipment WHERE character_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) result.add(rs.getString("item_name"));
        } catch (SQLException e) {
            System.err.println("Error loading character equipment: " + e.getMessage());
        }
        return result;
    }

    private List<String> getCharacterSpells(long characterId, boolean cantrips) {
        List<String> result = new ArrayList<>();
        String query = "SELECT s.name FROM character_spell cs JOIN spell s ON cs.spell_id = s.id WHERE cs.character_id = ? AND s.spell_level " + (cantrips ? "= 0" : "> 0");
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) result.add(rs.getString("name"));
        } catch (SQLException e) {
            System.err.println("Error loading character spells: " + e.getMessage());
        }
        return result;
    }


    private List<String[]> getCharacterWeaponAttacks(long characterId, com.dnd.creator.model.CharacterModel character) {
        List<String[]> result = new ArrayList<>();
        String query = "SELECT w.name, w.damage_dice, w.damage_type, w.range_normal " +
                "FROM character_equipment ce " +
                "JOIN weapon w ON w.name = ce.item_name " +
                "WHERE ce.character_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String damageDice = rs.getString("damage_dice");
                String damageType = rs.getString("damage_type");
                boolean isRanged = rs.getObject("range_normal") != null;

                com.dnd.creator.model.Race race = character.getRace();
                int strBase = character.getStrength() + (race != null ? race.getAbilityBonuses().getOrDefault("STR", 0) : 0);
                int dexBase = character.getDexterity() + (race != null ? race.getAbilityBonuses().getOrDefault("DEX", 0) : 0);
                int statMod = isRanged ? (dexBase - 10) / 2 : (strBase - 10) / 2;
                int atkBonus = statMod + 2; // +2 proficiency at level 1

                String atkStr = (atkBonus >= 0 ? "+" : "") + atkBonus;
                String dmgStr = damageDice + " " + damageType;
                result.add(new String[]{name, atkStr, dmgStr});
            }
        } catch (SQLException e) {
            System.err.println("Error loading character weapon attacks: " + e.getMessage());
        }
        return result;
    }

    public List<String[]> getClassFeaturesAtLevel(String className, int level) {
        List<String[]> result = new ArrayList<>();
        String query = "SELECT feature_name, description FROM class_feature " +
                "WHERE class_name = ? AND level = ? ORDER BY feature_name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                result.add(new String[]{rs.getString("feature_name"), rs.getString("description")});
        } catch (SQLException e) {
            System.err.println("Error loading class features: " + e.getMessage());
        }
        return result;
    }

    public List<String[]> getSubclassFeaturesAtLevel(String subclassName, int level) {
        List<String[]> result = new ArrayList<>();
        String query = "SELECT feature_name, description FROM subclass_feature " +
                "WHERE subclass_name = ? AND level = ? ORDER BY feature_name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, subclassName);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                result.add(new String[]{rs.getString("feature_name"), rs.getString("description")});
        } catch (SQLException e) {
            System.err.println("Error loading subclass features: " + e.getMessage());
        }
        return result;
    }

    public List<String[]> getSubclassesForClass(String className) {
        List<String[]> result = new ArrayList<>();
        String query = "SELECT name, description FROM subclass WHERE class_name = ? ORDER BY name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                result.add(new String[]{rs.getString("name"), rs.getString("description")});
        } catch (SQLException e) {
            System.err.println("Error loading subclasses: " + e.getMessage());
        }
        return result;
    }

    public boolean isAsiLevel(String className, int level) {
        String query = "SELECT 1 FROM class_asi_levels WHERE class_name = ? AND level = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, level);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public Map<Integer, Integer> getAllSpellSlotsAtLevel(String className, int characterLevel) {
        Map<Integer, Integer> slots = new LinkedHashMap<>();
        String query = "SELECT slot_level, slots FROM class_spell_slots " +
                "WHERE class_name = ? AND character_level = ? ORDER BY slot_level";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, characterLevel);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) slots.put(rs.getInt("slot_level"), rs.getInt("slots"));
        } catch (SQLException e) {
            System.err.println("Error loading spell slots: " + e.getMessage());
        }
        return slots;
    }

    public Integer getSpellsKnownAtLevel(String className, int level) {
        String query = "SELECT spells_known FROM class_spells_known WHERE class_name = ? AND character_level = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int v = rs.getInt("spells_known");
                return rs.wasNull() ? null : v;
            }
        } catch (SQLException e) {
            System.err.println("Error loading spells_known: " + e.getMessage());
        }
        return null;
    }

    public Integer getCantripsKnownAtLevel(String className, int level) {
        String query = "SELECT cantrips_known FROM class_spells_known WHERE class_name = ? AND character_level = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, level);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int v = rs.getInt("cantrips_known");
                return rs.wasNull() ? null : v;
            }
        } catch (SQLException e) {
            System.err.println("Error loading cantrips_known: " + e.getMessage());
        }
        return null;
    }

    public List<Map<String,String>> getSpellsForClassAndLevel(String className, int spellLevel) {
        List<Map<String,String>> result = new ArrayList<>();
        String query = "SELECT s.name, s.spell_school, s.casting_time, s.range_area, s.duration, s.description " +
                "FROM spell s JOIN class_spell cs ON s.id = cs.spell_id " +
                "WHERE cs.class_name = ? AND s.spell_level = ? ORDER BY s.name";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            stmt.setInt(2, spellLevel);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String,String> sp = new HashMap<>();
                sp.put("name", rs.getString("name"));
                sp.put("school", rs.getString("spell_school"));
                sp.put("casting_time", rs.getString("casting_time"));
                sp.put("range", rs.getString("range_area"));
                sp.put("duration", rs.getString("duration"));
                sp.put("description", rs.getString("description"));
                result.add(sp);
            }
        } catch (SQLException e) {
            System.err.println("Error loading spells: " + e.getMessage());
        }
        return result;
    }

    public int getCharacterMaxHp(long characterId) {
        String query = "SELECT max_hp FROM character_stats WHERE character_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("max_hp");
        } catch (SQLException e) {
            System.err.println("Error loading max_hp: " + e.getMessage());
        }
        return 0;
    }

    public void saveLevelUp(long characterId, int newLevel, int newMaxHp,
                            String subclassChosen,
                            String asi1, int bonus1,
                            String asi2, Integer bonus2,
                            String feat,
                            List<String> newSpells,
                            List<String> newCantrips,
                            List<String> replacedSpells) {
        // 1. Update level
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE \"character\" SET level = ? WHERE id = ?")) {
            stmt.setInt(1, newLevel);
            stmt.setLong(2, characterId);
            stmt.executeUpdate();
        } catch (SQLException e) { System.err.println("LevelUp - level: " + e.getMessage()); }

        // 2. Update max_hp
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE character_stats SET max_hp = ? WHERE character_id = ?")) {
            stmt.setInt(1, newMaxHp);
            stmt.setLong(2, characterId);
            stmt.executeUpdate();
        } catch (SQLException e) { System.err.println("LevelUp - max_hp: " + e.getMessage()); }

        // 3. Subclass (if chosen)
        if (subclassChosen != null && !subclassChosen.isBlank()) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE \"character\" SET subclass_name = ? WHERE id = ?")) {
                stmt.setString(1, subclassChosen);
                stmt.setLong(2, characterId);
                stmt.executeUpdate();
            } catch (SQLException e) { System.err.println("LevelUp - subclass: " + e.getMessage()); }
        }

        // 4. ASI (only if no feat chosen)
        if (asi1 != null && !asi1.isBlank() && (feat == null || feat.isBlank())) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO character_asi (character_id, level_gained, ability_1, bonus_1, ability_2, bonus_2) " +
                            "VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setLong(1, characterId);
                stmt.setInt(2, newLevel);
                stmt.setString(3, asi1);
                stmt.setInt(4, bonus1);
                if (asi2 != null) { stmt.setString(5, asi2); stmt.setInt(6, bonus2); }
                else { stmt.setNull(5, java.sql.Types.VARCHAR); stmt.setNull(6, java.sql.Types.INTEGER); }
                stmt.executeUpdate();
            } catch (SQLException e) { System.err.println("LevelUp - ASI: " + e.getMessage()); }
            applyAsiToStats(characterId, asi1, bonus1);
            if (asi2 != null) applyAsiToStats(characterId, asi2, bonus2);
        }

        // 5. Replaced spells — delete from character_spell
        if (replacedSpells != null) {
            for (String spellName : replacedSpells) {
                try (PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM character_spell WHERE character_id = ? AND spell_id = " +
                                "(SELECT id FROM spell WHERE name = ?)")) {
                    stmt.setLong(1, characterId);
                    stmt.setString(2, spellName);
                    stmt.executeUpdate();
                } catch (SQLException e) { System.err.println("LevelUp - removeSpell: " + e.getMessage()); }
            }
        }

        // 6. New spells + cantrips
        List<String> allNew = new ArrayList<>();
        if (newSpells != null) allNew.addAll(newSpells);
        if (newCantrips != null) allNew.addAll(newCantrips);
        for (String spellName : allNew) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT OR IGNORE INTO character_spell (character_id, spell_id) " +
                            "VALUES (?, (SELECT id FROM spell WHERE name = ?))")) {
                stmt.setLong(1, characterId);
                stmt.setString(2, spellName);
                stmt.executeUpdate();
            } catch (SQLException e) { System.err.println("LevelUp - addSpell: " + e.getMessage()); }
        }

        // 7. Log level up
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO character_level_up (character_id, new_level, hp_gained, subclass_chosen) " +
                        "VALUES (?, ?, ?, ?)")) {
            stmt.setLong(1, characterId);
            stmt.setInt(2, newLevel);
            stmt.setInt(3, 0);
            if (subclassChosen != null) stmt.setString(4, subclassChosen);
            else stmt.setNull(4, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
        } catch (SQLException e) { System.err.println("LevelUp - log: " + e.getMessage()); }
    }

    private void applyAsiToStats(long characterId, String ability, int bonus) {
        String col = switch (ability) {
            case "Strength"     -> "strength";
            case "Dexterity"    -> "dexterity";
            case "Constitution" -> "constitution";
            case "Intelligence" -> "intelligence";
            case "Wisdom"       -> "wisdom";
            case "Charisma"     -> "charisma";
            default -> null;
        };
        if (col == null) return;
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE character_stats SET " + col + " = MIN(" + col + " + ?, 20) WHERE character_id = ?")) {
            stmt.setInt(1, bonus);
            stmt.setLong(2, characterId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error applying ASI to stats: " + e.getMessage());
        }
    }

    public boolean deleteCharacter(long id) {
        // SQLite FK enforcement is off on this connection, so ON DELETE CASCADE
        // does not fire — delete child rows explicitly, then the character row,
        // all within one transaction (mirrors updateCharacter).
        try {
            connection.setAutoCommit(false);
            try (Statement s = connection.createStatement()) {
                s.executeUpdate("DELETE FROM character_stats WHERE character_id = " + id);
                s.executeUpdate("DELETE FROM character_skill WHERE character_id = " + id);
                s.executeUpdate("DELETE FROM character_equipment WHERE character_id = " + id);
                s.executeUpdate("DELETE FROM character_spell WHERE character_id = " + id);
            }

            int affected;
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM \"character\" WHERE id = ?")) {
                stmt.setLong(1, id);
                affected = stmt.executeUpdate();
            }

            connection.commit();
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting character: " + e.getMessage());
            try { connection.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public boolean updateCharacterBackgroundFields(long id, String personality, String ideals, String bonds, String flaws) {
        String sql = "UPDATE \"character\" SET personality_traits = ?, ideals = ?, bonds = ?, flaws = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, personality);
            stmt.setString(2, ideals);
            stmt.setString(3, bonds);
            stmt.setString(4, flaws);
            stmt.setLong(5, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating character background fields: " + e.getMessage());
            return false;
        }
    }
}

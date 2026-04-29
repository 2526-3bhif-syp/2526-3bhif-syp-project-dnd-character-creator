package com.dnd.creator.data;

import com.dnd.creator.model.Race;
import java.io.File;
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
            boolean isNew = !new File("src/main/data/data.db").exists();
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection with Database successful!");
            if (isNew) {
                initializeDatabase();
            }
        } catch (SQLException e) {
            System.out.println("Connection with Database failed!");
        }
    }

    private void initializeDatabase() {
        System.out.println("Database not found — initializing from SQL script...");
        runSqlScript("src/main/data/dnd5e.sql");
        System.out.println("Database initialized successfully!");
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
        String query = "SELECT name FROM race ORDER BY name";
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

        String insertChar = "INSERT INTO \"character\" " +
                "(character_name, race_name, class_name, background_name, alignment_id, level, character_picture) " +
                "VALUES (?, ?, ?, ?, ?, 1, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertChar, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, character.getName());
            stmt.setString(2, character.getRace().getName());
            stmt.setString(3, character.getCharacterClass());
            stmt.setString(4, character.getSelectedBackground());
            if (alignmentId != null) stmt.setInt(5, alignmentId);
            else stmt.setNull(5, java.sql.Types.INTEGER);
            stmt.setString(6, character.getImagePath());

            if (stmt.executeUpdate() == 0) return false;

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    saveCharacterStats(id, character);
                    saveCharacterSkills(id, character.getSelectedSkills());
                    saveCharacterEquipment(id, character.getSelectedEquipment());
                    saveCharacterSpells(id, character.getSelectedSpells());
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving character: " + e.getMessage());
        }
        return false;
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
        String query = "INSERT INTO character_equipment (character_id, item_name) VALUES (?, ?)";
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

    public List<com.dnd.creator.model.CharacterModel> getAllSavedCharacters() {
        List<com.dnd.creator.model.CharacterModel> characters = new ArrayList<>();
        String query = "SELECT c.id, c.character_name, c.race_name, c.class_name, c.background_name, " +
                "c.character_picture, a.name_x, a.name_y, " +
                "cs.strength, cs.dexterity, cs.constitution, cs.intelligence, cs.wisdom, cs.charisma " +
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
                character.setSelectedBackground(rs.getString("background_name"));
                character.setSelectedSkills(getCharacterSkills(id));
                character.setSelectedEquipment(getCharacterEquipment(id));
                character.setSelectedSpells(getCharacterSpells(id));
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

    private List<String> getCharacterSpells(long characterId) {
        List<String> result = new ArrayList<>();
        String query = "SELECT s.name FROM character_spell cs JOIN spell s ON cs.spell_id = s.id WHERE cs.character_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) result.add(rs.getString("name"));
        } catch (SQLException e) {
            System.err.println("Error loading character spells: " + e.getMessage());
        }
        return result;
    }

    public List<String> getAlignments() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM alignments ORDER BY \"index\"";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) result.add(rs.getString("name"));
        } catch (SQLException e) {
            System.err.println("Error loading alignments: " + e.getMessage());
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
}

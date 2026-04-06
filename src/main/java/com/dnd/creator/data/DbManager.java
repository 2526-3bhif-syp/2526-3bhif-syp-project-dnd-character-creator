package com.dnd.creator.data;
import com.dnd.creator.model.Race;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbManager {

    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:src/main/data/data.db";

    public void connect(){

        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection with Database successful!");
        } catch (SQLException e) {

            System.out.println("Connection with Database failed!");
        }
    }

    public List<String> getAllRaces(){

        List<String> result = new ArrayList<>();

        String query= "SELECT name FROM races ORDER BY name";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)){

            while(resultSet.next()){

                result.add(resultSet.getString("name"));

            }


        } catch (SQLException e) {

            System.err.println(e.getMessage());

        }

        return result;
    }

    public Race getRaceByName(String raceName) {
        String query = "SELECT * FROM races WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, raceName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Race race = new Race();
                race.setIndex(rs.getString("index"));
                race.setName(rs.getString("name"));
                race.setSpeed(rs.getInt("speed"));
                race.setAlignment(rs.getString("alignment"));
                race.setAge(rs.getString("age"));
                race.setSize(rs.getString("size"));
                race.setSizeDescription(rs.getString("size_description"));
                race.setLanguageDesc(rs.getString("language_desc"));

                // Load ability bonuses
                loadAbilityBonuses(race);

                // Load languages
                loadLanguages(race);

                // Load traits
                loadTraits(race);

                return race;
            }
        } catch (SQLException e) {
            System.err.println("Error loading race: " + e.getMessage());
        }

        return null;
    }

    private void loadAbilityBonuses(Race race) {
        String query = "SELECT ability_scores.name, races_ability_bonuses.bonus FROM races_ability_bonuses " +
                      "JOIN ability_scores ON races_ability_bonuses.ability_score_index = ability_scores.\"index\" " +
                      "WHERE races_ability_bonuses.races_index = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, race.getIndex());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String abilityName = rs.getString("name");
                int bonus = rs.getInt("bonus");
                System.out.println("Loaded bonus: " + abilityName + " = " + bonus);
                race.addAbilityBonus(abilityName, bonus);
            }
        } catch (SQLException e) {
            System.err.println("Error loading ability bonuses: " + e.getMessage());
        }
    }

    private void loadLanguages(Race race) {
        String query = "SELECT languages.name FROM races_languages " +
                      "JOIN languages ON races_languages.languages_index = languages.\"index\" " +
                      "WHERE races_languages.races_index = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, race.getIndex());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                race.addLanguage(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading languages: " + e.getMessage());
        }
    }

    private void loadTraits(Race race) {
        String query = "SELECT traits.\"index\", traits.name FROM races_traits " +
                      "JOIN traits ON races_traits.traits_index = traits.\"index\" " +
                      "WHERE races_traits.races_index = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, race.getIndex());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String traitIndex = rs.getString("index");
                String traitName = rs.getString("name");
                String traitDesc = getTraitDescription(traitIndex);

                Race.Trait trait = new Race.Trait(traitIndex, traitName, traitDesc);
                race.addTrait(trait);
            }
        } catch (SQLException e) {
            System.err.println("Error loading traits: " + e.getMessage());
        }
    }

    private String getTraitDescription(String traitIndex) {
        String query = "SELECT GROUP_CONCAT(\"value\", ' ') FROM traits_desc WHERE traits_index = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, traitIndex);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullDesc = rs.getString(1);
                if (fullDesc != null && !fullDesc.isEmpty()) {
                    // Kürze die Beschreibung auf max 100 Zeichen
                    if (fullDesc.length() > 100) {
                        return fullDesc.substring(0, 100) + "...";
                    }
                    return fullDesc;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading trait description: " + e.getMessage());
        }

        return "";
    }

    // ===== CLASS METHODS =====
    public List<String> getAllClasses() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name FROM classes ORDER BY name";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                result.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return result;
    }

    public Map<String, Object> getClassByName(String className) {
        String query = "SELECT * FROM classes WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, className);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> classData = new HashMap<>();
                classData.put("index", rs.getString("index"));
                classData.put("name", rs.getString("name"));
                classData.put("hit_die", rs.getInt("hit_die"));
                classData.put("spellcasting_level", rs.getInt("spellcasting_level"));
                classData.put("spellcasting_ability", rs.getString("spellcasting_spellcasting_ability_index"));
                classData.put("has_spells", rs.getInt("spellcasting_level") > 0);

                // Load proficiencies
                classData.put("proficiencies", getClassProficiencies(rs.getString("index")));

                // Load saving throws
                classData.put("saving_throws", getClassSavingThrows(rs.getString("index")));

                return classData;
            }
        } catch (SQLException e) {
            System.err.println("Error loading class: " + e.getMessage());
        }

        return null;
    }

     private List<String> getClassProficiencies(String classIndex) {
         List<String> result = new ArrayList<>();
         String query = "SELECT DISTINCT proficiencies.name FROM classes_proficiencies " +
                       "JOIN proficiencies ON classes_proficiencies.proficiencies_index = proficiencies.\"index\" " +
                       "WHERE classes_proficiencies.classes_index = ?";

         try (PreparedStatement stmt = connection.prepareStatement(query)) {
             stmt.setString(1, classIndex);
             ResultSet rs = stmt.executeQuery();

             while (rs.next()) {
                 result.add(rs.getString("name"));
             }
         } catch (SQLException e) {
             System.err.println("Error loading class proficiencies: " + e.getMessage());
         }

         return result;
     }

     private List<String> getClassSavingThrows(String classIndex) {
         List<String> result = new ArrayList<>();
         String query = "SELECT DISTINCT ability_scores.name FROM classes_saving_throws " +
                       "JOIN ability_scores ON classes_saving_throws.ability_scores_index = ability_scores.\"index\" " +
                       "WHERE classes_saving_throws.classes_index = ?";

         try (PreparedStatement stmt = connection.prepareStatement(query)) {
             stmt.setString(1, classIndex);
             ResultSet rs = stmt.executeQuery();

             while (rs.next()) {
                 result.add(rs.getString("name"));
             }
         } catch (SQLException e) {
             System.err.println("Error loading class saving throws: " + e.getMessage());
         }

         return result;
     }

    public List<String> getClassStartingEquipment(String classIndex) {
        List<String> result = new ArrayList<>();
        String query = "SELECT DISTINCT equipment.name FROM classes_starting_equipment " +
                      "JOIN equipment ON classes_starting_equipment.equipment_index = equipment.\"index\" " +
                      "WHERE classes_starting_equipment.classes_index = ? " +
                      "GROUP BY equipment.\"index\" " +
                      "ORDER BY classes_starting_equipment.order_num";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, classIndex);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null && !name.trim().isEmpty()) {
                    result.add(name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading class starting equipment: " + e.getMessage());
        }

        return result;
    }

    public List<Map<String, Object>> getClassSkillProficiencies(String classIndex) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = "SELECT describe, choose FROM classes_proficiency_choices " +
                      "WHERE classes_index = ? " +
                      "ORDER BY order_num";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, classIndex);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> skillChoice = new HashMap<>();
                skillChoice.put("description", rs.getString("describe"));
                skillChoice.put("choose", rs.getInt("choose"));
                result.add(skillChoice);
            }
        } catch (SQLException e) {
            System.err.println("Error loading class skill proficiencies: " + e.getMessage());
        }

        return result;
    }

    // ===== SPELL METHODS =====
    public List<Map<String, Object>> getSpellsByClass(String classIndex, int maxLevel) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = "SELECT DISTINCT spells.\"index\", spells.name, spells.level, " +
                      "spells.casting_time, spells.range " +
                      "FROM spells " +
                      "JOIN spells_classes ON spells.\"index\" = spells_classes.spells_index " +
                      "WHERE spells_classes.classes_index = ? " +
                      "AND spells.level <= ? " +
                      "ORDER BY spells.level, spells.name";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, classIndex);
            stmt.setInt(2, maxLevel);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> spell = new HashMap<>();
                spell.put("index", rs.getString("index"));
                spell.put("name", rs.getString("name"));
                spell.put("level", rs.getInt("level"));
                spell.put("casting_time", rs.getString("casting_time"));
                spell.put("range", rs.getString("range"));
                spell.put("description", getSpellDescription(rs.getString("index")));
                result.add(spell);
            }
        } catch (SQLException e) {
            System.err.println("Error loading spells by class: " + e.getMessage());
        }

        return result;
    }

    public List<Map<String, Object>> getLevel1Spells(String classIndex) {
        return getSpellsByClass(classIndex, 1);
    }

    private String getSpellDescription(String spellIndex) {
        String query = "SELECT GROUP_CONCAT(\"value\", ' ') FROM spells_desc WHERE spells_index = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, spellIndex);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullDesc = rs.getString(1);
                if (fullDesc != null && !fullDesc.isEmpty()) {
                    // Kürze die Beschreibung auf max 150 Zeichen
                    if (fullDesc.length() > 150) {
                        return fullDesc.substring(0, 150) + "...";
                    }
                    return fullDesc;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading spell description: " + e.getMessage());
        }

        return "";
    }

    public Map<String, Object> getSpellDetails(String spellIndex) {
        String query = "SELECT * FROM spells WHERE \"index\" = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, spellIndex);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, Object> spell = new HashMap<>();
                spell.put("index", rs.getString("index"));
                spell.put("name", rs.getString("name"));
                spell.put("level", rs.getInt("level"));
                spell.put("casting_time", rs.getString("casting_time"));
                spell.put("range", rs.getString("range"));
                spell.put("duration", rs.getString("duration"));
                spell.put("concentration", rs.getInt("concentration") > 0);
                spell.put("ritual", rs.getInt("ritual") > 0);
                spell.put("material", rs.getString("material"));
                spell.put("description", getSpellDescription(spellIndex));
                return spell;
            }
        } catch (SQLException e) {
            System.err.println("Error loading spell details: " + e.getMessage());
        }

        return null;
    }

    // ===== EQUIPMENT METHODS =====
    public List<Map<String, Object>> getEquipmentOptions(String classIndex) {
        List<Map<String, Object>> result = new ArrayList<>();
        String query = "SELECT id, order_num, \"desc\", choose FROM classes_starting_equipment_options " +
                      "WHERE classes_index = ? " +
                      "ORDER BY order_num";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, classIndex);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> option = new HashMap<>();
                option.put("id", rs.getInt("id"));
                option.put("order_num", rs.getInt("order_num"));
                option.put("description", rs.getString("desc"));
                option.put("choose", rs.getInt("choose"));
                result.add(option);
            }
        } catch (SQLException e) {
            System.err.println("Error loading equipment options: " + e.getMessage());
        }

        return result;
    }

    public List<String> getStartingEquipment(String classIndex) {
        List<String> result = new ArrayList<>();
        String query = "SELECT DISTINCT equipment.name FROM classes_starting_equipment " +
                      "JOIN equipment ON classes_starting_equipment.equipment_index = equipment.\"index\" " +
                      "WHERE classes_starting_equipment.classes_index = ? " +
                      "GROUP BY equipment.\"index\" " +
                      "ORDER BY classes_starting_equipment.order_num";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, classIndex);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                if (name != null && !name.trim().isEmpty()) {
                    result.add(name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading starting equipment: " + e.getMessage());
        }

        return result;
    }
}

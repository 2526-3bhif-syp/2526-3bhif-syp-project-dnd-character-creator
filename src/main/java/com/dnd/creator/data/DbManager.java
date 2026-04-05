package com.dnd.creator.data;
import com.dnd.creator.model.Race;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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




}

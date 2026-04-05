DROP TABLE IF EXISTS ability_scores;
CREATE TABLE ability_scores (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  full_name TEXT,
  url TEXT
);

DROP TABLE IF EXISTS ability_scores_desc;
CREATE TABLE ability_scores_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ability_scores_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (ability_scores_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS ability_scores_skills;
CREATE TABLE ability_scores_skills (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ability_scores_index TEXT,
  order_num INTEGER,
  skills_index TEXT,
  FOREIGN KEY (ability_scores_index) REFERENCES ability_scores("index"),
  FOREIGN KEY (skills_index) REFERENCES skills("index")
);

DROP TABLE IF EXISTS alignments;
CREATE TABLE alignments (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  abbreviation TEXT,
  "desc" TEXT,
  url TEXT
);

DROP TABLE IF EXISTS backgrounds;
CREATE TABLE backgrounds (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  language_options_choose INTEGER,
  language_options_type TEXT,
  language_options_from_option_set_type TEXT,
  language_options_from_resource_list_url TEXT,
  feature_name TEXT,
  personality_traits_choose INTEGER,
  personality_traits_type TEXT,
  personality_traits_from_option_set_type TEXT,
  ideals_choose INTEGER,
  ideals_type TEXT,
  ideals_from_option_set_type TEXT,
  bonds_choose INTEGER,
  bonds_type TEXT,
  bonds_from_option_set_type TEXT,
  flaws_choose INTEGER,
  flaws_type TEXT,
  flaws_from_option_set_type TEXT,
  url TEXT
);

DROP TABLE IF EXISTS backgrounds_starting_proficiencies;
CREATE TABLE backgrounds_starting_proficiencies (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backgrounds_index TEXT,
  order_num INTEGER,
  proficiencies_index TEXT,
  FOREIGN KEY (backgrounds_index) REFERENCES backgrounds("index"),
  FOREIGN KEY (proficiencies_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS backgrounds_starting_equipment;
CREATE TABLE backgrounds_starting_equipment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backgrounds_index TEXT,
  order_num INTEGER,
  equipment_index TEXT,
  quantity INTEGER,
  FOREIGN KEY (backgrounds_index) REFERENCES backgrounds("index"),
  FOREIGN KEY (equipment_index) REFERENCES equipment("index")
);

DROP TABLE IF EXISTS backgrounds_starting_equipment_options;
CREATE TABLE backgrounds_starting_equipment_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backgrounds_index TEXT,
  order_num INTEGER,
  choose INTEGER,
  "type" TEXT,
  from_option_set_type TEXT,
  from_equipment_category_index TEXT,
  FOREIGN KEY (backgrounds_index) REFERENCES backgrounds("index"),
  FOREIGN KEY (from_equipment_category_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS backgrounds_desc;
CREATE TABLE backgrounds_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backgrounds_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (backgrounds_index) REFERENCES backgrounds("index")
);

DROP TABLE IF EXISTS backgrounds_options;
CREATE TABLE backgrounds_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backgrounds_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  string TEXT,
  "desc" TEXT,
  FOREIGN KEY (backgrounds_index) REFERENCES backgrounds("index")
);

DROP TABLE IF EXISTS backgrounds_options_alignments;
CREATE TABLE backgrounds_options_alignments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  backgrounds_options_index TEXT,
  order_num INTEGER,
  alignments_index TEXT,
  FOREIGN KEY (backgrounds_options_index) REFERENCES backgrounds_options("index"),
  FOREIGN KEY (alignments_index) REFERENCES alignments("index")
);

DROP TABLE IF EXISTS classes;
CREATE TABLE classes (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  hit_die INTEGER,
  class_levels TEXT,
  url TEXT,
  spellcasting_level INTEGER,
  spellcasting_spellcasting_ability_index TEXT,
  spells TEXT,
  multi_classing_prerequisite_options_type TEXT,
  multi_classing_prerequisite_options_choose INTEGER,
  multi_classing_prerequisite_options_from_option_set_type TEXT,
  FOREIGN KEY (spellcasting_spellcasting_ability_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS classes_proficiency_choices;
CREATE TABLE classes_proficiency_choices (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  "desc" TEXT,
  choose INTEGER,
  "type" TEXT,
  from_option_set_type TEXT,
  FOREIGN KEY (classes_index) REFERENCES classes("index")
);

DROP TABLE IF EXISTS classes_proficiency_choices_options;
CREATE TABLE classes_proficiency_choices_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_proficiency_choices_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  choice_desc TEXT,
  choice_type TEXT,
  choice_choose INTEGER,
  choice_from_option_set_type TEXT,
  FOREIGN KEY (classes_proficiency_choices_index) REFERENCES classes_proficiency_choices("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS classes_proficiencies;
CREATE TABLE classes_proficiencies (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  proficiencies_index TEXT,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (proficiencies_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS classes_saving_throws;
CREATE TABLE classes_saving_throws (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  ability_scores_index TEXT,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (ability_scores_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS classes_starting_equipment;
CREATE TABLE classes_starting_equipment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  equipment_index TEXT,
  quantity INTEGER,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (equipment_index) REFERENCES equipment("index")
);

DROP TABLE IF EXISTS classes_starting_equipment_options;
CREATE TABLE classes_starting_equipment_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  "desc" TEXT,
  choose INTEGER,
  "type" TEXT,
  from_option_set_type TEXT,
  from_equipment_category_index TEXT,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (from_equipment_category_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS classes_starting_equipment_options_options;
CREATE TABLE classes_starting_equipment_options_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_starting_equipment_options_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  count INTEGER,
  of_index TEXT,
  choice_desc TEXT,
  choice_choose INTEGER,
  choice_type TEXT,
  choice_from_option_set_type TEXT,
  choice_from_equipment_category_index TEXT,
  FOREIGN KEY (classes_starting_equipment_options_index) REFERENCES classes_starting_equipment_options("index"),
  FOREIGN KEY (of_index) REFERENCES equipment("index"),
  FOREIGN KEY (choice_from_equipment_category_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS classes_prerequisites;
CREATE TABLE classes_prerequisites (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  ability_score_index TEXT,
  minimum_score INTEGER,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS classes_subclasses;
CREATE TABLE classes_subclasses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  subclasses_index TEXT,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (subclasses_index) REFERENCES subclasses("index")
);

DROP TABLE IF EXISTS classes_info;
CREATE TABLE classes_info (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  name TEXT,
  FOREIGN KEY (classes_index) REFERENCES classes("index")
);

DROP TABLE IF EXISTS classes_info_desc;
CREATE TABLE classes_info_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_info_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (classes_info_index) REFERENCES classes_info("index")
);

DROP TABLE IF EXISTS classes_starting_equipment_options_options_prerequisites;
CREATE TABLE classes_starting_equipment_options_options_prerequisites (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_starting_equipment_options_options_index TEXT,
  order_num INTEGER,
  "type" TEXT,
  proficiency_index TEXT,
  FOREIGN KEY (classes_starting_equipment_options_options_index) REFERENCES classes_starting_equipment_options_options("index"),
  FOREIGN KEY (proficiency_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS classes_starting_equipment_options_options_items;
CREATE TABLE classes_starting_equipment_options_options_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_starting_equipment_options_options_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  count INTEGER,
  of_index TEXT,
  choice_desc TEXT,
  choice_choose INTEGER,
  choice_type TEXT,
  choice_from_option_set_type TEXT,
  choice_from_equipment_category_index TEXT,
  FOREIGN KEY (classes_starting_equipment_options_options_index) REFERENCES classes_starting_equipment_options_options("index"),
  FOREIGN KEY (of_index) REFERENCES equipment("index"),
  FOREIGN KEY (choice_from_equipment_category_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS classes_options;
CREATE TABLE classes_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  ability_score_index TEXT,
  minimum_score INTEGER,
  FOREIGN KEY (classes_index) REFERENCES classes("index"),
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS classes_proficiency_choices_options_options;
CREATE TABLE classes_proficiency_choices_options_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  classes_proficiency_choices_options_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  FOREIGN KEY (classes_proficiency_choices_options_index) REFERENCES classes_proficiency_choices_options("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS conditions;
CREATE TABLE conditions (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  url TEXT
);

DROP TABLE IF EXISTS conditions_desc;
CREATE TABLE conditions_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  conditions_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (conditions_index) REFERENCES conditions("index")
);

DROP TABLE IF EXISTS damage_types;
CREATE TABLE damage_types (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  url TEXT
);

DROP TABLE IF EXISTS damage_types_desc;
CREATE TABLE damage_types_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  damage_types_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (damage_types_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS equipment_categories;
CREATE TABLE equipment_categories (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  url TEXT
);

DROP TABLE IF EXISTS equipment_categories_equipment;
CREATE TABLE equipment_categories_equipment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  equipment_categories_index TEXT,
  order_num INTEGER,
  equipment_index TEXT,
  magic_items_index TEXT,
  FOREIGN KEY (equipment_categories_index) REFERENCES equipment_categories("index"),
  FOREIGN KEY (equipment_index) REFERENCES equipment("index"),
  FOREIGN KEY (magic_items_index) REFERENCES magic_items("index")
);

DROP TABLE IF EXISTS equipment;
CREATE TABLE equipment (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  equipment_category_index TEXT,
  weapon_category TEXT,
  weapon_range TEXT,
  category_range TEXT,
  cost_quantity INTEGER,
  cost_unit TEXT,
  damage_damage_dice TEXT,
  damage_damage_type_index TEXT,
  range_normal INTEGER,
  weight INTEGER,
  url TEXT,
  throw_range_normal INTEGER,
  throw_range_long INTEGER,
  two_handed_damage_damage_dice TEXT,
  two_handed_damage_damage_type_index TEXT,
  range_long INTEGER,
  image TEXT,
  armor_category TEXT,
  armor_class_base INTEGER,
  armor_class_dex_bonus INTEGER,
  str_minimum INTEGER,
  stealth_disadvantage INTEGER,
  armor_class_max_bonus INTEGER,
  gear_category_index TEXT,
  quantity INTEGER,
  tool_category TEXT,
  vehicle_category TEXT,
  speed_quantity INTEGER,
  speed_unit TEXT,
  capacity TEXT,
  FOREIGN KEY (equipment_category_index) REFERENCES equipment_categories("index"),
  FOREIGN KEY (damage_damage_type_index) REFERENCES damage_types("index"),
  FOREIGN KEY (two_handed_damage_damage_type_index) REFERENCES damage_types("index"),
  FOREIGN KEY (gear_category_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS equipment_properties;
CREATE TABLE equipment_properties (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  equipment_index TEXT,
  order_num INTEGER,
  weapon_properties_index TEXT,
  FOREIGN KEY (equipment_index) REFERENCES equipment("index"),
  FOREIGN KEY (weapon_properties_index) REFERENCES weapon_properties("index")
);

DROP TABLE IF EXISTS equipment_special;
CREATE TABLE equipment_special (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  equipment_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (equipment_index) REFERENCES equipment("index")
);

DROP TABLE IF EXISTS equipment_desc;
CREATE TABLE equipment_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  equipment_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (equipment_index) REFERENCES equipment("index")
);

DROP TABLE IF EXISTS equipment_contents;
CREATE TABLE equipment_contents (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  equipment_index TEXT,
  order_num INTEGER,
  item_index TEXT,
  quantity INTEGER,
  FOREIGN KEY (equipment_index) REFERENCES equipment("index"),
  FOREIGN KEY (item_index) REFERENCES equipment("index")
);

DROP TABLE IF EXISTS feats;
CREATE TABLE feats (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  url TEXT
);

DROP TABLE IF EXISTS feats_prerequisites;
CREATE TABLE feats_prerequisites (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  feats_index TEXT,
  order_num INTEGER,
  ability_score_index TEXT,
  minimum_score INTEGER,
  FOREIGN KEY (feats_index) REFERENCES feats("index"),
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS feats_desc;
CREATE TABLE feats_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  feats_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (feats_index) REFERENCES feats("index")
);

DROP TABLE IF EXISTS features;
CREATE TABLE features (
  "index" TEXT PRIMARY KEY,
  class_index TEXT,
  name TEXT,
  level INTEGER,
  url TEXT,
  subclass_index TEXT,
  reference TEXT,
  feature_specific_expertise_options_choose INTEGER,
  feature_specific_expertise_options_type TEXT,
  feature_specific_expertise_options_from_option_set_type TEXT,
  feature_specific_subfeature_options_choose INTEGER,
  feature_specific_subfeature_options_type TEXT,
  feature_specific_subfeature_options_from_option_set_type TEXT,
  parent_index TEXT,
  feature_specific_enemy_type_options_desc TEXT,
  feature_specific_enemy_type_options_choose INTEGER,
  feature_specific_enemy_type_options_type TEXT,
  feature_specific_enemy_type_options_from_option_set_type TEXT,
  feature_specific_terrain_type_options_desc TEXT,
  feature_specific_terrain_type_options_choose INTEGER,
  feature_specific_terrain_type_options_type TEXT,
  feature_specific_terrain_type_options_from_option_set_type TEXT,
  FOREIGN KEY (class_index) REFERENCES classes("index"),
  FOREIGN KEY (subclass_index) REFERENCES subclasses("index"),
  FOREIGN KEY (parent_index) REFERENCES features("index")
);

DROP TABLE IF EXISTS features_desc;
CREATE TABLE features_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (features_index) REFERENCES features("index")
);

DROP TABLE IF EXISTS features_options;
CREATE TABLE features_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  "value" TEXT,
  choice_choose INTEGER,
  choice_type TEXT,
  choice_from_option_set_type TEXT,
  FOREIGN KEY (features_index) REFERENCES features("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS features_options_options;
CREATE TABLE features_options_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_options_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  FOREIGN KEY (features_options_index) REFERENCES features_options("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS features_options_items;
CREATE TABLE features_options_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_options_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  choice_choose INTEGER,
  choice_type TEXT,
  choice_from_option_set_type TEXT,
  item_index TEXT,
  FOREIGN KEY (features_options_index) REFERENCES features_options("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS features_options_items_options;
CREATE TABLE features_options_items_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_options_items_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  FOREIGN KEY (features_options_items_index) REFERENCES features_options_items("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS features_invocations;
CREATE TABLE features_invocations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_index TEXT,
  order_num INTEGER,
  FOREIGN KEY (features_index) REFERENCES features("index")
);

DROP TABLE IF EXISTS features_prerequisites;
CREATE TABLE features_prerequisites (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  features_index TEXT,
  order_num INTEGER,
  "type" TEXT,
  spell TEXT,
  feature TEXT,
  level INTEGER,
  FOREIGN KEY (features_index) REFERENCES features("index")
);

DROP TABLE IF EXISTS languages;
CREATE TABLE languages (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  "type" TEXT,
  script TEXT,
  url TEXT,
  "desc" TEXT
);

DROP TABLE IF EXISTS languages_typical_speakers;
CREATE TABLE languages_typical_speakers (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  languages_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (languages_index) REFERENCES languages("index")
);

DROP TABLE IF EXISTS levels;
CREATE TABLE levels (
  "index" TEXT PRIMARY KEY,
  level INTEGER,
  ability_score_bonuses INTEGER,
  prof_bonus INTEGER,
  class_specific_rage_count INTEGER,
  class_specific_rage_damage_bonus INTEGER,
  class_specific_brutal_critical_dice INTEGER,
  class_index TEXT,
  url TEXT,
  spellcasting_cantrips_known INTEGER,
  spellcasting_spells_known INTEGER,
  spellcasting_spell_slots_level_1 INTEGER,
  spellcasting_spell_slots_level_2 INTEGER,
  spellcasting_spell_slots_level_3 INTEGER,
  spellcasting_spell_slots_level_4 INTEGER,
  spellcasting_spell_slots_level_5 INTEGER,
  spellcasting_spell_slots_level_6 INTEGER,
  spellcasting_spell_slots_level_7 INTEGER,
  spellcasting_spell_slots_level_8 INTEGER,
  spellcasting_spell_slots_level_9 INTEGER,
  class_specific_bardic_inspiration_die INTEGER,
  class_specific_song_of_rest_die INTEGER,
  class_specific_magical_secrets_max_5 INTEGER,
  class_specific_magical_secrets_max_7 INTEGER,
  class_specific_magical_secrets_max_9 INTEGER,
  class_specific_channel_divinity_charges INTEGER,
  class_specific_destroy_undead_cr INTEGER,
  class_specific_wild_shape_max_cr INTEGER,
  class_specific_wild_shape_swim INTEGER,
  class_specific_wild_shape_fly INTEGER,
  class_specific_action_surges INTEGER,
  class_specific_indomitable_uses INTEGER,
  class_specific_extra_attacks INTEGER,
  class_specific_martial_arts_dice_count INTEGER,
  class_specific_martial_arts_dice_value INTEGER,
  class_specific_ki_points INTEGER,
  class_specific_unarmored_movement INTEGER,
  class_specific_aura_range INTEGER,
  class_specific_favored_enemies INTEGER,
  class_specific_favored_terrain INTEGER,
  class_specific_sneak_attack_dice_count INTEGER,
  class_specific_sneak_attack_dice_value INTEGER,
  class_specific_sorcery_points INTEGER,
  class_specific_metamagic_known INTEGER,
  class_specific_invocations_known INTEGER,
  class_specific_mystic_arcanum_level_6 INTEGER,
  class_specific_mystic_arcanum_level_7 INTEGER,
  class_specific_mystic_arcanum_level_8 INTEGER,
  class_specific_mystic_arcanum_level_9 INTEGER,
  class_specific_arcane_recovery_levels INTEGER,
  subclass_index TEXT,
  subclass_specific_additional_magical_secrets_max_lvl INTEGER,
  subclass_specific_aura_range INTEGER,
  FOREIGN KEY (class_index) REFERENCES classes("index"),
  FOREIGN KEY (subclass_index) REFERENCES subclasses("index")
);

DROP TABLE IF EXISTS levels_features;
CREATE TABLE levels_features (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  levels_index TEXT,
  order_num INTEGER,
  features_index TEXT,
  FOREIGN KEY (levels_index) REFERENCES levels("index"),
  FOREIGN KEY (features_index) REFERENCES features("index")
);

DROP TABLE IF EXISTS levels_creating_spell_slots;
CREATE TABLE levels_creating_spell_slots (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  levels_index TEXT,
  order_num INTEGER,
  spell_slot_level INTEGER,
  sorcery_point_cost INTEGER,
  FOREIGN KEY (levels_index) REFERENCES levels("index")
);

DROP TABLE IF EXISTS magic_items;
CREATE TABLE magic_items (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  equipment_category_index TEXT,
  rarity_name TEXT,
  variant INTEGER,
  image TEXT,
  url TEXT,
  FOREIGN KEY (equipment_category_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS magic_items_desc;
CREATE TABLE magic_items_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  magic_items_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (magic_items_index) REFERENCES magic_items("index")
);

DROP TABLE IF EXISTS magic_items_variants;
CREATE TABLE magic_items_variants (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  magic_items_index TEXT,
  order_num INTEGER,
  FOREIGN KEY (magic_items_index) REFERENCES magic_items("index")
);

DROP TABLE IF EXISTS magic_schools;
CREATE TABLE magic_schools (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  "desc" TEXT,
  url TEXT
);

DROP TABLE IF EXISTS monsters;
CREATE TABLE monsters (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  size TEXT,
  "type" TEXT,
  alignment TEXT,
  hit_points INTEGER,
  hit_dice TEXT,
  hit_points_roll TEXT,
  speed_walk TEXT,
  speed_swim TEXT,
  strength INTEGER,
  dexterity INTEGER,
  constitution INTEGER,
  intelligence INTEGER,
  wisdom INTEGER,
  charisma INTEGER,
  senses_darkvision TEXT,
  senses_passive_perception INTEGER,
  languages TEXT,
  challenge_rating INTEGER,
  proficiency_bonus INTEGER,
  xp INTEGER,
  image TEXT,
  url TEXT,
  "desc" TEXT,
  subtype TEXT,
  speed_fly TEXT,
  senses_blindsight TEXT,
  speed_burrow TEXT,
  speed_climb TEXT,
  speed_hover INTEGER,
  senses_truesight TEXT,
  senses_tremorsense TEXT
);

DROP TABLE IF EXISTS monsters_armor_class;
CREATE TABLE monsters_armor_class (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  "type" TEXT,
  "value" INTEGER,
  condition_index TEXT,
  spell_index TEXT,
  "desc" TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (condition_index) REFERENCES conditions("index"),
  FOREIGN KEY (spell_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS monsters_proficiencies;
CREATE TABLE monsters_proficiencies (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  "value" INTEGER,
  proficiency_index TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (proficiency_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS monsters_special_abilities;
CREATE TABLE monsters_special_abilities (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  name TEXT,
  "desc" TEXT,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  spellcasting_level INTEGER,
  spellcasting_ability_index TEXT,
  spellcasting_dc INTEGER,
  spellcasting_modifier INTEGER,
  spellcasting_school TEXT,
  usage_type TEXT,
  usage_times INTEGER,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index"),
  FOREIGN KEY (spellcasting_ability_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_actions;
CREATE TABLE monsters_actions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  name TEXT,
  multiattack_type TEXT,
  "desc" TEXT,
  attack_bonus INTEGER,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  usage_type TEXT,
  usage_times INTEGER,
  usage_dice TEXT,
  usage_min_value INTEGER,
  options_choose INTEGER,
  options_type TEXT,
  options_from_option_set_type TEXT,
  action_options_choose INTEGER,
  action_options_type TEXT,
  action_options_from_option_set_type TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_actions_actions;
CREATE TABLE monsters_actions_actions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_index TEXT,
  order_num INTEGER,
  action_name TEXT,
  count INTEGER,
  "type" TEXT,
  FOREIGN KEY (monsters_actions_index) REFERENCES monsters_actions("index")
);

DROP TABLE IF EXISTS monsters_actions_damage;
CREATE TABLE monsters_actions_damage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_index TEXT,
  order_num INTEGER,
  damage_type_index TEXT,
  damage_dice TEXT,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  choose INTEGER,
  "type" TEXT,
  from_option_set_type TEXT,
  FOREIGN KEY (monsters_actions_index) REFERENCES monsters_actions("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_legendary_actions;
CREATE TABLE monsters_legendary_actions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  name TEXT,
  "desc" TEXT,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  attack_bonus INTEGER,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_legendary_actions_damage;
CREATE TABLE monsters_legendary_actions_damage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_legendary_actions_index TEXT,
  order_num INTEGER,
  damage_type_index TEXT,
  damage_dice TEXT,
  FOREIGN KEY (monsters_legendary_actions_index) REFERENCES monsters_legendary_actions("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS monsters_special_abilities_components_required;
CREATE TABLE monsters_special_abilities_components_required (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_special_abilities_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_special_abilities_index) REFERENCES monsters_special_abilities("index")
);

DROP TABLE IF EXISTS monsters_special_abilities_slots;
CREATE TABLE monsters_special_abilities_slots (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_special_abilities_index TEXT,
  key_level INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_special_abilities_index) REFERENCES monsters_special_abilities("index")
);

DROP TABLE IF EXISTS monsters_special_abilities_spells;
CREATE TABLE monsters_special_abilities_spells (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_special_abilities_index TEXT,
  order_num INTEGER,
  spells_index TEXT,
  FOREIGN KEY (monsters_special_abilities_index) REFERENCES monsters_special_abilities("index"),
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS monsters_damage_immunities;
CREATE TABLE monsters_damage_immunities (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index")
);

DROP TABLE IF EXISTS monsters_actions_options;
CREATE TABLE monsters_actions_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  name TEXT,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  action_name TEXT,
  count INTEGER,
  "type" TEXT,
  "desc" TEXT,
  FOREIGN KEY (monsters_actions_index) REFERENCES monsters_actions("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_actions_options_damage;
CREATE TABLE monsters_actions_options_damage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_options_index TEXT,
  order_num INTEGER,
  damage_type_index TEXT,
  damage_dice TEXT,
  FOREIGN KEY (monsters_actions_options_index) REFERENCES monsters_actions_options("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS monsters_condition_immunities;
CREATE TABLE monsters_condition_immunities (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  conditions_index TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (conditions_index) REFERENCES conditions("index")
);

DROP TABLE IF EXISTS monsters_damage_resistances;
CREATE TABLE monsters_damage_resistances (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index")
);

DROP TABLE IF EXISTS monsters_actions_attacks;
CREATE TABLE monsters_actions_attacks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_index TEXT,
  order_num INTEGER,
  name TEXT,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  FOREIGN KEY (monsters_actions_index) REFERENCES monsters_actions("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_actions_attacks_damage;
CREATE TABLE monsters_actions_attacks_damage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_attacks_index TEXT,
  order_num INTEGER,
  damage_type_index TEXT,
  damage_dice TEXT,
  FOREIGN KEY (monsters_actions_attacks_index) REFERENCES monsters_actions_attacks("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS monsters_armor_class_armor;
CREATE TABLE monsters_armor_class_armor (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_armor_class_index TEXT,
  order_num INTEGER,
  equipment_index TEXT,
  FOREIGN KEY (monsters_armor_class_index) REFERENCES monsters_armor_class("index"),
  FOREIGN KEY (equipment_index) REFERENCES equipment("index")
);

DROP TABLE IF EXISTS monsters_damage_vulnerabilities;
CREATE TABLE monsters_damage_vulnerabilities (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index")
);

DROP TABLE IF EXISTS monsters_special_abilities_damage;
CREATE TABLE monsters_special_abilities_damage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_special_abilities_index TEXT,
  order_num INTEGER,
  damage_type_index TEXT,
  damage_dice TEXT,
  FOREIGN KEY (monsters_special_abilities_index) REFERENCES monsters_special_abilities("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS monsters_actions_options_items;
CREATE TABLE monsters_actions_options_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_options_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  action_name TEXT,
  count INTEGER,
  "type" TEXT,
  "desc" TEXT,
  FOREIGN KEY (monsters_actions_options_index) REFERENCES monsters_actions_options("index")
);

DROP TABLE IF EXISTS monsters_reactions;
CREATE TABLE monsters_reactions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  name TEXT,
  "desc" TEXT,
  dc_dc_type_index TEXT,
  dc_dc_value INTEGER,
  dc_success_type TEXT,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS monsters_special_abilities_rest_types;
CREATE TABLE monsters_special_abilities_rest_types (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_special_abilities_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_special_abilities_index) REFERENCES monsters_special_abilities("index")
);

DROP TABLE IF EXISTS monsters_actions_rest_types;
CREATE TABLE monsters_actions_rest_types (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (monsters_actions_index) REFERENCES monsters_actions("index")
);

DROP TABLE IF EXISTS monsters_actions_damage_options;
CREATE TABLE monsters_actions_damage_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_actions_damage_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  damage_type_index TEXT,
  damage_dice TEXT,
  notes TEXT,
  FOREIGN KEY (monsters_actions_damage_index) REFERENCES monsters_actions_damage("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS monsters_forms;
CREATE TABLE monsters_forms (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  monsters_index TEXT,
  order_num INTEGER,
  FOREIGN KEY (monsters_index) REFERENCES monsters("index")
);

DROP TABLE IF EXISTS proficiencies;
CREATE TABLE proficiencies (
  "index" TEXT PRIMARY KEY,
  "type" TEXT,
  name TEXT,
  url TEXT,
  reference_index TEXT,
  FOREIGN KEY (reference_index) REFERENCES equipment_categories("index")
);

DROP TABLE IF EXISTS proficiencies_classes;
CREATE TABLE proficiencies_classes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  proficiencies_index TEXT,
  order_num INTEGER,
  classes_index TEXT,
  FOREIGN KEY (proficiencies_index) REFERENCES proficiencies("index"),
  FOREIGN KEY (classes_index) REFERENCES classes("index")
);

DROP TABLE IF EXISTS proficiencies_races;
CREATE TABLE proficiencies_races (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  proficiencies_index TEXT,
  order_num INTEGER,
  races_index TEXT,
  subraces_index TEXT,
  FOREIGN KEY (proficiencies_index) REFERENCES proficiencies("index"),
  FOREIGN KEY (races_index) REFERENCES races("index"),
  FOREIGN KEY (subraces_index) REFERENCES subraces("index")
);

DROP TABLE IF EXISTS races;
CREATE TABLE races (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  speed INTEGER,
  alignment TEXT,
  age TEXT,
  size TEXT,
  size_description TEXT,
  language_desc TEXT,
  url TEXT,
  language_options_choose INTEGER,
  language_options_type TEXT,
  language_options_from_option_set_type TEXT,
  ability_bonus_options_choose INTEGER,
  ability_bonus_options_type TEXT,
  ability_bonus_options_from_option_set_type TEXT
);

DROP TABLE IF EXISTS races_ability_bonuses;
CREATE TABLE races_ability_bonuses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  races_index TEXT,
  order_num INTEGER,
  ability_score_index TEXT,
  bonus INTEGER,
  FOREIGN KEY (races_index) REFERENCES races("index"),
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS races_languages;
CREATE TABLE races_languages (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  races_index TEXT,
  order_num INTEGER,
  languages_index TEXT,
  FOREIGN KEY (races_index) REFERENCES races("index"),
  FOREIGN KEY (languages_index) REFERENCES languages("index")
);

DROP TABLE IF EXISTS races_traits;
CREATE TABLE races_traits (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  races_index TEXT,
  order_num INTEGER,
  traits_index TEXT,
  FOREIGN KEY (races_index) REFERENCES races("index"),
  FOREIGN KEY (traits_index) REFERENCES traits("index")
);

DROP TABLE IF EXISTS races_subraces;
CREATE TABLE races_subraces (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  races_index TEXT,
  order_num INTEGER,
  subraces_index TEXT,
  FOREIGN KEY (races_index) REFERENCES races("index"),
  FOREIGN KEY (subraces_index) REFERENCES subraces("index")
);

DROP TABLE IF EXISTS races_options;
CREATE TABLE races_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  races_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  ability_score_index TEXT,
  bonus INTEGER,
  FOREIGN KEY (races_index) REFERENCES races("index"),
  FOREIGN KEY (item_index) REFERENCES languages("index"),
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS rule_sections;
CREATE TABLE rule_sections (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  "desc" TEXT,
  url TEXT
);

DROP TABLE IF EXISTS rules;
CREATE TABLE rules (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  "desc" TEXT,
  url TEXT
);

DROP TABLE IF EXISTS rules_subsections;
CREATE TABLE rules_subsections (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  rules_index TEXT,
  order_num INTEGER,
  rule_sections_index TEXT,
  FOREIGN KEY (rules_index) REFERENCES rules("index"),
  FOREIGN KEY (rule_sections_index) REFERENCES rule_sections("index")
);

DROP TABLE IF EXISTS skills;
CREATE TABLE skills (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  ability_score_index TEXT,
  url TEXT,
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS skills_desc;
CREATE TABLE skills_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  skills_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (skills_index) REFERENCES skills("index")
);

DROP TABLE IF EXISTS spells;
CREATE TABLE spells (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  range TEXT,
  material TEXT,
  ritual INTEGER,
  duration TEXT,
  concentration INTEGER,
  casting_time TEXT,
  level INTEGER,
  attack_type TEXT,
  damage_damage_type_index TEXT,
  school_index TEXT,
  url TEXT,
  dc_dc_type_index TEXT,
  dc_dc_success TEXT,
  area_of_effect_type TEXT,
  area_of_effect_size INTEGER,
  dc_desc TEXT,
  FOREIGN KEY (damage_damage_type_index) REFERENCES damage_types("index"),
  FOREIGN KEY (school_index) REFERENCES magic_schools("index"),
  FOREIGN KEY (dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS spells_desc;
CREATE TABLE spells_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS spells_higher_level;
CREATE TABLE spells_higher_level (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS spells_components;
CREATE TABLE spells_components (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS spells_damage_at_slot_level;
CREATE TABLE spells_damage_at_slot_level (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  key_level INTEGER,
  "value" TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS spells_classes;
CREATE TABLE spells_classes (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  order_num INTEGER,
  classes_index TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index"),
  FOREIGN KEY (classes_index) REFERENCES classes("index")
);

DROP TABLE IF EXISTS spells_subclasses;
CREATE TABLE spells_subclasses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  order_num INTEGER,
  subclasses_index TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index"),
  FOREIGN KEY (subclasses_index) REFERENCES subclasses("index")
);

DROP TABLE IF EXISTS spells_damage_at_character_level;
CREATE TABLE spells_damage_at_character_level (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  key_level INTEGER,
  "value" TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS spells_heal_at_slot_level;
CREATE TABLE spells_heal_at_slot_level (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  spells_index TEXT,
  key_level INTEGER,
  "value" TEXT,
  FOREIGN KEY (spells_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS subclasses;
CREATE TABLE subclasses (
  "index" TEXT PRIMARY KEY,
  class_index TEXT,
  name TEXT,
  subclass_flavor TEXT,
  subclass_levels TEXT,
  url TEXT,
  FOREIGN KEY (class_index) REFERENCES classes("index")
);

DROP TABLE IF EXISTS subclasses_desc;
CREATE TABLE subclasses_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  subclasses_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (subclasses_index) REFERENCES subclasses("index")
);

DROP TABLE IF EXISTS subclasses_spells;
CREATE TABLE subclasses_spells (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  subclasses_index TEXT,
  order_num INTEGER,
  spell_index TEXT,
  FOREIGN KEY (subclasses_index) REFERENCES subclasses("index"),
  FOREIGN KEY (spell_index) REFERENCES spells("index")
);

DROP TABLE IF EXISTS subclasses_spells_prerequisites;
CREATE TABLE subclasses_spells_prerequisites (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  subclasses_spells_index TEXT,
  order_num INTEGER,
  levels_index TEXT,
  FOREIGN KEY (subclasses_spells_index) REFERENCES subclasses_spells("index"),
  FOREIGN KEY (levels_index) REFERENCES levels("index")
);

DROP TABLE IF EXISTS subraces;
CREATE TABLE subraces (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  race_index TEXT,
  "desc" TEXT,
  url TEXT,
  FOREIGN KEY (race_index) REFERENCES races("index")
);

DROP TABLE IF EXISTS subraces_ability_bonuses;
CREATE TABLE subraces_ability_bonuses (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  subraces_index TEXT,
  order_num INTEGER,
  ability_score_index TEXT,
  bonus INTEGER,
  FOREIGN KEY (subraces_index) REFERENCES subraces("index"),
  FOREIGN KEY (ability_score_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS subraces_racial_traits;
CREATE TABLE subraces_racial_traits (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  subraces_index TEXT,
  order_num INTEGER,
  traits_index TEXT,
  FOREIGN KEY (subraces_index) REFERENCES subraces("index"),
  FOREIGN KEY (traits_index) REFERENCES traits("index")
);

DROP TABLE IF EXISTS traits;
CREATE TABLE traits (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  url TEXT,
  proficiency_choices_choose INTEGER,
  proficiency_choices_type TEXT,
  proficiency_choices_from_option_set_type TEXT,
  trait_specific_spell_options_choose INTEGER,
  trait_specific_spell_options_from_option_set_type TEXT,
  trait_specific_spell_options_type TEXT,
  language_options_choose INTEGER,
  language_options_type TEXT,
  language_options_from_option_set_type TEXT,
  trait_specific_subtrait_options_choose INTEGER,
  trait_specific_subtrait_options_from_option_set_type TEXT,
  trait_specific_subtrait_options_type TEXT,
  parent_index TEXT,
  trait_specific_damage_type_index TEXT,
  trait_specific_breath_weapon_name TEXT,
  trait_specific_breath_weapon_desc TEXT,
  trait_specific_breath_weapon_area_of_effect_size INTEGER,
  trait_specific_breath_weapon_area_of_effect_type TEXT,
  trait_specific_breath_weapon_usage_type TEXT,
  trait_specific_breath_weapon_usage_times INTEGER,
  trait_specific_breath_weapon_dc_dc_type_index TEXT,
  trait_specific_breath_weapon_dc_success_type TEXT,
  FOREIGN KEY (parent_index) REFERENCES traits("index"),
  FOREIGN KEY (trait_specific_damage_type_index) REFERENCES damage_types("index"),
  FOREIGN KEY (trait_specific_breath_weapon_dc_dc_type_index) REFERENCES ability_scores("index")
);

DROP TABLE IF EXISTS traits_races;
CREATE TABLE traits_races (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_index TEXT,
  order_num INTEGER,
  races_index TEXT,
  FOREIGN KEY (traits_index) REFERENCES traits("index"),
  FOREIGN KEY (races_index) REFERENCES races("index")
);

DROP TABLE IF EXISTS traits_desc;
CREATE TABLE traits_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (traits_index) REFERENCES traits("index")
);

DROP TABLE IF EXISTS traits_proficiencies;
CREATE TABLE traits_proficiencies (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_index TEXT,
  order_num INTEGER,
  proficiencies_index TEXT,
  FOREIGN KEY (traits_index) REFERENCES traits("index"),
  FOREIGN KEY (proficiencies_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS traits_options;
CREATE TABLE traits_options (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_index TEXT,
  order_num INTEGER,
  option_type TEXT,
  item_index TEXT,
  FOREIGN KEY (traits_index) REFERENCES traits("index"),
  FOREIGN KEY (item_index) REFERENCES proficiencies("index")
);

DROP TABLE IF EXISTS traits_subraces;
CREATE TABLE traits_subraces (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_index TEXT,
  order_num INTEGER,
  subraces_index TEXT,
  FOREIGN KEY (traits_index) REFERENCES traits("index"),
  FOREIGN KEY (subraces_index) REFERENCES subraces("index")
);

DROP TABLE IF EXISTS traits_damage;
CREATE TABLE traits_damage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_index TEXT,
  order_num INTEGER,
  damage_type_index TEXT,
  FOREIGN KEY (traits_index) REFERENCES traits("index"),
  FOREIGN KEY (damage_type_index) REFERENCES damage_types("index")
);

DROP TABLE IF EXISTS traits_damage_damage_at_character_level;
CREATE TABLE traits_damage_damage_at_character_level (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  traits_damage_index TEXT,
  key_level INTEGER,
  "value" TEXT,
  FOREIGN KEY (traits_damage_index) REFERENCES traits_damage("index")
);

DROP TABLE IF EXISTS weapon_properties;
CREATE TABLE weapon_properties (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  url TEXT
);

DROP TABLE IF EXISTS weapon_properties_desc;
CREATE TABLE weapon_properties_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  weapon_properties_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (weapon_properties_index) REFERENCES weapon_properties("index")
);


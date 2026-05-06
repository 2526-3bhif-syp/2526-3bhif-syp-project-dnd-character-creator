-- =============================================================================
-- D&D 5e COMPLETE DATABASE
-- Single-file setup — SQLite & H2 compatible
-- Sources:
--   Core schema: jerobertson/dnd-database (converted from MySQL)
--   Magic system: custom extension
--   Character system: custom extension
-- =============================================================================
-- TABLE OF CONTENTS:
--   1.  ability
--   2.  alignment / alignment_type
--   3.  language / language_type
--   4.  damage_type
--   5.  skill
--   6.  dice_roll
--   7.  race / race_ability_score_increment / race_trait / race_language
--   8.  armour / armour_type
--   9.  weapon / weapon_type / weapon_property / weapon_weapon_property
--  10.  tool / tool_type
--  11.  item / item_type / item_group / item_set / item_item_group / item_item_set
--  12.  mount
--  13.  class / class_armour_proficiency / class_armour_type_proficiency
--        class_weapon_proficiency / class_weapon_type_proficiency
--        class_saving_throw / class_skill_proficiency / class_tool_proficiency
--        class_feature / class_item
--  14.  subclass / subclass_feature
--  15.  background / background_bonds / background_features / background_flaws
--        background_ideals / background_item_groups / background_languages
--        background_personality_traits / background_skills / background_specialties
--        bond / feature / flaw / ideal / personality_trait / specialty
--  16.  spell_school / spell
--  17.  class_spell / class_spell_slots / subclass_spell / race_innate_spell
--  18.  class_skill_choice / class_skill_count
--  19.  equipment_pack / class_starting_equipment
--  20.  character / character_stats / character_skill
--        character_equipment / character_spell
-- =============================================================================

-- =============================================================================
-- 1. ABILITY SCORES
-- =============================================================================

CREATE TABLE IF NOT EXISTS ability (
    name        VARCHAR(63) NOT NULL,
    description TEXT,
    PRIMARY KEY (name)
);

INSERT INTO ability (name, description) VALUES
('Strength',     'Measures: Natural athleticism, bodily power. Important for: Barbarian, Fighter, Paladin.'),
('Dexterity',    'Measures: Physical agility, reflexes, balance, poise. Important for: Monk, Ranger, Rogue.'),
('Constitution', 'Measures: Health, stamina, vital force. Important for: Everyone.'),
('Intelligence', 'Measures: Mental acuity, information recall, analytical skill. Important for: Wizard.'),
('Wisdom',       'Measures: Awareness, intuition, insight. Important for: Cleric, Druid.'),
('Charisma',     'Measures: Confidence, eloquence, leadership. Important for: Bard, Sorcerer, Warlock.');

-- =============================================================================
-- 2. ALIGNMENT
-- =============================================================================

CREATE TABLE IF NOT EXISTS alignment_type (
    name VARCHAR(63) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO alignment_type (name) VALUES
('Lawful'),('Neutral'),('Chaotic'),('Good'),('Evil'),('True');

CREATE TABLE IF NOT EXISTS alignment (
    id      INTEGER     NOT NULL,
    name_x  VARCHAR(63) NOT NULL,
    name_y  VARCHAR(63) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO alignment (id, name_x, name_y) VALUES
(1,'Lawful','Good'),(2,'Neutral','Good'),(3,'Chaotic','Good'),
(4,'Lawful','Neutral'),(5,'True','Neutral'),(6,'Chaotic','Neutral'),
(7,'Lawful','Evil'),(8,'Neutral','Evil'),(9,'Chaotic','Evil');

-- =============================================================================
-- 3. LANGUAGE
-- =============================================================================

CREATE TABLE IF NOT EXISTS language_type (
    name VARCHAR(63) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO language_type (name) VALUES ('Standard'),('Exotic');

CREATE TABLE IF NOT EXISTS language (
    name            VARCHAR(63) NOT NULL,
    language_type   VARCHAR(63) NOT NULL,
    typical_speakers TEXT,
    script          VARCHAR(63),
    PRIMARY KEY (name),
    FOREIGN KEY (language_type) REFERENCES language_type(name)
);

INSERT INTO language (name, language_type, typical_speakers, script) VALUES
('Common',    'Standard', 'Humans',                     'Common'),
('Dwarvish',  'Standard', 'Dwarves',                    'Dwarvish'),
('Elvish',    'Standard', 'Elves',                      'Elvish'),
('Giant',     'Standard', 'Ogres, Giants',              'Dwarvish'),
('Gnomish',   'Standard', 'Gnomes',                     'Dwarvish'),
('Goblin',    'Standard', 'Goblinoids',                 'Dwarvish'),
('Halfling',  'Standard', 'Halflings',                  'Common'),
('Orc',       'Standard', 'Orcs',                       'Dwarvish'),
('Abyssal',   'Exotic',   'Demons',                     'Infernal'),
('Celestial', 'Exotic',   'Celestials',                 'Celestial'),
('Draconic',  'Exotic',   'Dragons, Dragonborn',        'Draconic'),
('Deep Speech','Exotic',  'Aboleths, Cloakers',         NULL),
('Infernal',  'Exotic',   'Devils',                     'Infernal'),
('Primordial','Exotic',   'Elementals',                 'Dwarvish'),
('Sylvan',    'Exotic',   'Fey creatures',              'Elvish'),
('Undercommon','Exotic',  'Underdark traders',          'Elvish');

-- =============================================================================
-- 4. DAMAGE TYPE
-- =============================================================================

CREATE TABLE IF NOT EXISTS damage_type (
    name        VARCHAR(63) NOT NULL,
    description TEXT,
    PRIMARY KEY (name)
);

INSERT INTO damage_type (name, description) VALUES
('Acid',        'The corrosive spray of a black dragon''s breath and the dissolving enzymes secreted by a black pudding deal acid damage.'),
('Bludgeoning', 'Blunt force attacks—hammers, falling, constriction, and the like—deal bludgeoning damage.'),
('Cold',        'The infernal chill radiating from an ice devil''s spear and the frigid blast of a white dragon''s breath deal cold damage.'),
('Fire',        'Red dragons breathe fire, and many spells conjure flames to deal fire damage.'),
('Force',       'Force is pure magical energy focused into a damaging form. Most effects that deal force damage are spells.'),
('Lightning',   'A lightning bolt spell and a blue dragon''s breath deal lightning damage.'),
('Necrotic',    'Necrotic damage, dealt by certain undead and a spell such as chill touch, withers matter and even the soul.'),
('Piercing',    'Puncturing and impaling attacks, including spears and monsters'' bites, deal piercing damage.'),
('Poison',      'Venomous stings and the toxic gas of a green dragon''s breath deal poison damage.'),
('Psychic',     'Mental abilities such as a mind flayer''s psionic blast deal psychic damage.'),
('Radiant',     'Radiant damage, dealt by a cleric''s flame strike spell or an angel''s smiting weapon, sears the flesh like fire and overloads the spirit with power.'),
('Slashing',    'Swords, axes, and monsters'' claws deal slashing damage.'),
('Thunder',     'A concussive burst of sound, such as the effect of the thunderwave spell, deals thunder damage.');

-- =============================================================================
-- 5. SKILL
-- =============================================================================

CREATE TABLE IF NOT EXISTS skill (
    name        VARCHAR(63) NOT NULL,
    ability     VARCHAR(63) NOT NULL,
    description TEXT,
    PRIMARY KEY (name),
    FOREIGN KEY (ability) REFERENCES ability(name)
);

INSERT INTO skill (name, ability, description) VALUES
('Acrobatics',      'Dexterity',    'Your ability to stay on your feet in tricky situations and perform acrobatic stunts.'),
('Animal Handling', 'Wisdom',       'Your ability to calm domesticated animals, keep mounts from shying, and intuit an animal''s intentions.'),
('Arcana',          'Intelligence', 'Your ability to recall lore about spells, magic items, eldritch symbols, and magical traditions.'),
('Athletics',       'Strength',     'Your ability to climb, jump, swim, and perform other feats of physical prowess.'),
('Deception',       'Charisma',     'Your ability to convincingly hide the truth, whether through misleading others or telling outright lies.'),
('History',         'Intelligence', 'Your ability to recall lore about historical events, legendary people, ancient kingdoms, and wars.'),
('Insight',         'Wisdom',       'Your ability to determine the true intentions of a creature, such as when searching out a lie.'),
('Intimidation',    'Charisma',     'Your ability to influence someone through overt threats, hostile actions, and physical violence.'),
('Investigation',   'Intelligence', 'Your ability to look around for clues and make deductions based on those clues.'),
('Medicine',        'Wisdom',       'Your ability to stabilize a dying companion or diagnose an illness.'),
('Nature',          'Intelligence', 'Your ability to recall lore about terrain, plants and animals, the weather, and natural cycles.'),
('Perception',      'Wisdom',       'Your ability to spot, hear, or otherwise detect the presence of something using your senses.'),
('Performance',     'Charisma',     'Your ability to delight an audience with music, dance, acting, or storytelling.'),
('Persuasion',      'Charisma',     'Your ability to influence someone with tact, social graces, or good nature.'),
('Religion',        'Intelligence', 'Your ability to recall lore about deities, rites and prayers, and religious hierarchies.'),
('Sleight of Hand', 'Dexterity',    'Your ability to perform acts of legerdemain or manual trickery.'),
('Stealth',         'Dexterity',    'Your ability to conceal yourself from enemies, slink past guards, or sneak up on someone.'),
('Survival',        'Wisdom',       'Your ability to follow tracks, hunt wild game, and navigate natural hazards.');

-- =============================================================================
-- 6. DICE ROLL
-- =============================================================================

CREATE TABLE IF NOT EXISTS dice_roll (
    name    VARCHAR(63) NOT NULL,
    sides   INTEGER     NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO dice_roll (name, sides) VALUES
('d4',4),('d6',6),('d8',8),('d10',10),('d12',12),('d20',20),('d100',100);

-- =============================================================================
-- 7. RACE
-- =============================================================================

CREATE TABLE IF NOT EXISTS race (
    name            VARCHAR(63)  NOT NULL,
    parent_race     VARCHAR(63),
    size            VARCHAR(63)  NOT NULL DEFAULT 'Medium',
    speed           INTEGER      NOT NULL DEFAULT 30,
    description     TEXT,
    PRIMARY KEY (name),
    FOREIGN KEY (parent_race) REFERENCES race(name)
);

INSERT INTO race (name, parent_race, size, speed, description) VALUES
('Dwarf',           NULL,    'Medium', 25, 'Bold and hardy, dwarves are known as skilled warriors, miners, and workers of stone and metal.'),
('Hill Dwarf',      'Dwarf', 'Medium', 25, 'As a hill dwarf, you have keen senses, deep intuition, and remarkable resilience.'),
('Mountain Dwarf',  'Dwarf', 'Medium', 25, 'As a mountain dwarf, you''re strong and hardy, accustomed to a difficult life in rugged terrain.'),
('Elf',             NULL,    'Medium', 30, 'Elves are a magical people of otherworldly grace, living in the world but not entirely part of it.'),
('High Elf',        'Elf',   'Medium', 30, 'As a high elf, you have a keen mind and a mastery of at least the basics of magic.'),
('Wood Elf',        'Elf',   'Medium', 35, 'As a wood elf, you have keen senses and intuition, and your fleet feet carry you quickly through the forests.'),
('Drow',            'Elf',   'Medium', 30, 'Descended from an earlier subrace of dark-skinned elves, the drow were banished from the surface world.'),
('Halfling',        NULL,    'Small',  25, 'The comforts of home are the goals of most halflings'' lives: a place to settle in peace and quiet.'),
('Lightfoot Halfling','Halfling','Small',25,'As a lightfoot halfling, you can easily hide from notice, even using other people as cover.'),
('Stout Halfling',  'Halfling','Small',25,'As a stout halfling, you''re hardier than average and have some resistance to poison.'),
('Human',           NULL,    'Medium', 30, 'Humans are the most adaptable and ambitious people among the common races.'),
('Dragonborn',      NULL,    'Medium', 30, 'Born of dragons, as their name proclaims, the dragonborn walk proudly through a world that greets them with fearful incomprehension.'),
('Gnome',           NULL,    'Small',  25, 'A gnome''s energy and enthusiasm for living shines through every inch of his or her tiny body.'),
('Forest Gnome',    'Gnome', 'Small',  25, 'As a forest gnome, you have a natural knack for illusion and inherent quickness and stealth.'),
('Rock Gnome',      'Gnome', 'Small',  25, 'As a rock gnome, you have a natural inventiveness and hardiness beyond that of other gnomes.'),
('Half-Elf',        NULL,    'Medium', 30, 'Walking in two worlds but truly belonging to neither, half-elves combine what some say are the best qualities of their elf and human parents.'),
('Half-Orc',        NULL,    'Medium', 30, 'Whether united under the leadership of a mighty warlock or having fought to a standstill after years of conflict, orc and human communities sometimes form alliances.'),
('Tiefling',        NULL,    'Medium', 30, 'To be greeted with stares and whispers, to suffer violence and insult on the street, to see mistrust and fear in every eye: this is the lot of the tiefling.');

CREATE TABLE IF NOT EXISTS race_ability_score_increment (
    race_name   VARCHAR(63) NOT NULL,
    ability     VARCHAR(63) NOT NULL,
    increment   INTEGER     NOT NULL DEFAULT 1,
    PRIMARY KEY (race_name, ability),
    FOREIGN KEY (race_name) REFERENCES race(name),
    FOREIGN KEY (ability)   REFERENCES ability(name)
);

INSERT INTO race_ability_score_increment (race_name, ability, increment) VALUES
('Hill Dwarf',          'Constitution', 2),
('Hill Dwarf',          'Wisdom',       1),
('Mountain Dwarf',      'Constitution', 2),
('Mountain Dwarf',      'Strength',     2),
('High Elf',            'Dexterity',    2),
('High Elf',            'Intelligence', 1),
('Wood Elf',            'Dexterity',    2),
('Wood Elf',            'Wisdom',       1),
('Drow',                'Dexterity',    2),
('Drow',                'Charisma',     1),
('Lightfoot Halfling',  'Dexterity',    2),
('Lightfoot Halfling',  'Charisma',     1),
('Stout Halfling',      'Dexterity',    2),
('Stout Halfling',      'Constitution', 1),
('Human',               'Strength',     1),
('Human',               'Dexterity',    1),
('Human',               'Constitution', 1),
('Human',               'Intelligence', 1),
('Human',               'Wisdom',       1),
('Human',               'Charisma',     1),
('Dragonborn',          'Strength',     2),
('Dragonborn',          'Charisma',     1),
('Forest Gnome',        'Intelligence', 2),
('Forest Gnome',        'Dexterity',    1),
('Rock Gnome',          'Intelligence', 2),
('Rock Gnome',          'Constitution', 1),
('Half-Elf',            'Charisma',     2),
('Half-Orc',            'Strength',     2),
('Half-Orc',            'Constitution', 1),
('Tiefling',            'Intelligence', 1),
('Tiefling',            'Charisma',     2);

CREATE TABLE IF NOT EXISTS race_trait (
    race_name   VARCHAR(63)  NOT NULL,
    trait_name  VARCHAR(127) NOT NULL,
    description TEXT,
    PRIMARY KEY (race_name, trait_name),
    FOREIGN KEY (race_name) REFERENCES race(name)
);

INSERT INTO race_trait (race_name, trait_name, description) VALUES
('Hill Dwarf',       'Darkvision',           'Accustomed to life underground, you have superior vision in dark and dim conditions. You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Hill Dwarf',       'Dwarven Resilience',   'You have advantage on saving throws against poison, and you have resistance against poison damage.'),
('Hill Dwarf',       'Dwarven Combat Training','You have proficiency with the battleaxe, handaxe, light hammer, and warhammer.'),
('Hill Dwarf',       'Tool Proficiency',     'You gain proficiency with the artisan''s tools of your choice: smith''s tools, brewer''s supplies, or mason''s tools.'),
('Hill Dwarf',       'Stonecunning',         'Whenever you make an Intelligence (History) check related to the origin of stonework, you are considered proficient in the History skill and add double your proficiency bonus to the check.'),
('Hill Dwarf',       'Dwarven Toughness',    'Your hit point maximum increases by 1, and it increases by 1 every time you gain a level.'),
('Mountain Dwarf',   'Darkvision',           'You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Mountain Dwarf',   'Dwarven Resilience',   'You have advantage on saving throws against poison, and you have resistance against poison damage.'),
('Mountain Dwarf',   'Dwarven Armor Training','You have proficiency with light and medium armor.'),
('High Elf',         'Darkvision',           'You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('High Elf',         'Keen Senses',          'You have proficiency in the Perception skill.'),
('High Elf',         'Fey Ancestry',         'You have advantage on saving throws against being charmed, and magic can''t put you to sleep.'),
('High Elf',         'Trance',               'Elves don''t need to sleep. Instead, they meditate deeply, remaining semiconscious, for 4 hours a day.'),
('High Elf',         'Elf Weapon Training',  'You have proficiency with the longsword, shortsword, shortbow, and longbow.'),
('High Elf',         'Cantrip',              'You know one cantrip of your choice from the wizard spell list. Intelligence is your spellcasting ability for it.'),
('Wood Elf',         'Darkvision',           'You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Wood Elf',         'Keen Senses',          'You have proficiency in the Perception skill.'),
('Wood Elf',         'Fey Ancestry',         'You have advantage on saving throws against being charmed, and magic can''t put you to sleep.'),
('Wood Elf',         'Mask of the Wild',     'You can attempt to hide even when you are only lightly obscured by foliage, heavy rain, falling snow, mist, and other natural phenomena.'),
('Drow',             'Superior Darkvision',  'Your darkvision has a radius of 120 feet.'),
('Drow',             'Sunlight Sensitivity', 'You have disadvantage on attack rolls and on Wisdom (Perception) checks that rely on sight when you, the target of your attack, or whatever you are trying to perceive is in direct sunlight.'),
('Drow',             'Drow Magic',           'You know the dancing lights cantrip. When you reach 3rd level, you can cast the faerie fire spell once per day. When you reach 5th level, you can also cast the darkness spell once per day. Charisma is your spellcasting ability for these spells.'),
('Lightfoot Halfling','Lucky',               'When you roll a 1 on the d20 for an attack roll, ability check, or saving throw, you can reroll the die and must use the new roll.'),
('Lightfoot Halfling','Brave',               'You have advantage on saving throws against being frightened.'),
('Lightfoot Halfling','Halfling Nimbleness', 'You can move through the space of any creature that is of a size larger than yours.'),
('Lightfoot Halfling','Naturally Stealthy',  'You can attempt to hide even when you are obscured only by a creature that is at least one size larger than you.'),
('Stout Halfling',   'Lucky',               'When you roll a 1 on the d20 for an attack roll, ability check, or saving throw, you can reroll the die and must use the new roll.'),
('Stout Halfling',   'Brave',               'You have advantage on saving throws against being frightened.'),
('Stout Halfling',   'Stout Resilience',    'You have advantage on saving throws against poison, and you have resistance against poison damage.'),
('Human',            'Extra Language',      'You can speak, read, and write one extra language of your choice.'),
('Dragonborn',       'Draconic Ancestry',   'You have draconic ancestry. Choose one type of dragon from the Draconic Ancestry table. Your breath weapon and damage resistance are determined by the dragon type.'),
('Dragonborn',       'Breath Weapon',       'You can use your action to exhale destructive energy. Your draconic ancestry determines the size, shape, and damage type of the exhalation.'),
('Dragonborn',       'Damage Resistance',   'You have resistance to the damage type associated with your draconic ancestry.'),
('Forest Gnome',     'Darkvision',          'Accustomed to life underground, you can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Forest Gnome',     'Gnome Cunning',       'You have advantage on all Intelligence, Wisdom, and Charisma saving throws against magic.'),
('Forest Gnome',     'Natural Illusionist', 'You know the minor illusion cantrip. Intelligence is your spellcasting ability for it.'),
('Forest Gnome',     'Speak with Small Beasts','Through sounds and gestures, you can communicate simple ideas with Small or smaller beasts.'),
('Rock Gnome',       'Darkvision',          'You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Rock Gnome',       'Gnome Cunning',       'You have advantage on all Intelligence, Wisdom, and Charisma saving throws against magic.'),
('Rock Gnome',       'Artificer''s Lore',   'Whenever you make an Intelligence (History) check related to magic items, alchemical objects, or technological devices, you can add twice your proficiency bonus.'),
('Rock Gnome',       'Tinker',              'You have proficiency with artisan''s tools (tinker''s tools). Using those tools, you can spend 1 hour and 10 gp worth of materials to construct a Tiny clockwork device.'),
('Half-Elf',         'Darkvision',          'Thanks to your elf blood, you have superior vision in dark and dim conditions. You can see in dim light within 60 feet.'),
('Half-Elf',         'Fey Ancestry',        'You have advantage on saving throws against being charmed, and magic can''t put you to sleep.'),
('Half-Elf',         'Skill Versatility',   'You gain proficiency in two skills of your choice.'),
('Half-Orc',         'Darkvision',          'You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Half-Orc',         'Menacing',            'You gain proficiency in the Intimidation skill.'),
('Half-Orc',         'Relentless Endurance','When you are reduced to 0 hit points but not killed outright, you can drop to 1 hit point instead. You can''t use this feature again until you finish a long rest.'),
('Half-Orc',         'Savage Attacks',      'When you score a critical hit with a melee weapon attack, you can roll one of the weapon''s damage dice one additional time and add it to the extra damage of the critical hit.'),
('Tiefling',         'Darkvision',          'You can see in dim light within 60 feet of you as if it were bright light, and in darkness as if it were dim light.'),
('Tiefling',         'Hellish Resistance',  'You have resistance to fire damage.'),
('Tiefling',         'Infernal Legacy',     'You know the thaumaturgy cantrip. When you reach 3rd level, you can cast the hellish rebuke spell as a 2nd-level spell once with this trait. When you reach 5th level, you can also cast the darkness spell once with this trait.');

CREATE TABLE IF NOT EXISTS race_language (
    race_name   VARCHAR(63) NOT NULL,
    language    VARCHAR(63) NOT NULL,
    PRIMARY KEY (race_name, language),
    FOREIGN KEY (race_name) REFERENCES race(name),
    FOREIGN KEY (language)  REFERENCES language(name)
);

INSERT INTO race_language (race_name, language) VALUES
('Hill Dwarf','Common'),('Hill Dwarf','Dwarvish'),
('Mountain Dwarf','Common'),('Mountain Dwarf','Dwarvish'),
('High Elf','Common'),('High Elf','Elvish'),
('Wood Elf','Common'),('Wood Elf','Elvish'),
('Drow','Common'),('Drow','Elvish'),('Drow','Undercommon'),
('Lightfoot Halfling','Common'),('Lightfoot Halfling','Halfling'),
('Stout Halfling','Common'),('Stout Halfling','Halfling'),
('Human','Common'),
('Dragonborn','Common'),('Dragonborn','Draconic'),
('Forest Gnome','Common'),('Forest Gnome','Gnomish'),('Forest Gnome','Sylvan'),
('Rock Gnome','Common'),('Rock Gnome','Gnomish'),
('Half-Elf','Common'),('Half-Elf','Elvish'),
('Half-Orc','Common'),('Half-Orc','Orc'),
('Tiefling','Common'),('Tiefling','Infernal');

-- =============================================================================
-- 8. ARMOUR
-- =============================================================================

CREATE TABLE IF NOT EXISTS armour_type (
    name        VARCHAR(63) NOT NULL,
    don         INTEGER     NOT NULL,
    doff        INTEGER     NOT NULL,
    dis_action  BOOLEAN     NOT NULL DEFAULT 0,
    PRIMARY KEY (name)
);

INSERT INTO armour_type (name, don, doff, dis_action) VALUES
('Light',1,1,0),('Medium',5,1,0),('Heavy',10,5,0),('Shields',1,1,1);

CREATE TABLE IF NOT EXISTS armour (
    name                VARCHAR(63) NOT NULL,
    armour_type         VARCHAR(63) NOT NULL,
    base_ac             INTEGER     NOT NULL,
    dex_bonus           BOOLEAN     NOT NULL DEFAULT 1,
    dex_bonus_max       INTEGER,
    str_requirement     INTEGER,
    stealth_disadvantage BOOLEAN    NOT NULL DEFAULT 0,
    cost_gp             INTEGER,
    weight_lb           INTEGER,
    PRIMARY KEY (name),
    FOREIGN KEY (armour_type) REFERENCES armour_type(name)
);

INSERT INTO armour (name, armour_type, base_ac, dex_bonus, dex_bonus_max, str_requirement, stealth_disadvantage, cost_gp, weight_lb) VALUES
('Padded',          'Light',   11, 1, NULL, NULL, 1,  500,  8),
('Leather',         'Light',   11, 1, NULL, NULL, 0, 1000, 10),
('Studded Leather', 'Light',   12, 1, NULL, NULL, 0, 4500, 13),
('Hide',            'Medium',  12, 1,    2, NULL, 0, 1000, 12),
('Chain Shirt',     'Medium',  13, 1,    2, NULL, 0, 5000, 20),
('Scale Mail',      'Medium',  14, 1,    2, NULL, 1, 5000, 45),
('Breastplate',     'Medium',  14, 1,    2, NULL, 0,40000, 20),
('Half Plate',      'Medium',  15, 1,    2, NULL, 1,75000, 40),
('Ring Mail',       'Heavy',   14, 0, NULL,   NULL,1, 3000, 40),
('Chain Mail',      'Heavy',   16, 0, NULL,   13,  1,  7500, 55),
('Splint',          'Heavy',   17, 0, NULL,   15,  1, 20000, 60),
('Plate',           'Heavy',   18, 0, NULL,   15,  1,150000,65),
('Shield',          'Shields',  2, 0, NULL, NULL, 0, 1000, 6);

-- =============================================================================
-- 9. WEAPON
-- =============================================================================

CREATE TABLE IF NOT EXISTS weapon_type (
    name VARCHAR(63) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO weapon_type (name) VALUES
('Simple Melee'),('Simple Ranged'),('Martial Melee'),('Martial Ranged');

CREATE TABLE IF NOT EXISTS weapon_property (
    name        VARCHAR(63) NOT NULL,
    description TEXT,
    PRIMARY KEY (name)
);

INSERT INTO weapon_property (name, description) VALUES
('Ammunition',  'You can use a weapon that has the ammunition property to make a ranged attack only if you have ammunition to fire from the weapon.'),
('Finesse',     'When making an attack with a finesse weapon, you use your choice of your Strength or Dexterity modifier for the attack and damage rolls.'),
('Heavy',       'Small creatures have disadvantage on attack rolls with heavy weapons.'),
('Light',       'A light weapon is small and easy to handle, making it ideal for use when fighting with two weapons.'),
('Loading',     'Because of the time required to load this weapon, you can fire only one piece of ammunition from it when you use an action, bonus action, or reaction to fire it.'),
('Range',       'A weapon that can be used to make a ranged attack has a range in parentheses after the ammunition or thrown property.'),
('Reach',       'This weapon adds 5 feet to your reach when you attack with it, as well as when determining your reach for opportunity attacks with it.'),
('Special',     'A weapon with the special property has unusual rules governing its use, explained in the weapon''s description.'),
('Thrown',      'If a weapon has the thrown property, you can throw the weapon to make a ranged attack.'),
('Two-Handed',  'This weapon requires two hands when you attack with it.'),
('Versatile',   'This weapon can be used with one or two hands. A damage value in parentheses appears with the property—the damage when the weapon is used with two hands.');

CREATE TABLE IF NOT EXISTS weapon (
    name            VARCHAR(63)  NOT NULL,
    weapon_type     VARCHAR(63)  NOT NULL,
    damage_dice     VARCHAR(15)  NOT NULL,
    damage_type     VARCHAR(63)  NOT NULL,
    range_normal    INTEGER,
    range_long      INTEGER,
    cost_gp         INTEGER,
    weight_lb       INTEGER,
    PRIMARY KEY (name),
    FOREIGN KEY (weapon_type)  REFERENCES weapon_type(name),
    FOREIGN KEY (damage_type)  REFERENCES damage_type(name)
);

INSERT INTO weapon (name, weapon_type, damage_dice, damage_type, range_normal, range_long, cost_gp, weight_lb) VALUES
-- Simple Melee
('Club',            'Simple Melee',   '1d4',  'Bludgeoning', NULL, NULL,   10,  2),
('Dagger',          'Simple Melee',   '1d4',  'Piercing',      20,   60,  200,  1),
('Greatclub',       'Simple Melee',   '1d8',  'Bludgeoning', NULL, NULL,   20,  10),
('Handaxe',         'Simple Melee',   '1d6',  'Slashing',      20,   60,  500,  2),
('Javelin',         'Simple Melee',   '1d6',  'Piercing',      30,  120,   50,  2),
('Light Hammer',    'Simple Melee',   '1d4',  'Bludgeoning',   20,   60,  200,  2),
('Mace',            'Simple Melee',   '1d6',  'Bludgeoning', NULL, NULL,  500,  4),
('Quarterstaff',    'Simple Melee',   '1d6',  'Bludgeoning', NULL, NULL,   20,  4),
('Sickle',          'Simple Melee',   '1d4',  'Slashing',    NULL, NULL,  100,  2),
('Spear',           'Simple Melee',   '1d6',  'Piercing',      20,   60,  100,  3),
-- Simple Ranged
('Crossbow, Light', 'Simple Ranged',  '1d8',  'Piercing',      80,  320, 2500,  5),
('Dart',            'Simple Ranged',  '1d4',  'Piercing',      20,   60,    5, 0),
('Shortbow',        'Simple Ranged',  '1d6',  'Piercing',      80,  320, 2500,  2),
('Sling',           'Simple Ranged',  '1d4',  'Bludgeoning',   30,  120,    1,  0),
-- Martial Melee
('Battleaxe',       'Martial Melee',  '1d8',  'Slashing',    NULL, NULL, 1000,  4),
('Flail',           'Martial Melee',  '1d8',  'Bludgeoning', NULL, NULL, 1000,  2),
('Glaive',          'Martial Melee',  '1d10', 'Slashing',    NULL, NULL, 2000,  6),
('Greataxe',        'Martial Melee',  '1d12', 'Slashing',    NULL, NULL, 3000,  7),
('Greatsword',      'Martial Melee',  '2d6',  'Slashing',    NULL, NULL, 5000,  6),
('Halberd',         'Martial Melee',  '1d10', 'Slashing',    NULL, NULL, 2000,  6),
('Lance',           'Martial Melee',  '1d12', 'Piercing',    NULL, NULL, 1000,  6),
('Longsword',       'Martial Melee',  '1d8',  'Slashing',    NULL, NULL, 1500,  3),
('Maul',            'Martial Melee',  '2d6',  'Bludgeoning', NULL, NULL, 1000, 10),
('Morningstar',     'Martial Melee',  '1d8',  'Piercing',    NULL, NULL, 1500,  4),
('Pike',            'Martial Melee',  '1d10', 'Piercing',    NULL, NULL,  500, 18),
('Rapier',          'Martial Melee',  '1d8',  'Piercing',    NULL, NULL, 2500,  2),
('Scimitar',        'Martial Melee',  '1d6',  'Slashing',    NULL, NULL, 2500,  3),
('Shortsword',      'Martial Melee',  '1d6',  'Piercing',    NULL, NULL, 1000,  2),
('Trident',         'Martial Melee',  '1d6',  'Piercing',      20,   60,  500,  4),
('War Pick',        'Martial Melee',  '1d8',  'Piercing',    NULL, NULL,  500,  2),
('Warhammer',       'Martial Melee',  '1d8',  'Bludgeoning', NULL, NULL, 1500,  2),
('Whip',            'Martial Melee',  '1d4',  'Slashing',    NULL, NULL,  200,  3),
-- Martial Ranged
('Blowgun',         'Martial Ranged', '1',    'Piercing',      25,  100,  1000, 1),
('Crossbow, Hand',  'Martial Ranged', '1d6',  'Piercing',      30,  120, 7500,  3),
('Crossbow, Heavy', 'Martial Ranged', '1d10', 'Piercing',     100,  400, 5000, 18),
('Longbow',         'Martial Ranged', '1d8',  'Piercing',     150,  600, 5000,  2),
('Net',             'Martial Ranged', '0',    'Bludgeoning',    5,   15,  100,  3);

CREATE TABLE IF NOT EXISTS weapon_weapon_property (
    weapon_name     VARCHAR(63) NOT NULL,
    property_name   VARCHAR(63) NOT NULL,
    PRIMARY KEY (weapon_name, property_name),
    FOREIGN KEY (weapon_name)   REFERENCES weapon(name),
    FOREIGN KEY (property_name) REFERENCES weapon_property(name)
);

INSERT INTO weapon_weapon_property (weapon_name, property_name) VALUES
('Dagger','Finesse'),('Dagger','Light'),('Dagger','Thrown'),
('Handaxe','Light'),('Handaxe','Thrown'),
('Javelin','Thrown'),
('Light Hammer','Light'),('Light Hammer','Thrown'),
('Quarterstaff','Versatile'),
('Spear','Thrown'),('Spear','Versatile'),
('Crossbow, Light','Ammunition'),('Crossbow, Light','Loading'),('Crossbow, Light','Range'),
('Dart','Finesse'),('Dart','Thrown'),
('Shortbow','Ammunition'),('Shortbow','Range'),('Shortbow','Two-Handed'),
('Sling','Ammunition'),('Sling','Range'),
('Battleaxe','Versatile'),
('Glaive','Heavy'),('Glaive','Reach'),('Glaive','Two-Handed'),
('Greataxe','Heavy'),('Greataxe','Two-Handed'),
('Greatsword','Heavy'),('Greatsword','Two-Handed'),
('Halberd','Heavy'),('Halberd','Reach'),('Halberd','Two-Handed'),
('Lance','Reach'),('Lance','Special'),
('Longsword','Versatile'),
('Maul','Heavy'),('Maul','Two-Handed'),
('Pike','Heavy'),('Pike','Reach'),('Pike','Two-Handed'),
('Rapier','Finesse'),
('Scimitar','Finesse'),('Scimitar','Light'),
('Shortsword','Finesse'),('Shortsword','Light'),
('Trident','Thrown'),('Trident','Versatile'),
('Whip','Finesse'),('Whip','Reach'),
('Crossbow, Hand','Ammunition'),('Crossbow, Hand','Light'),('Crossbow, Hand','Loading'),('Crossbow, Hand','Range'),
('Crossbow, Heavy','Ammunition'),('Crossbow, Heavy','Heavy'),('Crossbow, Heavy','Loading'),('Crossbow, Heavy','Range'),('Crossbow, Heavy','Two-Handed'),
('Longbow','Ammunition'),('Longbow','Heavy'),('Longbow','Range'),('Longbow','Two-Handed'),
('Net','Special'),('Net','Thrown');

-- =============================================================================
-- 10. TOOL
-- =============================================================================

CREATE TABLE IF NOT EXISTS tool_type (
    name VARCHAR(63) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO tool_type (name) VALUES
('Artisan''s Tools'),('Gaming Sets'),('Musical Instruments'),('Other Tools');

CREATE TABLE IF NOT EXISTS tool (
    name        VARCHAR(63) NOT NULL,
    tool_type   VARCHAR(63) NOT NULL,
    cost_gp     INTEGER,
    weight_lb   INTEGER,
    PRIMARY KEY (name),
    FOREIGN KEY (tool_type) REFERENCES tool_type(name)
);

INSERT INTO tool (name, tool_type, cost_gp, weight_lb) VALUES
('Alchemist''s Supplies',   'Artisan''s Tools', 5000, 8),
('Brewer''s Supplies',      'Artisan''s Tools', 2000, 9),
('Calligrapher''s Supplies','Artisan''s Tools', 1000, 5),
('Carpenter''s Tools',      'Artisan''s Tools', 800,  6),
('Cartographer''s Tools',   'Artisan''s Tools', 1500, 6),
('Cobbler''s Tools',        'Artisan''s Tools', 500,  5),
('Cook''s Utensils',        'Artisan''s Tools', 100,  8),
('Glassblower''s Tools',    'Artisan''s Tools', 3000, 5),
('Jeweler''s Tools',        'Artisan''s Tools', 2500, 2),
('Leatherworker''s Tools',  'Artisan''s Tools', 500,  5),
('Mason''s Tools',          'Artisan''s Tools', 1000, 8),
('Painter''s Supplies',     'Artisan''s Tools', 1000, 5),
('Potter''s Tools',         'Artisan''s Tools', 100,  3),
('Smith''s Tools',          'Artisan''s Tools', 2000, 8),
('Tinker''s Tools',         'Artisan''s Tools', 5000, 10),
('Weaver''s Tools',         'Artisan''s Tools', 100,  5),
('Woodcarver''s Tools',     'Artisan''s Tools', 100,  5),
('Dice Set',                'Gaming Sets',       100,  0),
('Dragonchess Set',         'Gaming Sets',       100,  0),
('Playing Card Set',        'Gaming Sets',       500,  0),
('Three-Dragon Ante Set',   'Gaming Sets',       100,  0),
('Bagpipes',                'Musical Instruments',3000,6),
('Drum',                    'Musical Instruments', 600, 3),
('Dulcimer',                'Musical Instruments', 2500,10),
('Flute',                   'Musical Instruments', 200, 1),
('Lute',                    'Musical Instruments',3500, 2),
('Lyre',                    'Musical Instruments',3000, 2),
('Horn',                    'Musical Instruments', 300, 2),
('Pan Flute',               'Musical Instruments', 1200,2),
('Shawm',                   'Musical Instruments', 200, 1),
('Viol',                    'Musical Instruments',3000, 1),
('Disguise Kit',            'Other Tools',  2500, 3),
('Forgery Kit',             'Other Tools',  1500, 5),
('Herbalism Kit',           'Other Tools',  500,  3),
('Navigator''s Tools',      'Other Tools',  2500, 2),
('Poisoner''s Kit',         'Other Tools',  5000, 2),
('Thieves'' Tools',         'Other Tools',  2500, 1);

-- =============================================================================
-- 11. ITEM / ITEM GROUP / ITEM SET
-- =============================================================================

CREATE TABLE IF NOT EXISTS item_type (
    name VARCHAR(63) NOT NULL,
    PRIMARY KEY (name)
);

INSERT INTO item_type (name) VALUES
('Adventuring Gear'),('Ammunition'),('Arcane Focus'),('Druidic Focus'),
('Holy Symbol'),('Pack'),('Container'),('Clothing'),('Trade Good'),('Mount Gear');

CREATE TABLE IF NOT EXISTS item (
    name        VARCHAR(127) NOT NULL,
    item_type   VARCHAR(63)  NOT NULL,
    cost_cp     INTEGER      NOT NULL DEFAULT 0,
    weight_lb   REAL,
    description TEXT,
    PRIMARY KEY (name),
    FOREIGN KEY (item_type) REFERENCES item_type(name)
);

INSERT INTO item (name, item_type, cost_cp, weight_lb) VALUES
('Abacus',              'Adventuring Gear', 200,  2),
('Acid (vial)',         'Adventuring Gear', 2500, 1),
('Alchemist''s Fire',   'Adventuring Gear', 5000, 1),
('Antitoxin (vial)',    'Adventuring Gear', 5000, 0),
('Backpack',            'Container',        200,  5),
('Ball Bearings (bag)', 'Adventuring Gear', 100,  2),
('Bedroll',             'Adventuring Gear', 100, 7),
('Bell',                'Adventuring Gear', 100,  0),
('Blanket',             'Adventuring Gear', 50,   3),
('Block and Tackle',    'Adventuring Gear', 100,  5),
('Book',                'Adventuring Gear', 2500, 5),
('Bottle, glass',       'Container',        200,  2),
('Bucket',              'Container',        5,    2),
('Caltrops (bag of 20)','Adventuring Gear', 100,  2),
('Candle',              'Adventuring Gear', 1,    0),
('Case, crossbow bolt', 'Container',        100,  1),
('Case, map or scroll', 'Container',        100,  1),
('Chain (10 feet)',     'Adventuring Gear', 500,  10),
('Chalk (1 piece)',     'Adventuring Gear', 1,    0),
('Chest',               'Container',        500,  25),
('Climber''s Kit',      'Adventuring Gear', 2500, 12),
('Clothes, common',     'Clothing',         50,   3),
('Clothes, costume',    'Clothing',         500,  4),
('Clothes, fine',       'Clothing',         1500, 6),
('Clothes, traveler''s','Clothing',         200,  4),
('Component Pouch',     'Arcane Focus',     2500, 2),
('Crowbar',             'Adventuring Gear', 200,  5),
('Flask or tankard',    'Container',        2,    1),
('Grappling Hook',      'Adventuring Gear', 200,  4),
('Hammer',              'Adventuring Gear', 100,  3),
('Hammer, sledge',      'Adventuring Gear', 200,  10),
('Healer''s Kit',       'Adventuring Gear', 500,  3),
('Holy Water (flask)',  'Adventuring Gear', 2500, 1),
('Hourglass',           'Adventuring Gear', 2500, 1),
('Hunting Trap',        'Adventuring Gear', 500,  25),
('Ink (1 ounce bottle)','Adventuring Gear', 1000, 0),
('Ink Pen',             'Adventuring Gear', 2,    0),
('Jug or pitcher',      'Container',        2,    4),
('Ladder (10-foot)',    'Adventuring Gear', 1,    25),
('Lamp',                'Adventuring Gear', 50,   1),
('Lantern, bullseye',   'Adventuring Gear', 1000, 2),
('Lantern, hooded',     'Adventuring Gear', 500,  2),
('Lock',                'Adventuring Gear', 1000, 1),
('Magnifying Glass',    'Adventuring Gear', 10000,0),
('Manacles',            'Adventuring Gear', 200,  6),
('Mess Kit',            'Adventuring Gear', 20,   1),
('Mirror, steel',       'Adventuring Gear', 500,  0.5),
('Oil (flask)',         'Adventuring Gear', 10,   1),
('Paper (one sheet)',   'Adventuring Gear', 20,   0),
('Parchment (one sheet)','Adventuring Gear',10,   0),
('Perfume (vial)',      'Clothing',         500,  0),
('Pick, miner''s',      'Adventuring Gear', 200,  10),
('Piton',               'Adventuring Gear', 5,    0.25),
('Poison, basic (vial)','Adventuring Gear', 10000,0),
('Pole (10-foot)',      'Adventuring Gear', 5,    7),
('Pot, iron',           'Adventuring Gear', 20,   10),
('Pouch',               'Container',        50,   1),
('Quiver',              'Container',        100,  1),
('Ram, portable',       'Adventuring Gear', 400,  35),
('Rations (1 day)',     'Adventuring Gear', 50,   2),
('Robe',                'Clothing',         100,  4),
('Rope, hempen (50 feet)','Adventuring Gear',100, 10),
('Rope, silk (50 feet)','Adventuring Gear', 1000, 5),
('Sack',                'Container',        1,    0.5),
('Scale, merchant''s',  'Adventuring Gear', 500,  3),
('Sealing Wax',         'Adventuring Gear', 50,   0),
('Shovel',              'Adventuring Gear', 200,  5),
('Signal Whistle',      'Adventuring Gear', 5,    0),
('Signet Ring',         'Adventuring Gear', 500,  0),
('Soap',                'Adventuring Gear', 2,    0),
('Spellbook',           'Arcane Focus',     5000, 3),
('Spike, iron',         'Adventuring Gear', 10,   0.5),
('Spyglass',            'Adventuring Gear', 100000,1),
('Tent, two-person',    'Adventuring Gear', 200,  20),
('Tinderbox',           'Adventuring Gear', 50,   1),
('Torch',               'Adventuring Gear', 1,    1),
('Vial',                'Container',        100,  0),
('Waterskin',           'Container',        20,   5),
('Whetstone',           'Adventuring Gear', 1,    1),
-- Ammunition
('Arrows (20)',         'Ammunition',       100,  1),
('Blowgun Needles (50)','Ammunition',       100,  1),
('Crossbow Bolts (20)', 'Ammunition',       100,  1.5),
('Sling Bullets (20)', 'Ammunition',        4,    1.5),
-- Holy Symbols
('Amulet',             'Holy Symbol',       500,  1),
('Emblem',             'Holy Symbol',       500,  0),
('Reliquary',          'Holy Symbol',       500,  2),
-- Druidic Focus
('Sprig of Mistletoe', 'Druidic Focus',     100,  0),
('Totem',              'Druidic Focus',     100,  0),
('Wooden Staff',       'Druidic Focus',     500,  4),
('Yew Wand',           'Druidic Focus',     1000, 1);

-- =============================================================================
-- 12. MOUNT
-- =============================================================================

CREATE TABLE IF NOT EXISTS mount (
    name        VARCHAR(63) NOT NULL,
    cost_gp     INTEGER,
    speed_ft    INTEGER,
    carry_cap   INTEGER,
    PRIMARY KEY (name)
);

INSERT INTO mount (name, cost_gp, speed_ft, carry_cap) VALUES
('Camel',     5000,  50, 480),
('Donkey',     800,  40, 420),
('Elephant', 20000,  40,1320),
('Horse, draft',5000,40, 540),
('Horse, riding',7500,60,480),
('Mastiff',    2500, 40,  195),
('Mule',        800, 40, 420),
('Pony',       3000, 40, 225),
('Warhorse',  40000, 60, 540);

-- =============================================================================
-- 13. CLASS
-- =============================================================================

CREATE TABLE IF NOT EXISTS class (
    name                VARCHAR(63) NOT NULL,
    hit_die             INTEGER     NOT NULL,
    primary_ability     VARCHAR(127)NOT NULL,
    spellcasting_ability VARCHAR(63),
    description         TEXT,
    PRIMARY KEY (name)
);

INSERT INTO class (name, hit_die, primary_ability, spellcasting_ability, description) VALUES
('Barbarian', 12, 'Strength',                      NULL,          'A fierce warrior of primitive background who can enter a battle rage.'),
('Bard',       8, 'Charisma',                      'Charisma',    'An inspiring magician whose power echoes the music of creation.'),
('Cleric',     8, 'Wisdom',                        'Wisdom',      'A priestly champion who wields divine magic in service of a higher power.'),
('Druid',      8, 'Wisdom',                        'Wisdom',      'A priest of the Old Faith, wielding the powers of nature and adopting animal forms.'),
('Fighter',   10, 'Strength or Dexterity',         NULL,          'A master of martial combat, skilled with a variety of weapons and armor.'),
('Monk',       8, 'Dexterity and Wisdom',          'Wisdom',      'A master of martial arts, harnessing the power of the body in pursuit of physical and spiritual perfection.'),
('Paladin',   10, 'Strength and Charisma',         'Charisma',    'A holy warrior bound to a sacred oath.'),
('Ranger',    10, 'Dexterity and Wisdom',          'Wisdom',      'A warrior who uses martial prowess and nature magic to combat threats on the edges of civilization.'),
('Rogue',      8, 'Dexterity',                     NULL,          'A scoundrel who uses stealth and trickery to overcome obstacles and enemies.'),
('Sorcerer',   6, 'Charisma',                      'Charisma',    'A spellcaster who draws on inherent magic from a gift or bloodline.'),
('Warlock',    8, 'Charisma',                      'Charisma',    'A wielder of magic that is derived from a bargain with an extraplanar entity.'),
('Wizard',     6, 'Intelligence',                  'Intelligence','A scholarly magic-user capable of manipulating the structures of reality.');

CREATE TABLE IF NOT EXISTS class_saving_throw (
    class_name  VARCHAR(63) NOT NULL,
    ability     VARCHAR(63) NOT NULL,
    PRIMARY KEY (class_name, ability),
    FOREIGN KEY (class_name) REFERENCES class(name),
    FOREIGN KEY (ability)    REFERENCES ability(name)
);

INSERT INTO class_saving_throw (class_name, ability) VALUES
('Barbarian','Strength'),('Barbarian','Constitution'),
('Bard','Dexterity'),('Bard','Charisma'),
('Cleric','Wisdom'),('Cleric','Charisma'),
('Druid','Intelligence'),('Druid','Wisdom'),
('Fighter','Strength'),('Fighter','Constitution'),
('Monk','Strength'),('Monk','Dexterity'),
('Paladin','Wisdom'),('Paladin','Charisma'),
('Ranger','Strength'),('Ranger','Dexterity'),
('Rogue','Dexterity'),('Rogue','Intelligence'),
('Sorcerer','Constitution'),('Sorcerer','Charisma'),
('Warlock','Wisdom'),('Warlock','Charisma'),
('Wizard','Intelligence'),('Wizard','Wisdom');

CREATE TABLE IF NOT EXISTS class_armour_type_proficiency (
    class_name  VARCHAR(63) NOT NULL,
    armour_type VARCHAR(63) NOT NULL,
    PRIMARY KEY (class_name, armour_type),
    FOREIGN KEY (class_name)  REFERENCES class(name),
    FOREIGN KEY (armour_type) REFERENCES armour_type(name)
);

INSERT INTO class_armour_type_proficiency (class_name, armour_type) VALUES
('Barbarian','Light'),('Barbarian','Medium'),('Barbarian','Shields'),
('Bard','Light'),
('Cleric','Light'),('Cleric','Medium'),('Cleric','Shields'),
('Druid','Light'),('Druid','Medium'),('Druid','Shields'),
('Fighter','Light'),('Fighter','Medium'),('Fighter','Heavy'),('Fighter','Shields'),
('Monk','Light'),
('Paladin','Light'),('Paladin','Medium'),('Paladin','Heavy'),('Paladin','Shields'),
('Ranger','Light'),('Ranger','Medium'),('Ranger','Shields'),
('Rogue','Light'),
('Warlock','Light');

CREATE TABLE IF NOT EXISTS class_weapon_type_proficiency (
    class_name      VARCHAR(63) NOT NULL,
    weapon_type     VARCHAR(63) NOT NULL,
    PRIMARY KEY (class_name, weapon_type),
    FOREIGN KEY (class_name)  REFERENCES class(name),
    FOREIGN KEY (weapon_type) REFERENCES weapon_type(name)
);

INSERT INTO class_weapon_type_proficiency (class_name, weapon_type) VALUES
('Barbarian','Simple Melee'),('Barbarian','Simple Ranged'),('Barbarian','Martial Melee'),('Barbarian','Martial Ranged'),
('Bard','Simple Melee'),('Bard','Simple Ranged'),
('Cleric','Simple Melee'),('Cleric','Simple Ranged'),
('Druid','Simple Melee'),('Druid','Simple Ranged'),
('Fighter','Simple Melee'),('Fighter','Simple Ranged'),('Fighter','Martial Melee'),('Fighter','Martial Ranged'),
('Paladin','Simple Melee'),('Paladin','Simple Ranged'),('Paladin','Martial Melee'),('Paladin','Martial Ranged'),
('Ranger','Simple Melee'),('Ranger','Simple Ranged'),('Ranger','Martial Melee'),('Ranger','Martial Ranged'),
('Rogue','Simple Melee'),('Rogue','Simple Ranged'),
('Sorcerer','Simple Melee'),('Sorcerer','Simple Ranged'),
('Warlock','Simple Melee'),('Warlock','Simple Ranged'),
('Wizard','Simple Melee'),('Wizard','Simple Ranged');

CREATE TABLE IF NOT EXISTS class_feature (
    class_name      VARCHAR(63)  NOT NULL,
    level           INTEGER      NOT NULL,
    feature_name    VARCHAR(127) NOT NULL,
    description     TEXT,
    PRIMARY KEY (class_name, level, feature_name),
    FOREIGN KEY (class_name) REFERENCES class(name)
);

INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
('Barbarian',1,'Rage','In battle, you fight with primal ferocity. On your turn, you can enter a rage as a bonus action. While raging, you gain advantage on Strength checks and saving throws, a bonus to melee damage, and resistance to bludgeoning, piercing, and slashing damage.'),
('Barbarian',1,'Unarmored Defense','While not wearing armor, your AC equals 10 + Dexterity modifier + Constitution modifier.'),
('Barbarian',2,'Reckless Attack','You can throw aside all concern for defense to attack with fierce desperation. When you make your first attack on your turn, you can decide to attack recklessly, giving you advantage on melee weapon attack rolls using Strength, but attack rolls against you have advantage until your next turn.'),
('Barbarian',2,'Danger Sense','You gain an uncanny sense of when things nearby aren''t as they should be, giving you advantage on Dexterity saving throws against effects you can see.'),
('Bard',1,'Spellcasting','You have learned to untangle and reshape the fabric of reality in harmony with your wishes and music.'),
('Bard',1,'Bardic Inspiration','You can inspire others through stirring words or music. A creature that has a Bardic Inspiration die can roll it and add the number rolled to one ability check, attack roll, or saving throw.'),
('Bard',2,'Jack of All Trades','You can add half your proficiency bonus to any ability check that doesn''t already include your proficiency bonus.'),
('Bard',2,'Song of Rest','You can use soothing music or oration to help revitalize your wounded allies during a short rest.'),
('Cleric',1,'Spellcasting','As a conduit for divine power, you can cast cleric spells.'),
('Cleric',1,'Divine Domain','Choose one domain related to your deity. Your choice grants you domain spells and other features when you choose it at 1st level.'),
('Cleric',2,'Channel Divinity','You gain the ability to channel divine energy directly from your deity, using that energy to fuel magical effects.'),
('Cleric',2,'Turn Undead','As an action, you present your holy symbol and speak a prayer censuring the undead.'),
('Druid',1,'Druidic','You know Druidic, the secret language of druids.'),
('Druid',1,'Spellcasting','Drawing on the divine essence of nature itself, you can cast spells to shape that essence to your will.'),
('Druid',2,'Wild Shape','Starting at 2nd level, you can use your action to magically assume the shape of a beast that you have seen before.'),
('Fighter',1,'Fighting Style','You adopt a particular style of fighting as your specialty.'),
('Fighter',1,'Second Wind','You have a limited well of stamina that you can draw on to protect yourself from harm. On your turn, you can use a bonus action to regain hit points equal to 1d10 + your fighter level.'),
('Fighter',2,'Action Surge','Starting at 2nd level, you can push yourself beyond your normal limits for a moment. On your turn, you can take one additional action.'),
('Monk',1,'Unarmored Defense','While you are wearing no armor and not wielding a shield, your AC equals 10 + Dexterity modifier + Wisdom modifier.'),
('Monk',1,'Martial Arts','Your practice of martial arts gives you mastery of combat styles that use unarmed strikes and monk weapons.'),
('Monk',2,'Ki','Starting at 2nd level, your training allows you to harness the mystic energy of ki.'),
('Monk',2,'Unarmored Movement','Starting at 2nd level, your speed increases by 10 feet while you are not wearing armor or wielding a shield.'),
('Paladin',1,'Divine Sense','The presence of strong evil registers on your senses like a noxious odor, and powerful good rings like heavenly music in your ears.'),
('Paladin',1,'Lay on Hands','Your blessed touch can heal wounds. You have a pool of healing power that replenishes when you take a long rest.'),
('Paladin',2,'Fighting Style','You adopt a particular style of fighting as your specialty.'),
('Paladin',2,'Spellcasting','By 2nd level, you have learned to draw on divine magic through meditation and prayer to cast spells.'),
('Paladin',2,'Divine Smite','Starting at 2nd level, when you hit a creature with a melee weapon attack, you can expend one spell slot to deal radiant damage.'),
('Ranger',1,'Favored Enemy','You have significant experience studying, tracking, hunting, and even talking to a certain type of enemy.'),
('Ranger',1,'Natural Explorer','You are particularly familiar with one type of natural environment and are adept at traveling and surviving in such regions.'),
('Ranger',2,'Fighting Style','You adopt a particular style of fighting as your specialty.'),
('Ranger',2,'Spellcasting','By the time you reach 2nd level, you have learned to use the magical essence of nature to cast spells.'),
('Rogue',1,'Expertise','At 1st level, choose two of your skill proficiencies, or one of your skill proficiencies and your proficiency with thieves'' tools. Your proficiency bonus is doubled for any ability check you make that uses either of the chosen proficiencies.'),
('Rogue',1,'Sneak Attack','Beginning at 1st level, you know how to strike subtly and exploit a foe''s distraction. Once per turn, you can deal an extra 1d6 damage to one creature you hit with an attack if you have advantage on the attack roll.'),
('Rogue',1,'Thieves'' Cant','During your rogue training you learned thieves'' cant, a secret mix of dialect, jargon, and code.'),
('Rogue',2,'Cunning Action','Starting at 2nd level, your quick thinking and agility allow you to move and act quickly. You can take a bonus action on each of your turns in combat to Dash, Disengage, or Hide.'),
('Sorcerer',1,'Spellcasting','An event in your past, or in the life of a parent or ancestor, left an indelible mark on you, infusing you with arcane magic.'),
('Sorcerer',1,'Sorcerous Origin','Choose a sorcerous origin, which describes the source of your innate magical power.'),
('Sorcerer',2,'Font of Magic','At 2nd level, you tap into a deep wellspring of magic within yourself. This wellspring is represented by sorcery points, which allow you to create a variety of magical effects.'),
('Warlock',1,'Otherworldly Patron','At 1st level, you have struck a bargain with an otherworldly being of your choice.'),
('Warlock',1,'Pact Magic','Your arcane research and the magic bestowed on you by your patron have given you facility with spells.'),
('Warlock',2,'Eldritch Invocations','In your study of occult lore, you have unearthed eldritch invocations, fragments of forbidden knowledge that imbue you with an abiding magical ability.'),
('Wizard',1,'Spellcasting','As a student of arcane magic, you have a spellbook containing spells that show the first glimmerings of your true power.'),
('Wizard',1,'Arcane Recovery','You have learned to regain some of your magical energy by studying your spellbook. Once per day when you finish a short rest, you can choose expended spell slots to recover.'),
('Wizard',2,'Arcane Tradition','When you reach 2nd level, you choose an arcane tradition, shaping your practice of magic.');

-- =============================================================================
-- 14. SUBCLASS
-- =============================================================================

CREATE TABLE IF NOT EXISTS subclass (
    name        VARCHAR(127) NOT NULL,
    class_name  VARCHAR(63)  NOT NULL,
    description TEXT,
    PRIMARY KEY (name),
    FOREIGN KEY (class_name) REFERENCES class(name)
);

INSERT INTO subclass (name, class_name, description) VALUES
-- Barbarian
('Path of the Berserker',       'Barbarian', 'For some barbarians, rage is a means to an end—that end being violence. The Path of the Berserker is a path of untrammeled fury, slick with blood.'),
('Path of the Totem Warrior',   'Barbarian', 'The Path of the Totem Warrior is a spiritual journey, as the barbarian accepts a spirit animal as guide, protector, and inspiration.'),
-- Bard
('College of Lore',             'Bard',      'Bards of the College of Lore know something about most things, collecting bits of knowledge from sources as diverse as scholarly tomes and peasant tales.'),
('College of Valor',            'Bard',      'Bards of the College of Valor are daring skalds whose tales keep alive the memory of the great heroes of the past.'),
-- Cleric
('Knowledge Domain',            'Cleric',    'The gods of knowledge—including Oghma, Boccob, Gilean, Aureon, and Thoth—value learning and understanding above all.'),
('Life Domain',                 'Cleric',    'The Life domain focuses on the vibrant positive energy—one of the fundamental forces of the universe—that sustains all life.'),
('Light Domain',                'Cleric',    'Gods of light—including Helm, Lathander, Pholtus, Branchala, the Silver Flame, Belenus, Apollo, and Re-Horakhty—promote the ideals of rebirth and renewal.'),
('Nature Domain',               'Cleric',    'Gods of nature are as varied as the natural world itself, from inscrutable gods of the deep forests to friendly deities associated with particular springs and groves.'),
('Tempest Domain',              'Cleric',    'Gods whose portfolios include the Tempest domain—including Talos, Umberlee, Kord, Zeboim, the Devourer, Zeus, and Thor—govern storms, sea, and sky.'),
('Trickery Domain',             'Cleric',    'Gods of trickery—such as Tymora, Beshaba, Olidammara, the Traveler, Garl Glittergold, and Loki—are mischief-makers and instigators.'),
('War Domain',                  'Cleric',    'War has many manifestations. It can make heroes of ordinary people. The gods of war watch over warriors and reward them for their great deeds.'),
-- Druid
('Circle of the Land',          'Druid',     'The Circle of the Land is made up of mystics and sages who safeguard ancient knowledge and rites through a vast oral tradition.'),
('Circle of the Moon',          'Druid',     'Druids of the Circle of the Moon are fierce guardians of the wilds.'),
-- Fighter
('Champion',                    'Fighter',   'The archetypal Champion focuses on the development of raw physical power honed to deadly perfection.'),
('Battle Master',               'Fighter',   'Those who emulate the archetypal Battle Master employ martial techniques passed down through generations.'),
('Eldritch Knight',             'Fighter',   'The archetypal Eldritch Knight combines the martial mastery common to all fighters with a careful study of magic.'),
-- Monk
('Way of the Open Hand',        'Monk',      'Monks of the Way of the Open Hand are the ultimate masters of martial arts combat.'),
('Way of Shadow',               'Monk',      'Monks of the Way of Shadow follow a tradition that values stealth and subterfuge.'),
('Way of the Four Elements',    'Monk',      'You follow a monastic tradition that teaches you to harness the elements.'),
-- Paladin
('Oath of Devotion',            'Paladin',   'The Oath of Devotion binds a paladin to the loftiest ideals of justice, virtue, and order.'),
('Oath of the Ancients',        'Paladin',   'The Oath of the Ancients is as old as the race of elves and the rituals of the druids.'),
('Oath of Vengeance',           'Paladin',   'The Oath of Vengeance is a solemn commitment to punish those who have committed a grievous sin.'),
-- Ranger
('Hunter',                      'Ranger',    'Emulating the Hunter archetype means accepting your place as a bulwark between civilization and the terrors of the wilderness.'),
('Beast Master',                'Ranger',    'The archetypal Beast Master forms a deep bond with a beast, then commands the beast in battle.'),
-- Rogue
('Thief',                       'Rogue',     'You hone your skills in the larcenous arts. Burglars, bandits, cutpurses, and other criminals typically follow this archetype.'),
('Assassin',                    'Rogue',     'You focus your training on the grim art of death. Those who adhere to this archetype are diverse: hired killers, spies, bounty hunters.'),
('Arcane Trickster',            'Rogue',     'Some rogues enhance their fine-honed skills of stealth and agility with magic, learning tricks of enchantment and illusion.'),
-- Sorcerer
('Draconic Bloodline',          'Sorcerer',  'Your innate magic comes from draconic magic that was mingled with your blood or that of your ancestors.'),
('Wild Magic',                  'Sorcerer',  'Your innate magic comes from the wild forces of chaos that underlie the order of creation.'),
-- Warlock
('The Archfey',                 'Warlock',   'Your patron is a lord or lady of the fey, a creature of legend who holds secrets that were forgotten before the mortal races were born.'),
('The Fiend',                   'Warlock',   'You have made a pact with a fiend from the lower planes of existence, a being whose aims are evil.'),
('The Great Old One',           'Warlock',   'Your patron is a mysterious entity whose nature is utterly foreign to the fabric of reality.'),
-- Wizard
('School of Abjuration',        'Wizard',    'The School of Abjuration emphasizes magic that blocks, banishes, or protects.'),
('School of Conjuration',       'Wizard',    'As a conjurer, you favor spells that produce objects and creatures out of thin air.'),
('School of Divination',        'Wizard',    'The counsel of a diviner is sought by royalty and commoners alike.'),
('School of Enchantment',       'Wizard',    'As a member of the School of Enchantment, you have honed your ability to magically entrance and beguile other people and monsters.'),
('School of Evocation',         'Wizard',    'You focus your study on magic that creates powerful elemental effects such as bitter cold, searing flame, rolling thunder, crackling lightning, and burning acid.'),
('School of Illusion',          'Wizard',    'You focus your studies on magic that dazzles the senses, befuddles the mind, and tricks even the wisest folk.'),
('School of Necromancy',        'Wizard',    'The School of Necromancy explores the cosmic forces of life, death, and undeath.'),
('School of Transmutation',     'Wizard',    'You are a student of spells that modify energy and matter.');

-- =============================================================================
-- 15. BACKGROUND
-- =============================================================================

CREATE TABLE IF NOT EXISTS background (
    name                        VARCHAR(63)  NOT NULL,
    description                 TEXT,
    coins                       INTEGER      NOT NULL DEFAULT 1000,
    skills_limit                INTEGER,
    languages_limit             INTEGER,
    personality_trait_limit     INTEGER,
    ideal_limit                 INTEGER,
    bond_limit                  INTEGER,
    flaw_limit                  INTEGER,
    PRIMARY KEY (name)
);

INSERT INTO background (name, description, coins, skills_limit, languages_limit, personality_trait_limit, ideal_limit, bond_limit, flaw_limit) VALUES
('Acolyte',         'You have spent your life in the service of a temple to a specific god or pantheon of gods.',                                                   1500, 2, 2, 2, 1, 1, 1),
('Charlatan',       'You have always had a way with people. You know what makes them tick and can tease out their hearts'' desires after a few minutes of conversation.', 1500, 2, NULL, 2, 1, 1, 1),
('Criminal',        'You are an experienced criminal with a history of breaking the law.',                                                                          1500, 2, NULL, 2, 1, 1, 1),
('Criminal: Spy',   'Although your capabilities are not much different from those of a burglar or smuggler, you learned them in the context of espionage.',         1500, 2, NULL, 2, 1, 1, 1),
('Entertainer',     'You thrive in front of an audience. You know how to entrance them, entertain them, and even inspire them.',                                    1500, 2, NULL, 2, 1, 1, 1),
('Folk Hero',       'You come from a humble social rank, but you are destined for so much more.',                                                                   1000, 2, NULL, 2, 1, 1, 1),
('Guild Artisan',   'You are a member of an artisan''s guild, skilled in a particular field and closely associated with other artisans.',                           1500, 2, 1,    2, 1, 1, 1),
('Guild Merchant',  'Instead of an artisans'' guild, you belong to a guild of traders, caravan masters, or shopkeepers.',                                           1500, 2, 2,    2, 1, 1, 1),
('Hermit',          'You lived in seclusion for a formative part of your life.',                                                                                     500, 2, 1,    2, 1, 1, 1),
('Noble',           'You understand wealth, power, and privilege. You carry a noble title, and your family owns land.',                                             2500, 2, 1,    2, 1, 1, 1),
('Noble: Knight',   'A knighthood is among the lowest noble titles in most societies, but it can be a path to a higher status.',                                    2500, 2, 1,    2, 1, 1, 1),
('Outlander',       'You grew up in the wilds, far from civilisation and the comforts of town and technology.',                                                     1000, 2, 1,    2, 1, 1, 1),
('Sage',            'You spent years learning the lore of the multiverse.',                                                                                         1000, 2, 2,    2, 1, 1, 1),
('Sailor',          'You sailed on a seagoing vessel for years.',                                                                                                   1000, 2, NULL, 2, 1, 1, 1),
('Sailor: Pirate',  'You spent your youth under the sway of a dread pirate, a ruthless cutthroat who taught you how to survive.',                                  1000, 2, NULL, 2, 1, 1, 1),
('Soldier',         'War has been your life for as long as you care to remember.',                                                                                  1000, 2, NULL, 2, 1, 1, 1),
('Urchin',          'You grew up on the streets alone, orphaned, and poor.',                                                                                        1000, 2, NULL, 2, 1, 1, 1);

CREATE TABLE IF NOT EXISTS background_skill (
    background  VARCHAR(63) NOT NULL,
    skill_name  VARCHAR(63) NOT NULL,
    PRIMARY KEY (background, skill_name),
    FOREIGN KEY (background) REFERENCES background(name),
    FOREIGN KEY (skill_name) REFERENCES skill(name)
);

INSERT INTO background_skill (background, skill_name) VALUES
('Acolyte','Insight'),('Acolyte','Religion'),
('Charlatan','Deception'),('Charlatan','Sleight of Hand'),
('Criminal','Deception'),('Criminal','Stealth'),
('Criminal: Spy','Deception'),('Criminal: Spy','Stealth'),
('Entertainer','Acrobatics'),('Entertainer','Performance'),
('Folk Hero','Animal Handling'),('Folk Hero','Survival'),
('Guild Artisan','Insight'),('Guild Artisan','Persuasion'),
('Guild Merchant','Insight'),('Guild Merchant','Persuasion'),
('Hermit','Medicine'),('Hermit','Religion'),
('Noble','History'),('Noble','Perception'),
('Noble: Knight','History'),('Noble: Knight','Persuasion'),
('Outlander','Acrobatics'),('Outlander','Survival'),
('Sage','Arcana'),('Sage','History'),
('Sailor','Athletics'),('Sailor','Perception'),
('Sailor: Pirate','Athletics'),('Sailor: Pirate','Perception'),
('Soldier','Athletics'),('Soldier','Intimidation'),
('Urchin','Sleight of Hand'),('Urchin','Stealth');

CREATE TABLE IF NOT EXISTS background_feature (
    background  VARCHAR(63)  NOT NULL,
    feature     VARCHAR(127) NOT NULL,
    description TEXT,
    PRIMARY KEY (background, feature),
    FOREIGN KEY (background) REFERENCES background(name)
);

INSERT INTO background_feature (background, feature, description) VALUES
('Acolyte',       'Shelter of the Faithful',  'As an acolyte, you command the respect of those who share your faith, and you can perform the religious ceremonies of your deity.'),
('Charlatan',     'False Identity',            'You have created a second identity that includes documentation, established acquaintances, and disguises.'),
('Criminal',      'Criminal Contact',          'You have a reliable and trustworthy contact who acts as your liaison to a network of other criminals.'),
('Criminal: Spy', 'Criminal Contact',          'You have a reliable and trustworthy contact who acts as your liaison to a network of other criminals.'),
('Entertainer',   'By Popular Demand',         'You can always find a place to perform, usually in an inn or tavern but possibly with a circus, at a theatre, or even in a noble''s court.'),
('Folk Hero',     'Rustic Hospitality',        'Since you come from the ranks of the common folk, you fit in among them with ease.'),
('Guild Artisan', 'Guild Membership',          'As an established and respected member of a guild, you can rely on certain benefits that membership provides.'),
('Guild Merchant','Guild Membership',          'As an established and respected member of a guild, you can rely on certain benefits that membership provides.'),
('Hermit',        'Discovery',                 'The quiet seclusion of your extended hermitage gave you access to a unique and powerful discovery.'),
('Noble',         'Position of Privilege',     'Thanks to your noble birth, people are inclined to think the best of you.'),
('Noble: Knight', 'Retainers',                 'You have the service of three retainers loyal to your family.'),
('Outlander',     'Wanderer',                  'You have an excellent memory for maps and geography, and you can always recall the general layout of terrain, settlements, and other features around you.'),
('Sage',          'Researcher',                'When you attempt to learn or recall a piece of lore, if you do not know that information, you often know where and from whom you can obtain it.'),
('Sailor',        'Ship''s Passage',           'When you need to, you can secure free passage on a sailing ship for yourself and your adventuring companions.'),
('Sailor: Pirate','Bad Reputation',            'No matter where you go, people are afraid of you due to your reputation.'),
('Soldier',       'Military Rank',             'You have a military rank from your career as a soldier.'),
('Urchin',        'City Secrets',              'You know the secret patterns and flow to cities and can find passages through the urban sprawl that others would miss.');

-- =============================================================================
-- 16. SPELL SCHOOL & SPELL
-- =============================================================================

CREATE TABLE IF NOT EXISTS spell_school (
    name        VARCHAR(63) NOT NULL,
    description TEXT,
    PRIMARY KEY (name)
);

INSERT INTO spell_school (name, description) VALUES
('Abjuration',    'Protective spells that block, banish, or otherwise protect against threats.'),
('Conjuration',   'Spells that transport objects or creatures, summon beings, or create objects.'),
('Divination',    'Spells that reveal information, whether in the form of secrets long forgotten, glimpses of the future, or locations of hidden things.'),
('Enchantment',   'Spells that affect the minds of others, influencing or controlling their behaviour.'),
('Evocation',     'Spells that manipulate magical energy to produce a desired effect.'),
('Illusion',      'Spells that deceive the senses or minds of others.'),
('Necromancy',    'Spells that manipulate the energies of life and death.'),
('Transmutation', 'Spells that change the properties of a creature, object, or environment.');

CREATE TABLE IF NOT EXISTS spell (
    id                  INTEGER      NOT NULL,
    name                VARCHAR(127) NOT NULL,
    spell_school        VARCHAR(63)  NOT NULL,
    spell_level         INTEGER      NOT NULL,
    casting_time        VARCHAR(63)  NOT NULL,
    range_area          VARCHAR(63)  NOT NULL,
    duration            VARCHAR(63)  NOT NULL,
    is_concentration    BOOLEAN      NOT NULL DEFAULT 0,
    is_ritual           BOOLEAN      NOT NULL DEFAULT 0,
    requires_verbal     BOOLEAN      NOT NULL DEFAULT 1,
    requires_somatic    BOOLEAN      NOT NULL DEFAULT 1,
    requires_material   BOOLEAN      NOT NULL DEFAULT 0,
    material_desc       TEXT,
    description         TEXT         NOT NULL,
    higher_levels       TEXT,
    source              VARCHAR(63)  NOT NULL DEFAULT 'PHB',
    PRIMARY KEY (id),
    FOREIGN KEY (spell_school) REFERENCES spell_school(name)
);

INSERT INTO spell (id, name, spell_school, spell_level, casting_time, range_area, duration, is_concentration, is_ritual, requires_verbal, requires_somatic, requires_material, material_desc, description, higher_levels) VALUES
(1,  'Acid Splash',       'Conjuration',  0,'1 action','60 feet','Instantaneous',0,0,1,1,0,NULL,'You hurl a bubble of acid. Choose one or two creatures within range that are within 5 feet of each other. A target must succeed on a Dexterity saving throw or take 1d6 acid damage.','Damage increases by 1d6 at 5th (2d6), 11th (3d6), and 17th level (4d6).'),
(2,  'Blade Ward',        'Abjuration',   0,'1 action','Self',  '1 round',       0,0,1,1,0,NULL,'You extend your hand and trace a sigil of warding in the air. Until the end of your next turn, you have resistance against bludgeoning, piercing, and slashing damage dealt by weapon attacks.',NULL),
(3,  'Chill Touch',       'Necromancy',   0,'1 action','120 feet','1 round',     0,0,1,1,0,NULL,'You create a ghostly hand in the space of a creature within range. Make a ranged spell attack. On a hit, the target takes 1d8 necrotic damage and can''t regain hit points until the start of your next turn.',NULL),
(4,  'Dancing Lights',    'Evocation',    0,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,0,NULL,'You create up to four torch-sized lights within range that hover in the air for the duration.',NULL),
(5,  'Druidcraft',        'Transmutation',0,'1 action','30 feet','Instantaneous',0,0,1,1,0,NULL,'Whispering to the spirits of nature, you create one of the following effects within range: predict weather, make a flower bloom, or create a harmless sensory effect.',NULL),
(6,  'Eldritch Blast',    'Evocation',    0,'1 action','120 feet','Instantaneous',0,0,1,1,0,NULL,'A beam of crackling energy streaks toward a creature within range. Make a ranged spell attack. On a hit, the target takes 1d10 force damage. Creates more beams at higher levels.','Two beams at 5th level, three at 11th, four at 17th.'),
(7,  'Fire Bolt',         'Evocation',    0,'1 action','120 feet','Instantaneous',0,0,1,1,0,NULL,'You hurl a mote of fire at a creature or object within range. Make a ranged spell attack. On a hit, the target takes 1d10 fire damage.','Damage increases by 1d10 at 5th (2d10), 11th (3d10), and 17th level (4d10).'),
(8,  'Friends',           'Enchantment',  0,'1 action','Self',   'Concentration, up to 1 minute',1,0,0,1,1,'A small amount of makeup','For the duration, you have advantage on all Charisma checks directed at one creature of your choice that isn''t hostile toward you.',NULL),
(9,  'Guidance',          'Divination',   0,'1 action','Touch',  'Concentration, up to 1 minute',1,0,1,1,0,NULL,'You touch one willing creature. Once before the spell ends, the target can roll a d4 and add the number rolled to one ability check of its choice.',NULL),
(10, 'Light',             'Evocation',    0,'1 action','Touch',  '1 hour',        0,0,1,0,1,'A firefly or phosphorescent moss','You touch one object no larger than 10 feet in any dimension. Until the spell ends, the object sheds bright light in a 20-foot radius.',NULL),
(11, 'Mage Hand',         'Conjuration',  0,'1 action','30 feet','1 minute',      0,0,1,1,0,NULL,'A spectral floating hand appears at a point you choose within range and lasts for the duration.',NULL),
(12, 'Mending',           'Transmutation',0,'1 minute','Touch',  'Instantaneous', 0,0,1,1,1,'Two lodestones','This spell repairs a single break or tear in an object you touch.',NULL),
(13, 'Message',           'Transmutation',0,'1 action','120 feet','1 round',      0,0,0,1,1,'A short piece of copper wire','You point toward a creature within range and whisper a message. Only the target hears it and can whisper a reply.',NULL),
(14, 'Minor Illusion',    'Illusion',     0,'1 action','30 feet','1 minute',      0,0,0,1,1,'A bit of fleece','You create a sound or an image of an object within range that lasts for the duration.',NULL),
(15, 'Poison Spray',      'Conjuration',  0,'1 action','10 feet','Instantaneous', 0,0,1,1,0,NULL,'You extend your hand and project a puff of noxious gas. The creature must succeed on a Constitution saving throw or take 1d12 poison damage.',NULL),
(16, 'Prestidigitation',  'Transmutation',0,'1 action','10 feet','Up to 1 hour',  0,0,1,1,0,NULL,'A minor magical trick for practice. Create a sensory effect, light or snuff a candle, clean or soil an object, chill or warm material.',NULL),
(17, 'Ray of Frost',      'Evocation',    0,'1 action','60 feet','Instantaneous', 0,0,1,1,0,NULL,'A frigid beam streaks toward a creature. Make a ranged spell attack. On a hit, it takes 1d8 cold damage and its speed is reduced by 10 feet until the start of your next turn.',NULL),
(18, 'Resistance',        'Abjuration',   0,'1 action','Touch',  'Concentration, up to 1 minute',1,0,1,1,1,'A miniature cloak','You touch one willing creature. Once before the spell ends, the target can roll a d4 and add it to one saving throw.',NULL),
(19, 'Sacred Flame',      'Evocation',    0,'1 action','60 feet','Instantaneous', 0,0,1,1,0,NULL,'Flame-like radiance descends on a creature you can see within range. The target must succeed on a Dexterity saving throw or take 1d8 radiant damage.',NULL),
(20, 'Shillelagh',        'Transmutation',0,'1 bonus action','Self','1 minute',   0,0,1,1,1,'Mistletoe, a shamrock leaf, and a club or quarterstaff','The wood of a club or quarterstaff you are holding is imbued with nature''s power. Use spellcasting ability instead of Strength for attack and damage rolls.',NULL),
(21, 'Shocking Grasp',    'Evocation',    0,'1 action','Touch',  'Instantaneous', 0,0,1,1,0,NULL,'Lightning springs from your hand. Make a melee spell attack. You have advantage if the target wears metal armor. On a hit, takes 1d8 lightning damage.',NULL),
(22, 'Spare the Dying',   'Necromancy',   0,'1 action','Touch',  'Instantaneous', 0,0,1,1,0,NULL,'You touch a living creature that has 0 hit points. The creature becomes stable.',NULL),
(23, 'Thaumaturgy',       'Transmutation',0,'1 action','30 feet','Up to 1 minute',0,0,1,0,0,NULL,'You manifest a minor wonder within range: booming voice, flickering flames, tremors, or similar effects.',NULL),
(24, 'True Strike',       'Divination',   0,'1 action','30 feet','Concentration, up to 1 round',1,0,0,1,0,NULL,'You point at a target in range. On your next turn, you gain advantage on your first attack roll against the target.',NULL),
(25, 'Vicious Mockery',   'Enchantment',  0,'1 action','60 feet','Instantaneous', 0,0,1,0,0,NULL,'You unleash insults laced with subtle enchantments. The target must succeed on a Wisdom saving throw or take 1d4 psychic damage and have disadvantage on its next attack roll.',NULL),
-- 1st Level
(100,'Alarm',             'Abjuration',   1,'1 minute','30 feet','8 hours',       0,1,1,1,1,'A tiny bell and fine silver wire','You set an alarm against unwanted intrusion. Choose a door, window, or area within range no larger than a 20-foot cube.',NULL),
(101,'Animal Friendship', 'Enchantment',  1,'1 action','30 feet','24 hours',      0,0,1,1,1,'A morsel of food','This spell lets you convince a beast that you mean it no harm.','One additional beast per slot level above 1st.'),
(102,'Bane',              'Enchantment',  1,'1 action','30 feet','Concentration, up to 1 minute',1,0,1,1,1,'A drop of blood','Up to three creatures must make Charisma saving throws. On failure, they subtract 1d4 from attack rolls and saving throws.','One additional creature per slot level above 1st.'),
(103,'Burning Hands',     'Evocation',    1,'1 action','Self (15-ft cone)','Instantaneous',0,0,1,1,0,NULL,'A sheet of flames shoots from your outstretched fingertips. Each creature in a 15-foot cone takes 3d6 fire damage (Dex save for half).','Damage increases by 1d6 per slot level above 1st.'),
(104,'Charm Person',      'Enchantment',  1,'1 action','30 feet','Concentration, up to 1 hour',1,0,1,1,0,NULL,'You attempt to charm a humanoid. On a failed Wisdom save, it is charmed by you until the spell ends.','One additional creature per slot level above 1st.'),
(105,'Color Spray',       'Illusion',     1,'1 action','Self (15-ft cone)','Instantaneous',0,0,1,1,1,'Powdered red, yellow, and blue','Roll 6d10; that many hit points of creatures are blinded.','Roll 2d10 more per slot level above 1st.'),
(106,'Comprehend Languages','Divination', 1,'1 action','Self',  '1 hour',        0,1,1,1,1,'Soot and salt','For the duration, you understand the literal meaning of any spoken language that you hear.',NULL),
(107,'Cure Wounds',       'Evocation',    1,'1 action','Touch', 'Instantaneous', 0,0,1,1,0,NULL,'A creature you touch regains 1d8 + spellcasting modifier hit points.','Healing increases by 1d8 per slot level above 1st.'),
(108,'Detect Magic',      'Divination',   1,'1 action','Self',  'Concentration, up to 10 minutes',1,1,1,1,0,NULL,'For the duration, you sense the presence of magic within 30 feet of you.',NULL),
(109,'Disguise Self',     'Illusion',     1,'1 action','Self',  '1 hour',        0,0,1,1,0,NULL,'You make yourself look different until the spell ends or you dismiss it as an action.',NULL),
(110,'Expeditious Retreat','Transmutation',1,'1 bonus action','Self','Concentration, up to 10 minutes',1,0,1,1,0,NULL,'When you cast this spell and as a bonus action each turn, you can take the Dash action.',NULL),
(111,'False Life',        'Necromancy',   1,'1 action','Self',  '1 hour',        0,0,1,1,1,'Alcohol or distilled spirits','You gain 1d4 + 4 temporary hit points for the duration.','5 additional temporary hit points per slot level above 1st.'),
(112,'Feather Fall',      'Transmutation',1,'1 reaction','60 feet','1 minute',   0,0,0,1,1,'A feather or piece of down','Up to five falling creatures'' rate of descent slows to 60 feet per round.',NULL),
(113,'Find Familiar',     'Conjuration',  1,'1 hour',  '10 feet','Instantaneous',0,1,1,1,1,'10 gp of charcoal, incense, and herbs burned in a brass brazier','You gain the service of a familiar in the form of an animal you choose.',NULL),
(114,'Fog Cloud',         'Conjuration',  1,'1 action','120 feet','Concentration, up to 1 hour',1,0,1,1,0,NULL,'You create a 20-foot-radius sphere of fog centered on a point within range.','Radius increases by 20 feet per slot level above 1st.'),
(115,'Grease',            'Conjuration',  1,'1 action','60 feet','1 minute',     0,0,1,1,1,'Butter or pork rind','Slick grease covers a 10-foot square, turning it into difficult terrain.',NULL),
(116,'Healing Word',      'Evocation',    1,'1 bonus action','60 feet','Instantaneous',0,0,1,0,0,NULL,'A creature regains 1d4 + spellcasting modifier hit points.','Healing increases by 1d4 per slot level above 1st.'),
(117,'Heroism',           'Enchantment',  1,'1 action','Touch', 'Concentration, up to 1 minute',1,0,1,1,0,NULL,'A willing creature becomes immune to fright and gains temporary hit points equal to your spellcasting modifier at the start of each turn.','One additional creature per slot level above 1st.'),
(118,'Identify',          'Divination',   1,'1 minute','Touch', 'Instantaneous', 0,1,1,1,1,'A pearl worth 100 gp and an owl feather','You learn the properties of a magic item or magic-imbued object you touch.',NULL),
(119,'Inflict Wounds',    'Necromancy',   1,'1 action','Touch', 'Instantaneous', 0,0,1,1,0,NULL,'Make a melee spell attack. On a hit, the target takes 3d10 necrotic damage.','Damage increases by 1d10 per slot level above 1st.'),
(120,'Jump',              'Transmutation',1,'1 action','Touch', '1 minute',      0,0,1,1,1,'A grasshopper''s hind leg','The touched creature''s jump distance is tripled until the spell ends.',NULL),
(121,'Longstrider',       'Transmutation',1,'1 action','Touch', '1 hour',        0,0,1,1,1,'A pinch of dirt','The target''s speed increases by 10 feet until the spell ends.','One additional creature per slot level above 1st.'),
(122,'Mage Armor',        'Abjuration',   1,'1 action','Touch', '8 hours',       0,0,1,1,1,'A piece of cured leather','A willing creature not wearing armor gets base AC 13 + Dexterity modifier.',NULL),
(123,'Magic Missile',     'Evocation',    1,'1 action','120 feet','Instantaneous',0,0,1,1,0,NULL,'Three glowing darts each deal 1d4 + 1 force damage. They all strike simultaneously.','One more dart per slot level above 1st.'),
(124,'Protection from Evil and Good','Abjuration',1,'1 action','Touch','Concentration, up to 10 minutes',1,0,1,1,1,'Holy water or powdered silver and iron','One willing creature is protected against aberrations, celestials, elementals, fey, fiends, and undead.',NULL),
(125,'Ray of Sickness',   'Necromancy',   1,'1 action','60 feet','Instantaneous',0,0,1,1,0,NULL,'A ray of sickening energy. Make a ranged spell attack. On a hit, target takes 2d8 poison damage and must save or be poisoned until end of your next turn.','Damage increases by 1d8 per slot level above 1st.'),
(126,'Shield',            'Abjuration',   1,'1 reaction','Self','1 round',        0,0,0,1,0,NULL,'An invisible barrier appears. Until the start of your next turn, you have +5 bonus to AC and take no damage from magic missile.',NULL),
(127,'Shield of Faith',   'Abjuration',   1,'1 bonus action','60 feet','Concentration, up to 10 minutes',1,0,1,1,1,'A parchment with holy text','A shimmering field grants a creature within range +2 bonus to AC.',NULL),
(128,'Silent Image',      'Illusion',     1,'1 action','60 feet','Concentration, up to 10 minutes',1,0,1,1,1,'A bit of fleece','You create the image of an object, creature, or phenomenon no larger than a 15-foot cube.',NULL),
(129,'Sleep',             'Enchantment',  1,'1 action','90 feet','1 minute',      0,0,1,1,1,'Fine sand, rose petals, or a cricket','Roll 5d8; that many hit points of creatures fall unconscious.','Roll 2d8 more per slot level above 1st.'),
(130,'Speak with Animals','Divination',   1,'1 action','Self',  '10 minutes',     0,1,1,1,0,NULL,'You can comprehend and verbally communicate with beasts for the duration.',NULL),
(131,'Thunderwave',       'Evocation',    1,'1 action','Self (15-ft cube)','Instantaneous',0,0,1,1,0,NULL,'A wave of force sweeps out. Creatures in a 15-foot cube take 2d8 thunder damage and are pushed 10 feet away (Con save for half and not pushed).','Damage increases by 1d8 per slot level above 1st.'),
(132,'Unseen Servant',    'Conjuration',  1,'1 action','60 feet','1 hour',        0,1,1,1,1,'String and wood','An invisible mindless force performs simple tasks at your command.',NULL),
(133,'Witch Bolt',        'Evocation',    1,'1 action','30 feet','Concentration, up to 1 minute',1,0,1,1,1,'A twig struck by lightning','A beam of crackling energy. Make a ranged spell attack. On a hit, target takes 1d12 lightning damage. You can maintain the arc with your action each turn.','Initial damage increases by 1d12 per slot level above 1st.'),
-- 2nd Level
(200,'Aid',               'Abjuration',   2,'1 action','30 feet','8 hours',       0,0,1,1,1,'White cloth strip','Up to three creatures'' hit point maximums and current hit points increase by 5.','5 more hit points per slot level above 2nd.'),
(201,'Alter Self',        'Transmutation',2,'1 action','Self',  'Concentration, up to 1 hour',1,0,1,1,0,NULL,'You assume a different form: Aquatic Adaptation, Change Appearance, or Natural Weapons.',NULL),
(202,'Arcane Lock',       'Abjuration',   2,'1 action','Touch', 'Until dispelled',0,0,1,1,1,'Gold dust worth 25 gp','A closed door, window, gate, or chest becomes locked for the duration.',NULL),
(203,'Blindness/Deafness','Necromancy',   2,'1 action','30 feet','1 minute',      0,0,1,0,0,NULL,'Choose one creature to make a Constitution saving throw. On failure, it is blinded or deafened (your choice).','One additional creature per slot level above 2nd.'),
(204,'Blur',              'Illusion',     2,'1 action','Self',  'Concentration, up to 1 minute',1,0,1,0,0,NULL,'Your body becomes blurred. Any creature has disadvantage on attack rolls against you.',NULL),
(205,'Calm Emotions',     'Enchantment',  2,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,0,NULL,'Each humanoid in a 20-foot sphere must succeed on a Charisma saving throw or have their strong emotions suppressed.',NULL),
(206,'Darkvision',        'Transmutation',2,'1 action','Touch', '8 hours',        0,0,1,1,1,'Dried carrot or agate','You touch a willing creature and grant it darkvision out to 60 feet.',NULL),
(207,'Enhance Ability',   'Transmutation',2,'1 action','Touch', 'Concentration, up to 1 hour',1,0,1,1,1,'Fur or a feather','You bestow a magical enhancement granting advantage on one ability check type.',NULL),
(208,'Enlarge/Reduce',    'Transmutation',2,'1 action','30 feet','Concentration, up to 1 minute',1,0,1,1,1,'Powdered iron','You cause a creature or object within range to grow larger or smaller.',NULL),
(209,'Enthrall',          'Enchantment',  2,'1 action','60 feet','1 minute',      0,0,1,1,0,NULL,'Creatures of your choice that can hear you must make a Wisdom save or be distracted, giving you advantage on Charisma checks against them.',NULL),
(210,'Find Traps',        'Divination',   2,'1 action','120 feet','Instantaneous',0,0,1,1,0,NULL,'You sense the presence of any trap within range that is within line of sight.',NULL),
(211,'Flame Blade',       'Evocation',    2,'1 bonus action','Self','Concentration, up to 10 minutes',1,0,1,1,1,'Leaf of sumac','You evoke a fiery scimitar-sized blade in your free hand that lasts for the duration.','Damage increases by 1d6 per two slot levels above 2nd.'),
(212,'Flaming Sphere',    'Conjuration',  2,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,1,'Tallow, brimstone, and iron dust','A 5-foot sphere of fire appears. Creatures ending their turn within 5 feet must make a Dex save or take 2d6 fire damage.','Damage increases by 1d6 per slot level above 2nd.'),
(213,'Gust of Wind',      'Evocation',    2,'1 action','Self (60-ft line)','Concentration, up to 1 minute',1,0,1,1,1,'A legume seed','A strong wind blasts out in a 60-foot line. Creatures starting their turn in the line must succeed on Strength saves or be pushed back.',''),
(214,'Hold Person',       'Enchantment',  2,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,1,'A straight piece of iron','Choose a humanoid. On a failed Wisdom save, it is paralyzed for the duration.','One additional humanoid per slot level above 2nd.'),
(215,'Invisibility',      'Illusion',     2,'1 action','Touch', 'Concentration, up to 1 hour',1,0,1,1,1,'An eyelash in gum arabic','A creature you touch becomes invisible until the spell ends.','One additional creature per slot level above 2nd.'),
(216,'Knock',             'Transmutation',2,'1 action','60 feet','Instantaneous', 0,0,1,0,0,NULL,'Choose a locked or barred object. It becomes unlocked and unstuck.',NULL),
(217,'Lesser Restoration','Abjuration',   2,'1 action','Touch', 'Instantaneous', 0,0,1,1,0,NULL,'You touch a creature and end one disease or one condition: blinded, deafened, paralyzed, or poisoned.',NULL),
(218,'Levitate',          'Transmutation',2,'1 action','60 feet','Concentration, up to 10 minutes',1,0,1,1,1,'Golden wire bent into a cup','One creature or object rises vertically up to 20 feet and remains suspended there.',NULL),
(219,'Locate Animals or Plants','Divination',2,'1 action','Self','Instantaneous', 0,1,1,1,1,'Bit of fur from a bloodhound','You learn the direction and distance to the closest creature or plant of a kind you describe within 5 miles.',NULL),
(220,'Locate Object',     'Divination',   2,'1 action','Self',  'Concentration, up to 10 minutes',1,0,1,1,1,'A forked twig','You sense the direction to an object familiar to you if within 1,000 feet.',NULL),
(221,'Magic Weapon',      'Transmutation',2,'1 bonus action','Touch','Concentration, up to 1 hour',1,0,1,1,0,NULL,'A nonmagical weapon becomes a magic weapon with +1 to attack and damage rolls.','Bonus increases to +2 at 4th level, +3 at 6th level.'),
(222,'Mirror Image',      'Illusion',     2,'1 action','Self',  '1 minute',       0,0,1,1,0,NULL,'Three illusory duplicates of yourself appear in your space for the duration.',NULL),
(223,'Misty Step',        'Conjuration',  2,'1 bonus action','Self','Instantaneous',0,0,1,0,0,NULL,'Surrounded by silvery mist, you teleport up to 30 feet to an unoccupied space you can see.',NULL),
(224,'Moonbeam',          'Evocation',    2,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,1,'Moonseed seeds and opalescent feldspar','A 5-foot-radius cylinder of pale light shines down. Creatures entering it take 2d10 radiant damage.','Damage increases by 1d10 per slot level above 2nd.'),
(225,'Pass Without Trace','Abjuration',   2,'1 action','Self',  'Concentration, up to 1 hour',1,0,1,1,1,'Ashes from burned mistletoe and a sprig of spruce','Creatures you choose within 30 feet gain +10 to Stealth checks and can''t be tracked.',NULL),
(226,'Prayer of Healing', 'Evocation',    2,'10 minutes','30 feet','Instantaneous',0,0,1,0,0,NULL,'Up to six creatures regain 2d8 + spellcasting modifier hit points.','Healing increases by 1d8 per slot level above 2nd.'),
(227,'Scorching Ray',     'Evocation',    2,'1 action','120 feet','Instantaneous',0,0,1,1,0,NULL,'You create three rays of fire. Make a ranged spell attack for each. On a hit, target takes 2d6 fire damage.','One additional ray per slot level above 2nd.'),
(228,'See Invisibility',  'Divination',   2,'1 action','Self',  '1 hour',         0,0,1,1,1,'Talc and powdered silver','For the duration, you see invisible creatures and objects and into the Ethereal Plane within 60 feet.',NULL),
(229,'Shatter',           'Evocation',    2,'1 action','60 feet','Instantaneous', 0,0,1,1,1,'A chip of mica','A sudden ringing erupts. Creatures in a 10-foot sphere make a Con save or take 3d8 thunder damage.','Damage increases by 1d8 per slot level above 2nd.'),
(230,'Silence',           'Illusion',     2,'1 action','120 feet','Concentration, up to 10 minutes',1,1,1,1,0,NULL,'No sound can be created in or pass through a 20-foot sphere. Creatures inside are deafened and immune to thunder damage.',NULL),
(231,'Spider Climb',      'Transmutation',2,'1 action','Touch', 'Concentration, up to 1 hour',1,0,1,1,1,'Bitumen and a spider','One willing creature can move on vertical surfaces and ceilings, leaving hands free.',NULL),
(232,'Spiritual Weapon',  'Evocation',    2,'1 bonus action','60 feet','1 minute',0,0,1,1,0,NULL,'You create a floating spectral weapon. You can make a melee spell attack with it as a bonus action each turn.','Damage increases by 1d8 per two slot levels above 2nd.'),
(233,'Suggestion',        'Enchantment',  2,'1 action','30 feet','Concentration, up to 8 hours',1,0,1,0,1,'A snake''s tongue and honeycomb','You suggest a course of activity to a creature that can hear and understand you.',NULL),
(234,'Web',               'Conjuration',  2,'1 action','60 feet','Concentration, up to 1 hour',1,0,1,1,1,'A bit of spiderweb','You conjure sticky webs filling a 20-foot cube. The area is difficult terrain and creatures can be restrained.',NULL),
-- 3rd Level
(300,'Animate Dead',      'Necromancy',   3,'1 minute','10 feet','Instantaneous', 0,0,1,1,1,'Blood, flesh, and bone dust','This spell creates an undead servant from a pile of bones or a corpse.','Two additional undead per slot level above 3rd.'),
(301,'Bestow Curse',      'Necromancy',   3,'1 action','Touch', 'Concentration, up to 1 minute',1,0,1,1,0,NULL,'You touch a creature that must succeed on a Wisdom saving throw or become cursed.','Duration extends with higher slots.'),
(302,'Clairvoyance',      'Divination',   3,'10 minutes','1 mile','Concentration, up to 10 minutes',1,0,1,1,1,'A jeweled horn or glass eye worth 100 gp','You create an invisible sensor at a location you choose within range.',NULL),
(303,'Conjure Animals',   'Conjuration',  3,'1 action','60 feet','Concentration, up to 1 hour',1,0,1,1,0,NULL,'You summon fey spirits that take the form of beasts in unoccupied spaces.',NULL),
(304,'Counterspell',      'Abjuration',   3,'1 reaction','60 feet','Instantaneous',0,0,0,1,0,NULL,'You interrupt a creature casting a spell. Spells of 3rd level or lower automatically fail.','Automatically counters higher-level spells when using a slot of equal or higher level.'),
(305,'Daylight',          'Evocation',    3,'1 action','60 feet','1 hour',         0,0,1,0,0,NULL,'A 60-foot-radius sphere of light spreads from a point you choose within range.',NULL),
(306,'Dispel Magic',      'Abjuration',   3,'1 action','120 feet','Instantaneous',0,0,1,1,0,NULL,'Any spell of 3rd level or lower on the target ends. Higher-level spells require an ability check.','Automatically dispels spells of equal or lower level to slot used.'),
(307,'Fear',              'Illusion',     3,'1 action','Self (30-ft cone)','Concentration, up to 1 minute',1,0,1,1,1,'A white feather or hen''s heart','Creatures in a 30-foot cone must succeed on a Wisdom save or become frightened.',NULL),
(308,'Fireball',          'Evocation',    3,'1 action','150 feet','Instantaneous', 0,0,1,1,1,'Bat guano and sulfur','A bright streak explodes in a 20-foot-radius sphere. Creatures take 8d6 fire damage (Dex save for half).','Damage increases by 1d6 per slot level above 3rd.'),
(309,'Fly',               'Transmutation',3,'1 action','Touch', 'Concentration, up to 10 minutes',1,0,1,1,1,'A wing feather from any bird','The target gains a flying speed of 60 feet.','One additional target per slot level above 3rd.'),
(310,'Gaseous Form',      'Transmutation',3,'1 action','Touch', 'Concentration, up to 1 hour',1,0,1,1,1,'Gauze and wisp of smoke','A willing creature transforms into a misty cloud.',NULL),
(311,'Haste',             'Transmutation',3,'1 action','30 feet','Concentration, up to 1 minute',1,0,1,1,1,'Licorice root shaving','A willing creature''s speed is doubled, gains +2 AC, advantage on Dex saves, and an additional action.',NULL),
(312,'Hypnotic Pattern',  'Illusion',     3,'1 action','120 feet','Concentration, up to 1 minute',1,0,0,1,1,'Glowing incense or phosphorescent material','A twisting pattern of colors weaves through the air. Creatures that see it must succeed on a Wisdom save or become incapacitated.',NULL),
(313,'Lightning Bolt',    'Evocation',    3,'1 action','Self (100-ft line)','Instantaneous',0,0,1,1,1,'Fur and amber, crystal, or glass rod','A stroke of lightning blasts in a 100-foot line. Creatures take 8d6 lightning damage (Dex save for half).','Damage increases by 1d6 per slot level above 3rd.'),
(314,'Major Image',       'Illusion',     3,'1 action','120 feet','Concentration, up to 10 minutes',1,0,1,1,1,'A bit of fleece','You create an image no larger than a 20-foot cube with sound, smell, and temperature.','Permanent when cast with a 6th-level slot or higher.'),
(315,'Mass Healing Word', 'Evocation',    3,'1 bonus action','60 feet','Instantaneous',0,0,1,0,0,NULL,'Up to six creatures regain 1d4 + spellcasting modifier hit points.','Healing increases by 1d4 per slot level above 3rd.'),
(316,'Slow',              'Transmutation',3,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,1,'A drop of molasses','Up to six creatures must succeed on Wisdom saves or have their speed halved, -2 AC, and other penalties.',NULL),
(317,'Speak with Dead',   'Necromancy',   3,'1 action','10 feet','10 minutes',     0,0,1,1,1,'Burning incense','You grant a corpse the semblance of life to answer up to five questions.',NULL),
(318,'Spirit Guardians',  'Conjuration',  3,'1 action','Self (15-ft radius)','Concentration, up to 10 minutes',1,0,1,1,1,'A holy symbol','Spirits flit around you. Creatures of your choice in a 15-foot radius take 3d8 damage when they enter or start their turn there.','Damage increases by 1d8 per slot level above 3rd.'),
(319,'Stinking Cloud',    'Conjuration',  3,'1 action','90 feet','Concentration, up to 1 minute',1,0,1,1,1,'Rotten egg or skunk cabbage','A 20-foot-radius sphere of nauseating gas. Creatures starting their turn inside must succeed on Con saves or waste their action.',NULL),
(320,'Tongues',           'Divination',   3,'1 action','Touch', '1 hour',          0,0,1,1,1,'A tiny clay ziggurat model','The creature you touch can understand any spoken language and be understood by any creature that knows a language.',NULL),
(321,'Water Breathing',   'Transmutation',3,'1 action','30 feet','24 hours',       0,1,1,1,1,'A short reed or straw','Up to ten willing creatures can breathe underwater until the spell ends.',NULL),
(322,'Wind Wall',         'Evocation',    3,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,1,'A tiny fan and exotic feather','A wall of strong wind rises from the ground. Small creatures and objects in it are hurled away.',NULL),
-- 4th Level
(400,'Arcane Eye',        'Divination',   4,'1 action','30 feet','Concentration, up to 1 hour',1,0,1,1,1,'Bat fur','You create an invisible magical eye that hovers and relays visual information to you.',NULL),
(401,'Banishment',        'Abjuration',   4,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,1,'An item distasteful to the target','You attempt to send one creature to another plane of existence on a failed Charisma save.','One additional target per slot level above 4th.'),
(402,'Blight',            'Necromancy',   4,'1 action','30 feet','Instantaneous',  0,0,1,1,0,NULL,'Necrotic energy washes over a creature. On a failed Con save, it takes 8d8 necrotic damage.','Damage increases by 1d8 per slot level above 4th.'),
(403,'Confusion',         'Enchantment',  4,'1 action','90 feet','Concentration, up to 1 minute',1,0,1,1,1,'Three nut shells','Creatures in a 10-foot sphere must succeed on Wisdom saves or act erratically.','Radius increases by 5 feet per slot level above 4th.'),
(404,'Death Ward',        'Abjuration',   4,'1 action','Touch', '8 hours',         0,0,1,1,0,NULL,'The first time the target would drop to 0 hit points, it instead drops to 1 hit point and the spell ends.',NULL),
(405,'Dimension Door',    'Conjuration',  4,'1 action','500 feet','Instantaneous', 0,0,1,0,0,NULL,'You teleport yourself to any spot within range. You can bring one willing creature of your size or smaller.',NULL),
(406,'Divination',        'Divination',   4,'1 action','Self',  'Instantaneous',   0,1,1,1,1,'Incense and a 25 gp offering','You ask a god''s servants a question about an event within 7 days and get a truthful reply.',NULL),
(407,'Dominate Beast',    'Enchantment',  4,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,0,NULL,'You attempt to beguile a beast on a failed Wisdom save.',NULL),
(408,'Fire Shield',       'Evocation',    4,'1 action','Self',  '10 minutes',      0,0,1,1,1,'Phosphorus or a firefly','Wispy flames wreathe your body. You gain resistance to cold or fire damage and attackers take 2d8 damage.',NULL),
(409,'Greater Invisibility','Illusion',   4,'1 action','Touch', 'Concentration, up to 1 minute',1,0,1,1,0,NULL,'You or a creature you touch becomes invisible for the duration. This persists even when attacking.',NULL),
(410,'Guardian of Faith', 'Conjuration',  4,'1 action','30 feet','8 hours',        0,0,1,0,0,NULL,'A Large spectral guardian appears. Hostile creatures within 10 feet must make a Dex save or take 20 radiant damage.',NULL),
(411,'Ice Storm',         'Evocation',    4,'1 action','300 feet','Instantaneous', 0,0,1,1,1,'Powdered dust and water drops','Hail pounds a 20-foot-radius cylinder. Creatures take 2d8 bludgeoning + 4d6 cold damage.','Bludgeoning damage increases by 1d8 per slot level above 4th.'),
(412,'Phantasmal Killer', 'Illusion',     4,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,0,NULL,'You manifest a creature''s deepest fears. On a failed Wisdom save, the target becomes frightened and takes 4d10 psychic damage per turn.','Damage increases by 1d10 per slot level above 4th.'),
(413,'Polymorph',         'Transmutation',4,'1 action','60 feet','Concentration, up to 1 hour',1,0,1,1,1,'A caterpillar cocoon','This spell transforms a creature into a new form. An unwilling creature makes a Wisdom save.',NULL),
(414,'Stone Shape',       'Transmutation',4,'1 action','Touch', 'Instantaneous',   0,0,1,1,1,'Soft clay shaped roughly like the desired form','You touch a stone object of Medium size or smaller and form it into any shape.',NULL),
(415,'Wall of Fire',      'Evocation',    4,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,1,'Phosphorus','A wall of fire up to 60 feet long and 20 feet high. Creatures that enter or start their turn in the wall take 5d8 fire damage.','Damage increases by 1d8 per slot level above 4th.'),
-- 5th Level
(500,'Animate Objects',   'Transmutation',5,'1 action','120 feet','Concentration, up to 1 minute',1,0,1,1,0,NULL,'Up to ten nonmagical objects come to life at your command.','Two additional objects per slot level above 5th.'),
(501,'Cloudkill',         'Conjuration',  5,'1 action','120 feet','Concentration, up to 10 minutes',1,0,1,1,0,NULL,'A 20-foot sphere of poisonous gas. Creatures starting their turn inside take 5d8 poison damage (Con save for half).','Damage increases by 1d8 per slot level above 5th.'),
(502,'Cone of Cold',      'Evocation',    5,'1 action','Self (60-ft cone)','Instantaneous',0,0,1,1,1,'A small crystal or glass cone','A blast of cold air erupts from your hands in a 60-foot cone. Creatures take 8d8 cold damage (Con save for half).','Damage increases by 1d8 per slot level above 5th.'),
(503,'Dominate Person',   'Enchantment',  5,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,0,NULL,'You attempt to beguile a humanoid on a failed Wisdom save.',NULL),
(504,'Greater Restoration','Abjuration',  5,'1 action','Touch', 'Instantaneous',   0,0,1,1,1,'Diamond dust worth 100 gp','You imbue a creature with positive energy to end exhaustion, charm, petrification, curse, ability reduction, or HP maximum reduction.',NULL),
(505,'Hold Monster',      'Enchantment',  5,'1 action','90 feet','Concentration, up to 1 minute',1,0,1,1,1,'A straight piece of iron','Any creature (not just humanoids) must succeed on a Wisdom save or be paralyzed.','One additional target per slot level above 5th.'),
(506,'Legend Lore',       'Divination',   5,'10 minutes','Self','Instantaneous',   0,0,1,1,1,'Incense worth 250 gp and ivory strips worth 50 gp each','Name or describe a person, place, or object and receive a brief summary of significant lore.',NULL),
(507,'Mass Cure Wounds',  'Evocation',    5,'1 action','60 feet','Instantaneous',  0,0,1,1,0,NULL,'Up to six creatures in a 30-foot sphere each regain 3d8 + spellcasting modifier hit points.','Healing increases by 1d8 per slot level above 5th.'),
(508,'Mislead',           'Illusion',     5,'1 action','Self',  'Concentration, up to 1 hour',1,0,0,1,0,NULL,'You become invisible and an illusory double appears where you were standing.',NULL),
(509,'Passwall',          'Transmutation',5,'1 action','30 feet','1 hour',         0,0,1,1,1,'Sesame seeds','A passage up to 5 feet wide, 8 feet tall, and 20 feet deep appears through a wooden, plaster, or stone surface.',NULL),
(510,'Telekinesis',       'Transmutation',5,'1 action','60 feet','Concentration, up to 10 minutes',1,0,1,1,0,NULL,'You can move or manipulate creatures or objects by thought.',NULL),
(511,'Teleportation Circle','Conjuration',5,'1 minute','10 feet','1 round',        0,0,1,1,1,'Rare chalks and inks worth 50 gp','You draw a circle that links to a permanent teleportation circle whose sigil you know.',NULL),
(512,'Wall of Stone',     'Evocation',    5,'1 action','120 feet','Concentration, up to 10 minutes',1,0,1,1,1,'A small block of granite','A nonmagical wall of stone springs into existence.',NULL),
-- 6th Level
(600,'Chain Lightning',   'Evocation',    6,'1 action','150 feet','Instantaneous', 0,0,1,1,1,'Fur, amber, and three silver pins','A bolt of lightning arcs to a target then leaps to three others within 30 feet. Each takes 10d8 lightning damage.','One additional arc per slot level above 6th.'),
(601,'Disintegrate',      'Transmutation',6,'1 action','60 feet','Instantaneous',  0,0,1,1,1,'Lodestone and pinch of dust','A thin green ray hits a target. On a failed Dex save, a creature takes 10d6 + 40 force damage. If reduced to 0, it is disintegrated.','Damage increases by 3d6 per slot level above 6th.'),
(602,'Globe of Invulnerability','Abjuration',6,'1 action','Self (10-ft radius)','Concentration, up to 1 minute',1,0,1,1,1,'A glass bead','An immobile shimmering barrier blocks spells of 5th level and lower from affecting creatures inside.','Blocks one additional level per slot level above 6th.'),
(603,'Heal',              'Evocation',    6,'1 action','60 feet','Instantaneous',  0,0,1,1,0,NULL,'A creature regains 70 hit points and is cured of blindness, deafness, and diseases.','Healing increases by 10 per slot level above 6th.'),
-- 7th Level
(700,'Etherealness',      'Transmutation',7,'1 action','Self',  'Up to 8 hours',   0,0,1,1,0,NULL,'You step into the Border Ethereal for the duration. You can see into the plane you left but can''t interact with it.','Up to three targets per slot level above 7th.'),
(701,'Forcecage',         'Evocation',    7,'1 action','100 feet','1 hour',        0,0,1,1,1,'Ruby dust worth 1,500 gp','An immobile invisible cube-shaped prison of magical force springs into existence.',NULL),
(702,'Plane Shift',       'Conjuration',  7,'1 action','Touch', 'Instantaneous',   0,0,1,1,1,'A forked metal rod worth 250 gp attuned to a plane','You and up to eight willing creatures are transported to a different plane of existence.',NULL),
(703,'Regenerate',        'Transmutation',7,'1 minute','Touch', '1 hour',          0,0,1,1,1,'Prayer wheel and holy water','The target regains 4d8 + 15 hit points and then 1 hit point at the start of each turn.',NULL),
-- 8th Level
(800,'Antimagic Field',   'Abjuration',   8,'1 action','Self (10-ft sphere)','Concentration, up to 1 hour',1,0,1,1,1,'Powdered iron or iron filings','A 10-foot-radius sphere of antimagic surrounds you, suppressing all magic within it.',NULL),
(801,'Clone',             'Necromancy',   8,'1 hour',  'Touch', 'Instantaneous',   0,0,1,1,1,'Flesh of the creature and a 2,000 gp vessel','This spell grows an inert duplicate as a safeguard against death.',NULL),
(802,'Earthquake',        'Evocation',    8,'1 action','500 feet','Concentration, up to 1 minute',1,0,1,1,1,'Dirt, rock, and clay','An intense tremor rips through the ground in a 100-foot radius causing difficult terrain, concentration checks, and structural damage.',NULL),
(803,'Mind Blank',        'Abjuration',   8,'1 action','Touch', '24 hours',        0,0,1,1,0,NULL,'One willing creature is immune to psychic damage, divination, charm, and telepathy.',NULL),
-- 9th Level
(900,'Foresight',         'Divination',   9,'1 minute','Touch', '8 hours',         0,0,1,1,1,'A hummingbird feather','The target can''t be surprised and has advantage on attack rolls, ability checks, and saving throws.',NULL),
(901,'Gate',              'Conjuration',  9,'1 action','60 feet','Concentration, up to 1 minute',1,0,1,1,1,'A diamond worth 5,000 gp','You conjure a portal to a precise location on a different plane of existence.',NULL),
(902,'Mass Heal',         'Evocation',    9,'1 action','60 feet','Instantaneous',  0,0,1,1,0,NULL,'You restore up to 700 hit points divided among creatures you can see within range.',NULL),
(903,'Power Word Kill',   'Enchantment',  9,'1 action','60 feet','Instantaneous',  0,0,1,0,0,NULL,'You utter a word of power. A creature with 100 hit points or fewer dies instantly.',NULL),
(904,'Time Stop',         'Transmutation',9,'1 action','Self',  'Instantaneous',   0,0,1,0,0,NULL,'You briefly stop the flow of time for everyone but yourself, taking 1d4 + 1 turns in a row.',NULL),
(905,'True Resurrection', 'Necromancy',   9,'1 hour',  'Touch', 'Instantaneous',   0,0,1,1,1,'Holy water and diamonds worth 25,000 gp','You touch a creature dead no longer than 200 years and restore it to life with all hit points.',NULL),
(906,'Wish',              'Conjuration',  9,'1 action','Self',  'Instantaneous',   0,0,1,0,0,NULL,'The mightiest spell a mortal can cast. You can duplicate any spell of 8th level or lower, or reshape reality in accord with your desires.',NULL);

-- =============================================================================
-- 17. CLASS SPELLS, SPELL SLOTS, SUBCLASS SPELLS, RACIAL INNATE SPELLS
-- =============================================================================

CREATE TABLE IF NOT EXISTS class_spell (
    class_name  VARCHAR(63) NOT NULL,
    spell_id    INTEGER     NOT NULL,
    PRIMARY KEY (class_name, spell_id),
    FOREIGN KEY (spell_id) REFERENCES spell(id)
);

INSERT INTO class_spell (class_name, spell_id) VALUES
('Bard',4),('Bard',8),('Bard',11),('Bard',14),('Bard',16),('Bard',25),
('Bard',101),('Bard',102),('Bard',104),('Bard',105),('Bard',106),('Bard',109),
('Bard',116),('Bard',117),('Bard',118),('Bard',128),('Bard',129),('Bard',130),
('Bard',204),('Bard',205),('Bard',209),('Bard',215),('Bard',216),('Bard',222),
('Bard',223),('Bard',228),('Bard',229),('Bard',230),('Bard',233),
('Bard',302),('Bard',307),('Bard',312),('Bard',314),('Bard',315),('Bard',316),('Bard',320),
('Bard',403),('Bard',405),('Bard',503),('Bard',506),('Bard',508),
('Cleric',9),('Cleric',18),('Cleric',19),('Cleric',22),('Cleric',23),
('Cleric',100),('Cleric',102),('Cleric',107),('Cleric',108),('Cleric',119),
('Cleric',124),('Cleric',127),
('Cleric',203),('Cleric',205),('Cleric',217),('Cleric',220),('Cleric',226),('Cleric',232),
('Cleric',305),('Cleric',306),('Cleric',315),('Cleric',317),('Cleric',318),
('Cleric',404),('Cleric',406),('Cleric',410),('Cleric',411),
('Cleric',504),('Cleric',507),('Cleric',603),('Cleric',703),('Cleric',803),
('Cleric',902),('Cleric',905),
('Druid',5),('Druid',9),('Druid',10),('Druid',18),
('Druid',101),('Druid',107),('Druid',108),('Druid',113),('Druid',114),
('Druid',116),('Druid',120),('Druid',121),('Druid',130),
('Druid',206),('Druid',207),('Druid',211),('Druid',212),('Druid',213),
('Druid',219),('Druid',220),('Druid',225),
('Druid',303),('Druid',305),('Druid',306),('Druid',313),('Druid',316),('Druid',321),('Druid',322),
('Druid',412),('Druid',413),('Druid',414),('Druid',415),
('Druid',500),('Druid',501),('Druid',512),
('Druid',702),('Druid',802),('Druid',900),
('Paladin',107),('Paladin',108),('Paladin',116),('Paladin',117),
('Paladin',118),('Paladin',124),('Paladin',127),
('Paladin',205),('Paladin',217),('Paladin',232),
('Paladin',305),('Paladin',306),('Paladin',318),
('Paladin',404),('Paladin',406),('Paladin',504),
('Ranger',100),('Ranger',101),('Ranger',108),('Ranger',112),
('Ranger',114),('Ranger',120),('Ranger',121),('Ranger',130),
('Ranger',206),('Ranger',213),('Ranger',219),('Ranger',225),
('Ranger',303),('Ranger',306),('Ranger',322),('Ranger',413),
('Sorcerer',1),('Sorcerer',3),('Sorcerer',4),('Sorcerer',6),('Sorcerer',7),
('Sorcerer',11),('Sorcerer',15),('Sorcerer',17),('Sorcerer',21),('Sorcerer',24),
('Sorcerer',103),('Sorcerer',104),('Sorcerer',105),('Sorcerer',106),('Sorcerer',109),
('Sorcerer',110),('Sorcerer',114),('Sorcerer',115),('Sorcerer',122),('Sorcerer',123),
('Sorcerer',125),('Sorcerer',126),('Sorcerer',128),('Sorcerer',129),('Sorcerer',131),('Sorcerer',133),
('Sorcerer',201),('Sorcerer',204),('Sorcerer',206),('Sorcerer',208),('Sorcerer',213),
('Sorcerer',215),('Sorcerer',216),('Sorcerer',218),('Sorcerer',222),('Sorcerer',223),
('Sorcerer',227),('Sorcerer',228),('Sorcerer',229),('Sorcerer',231),('Sorcerer',234),
('Sorcerer',304),('Sorcerer',305),('Sorcerer',308),('Sorcerer',309),('Sorcerer',310),
('Sorcerer',311),('Sorcerer',312),('Sorcerer',313),('Sorcerer',316),('Sorcerer',319),
('Sorcerer',401),('Sorcerer',408),('Sorcerer',409),('Sorcerer',411),('Sorcerer',413),('Sorcerer',415),
('Sorcerer',501),('Sorcerer',502),('Sorcerer',503),('Sorcerer',509),('Sorcerer',510),
('Sorcerer',600),('Sorcerer',601),('Sorcerer',802),('Sorcerer',904),
('Warlock',3),('Warlock',6),('Warlock',8),('Warlock',14),('Warlock',15),
('Warlock',21),('Warlock',24),('Warlock',25),
('Warlock',104),('Warlock',106),('Warlock',109),('Warlock',133),
('Warlock',203),('Warlock',214),('Warlock',215),('Warlock',216),
('Warlock',222),('Warlock',223),('Warlock',228),('Warlock',234),
('Warlock',304),('Warlock',307),('Warlock',314),('Warlock',316),
('Warlock',401),('Warlock',403),('Warlock',409),
('Warlock',502),('Warlock',503),('Warlock',508),('Warlock',510),
('Warlock',701),('Warlock',702),('Warlock',800),
('Warlock',901),('Warlock',903),('Warlock',904),
('Wizard',1),('Wizard',2),('Wizard',3),('Wizard',4),('Wizard',7),
('Wizard',8),('Wizard',11),('Wizard',12),('Wizard',13),('Wizard',14),
('Wizard',15),('Wizard',16),('Wizard',17),('Wizard',21),('Wizard',24),
('Wizard',100),('Wizard',103),('Wizard',104),('Wizard',105),('Wizard',106),
('Wizard',109),('Wizard',110),('Wizard',111),('Wizard',112),('Wizard',113),
('Wizard',114),('Wizard',115),('Wizard',118),('Wizard',122),('Wizard',123),
('Wizard',124),('Wizard',125),('Wizard',126),('Wizard',128),('Wizard',129),
('Wizard',132),('Wizard',133),
('Wizard',201),('Wizard',202),('Wizard',204),('Wizard',206),('Wizard',207),
('Wizard',208),('Wizard',210),('Wizard',214),('Wizard',215),('Wizard',216),
('Wizard',218),('Wizard',220),('Wizard',221),('Wizard',222),('Wizard',223),
('Wizard',228),('Wizard',229),('Wizard',231),('Wizard',233),('Wizard',234),
('Wizard',302),('Wizard',304),('Wizard',306),('Wizard',307),('Wizard',308),
('Wizard',309),('Wizard',310),('Wizard',311),('Wizard',312),('Wizard',313),
('Wizard',314),('Wizard',316),('Wizard',319),('Wizard',320),
('Wizard',400),('Wizard',401),('Wizard',402),('Wizard',403),('Wizard',405),
('Wizard',406),('Wizard',408),('Wizard',409),('Wizard',411),('Wizard',412),
('Wizard',413),('Wizard',414),('Wizard',415),
('Wizard',500),('Wizard',501),('Wizard',502),('Wizard',505),('Wizard',506),
('Wizard',508),('Wizard',509),('Wizard',510),('Wizard',511),('Wizard',512),
('Wizard',600),('Wizard',601),('Wizard',602),
('Wizard',700),('Wizard',701),('Wizard',702),
('Wizard',800),('Wizard',801),('Wizard',802),('Wizard',803),
('Wizard',900),('Wizard',901),('Wizard',903),('Wizard',904),('Wizard',906),
('Artificer',12),('Artificer',100),('Artificer',108),('Artificer',118),
('Artificer',122),('Artificer',123),('Artificer',202),('Artificer',207),
('Artificer',215),('Artificer',217),('Artificer',221),
('Artificer',306),('Artificer',311),('Artificer',400),('Artificer',414),('Artificer',504);

CREATE TABLE IF NOT EXISTS class_spell_slots (
    class_name      VARCHAR(63) NOT NULL,
    character_level INTEGER     NOT NULL,
    slot_level      INTEGER     NOT NULL,
    slots           INTEGER     NOT NULL,
    PRIMARY KEY (class_name, character_level, slot_level)
);

INSERT INTO class_spell_slots (class_name, character_level, slot_level, slots) VALUES
('Wizard',1,1,2),('Wizard',2,1,3),('Wizard',3,1,4),('Wizard',3,2,2),
('Wizard',4,1,4),('Wizard',4,2,3),('Wizard',5,1,4),('Wizard',5,2,3),('Wizard',5,3,2),
('Wizard',6,1,4),('Wizard',6,2,3),('Wizard',6,3,3),
('Wizard',7,1,4),('Wizard',7,2,3),('Wizard',7,3,3),('Wizard',7,4,1),
('Wizard',8,1,4),('Wizard',8,2,3),('Wizard',8,3,3),('Wizard',8,4,2),
('Wizard',9,1,4),('Wizard',9,2,3),('Wizard',9,3,3),('Wizard',9,4,3),('Wizard',9,5,1),
('Wizard',10,1,4),('Wizard',10,2,3),('Wizard',10,3,3),('Wizard',10,4,3),('Wizard',10,5,2),
('Wizard',11,1,4),('Wizard',11,2,3),('Wizard',11,3,3),('Wizard',11,4,3),('Wizard',11,5,2),('Wizard',11,6,1),
('Wizard',12,1,4),('Wizard',12,2,3),('Wizard',12,3,3),('Wizard',12,4,3),('Wizard',12,5,2),('Wizard',12,6,1),
('Wizard',13,1,4),('Wizard',13,2,3),('Wizard',13,3,3),('Wizard',13,4,3),('Wizard',13,5,2),('Wizard',13,6,1),('Wizard',13,7,1),
('Wizard',14,1,4),('Wizard',14,2,3),('Wizard',14,3,3),('Wizard',14,4,3),('Wizard',14,5,2),('Wizard',14,6,1),('Wizard',14,7,1),
('Wizard',15,1,4),('Wizard',15,2,3),('Wizard',15,3,3),('Wizard',15,4,3),('Wizard',15,5,2),('Wizard',15,6,1),('Wizard',15,7,1),('Wizard',15,8,1),
('Wizard',16,1,4),('Wizard',16,2,3),('Wizard',16,3,3),('Wizard',16,4,3),('Wizard',16,5,2),('Wizard',16,6,1),('Wizard',16,7,1),('Wizard',16,8,1),
('Wizard',17,1,4),('Wizard',17,2,3),('Wizard',17,3,3),('Wizard',17,4,3),('Wizard',17,5,2),('Wizard',17,6,1),('Wizard',17,7,1),('Wizard',17,8,1),('Wizard',17,9,1),
('Wizard',18,1,4),('Wizard',18,2,3),('Wizard',18,3,3),('Wizard',18,4,3),('Wizard',18,5,3),('Wizard',18,6,1),('Wizard',18,7,1),('Wizard',18,8,1),('Wizard',18,9,1),
('Wizard',19,1,4),('Wizard',19,2,3),('Wizard',19,3,3),('Wizard',19,4,3),('Wizard',19,5,3),('Wizard',19,6,2),('Wizard',19,7,1),('Wizard',19,8,1),('Wizard',19,9,1),
('Wizard',20,1,4),('Wizard',20,2,3),('Wizard',20,3,3),('Wizard',20,4,3),('Wizard',20,5,3),('Wizard',20,6,2),('Wizard',20,7,2),('Wizard',20,8,1),('Wizard',20,9,1);

INSERT INTO class_spell_slots SELECT 'Bard',    character_level, slot_level, slots FROM class_spell_slots WHERE class_name = 'Wizard';
INSERT INTO class_spell_slots SELECT 'Cleric',  character_level, slot_level, slots FROM class_spell_slots WHERE class_name = 'Wizard';
INSERT INTO class_spell_slots SELECT 'Druid',   character_level, slot_level, slots FROM class_spell_slots WHERE class_name = 'Wizard';
INSERT INTO class_spell_slots SELECT 'Sorcerer',character_level, slot_level, slots FROM class_spell_slots WHERE class_name = 'Wizard';

INSERT INTO class_spell_slots (class_name, character_level, slot_level, slots) VALUES
('Paladin',2,1,2),('Ranger',2,1,2),('Paladin',3,1,3),('Ranger',3,1,3),
('Paladin',4,1,3),('Ranger',4,1,3),
('Paladin',5,1,4),('Paladin',5,2,2),('Ranger',5,1,4),('Ranger',5,2,2),
('Paladin',6,1,4),('Paladin',6,2,2),('Ranger',6,1,4),('Ranger',6,2,2),
('Paladin',7,1,4),('Paladin',7,2,3),('Ranger',7,1,4),('Ranger',7,2,3),
('Paladin',8,1,4),('Paladin',8,2,3),('Ranger',8,1,4),('Ranger',8,2,3),
('Paladin',9,1,4),('Paladin',9,2,3),('Paladin',9,3,2),('Ranger',9,1,4),('Ranger',9,2,3),('Ranger',9,3,2),
('Paladin',10,1,4),('Paladin',10,2,3),('Paladin',10,3,2),('Ranger',10,1,4),('Ranger',10,2,3),('Ranger',10,3,2),
('Paladin',11,1,4),('Paladin',11,2,3),('Paladin',11,3,3),('Ranger',11,1,4),('Ranger',11,2,3),('Ranger',11,3,3),
('Paladin',12,1,4),('Paladin',12,2,3),('Paladin',12,3,3),('Ranger',12,1,4),('Ranger',12,2,3),('Ranger',12,3,3),
('Paladin',13,1,4),('Paladin',13,2,3),('Paladin',13,3,3),('Paladin',13,4,1),('Ranger',13,1,4),('Ranger',13,2,3),('Ranger',13,3,3),('Ranger',13,4,1),
('Paladin',14,1,4),('Paladin',14,2,3),('Paladin',14,3,3),('Paladin',14,4,1),('Ranger',14,1,4),('Ranger',14,2,3),('Ranger',14,3,3),('Ranger',14,4,1),
('Paladin',15,1,4),('Paladin',15,2,3),('Paladin',15,3,3),('Paladin',15,4,2),('Ranger',15,1,4),('Ranger',15,2,3),('Ranger',15,3,3),('Ranger',15,4,2),
('Paladin',16,1,4),('Paladin',16,2,3),('Paladin',16,3,3),('Paladin',16,4,2),('Ranger',16,1,4),('Ranger',16,2,3),('Ranger',16,3,3),('Ranger',16,4,2),
('Paladin',17,1,4),('Paladin',17,2,3),('Paladin',17,3,3),('Paladin',17,4,3),('Paladin',17,5,1),
('Ranger',17,1,4),('Ranger',17,2,3),('Ranger',17,3,3),('Ranger',17,4,3),('Ranger',17,5,1),
('Paladin',18,1,4),('Paladin',18,2,3),('Paladin',18,3,3),('Paladin',18,4,3),('Paladin',18,5,1),
('Ranger',18,1,4),('Ranger',18,2,3),('Ranger',18,3,3),('Ranger',18,4,3),('Ranger',18,5,1),
('Paladin',19,1,4),('Paladin',19,2,3),('Paladin',19,3,3),('Paladin',19,4,3),('Paladin',19,5,2),
('Ranger',19,1,4),('Ranger',19,2,3),('Ranger',19,3,3),('Ranger',19,4,3),('Ranger',19,5,2),
('Paladin',20,1,4),('Paladin',20,2,3),('Paladin',20,3,3),('Paladin',20,4,3),('Paladin',20,5,2),
('Ranger',20,1,4),('Ranger',20,2,3),('Ranger',20,3,3),('Ranger',20,4,3),('Ranger',20,5,2),
('Warlock',1,1,1),('Warlock',2,1,2),('Warlock',3,2,2),('Warlock',4,2,2),
('Warlock',5,3,2),('Warlock',6,3,2),('Warlock',7,4,2),('Warlock',8,4,2),
('Warlock',9,5,2),('Warlock',10,5,2),('Warlock',11,5,3),('Warlock',12,5,3),
('Warlock',13,5,3),('Warlock',14,5,3),('Warlock',15,5,3),('Warlock',16,5,3),
('Warlock',17,5,4),('Warlock',18,5,4),('Warlock',19,5,4),('Warlock',20,5,4),
('Artificer',1,1,2),('Artificer',2,1,2),('Artificer',3,1,3),('Artificer',4,1,3),
('Artificer',5,1,4),('Artificer',5,2,2),('Artificer',6,1,4),('Artificer',6,2,2),
('Artificer',7,1,4),('Artificer',7,2,3),('Artificer',8,1,4),('Artificer',8,2,3),
('Artificer',9,1,4),('Artificer',9,2,3),('Artificer',9,3,2),('Artificer',10,1,4),('Artificer',10,2,3),('Artificer',10,3,2),
('Artificer',11,1,4),('Artificer',11,2,3),('Artificer',11,3,3),('Artificer',12,1,4),('Artificer',12,2,3),('Artificer',12,3,3),
('Artificer',13,1,4),('Artificer',13,2,3),('Artificer',13,3,3),('Artificer',13,4,1),
('Artificer',14,1,4),('Artificer',14,2,3),('Artificer',14,3,3),('Artificer',14,4,1),
('Artificer',15,1,4),('Artificer',15,2,3),('Artificer',15,3,3),('Artificer',15,4,2),
('Artificer',16,1,4),('Artificer',16,2,3),('Artificer',16,3,3),('Artificer',16,4,2),
('Artificer',17,1,4),('Artificer',17,2,3),('Artificer',17,3,3),('Artificer',17,4,3),('Artificer',17,5,1),
('Artificer',18,1,4),('Artificer',18,2,3),('Artificer',18,3,3),('Artificer',18,4,3),('Artificer',18,5,1),
('Artificer',19,1,4),('Artificer',19,2,3),('Artificer',19,3,3),('Artificer',19,4,3),('Artificer',19,5,2),
('Artificer',20,1,4),('Artificer',20,2,3),('Artificer',20,3,3),('Artificer',20,4,3),('Artificer',20,5,2);

CREATE TABLE IF NOT EXISTS subclass_spell (
    subclass_name   VARCHAR(127) NOT NULL,
    spell_id        INTEGER      NOT NULL,
    min_class_level INTEGER      NOT NULL DEFAULT 1,
    PRIMARY KEY (subclass_name, spell_id),
    FOREIGN KEY (spell_id) REFERENCES spell(id)
);

INSERT INTO subclass_spell VALUES
('Life Domain',107,1),('Life Domain',129,1),('Life Domain',226,3),('Life Domain',232,3),
('Life Domain',505,5),('Life Domain',507,5),('Life Domain',410,7),('Life Domain',404,7),
('Life Domain',504,9),('Life Domain',603,9),
('Light Domain',10,1),('Light Domain',19,1),('Light Domain',227,3),('Light Domain',305,3),
('Light Domain',308,5),('Light Domain',409,7),('Light Domain',415,7),('Light Domain',600,9),('Light Domain',603,9),
('Trickery Domain',104,1),('Trickery Domain',109,1),('Trickery Domain',215,3),('Trickery Domain',222,3),
('Trickery Domain',304,5),('Trickery Domain',406,7),('Trickery Domain',508,9),
('The Fiend',103,1),('The Fiend',129,1),('The Fiend',308,3),('The Fiend',208,3),
('The Fiend',227,5),('The Fiend',307,5),('The Fiend',401,7),('The Fiend',415,7),
('The Fiend',502,9),('The Fiend',602,9),
('The Archfey',104,1),('The Archfey',129,1),('The Archfey',215,3),('The Archfey',316,3),
('The Archfey',307,5),('The Archfey',312,5),('The Archfey',209,7),('The Archfey',409,7),
('The Archfey',508,9),('The Archfey',505,9),
('Oath of Devotion',124,3),('Oath of Devotion',127,3),('Oath of Devotion',207,5),
('Oath of Devotion',219,5),('Oath of Devotion',305,9),('Oath of Devotion',410,9),
('Oath of the Ancients',101,3),('Oath of the Ancients',130,3),('Oath of the Ancients',212,5),
('Oath of the Ancients',213,5),('Oath of the Ancients',303,9),('Oath of the Ancients',311,9);

CREATE TABLE IF NOT EXISTS race_innate_spell (
    race_name       VARCHAR(63)  NOT NULL,
    spell_id        INTEGER      NOT NULL,
    uses_per_day    INTEGER,
    min_level       INTEGER      NOT NULL DEFAULT 1,
    ability_used    VARCHAR(63),
    notes           TEXT,
    PRIMARY KEY (race_name, spell_id),
    FOREIGN KEY (spell_id) REFERENCES spell(id)
);

INSERT INTO race_innate_spell (race_name, spell_id, uses_per_day, min_level, ability_used, notes) VALUES
('Tiefling',     23, NULL, 1, 'Charisma', 'Thaumaturgy cantrip at will'),
('Tiefling',      4,    1, 5, 'Charisma', 'Dancing Lights 1/day (maps to Darkness)'),
('Drow',          4, NULL, 1, 'Charisma', 'Dancing Lights cantrip at will'),
('Drow',        215,    1, 5, 'Charisma', 'Darkness 1/day (mapped to Invisibility)'),
('Forest Gnome', 14, NULL, 1, NULL,       'Minor Illusion cantrip at will'),
('High Elf',     14, NULL, 1, 'Intelligence','One wizard cantrip (Minor Illusion as example)'),
('Aasimar',       9, NULL, 1, 'Charisma', 'Light cantrip at will'),
('Aasimar',      19, NULL, 1, 'Charisma', 'Sacred Flame cantrip at will'),
('Aasimar',      22,    1, 1, 'Charisma', 'Spare the Dying 1/day');

-- =============================================================================
-- 18. CLASS SKILL CHOICES
-- =============================================================================

CREATE TABLE IF NOT EXISTS class_skill_choice (
    class_name  VARCHAR(63) NOT NULL,
    skill_name  VARCHAR(63) NOT NULL,
    PRIMARY KEY (class_name, skill_name),
    FOREIGN KEY (skill_name) REFERENCES skill(name)
);

INSERT INTO class_skill_choice (class_name, skill_name) VALUES
('Barbarian','Animal Handling'),('Barbarian','Athletics'),('Barbarian','Intimidation'),
('Barbarian','Nature'),('Barbarian','Perception'),('Barbarian','Survival'),
('Bard','Acrobatics'),('Bard','Animal Handling'),('Bard','Arcana'),('Bard','Athletics'),
('Bard','Deception'),('Bard','History'),('Bard','Insight'),('Bard','Intimidation'),
('Bard','Investigation'),('Bard','Medicine'),('Bard','Nature'),('Bard','Perception'),
('Bard','Performance'),('Bard','Persuasion'),('Bard','Religion'),('Bard','Sleight of Hand'),
('Bard','Stealth'),('Bard','Survival'),
('Cleric','History'),('Cleric','Insight'),('Cleric','Medicine'),('Cleric','Persuasion'),('Cleric','Religion'),
('Druid','Arcana'),('Druid','Animal Handling'),('Druid','Insight'),('Druid','Medicine'),
('Druid','Nature'),('Druid','Perception'),('Druid','Religion'),('Druid','Survival'),
('Fighter','Acrobatics'),('Fighter','Animal Handling'),('Fighter','Athletics'),
('Fighter','History'),('Fighter','Insight'),('Fighter','Intimidation'),
('Fighter','Perception'),('Fighter','Survival'),
('Monk','Acrobatics'),('Monk','Athletics'),('Monk','History'),
('Monk','Insight'),('Monk','Religion'),('Monk','Stealth'),
('Paladin','Athletics'),('Paladin','Insight'),('Paladin','Intimidation'),
('Paladin','Medicine'),('Paladin','Persuasion'),('Paladin','Religion'),
('Ranger','Animal Handling'),('Ranger','Athletics'),('Ranger','Insight'),
('Ranger','Investigation'),('Ranger','Nature'),('Ranger','Perception'),
('Ranger','Stealth'),('Ranger','Survival'),
('Rogue','Acrobatics'),('Rogue','Athletics'),('Rogue','Deception'),
('Rogue','Insight'),('Rogue','Intimidation'),('Rogue','Investigation'),
('Rogue','Perception'),('Rogue','Performance'),('Rogue','Persuasion'),
('Rogue','Sleight of Hand'),('Rogue','Stealth'),
('Sorcerer','Arcana'),('Sorcerer','Deception'),('Sorcerer','Insight'),
('Sorcerer','Intimidation'),('Sorcerer','Persuasion'),('Sorcerer','Religion'),
('Warlock','Arcana'),('Warlock','Deception'),('Warlock','History'),
('Warlock','Intimidation'),('Warlock','Investigation'),('Warlock','Nature'),('Warlock','Religion'),
('Wizard','Arcana'),('Wizard','History'),('Wizard','Insight'),
('Wizard','Investigation'),('Wizard','Medicine'),('Wizard','Religion');

CREATE TABLE IF NOT EXISTS class_skill_count (
    class_name  VARCHAR(63) NOT NULL,
    skill_count INTEGER     NOT NULL,
    PRIMARY KEY (class_name)
);

INSERT INTO class_skill_count (class_name, skill_count) VALUES
('Barbarian',2),('Bard',3),('Cleric',2),('Druid',2),('Fighter',2),
('Monk',2),('Paladin',2),('Ranger',3),('Rogue',4),
('Sorcerer',2),('Warlock',2),('Wizard',2);

-- =============================================================================
-- 19. EQUIPMENT PACKS & STARTING EQUIPMENT
-- =============================================================================

CREATE TABLE IF NOT EXISTS equipment_pack (
    name        VARCHAR(63) NOT NULL,
    description TEXT,
    PRIMARY KEY (name)
);

INSERT INTO equipment_pack (name, description) VALUES
('Burglar''s Pack',   'Backpack, 1000 ball bearings, 10 ft string, bell, 5 candles, crowbar, hammer, 10 pitons, hooded lantern, 2 flasks oil, 5 days rations, tinderbox, waterskin, 50 ft hempen rope.'),
('Diplomat''s Pack',  'Chest, 2 map/scroll cases, fine clothes, ink bottle, ink pen, lamp, 2 flasks oil, 5 sheets paper, perfume vial, sealing wax, soap.'),
('Dungeoneer''s Pack','Backpack, crowbar, hammer, 10 pitons, 10 torches, tinderbox, 10 days rations, waterskin, 50 ft hempen rope.'),
('Entertainer''s Pack','Backpack, bedroll, 2 costumes, 5 candles, 5 days rations, waterskin, disguise kit.'),
('Explorer''s Pack',  'Backpack, bedroll, mess kit, tinderbox, 10 torches, 10 days rations, waterskin, 50 ft hempen rope.'),
('Priest''s Pack',    'Backpack, blanket, 10 candles, tinderbox, alms box, 2 blocks incense, censer, vestments, 2 days rations, waterskin.'),
('Scholar''s Pack',   'Backpack, book of lore, ink bottle, ink pen, 10 sheets parchment, bag of sand, small knife.');

CREATE TABLE IF NOT EXISTS class_starting_equipment (
    id              INTEGER      NOT NULL,
    class_name      VARCHAR(63)  NOT NULL,
    choice_order    INTEGER      NOT NULL,
    is_mandatory    BOOLEAN      NOT NULL DEFAULT 0,
    mandatory_item  VARCHAR(127),
    option_a        VARCHAR(255),
    option_b        VARCHAR(255),
    option_c        VARCHAR(255),
    proficiency_req VARCHAR(127),
    PRIMARY KEY (id)
);

INSERT INTO class_starting_equipment (id, class_name, choice_order, is_mandatory, mandatory_item, option_a, option_b, option_c, proficiency_req) VALUES
-- Barbarian
(10,'Barbarian',1,0,NULL,'A greataxe','Any martial melee weapon',NULL,'Martial Weapons'),
(11,'Barbarian',2,0,NULL,'Two handaxes','Any simple weapon',NULL,NULL),
(12,'Barbarian',3,1,'Explorer''s Pack',NULL,NULL,NULL,NULL),
(13,'Barbarian',4,1,'Four javelins',NULL,NULL,NULL,NULL),
-- Bard
(20,'Bard',1,0,NULL,'A rapier','A longsword','Any simple weapon',NULL),
(21,'Bard',2,0,NULL,'A diplomat''s pack','An entertainer''s pack',NULL,NULL),
(22,'Bard',3,0,NULL,'A lute','Any other musical instrument',NULL,NULL),
(23,'Bard',4,1,'Leather Armor',NULL,NULL,NULL,NULL),
(24,'Bard',5,1,'Dagger',NULL,NULL,NULL,NULL),
-- Cleric (matches your screenshot)
(30,'Cleric',1,1,'Shield',NULL,NULL,NULL,NULL),
(31,'Cleric',2,0,NULL,'A mace','A warhammer',NULL,'Martial Weapons'),
(32,'Cleric',3,0,NULL,'Scale mail, leather armor','Chain mail',NULL,'Heavy Armor'),
(33,'Cleric',4,0,NULL,'A light crossbow and 20 bolts','Any simple weapon',NULL,NULL),
(34,'Cleric',5,0,NULL,'A priest''s pack','An explorer''s pack',NULL,NULL),
-- Druid
(40,'Druid',1,0,NULL,'A wooden shield','Any simple weapon',NULL,NULL),
(41,'Druid',2,0,NULL,'A scimitar','Any simple melee weapon',NULL,NULL),
(42,'Druid',3,1,'Leather Armor',NULL,NULL,NULL,NULL),
(43,'Druid',4,1,'Explorer''s Pack',NULL,NULL,NULL,NULL),
(44,'Druid',5,1,'Druidic Focus',NULL,NULL,NULL,NULL),
-- Fighter
(50,'Fighter',1,0,NULL,'Chain mail','Leather armor, longbow, 20 arrows',NULL,NULL),
(51,'Fighter',2,0,NULL,'A martial weapon and a shield','Two martial weapons',NULL,'Martial Weapons'),
(52,'Fighter',3,0,NULL,'A light crossbow and 20 bolts','Two handaxes',NULL,NULL),
(53,'Fighter',4,0,NULL,'A dungeoneer''s pack','An explorer''s pack',NULL,NULL),
-- Monk
(60,'Monk',1,0,NULL,'A shortsword','Any simple weapon',NULL,NULL),
(61,'Monk',2,0,NULL,'A dungeoneer''s pack','An explorer''s pack',NULL,NULL),
(62,'Monk',3,1,'10 darts',NULL,NULL,NULL,NULL),
-- Paladin
(70,'Paladin',1,0,NULL,'A martial weapon and a shield','Two martial weapons',NULL,'Martial Weapons'),
(71,'Paladin',2,0,NULL,'Five javelins','Any simple melee weapon',NULL,NULL),
(72,'Paladin',3,0,NULL,'A priest''s pack','An explorer''s pack',NULL,NULL),
(73,'Paladin',4,1,'Chain mail',NULL,NULL,NULL,NULL),
(74,'Paladin',5,1,'Holy symbol',NULL,NULL,NULL,NULL),
-- Ranger
(80,'Ranger',1,0,NULL,'Scale mail','Leather armor',NULL,NULL),
(81,'Ranger',2,0,NULL,'Two shortswords','Two simple melee weapons',NULL,NULL),
(82,'Ranger',3,0,NULL,'A dungeoneer''s pack','An explorer''s pack',NULL,NULL),
(83,'Ranger',4,1,'Longbow and quiver of 20 arrows',NULL,NULL,NULL,NULL),
-- Rogue
(90,'Rogue',1,0,NULL,'A rapier','A shortsword',NULL,NULL),
(91,'Rogue',2,0,NULL,'A shortbow and 20 arrows','A shortsword',NULL,NULL),
(92,'Rogue',3,0,NULL,'A burglar''s pack','A dungeoneer''s pack','An explorer''s pack',NULL),
(93,'Rogue',4,1,'Leather Armor',NULL,NULL,NULL,NULL),
(94,'Rogue',5,1,'Two daggers',NULL,NULL,NULL,NULL),
(95,'Rogue',6,1,'Thieves'' tools',NULL,NULL,NULL,NULL),
-- Sorcerer
(100,'Sorcerer',1,0,NULL,'A light crossbow and 20 bolts','Any simple weapon',NULL,NULL),
(101,'Sorcerer',2,0,NULL,'A component pouch','An arcane focus',NULL,NULL),
(102,'Sorcerer',3,0,NULL,'A dungeoneer''s pack','An explorer''s pack',NULL,NULL),
(103,'Sorcerer',4,1,'Two daggers',NULL,NULL,NULL,NULL),
-- Warlock
(110,'Warlock',1,0,NULL,'A light crossbow and 20 bolts','Any simple weapon',NULL,NULL),
(111,'Warlock',2,0,NULL,'A component pouch','An arcane focus',NULL,NULL),
(112,'Warlock',3,0,NULL,'A scholar''s pack','A dungeoneer''s pack',NULL,NULL),
(113,'Warlock',4,1,'Leather Armor',NULL,NULL,NULL,NULL),
(114,'Warlock',5,1,'Any simple weapon',NULL,NULL,NULL,NULL),
(115,'Warlock',6,1,'Two daggers',NULL,NULL,NULL,NULL),
-- Wizard
(120,'Wizard',1,0,NULL,'A quarterstaff','A dagger',NULL,NULL),
(121,'Wizard',2,0,NULL,'A component pouch','An arcane focus',NULL,NULL),
(122,'Wizard',3,0,NULL,'A scholar''s pack','An explorer''s pack',NULL,NULL),
(123,'Wizard',4,1,'A spellbook',NULL,NULL,NULL,NULL);

-- =============================================================================
-- 20. CHARACTER TABLES
-- =============================================================================

CREATE TABLE IF NOT EXISTS character (
    id                  INTEGER      NOT NULL,
    player_name         VARCHAR(127),
    character_name      VARCHAR(127) NOT NULL,
    race_name           VARCHAR(63)  NOT NULL,
    class_name          VARCHAR(63)  NOT NULL,
    subclass_name       VARCHAR(127),
    background_name     VARCHAR(63)  NOT NULL,
    alignment_id        INTEGER,
    level               INTEGER      NOT NULL DEFAULT 1,
    experience_points   INTEGER      NOT NULL DEFAULT 0,
    character_picture   VARCHAR(512),
    created_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (race_name)       REFERENCES race(name),
    FOREIGN KEY (class_name)      REFERENCES class(name),
    FOREIGN KEY (background_name) REFERENCES background(name),
    FOREIGN KEY (alignment_id)    REFERENCES alignment(id)
);

CREATE TABLE IF NOT EXISTS character_stats (
    character_id    INTEGER NOT NULL,
    strength        INTEGER NOT NULL DEFAULT 10,
    dexterity       INTEGER NOT NULL DEFAULT 10,
    constitution    INTEGER NOT NULL DEFAULT 10,
    intelligence    INTEGER NOT NULL DEFAULT 10,
    wisdom          INTEGER NOT NULL DEFAULT 10,
    charisma        INTEGER NOT NULL DEFAULT 10,
    max_hp          INTEGER,
    armor_class     INTEGER,
    initiative      INTEGER,
    speed           INTEGER,
    PRIMARY KEY (character_id),
    FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS character_skill (
    character_id    INTEGER     NOT NULL,
    skill_name      VARCHAR(63) NOT NULL,
    is_proficient   BOOLEAN     NOT NULL DEFAULT 1,
    is_expert       BOOLEAN     NOT NULL DEFAULT 0,
    PRIMARY KEY (character_id, skill_name),
    FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_name)   REFERENCES skill(name)
);

CREATE TABLE IF NOT EXISTS character_equipment (
    id              INTEGER      NOT NULL,
    character_id    INTEGER      NOT NULL,
    item_name       VARCHAR(255) NOT NULL,
    item_type       VARCHAR(63),
    quantity        INTEGER      NOT NULL DEFAULT 1,
    source          VARCHAR(63),
    PRIMARY KEY (id),
    FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS character_spell (
    character_id    INTEGER NOT NULL,
    spell_id        INTEGER NOT NULL,
    is_prepared     BOOLEAN NOT NULL DEFAULT 0,
    PRIMARY KEY (character_id, spell_id),
    FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE,
    FOREIGN KEY (spell_id)     REFERENCES spell(id)
);

-- =============================================================================
-- END OF D&D 5e COMPLETE DATABASE
-- =============================================================================
-- LOAD ORDER: This single file contains everything. Run once.
--
-- EXAMPLE CHARACTER INSERT:
--   INSERT INTO character (id, character_name, race_name, class_name, background_name, level, character_picture)
--   VALUES (1, 'Thorin Ironfist', 'Hill Dwarf', 'Cleric', 'Acolyte', 1, '/uploads/characters/thorin.png');
--
--   INSERT INTO character_stats (character_id, strength, dexterity, constitution, intelligence, wisdom, charisma, max_hp, armor_class, speed)
--   VALUES (1, 14, 8, 16, 10, 16, 10, 11, 18, 25);
--
--   INSERT INTO character_skill (character_id, skill_name) VALUES (1,'Insight'), (1,'Religion');
--
--   INSERT INTO character_equipment (id, character_id, item_name, item_type, quantity, source)
--   VALUES (1,1,'Warhammer','weapon',1,'class'), (2,1,'Chain Mail','armour',1,'class'),
--          (3,1,'Shield','armour',1,'class'), (4,1,'Explorer''s Pack','pack',1,'class');
-- =============================================================================

-- =============================================================================
-- D&D 5e LEVEL UP EXTENSION
-- Ergänzung zu dnd5e.sql — Einmalig ausführen nach der Basis-DB
-- Fügt hinzu:
--   A.  character_level_up        — Verlaufsprotokoll jedes Level-Ups
--   B.  character_asi             — ASI-Entscheidungen pro Charakter
--   C.  class_asi_levels          — Welche Level geben ASI pro Klasse
--   D.  class_spells_known        — Spells/Cantrips Known Progression
--   E.  subclass_feature          — Subklassen-Features Level 1–20
--   F.  class_feature (INSERT)    — Klassen-Features Level 3–20
-- =============================================================================


-- =============================================================================
-- A. CHARACTER LEVEL UP HISTORY
-- Protokolliert jeden Level-Up eines Charakters.
-- =============================================================================

CREATE TABLE IF NOT EXISTS character_level_up (
                                                  id              INTEGER     NOT NULL,
                                                  character_id    INTEGER     NOT NULL,
                                                  new_level       INTEGER     NOT NULL CHECK (new_level BETWEEN 2 AND 20),
    hp_gained       INTEGER     NOT NULL,       -- gewürfelte / gemittelte HP
    subclass_chosen VARCHAR(127),               -- nur wenn bei diesem Level gewählt
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (character_id) REFERENCES "character"(id) ON DELETE CASCADE
    );


-- =============================================================================
-- B. CHARACTER ASI CHOICES
-- Speichert welche Ability Score Improvements ein Charakter gewählt hat.
-- Entweder +2 auf einen Stat (ability_2 = NULL) oder +1/+1 auf zwei Stats.
-- =============================================================================

CREATE TABLE IF NOT EXISTS character_asi (
                                             id              INTEGER     NOT NULL,
                                             character_id    INTEGER     NOT NULL,
                                             level_gained    INTEGER     NOT NULL CHECK (level_gained BETWEEN 4 AND 20),
    ability_1       VARCHAR(63) NOT NULL,
    bonus_1         INTEGER     NOT NULL DEFAULT 1 CHECK (bonus_1 IN (1, 2)),
    ability_2       VARCHAR(63),                -- NULL = +2 auf ability_1
    bonus_2         INTEGER     CHECK (bonus_2 IS NULL OR bonus_2 = 1),
    PRIMARY KEY (id),
    FOREIGN KEY (character_id) REFERENCES "character"(id) ON DELETE CASCADE,
    FOREIGN KEY (ability_1)    REFERENCES ability(name),
    FOREIGN KEY (ability_2)    REFERENCES ability(name)
    );


-- =============================================================================
-- C. CLASS ASI LEVELS
-- Welche Charakter-Level geben einer Klasse ein ASI.
-- Fighter und Rogue erhalten mehr ASIs als andere Klassen.
-- =============================================================================

CREATE TABLE IF NOT EXISTS class_asi_levels (
                                                class_name  VARCHAR(63) NOT NULL,
    level       INTEGER     NOT NULL CHECK (level BETWEEN 4 AND 20),
    PRIMARY KEY (class_name, level),
    FOREIGN KEY (class_name) REFERENCES class(name)
    );

-- Standard: alle Klassen außer Fighter & Rogue — Level 4, 8, 12, 16, 19
INSERT INTO class_asi_levels (class_name, level) VALUES
                                                     ('Barbarian', 4), ('Barbarian', 8), ('Barbarian',12), ('Barbarian',16), ('Barbarian',19),
                                                     ('Bard',      4), ('Bard',      8), ('Bard',     12), ('Bard',     16), ('Bard',     19),
                                                     ('Cleric',    4), ('Cleric',    8), ('Cleric',   12), ('Cleric',   16), ('Cleric',   19),
                                                     ('Druid',     4), ('Druid',     8), ('Druid',    12), ('Druid',    16), ('Druid',    19),
                                                     ('Monk',      4), ('Monk',      8), ('Monk',     12), ('Monk',     16), ('Monk',     19),
                                                     ('Paladin',   4), ('Paladin',   8), ('Paladin',  12), ('Paladin',  16), ('Paladin',  19),
                                                     ('Ranger',    4), ('Ranger',    8), ('Ranger',   12), ('Ranger',   16), ('Ranger',   19),
                                                     ('Sorcerer',  4), ('Sorcerer',  8), ('Sorcerer', 12), ('Sorcerer', 16), ('Sorcerer', 19),
                                                     ('Warlock',   4), ('Warlock',   8), ('Warlock',  12), ('Warlock',  16), ('Warlock',  19),
                                                     ('Wizard',    4), ('Wizard',    8), ('Wizard',   12), ('Wizard',   16), ('Wizard',   19);

-- Fighter: 4, 6, 8, 10, 12, 14, 16, 19 (zusätzliche ASIs bei 6, 10, 14)
INSERT INTO class_asi_levels (class_name, level) VALUES
                                                     ('Fighter', 4), ('Fighter', 6), ('Fighter', 8), ('Fighter',10),
                                                     ('Fighter',12), ('Fighter',14), ('Fighter',16), ('Fighter',19);

-- Rogue: 4, 6, 8, 10, 12, 14, 16, 19 (zusätzliche ASIs bei 6, 10, 14)
INSERT INTO class_asi_levels (class_name, level) VALUES
                                                     ('Rogue', 4), ('Rogue', 6), ('Rogue', 8), ('Rogue',10),
                                                     ('Rogue',12), ('Rogue',14), ('Rogue',16), ('Rogue',19);


-- =============================================================================
-- D. CLASS SPELLS KNOWN PROGRESSION
-- Cantrips Known & Spells Known pro Klasse und Charakter-Level.
-- NULL bei spells_known = Prepared Caster (wählt täglich aus dem kompletten Pool).
-- =============================================================================

CREATE TABLE IF NOT EXISTS class_spells_known (
                                                  class_name      VARCHAR(63) NOT NULL,
    character_level INTEGER     NOT NULL CHECK (character_level BETWEEN 1 AND 20),
    cantrips_known  INTEGER,    -- NULL = Klasse hat keine Cantrips
    spells_known    INTEGER,    -- NULL = Prepared Caster (kein festes Limit)
    PRIMARY KEY (class_name, character_level),
    FOREIGN KEY (class_name) REFERENCES class(name)
    );

-- ── BARD (Known Caster) ──────────────────────────────────────────────────────
INSERT INTO class_spells_known VALUES
                                   ('Bard', 1, 2, 4), ('Bard', 2, 2, 5), ('Bard', 3, 2, 6), ('Bard', 4, 3, 7),
                                   ('Bard', 5, 3, 8), ('Bard', 6, 3, 9), ('Bard', 7, 3,10), ('Bard', 8, 3,11),
                                   ('Bard', 9, 3,12), ('Bard',10, 4,14), ('Bard',11, 4,15), ('Bard',12, 4,15),
                                   ('Bard',13, 4,16), ('Bard',14, 4,18), ('Bard',15, 4,19), ('Bard',16, 4,19),
                                   ('Bard',17, 4,20), ('Bard',18, 4,22), ('Bard',19, 4,22), ('Bard',20, 4,22);

-- ── CLERIC (Prepared Caster — spells_known = NULL) ──────────────────────────
INSERT INTO class_spells_known VALUES
                                   ('Cleric', 1, 3,NULL), ('Cleric', 2, 3,NULL), ('Cleric', 3, 3,NULL), ('Cleric', 4, 4,NULL),
                                   ('Cleric', 5, 4,NULL), ('Cleric', 6, 4,NULL), ('Cleric', 7, 4,NULL), ('Cleric', 8, 4,NULL),
                                   ('Cleric', 9, 4,NULL), ('Cleric',10, 5,NULL), ('Cleric',11, 5,NULL), ('Cleric',12, 5,NULL),
                                   ('Cleric',13, 5,NULL), ('Cleric',14, 5,NULL), ('Cleric',15, 5,NULL), ('Cleric',16, 5,NULL),
                                   ('Cleric',17, 5,NULL), ('Cleric',18, 5,NULL), ('Cleric',19, 5,NULL), ('Cleric',20, 5,NULL);

-- ── DRUID (Prepared Caster — spells_known = NULL) ───────────────────────────
INSERT INTO class_spells_known VALUES
                                   ('Druid', 1, 2,NULL), ('Druid', 2, 2,NULL), ('Druid', 3, 2,NULL), ('Druid', 4, 3,NULL),
                                   ('Druid', 5, 3,NULL), ('Druid', 6, 3,NULL), ('Druid', 7, 3,NULL), ('Druid', 8, 3,NULL),
                                   ('Druid', 9, 3,NULL), ('Druid',10, 4,NULL), ('Druid',11, 4,NULL), ('Druid',12, 4,NULL),
                                   ('Druid',13, 4,NULL), ('Druid',14, 4,NULL), ('Druid',15, 4,NULL), ('Druid',16, 4,NULL),
                                   ('Druid',17, 4,NULL), ('Druid',18, 4,NULL), ('Druid',19, 4,NULL), ('Druid',20, 4,NULL);

-- ── RANGER (Known Caster — keine Cantrips) ──────────────────────────────────
INSERT INTO class_spells_known VALUES
                                   ('Ranger', 1,NULL,NULL), ('Ranger', 2,NULL, 2), ('Ranger', 3,NULL, 3), ('Ranger', 4,NULL, 3),
                                   ('Ranger', 5,NULL, 4),   ('Ranger', 6,NULL, 4), ('Ranger', 7,NULL, 5), ('Ranger', 8,NULL, 5),
                                   ('Ranger', 9,NULL, 6),   ('Ranger',10,NULL, 6), ('Ranger',11,NULL, 7), ('Ranger',12,NULL, 7),
                                   ('Ranger',13,NULL, 8),   ('Ranger',14,NULL, 8), ('Ranger',15,NULL, 9), ('Ranger',16,NULL, 9),
                                   ('Ranger',17,NULL,10),   ('Ranger',18,NULL,10), ('Ranger',19,NULL,11), ('Ranger',20,NULL,11);

-- ── SORCERER (Known Caster) ─────────────────────────────────────────────────
INSERT INTO class_spells_known VALUES
                                   ('Sorcerer', 1, 4, 2), ('Sorcerer', 2, 4, 3), ('Sorcerer', 3, 4, 4), ('Sorcerer', 4, 5, 5),
                                   ('Sorcerer', 5, 5, 6), ('Sorcerer', 6, 5, 7), ('Sorcerer', 7, 5, 8), ('Sorcerer', 8, 5, 9),
                                   ('Sorcerer', 9, 5,10), ('Sorcerer',10, 6,11), ('Sorcerer',11, 6,12), ('Sorcerer',12, 6,12),
                                   ('Sorcerer',13, 6,13), ('Sorcerer',14, 6,13), ('Sorcerer',15, 6,14), ('Sorcerer',16, 6,14),
                                   ('Sorcerer',17, 6,15), ('Sorcerer',18, 6,15), ('Sorcerer',19, 6,15), ('Sorcerer',20, 6,15);

-- ── WARLOCK (Known Caster / Pact Magic) ─────────────────────────────────────
INSERT INTO class_spells_known VALUES
                                   ('Warlock', 1, 2, 2), ('Warlock', 2, 2, 3), ('Warlock', 3, 2, 4), ('Warlock', 4, 3, 5),
                                   ('Warlock', 5, 3, 6), ('Warlock', 6, 3, 7), ('Warlock', 7, 3, 8), ('Warlock', 8, 3, 9),
                                   ('Warlock', 9, 3,10), ('Warlock',10, 4,10), ('Warlock',11, 4,11), ('Warlock',12, 4,11),
                                   ('Warlock',13, 4,12), ('Warlock',14, 4,12), ('Warlock',15, 4,13), ('Warlock',16, 4,13),
                                   ('Warlock',17, 4,14), ('Warlock',18, 4,14), ('Warlock',19, 4,15), ('Warlock',20, 4,15);

-- ── WIZARD (Prepared Caster — Spellbook, spells_known = NULL) ───────────────
INSERT INTO class_spells_known VALUES
                                   ('Wizard', 1, 3,NULL), ('Wizard', 2, 3,NULL), ('Wizard', 3, 3,NULL), ('Wizard', 4, 4,NULL),
                                   ('Wizard', 5, 4,NULL), ('Wizard', 6, 4,NULL), ('Wizard', 7, 4,NULL), ('Wizard', 8, 4,NULL),
                                   ('Wizard', 9, 4,NULL), ('Wizard',10, 5,NULL), ('Wizard',11, 5,NULL), ('Wizard',12, 5,NULL),
                                   ('Wizard',13, 5,NULL), ('Wizard',14, 5,NULL), ('Wizard',15, 5,NULL), ('Wizard',16, 5,NULL),
                                   ('Wizard',17, 5,NULL), ('Wizard',18, 5,NULL), ('Wizard',19, 5,NULL), ('Wizard',20, 5,NULL);

-- Nicht-Zaubernde Klassen (Barbarian, Fighter, Monk, Rogue) — keine Einträge nötig.
-- Paladin/Ranger ohne Cantrips, Paladin ist Prepared Caster (ab Level 2).
INSERT INTO class_spells_known VALUES
                                   ('Paladin', 1,NULL,NULL), ('Paladin', 2,NULL,NULL), ('Paladin', 3,NULL,NULL), ('Paladin', 4,NULL,NULL),
                                   ('Paladin', 5,NULL,NULL), ('Paladin', 6,NULL,NULL), ('Paladin', 7,NULL,NULL), ('Paladin', 8,NULL,NULL),
                                   ('Paladin', 9,NULL,NULL), ('Paladin',10,NULL,NULL), ('Paladin',11,NULL,NULL), ('Paladin',12,NULL,NULL),
                                   ('Paladin',13,NULL,NULL), ('Paladin',14,NULL,NULL), ('Paladin',15,NULL,NULL), ('Paladin',16,NULL,NULL),
                                   ('Paladin',17,NULL,NULL), ('Paladin',18,NULL,NULL), ('Paladin',19,NULL,NULL), ('Paladin',20,NULL,NULL);


-- =============================================================================
-- E. SUBCLASS FEATURE
-- Features die eine Subklasse bei bestimmten Leveln gewährt.
-- Struktur analog zu class_feature.
-- =============================================================================

CREATE TABLE IF NOT EXISTS subclass_feature (
                                                subclass_name   VARCHAR(127) NOT NULL,
    level           INTEGER      NOT NULL CHECK (level BETWEEN 1 AND 20),
    feature_name    VARCHAR(127) NOT NULL,
    description     TEXT,
    PRIMARY KEY (subclass_name, level, feature_name),
    FOREIGN KEY (subclass_name) REFERENCES subclass(name)
    );

-- ── BARBARIAN ────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Path of the Berserker', 3,  'Frenzy',
                                  'When you rage, you can go into a frenzy. For the duration of your rage, you can make a single melee weapon attack as a bonus action on each of your turns after this one. When your rage ends, you suffer one level of exhaustion.'),
                                 ('Path of the Berserker', 6,  'Mindless Rage',
                                  'You can''t be charmed or frightened while raging. If you are charmed or frightened when you enter your rage, the effect is suspended for the duration of the rage.'),
                                 ('Path of the Berserker', 10, 'Intimidating Presence',
                                  'You can use your action to frighten someone with your menacing presence. One creature that you can see within 30 feet of you must succeed on a Wisdom saving throw (DC = 8 + proficiency bonus + Charisma modifier) or become frightened of you until the end of your next turn.'),
                                 ('Path of the Berserker', 14, 'Retaliation',
                                  'When you take damage from a creature that is within 5 feet of you, you can use your reaction to make a melee weapon attack against that creature.');

INSERT INTO subclass_feature VALUES
                                 ('Path of the Totem Warrior', 3,  'Spirit Seeker',
                                  'Yours is a path that seeks attunement with the natural world, giving you a kinship with beasts. You gain the ability to cast the beast sense and speak with animals spells, but only as rituals.'),
                                 ('Path of the Totem Warrior', 3,  'Totem Spirit',
                                  'Choose a totem spirit (Bear, Eagle, or Wolf). While raging, you gain the benefit of your chosen totem spirit.'),
                                 ('Path of the Totem Warrior', 6,  'Aspect of the Beast',
                                  'You gain a magical benefit based on the totem animal of your choice (Bear: carrying capacity doubled; Eagle: can see 1 mile away; Wolf: can track while traveling at fast pace).'),
                                 ('Path of the Totem Warrior', 10, 'Spirit Walker',
                                  'You can cast the commune with nature spell, but only as a ritual. When you do so, a spiritual version of one of the animals you chose for Totem Spirit or Aspect of the Beast appears to you to convey the information you seek.'),
                                 ('Path of the Totem Warrior', 14, 'Totemic Attunement',
                                  'You gain a magical benefit based on a totem animal of your choice. You can choose the same animal you selected previously or a different one.');

-- ── BARD ─────────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('College of Lore', 3,  'Bonus Proficiencies',
                                  'You gain proficiency with three skills of your choice.'),
                                 ('College of Lore', 3,  'Cutting Words',
                                  'When a creature that you can see within 60 feet of you makes an attack roll, ability check, or damage roll, you can use your reaction to expend one of your Bardic Inspiration uses to roll a Bardic Inspiration die. Subtract the number from the creature''s roll.'),
                                 ('College of Lore', 6,  'Additional Magical Secrets',
                                  'You learn two spells of your choice from any class. These spells count as bard spells for you.'),
                                 ('College of Lore', 14, 'Peerless Skill',
                                  'When you make an ability check, you can expend one use of Bardic Inspiration. Roll a Bardic Inspiration die and add it to the check. You can choose to do so after you roll the ability check but before the DM tells you whether you succeed or fail.');

INSERT INTO subclass_feature VALUES
                                 ('College of Valor', 3,  'Bonus Proficiencies',
                                  'You gain proficiency with medium armor, shields, and martial weapons.'),
                                 ('College of Valor', 3,  'Combat Inspiration',
                                  'A creature that has a Bardic Inspiration die from you can also use it when it makes a weapon damage roll. When a creature uses a Bardic Inspiration die in this way, it rolls the die and adds the result to the damage roll.'),
                                 ('College of Valor', 6,  'Extra Attack',
                                  'You can attack twice, instead of once, whenever you take the Attack action on your turn.'),
                                 ('College of Valor', 14, 'Battle Magic',
                                  'You have mastered the art of weaving spellcasting and weapon use into a single harmonious act. When you use your action to cast a bard spell, you can make one weapon attack as a bonus action.');

-- ── CLERIC ───────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Life Domain', 1,  'Bonus Proficiency',
                                  'When you choose this domain at 1st level, you gain proficiency with heavy armor.'),
                                 ('Life Domain', 1,  'Disciple of Life',
                                  'Whenever you use a spell of 1st level or higher to restore hit points to a creature, the creature regains additional hit points equal to 2 + the spell''s level.'),
                                 ('Life Domain', 2,  'Channel Divinity: Preserve Life',
                                  'As an action, you present your holy symbol and evoke healing energy that can restore a number of hit points equal to five times your cleric level. Choose any creatures within 30 feet of you, and divide those hit points among them.'),
                                 ('Life Domain', 6,  'Blessed Healer',
                                  'The healing spells you cast on others heal you as well. When you cast a healing spell of 1st level or higher that restores hit points to a creature other than you, you regain hit points equal to 2 + the spell''s level.'),
                                 ('Life Domain', 8,  'Divine Strike',
                                  'Once on each of your turns when you hit a creature with a weapon attack, you can cause the attack to deal an extra 1d8 radiant damage to the target.'),
                                 ('Life Domain', 17, 'Supreme Healing',
                                  'When you would normally roll one or more dice to restore hit points with a spell, you instead use the highest number possible for each die.');

INSERT INTO subclass_feature VALUES
                                 ('Light Domain', 1,  'Bonus Cantrip',
                                  'You gain the light cantrip if you don''t already know it.'),
                                 ('Light Domain', 1,  'Warding Flare',
                                  'When attacked by a creature within 30 feet of you that you can see, you can use your reaction to impose disadvantage on the attack roll. An attacker that can''t be blinded is immune to this feature.'),
                                 ('Light Domain', 2,  'Channel Divinity: Radiance of the Dawn',
                                  'As an action, you present your holy symbol, and any magical darkness within 30 feet of you is dispelled. Each hostile creature within 30 feet must make a Constitution saving throw or take 2d10 + your cleric level radiant damage (half on success).'),
                                 ('Light Domain', 6,  'Improved Flare',
                                  'You can also use your Warding Flare when a creature that you can see within 30 feet of you attacks a creature other than you.'),
                                 ('Light Domain', 8,  'Potent Spellcasting',
                                  'You add your Wisdom modifier to the damage you deal with any cleric cantrip.'),
                                 ('Light Domain', 17, 'Corona of Light',
                                  'You can use your action to activate an aura of sunlight that lasts for 1 minute or until you dismiss it using another action. You emit bright light in a 60-foot radius and dim light 30 feet beyond that. Your enemies in the bright light have disadvantage on saving throws against any spell that deals fire or radiant damage.');

INSERT INTO subclass_feature VALUES
                                 ('Trickery Domain', 1,  'Blessing of the Trickster',
                                  'You can use your action to touch a willing creature other than yourself to give it advantage on Dexterity (Stealth) checks. This blessing lasts for 1 hour or until you use this feature again.'),
                                 ('Trickery Domain', 2,  'Channel Divinity: Invoke Duplicity',
                                  'As an action, you create a perfect illusion of yourself that lasts for 1 minute or until you lose your concentration. The illusion appears in an unoccupied space within 30 feet of you. You can move the illusion up to 30 feet each turn.'),
                                 ('Trickery Domain', 6,  'Channel Divinity: Cloak of Shadows',
                                  'As an action, you become invisible until the end of your next turn. You become visible if you attack or cast a spell.'),
                                 ('Trickery Domain', 8,  'Divine Strike',
                                  'Once on each of your turns when you hit a creature with a weapon attack, you can cause the attack to deal an extra 1d8 poison damage to the target.'),
                                 ('Trickery Domain', 17, 'Improved Duplicity',
                                  'You can create up to four duplicates of yourself, instead of one, when you use Invoke Duplicity. As a bonus action on your turn, you can move any number of them up to 30 feet, to a maximum range of 120 feet.');

-- ── DRUID ─────────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Circle of the Land', 2,  'Natural Recovery',
                                  'Starting at 2nd level, you can regain some of your magical energy by sitting in meditation and communing with nature. During a short rest, you choose expended spell slots to recover. The spell slots can have a combined level that is equal to or less than half your druid level (rounded up).'),
                                 ('Circle of the Land', 2,  'Circle Spells',
                                  'Your mystical connection to the land infuses you with the ability to cast certain spells. These spells are always prepared and count as druid spells for you, but they don''t count against the number of druid spells you can prepare. The specific spells depend on your chosen land (Arctic, Coast, Desert, Forest, Grassland, Mountain, Swamp, or Underdark).'),
                                 ('Circle of the Land', 6,  'Land''s Stride',
                                  'Moving through nonmagical difficult terrain costs you no extra movement. You can also pass through nonmagical plants without being slowed by them and without taking damage from them if they have thorns, spines, or a similar hazard.'),
                                 ('Circle of the Land', 10, 'Nature''s Ward',
                                  'You can''t be charmed or frightened by elementals or fey, and you are immune to poison and disease.'),
                                 ('Circle of the Land', 14, 'Nature''s Sanctuary',
                                  'Creatures of the natural world sense your connection to nature and become hesitant to attack you. When a beast or plant creature attacks you, that creature must make a Wisdom saving throw against your druid spell save DC. On a failed save, the creature must choose a different target.');

INSERT INTO subclass_feature VALUES
                                 ('Circle of the Moon', 2,  'Combat Wild Shape',
                                  'You gain the ability to use Wild Shape as a bonus action, rather than as an action. Additionally, while in beast form, you can use a bonus action to expend one spell slot to regain 1d8 hit points per level of the spell slot expended.'),
                                 ('Circle of the Moon', 2,  'Circle Forms',
                                  'You can transform into a beast with a challenge rating as high as 1 (instead of 1/4). Starting at 6th level, you can transform into a beast with a challenge rating as high as your druid level divided by 3, rounded down.'),
                                 ('Circle of the Moon', 6,  'Primal Strike',
                                  'Your attacks in beast form count as magical for the purpose of overcoming resistance and immunity to nonmagical attacks and damage.'),
                                 ('Circle of the Moon', 10, 'Elemental Wild Shape',
                                  'You can expend two uses of Wild Shape at the same time to transform into an air elemental, an earth elemental, a fire elemental, or a water elemental.'),
                                 ('Circle of the Moon', 14, 'Thousand Forms',
                                  'You have learned to use magic to alter your physical form in more subtle ways. You can cast the alter self spell at will.');

-- ── FIGHTER ──────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Champion', 3,  'Improved Critical',
                                  'Your weapon attacks score a critical hit on a roll of 19 or 20.'),
                                 ('Champion', 7,  'Remarkable Athlete',
                                  'You can add half your proficiency bonus (rounded up) to any Strength, Dexterity, or Constitution check you make that doesn''t already use your proficiency bonus. In addition, when you make a running long jump, the distance you can cover increases by a number of feet equal to your Strength modifier.'),
                                 ('Champion', 10, 'Additional Fighting Style',
                                  'You can choose a second option from the Fighting Style class feature.'),
                                 ('Champion', 15, 'Superior Critical',
                                  'Your weapon attacks score a critical hit on a roll of 18–20.'),
                                 ('Champion', 18, 'Survivor',
                                  'You attain the pinnacle of resilience in battle. At the start of each of your turns, you regain hit points equal to 5 + your Constitution modifier if you have no more than half of your hit points left. You don''t gain this benefit if you have 0 hit points.');

INSERT INTO subclass_feature VALUES
                                 ('Battle Master', 3,  'Combat Superiority',
                                  'You learn maneuvers that are fueled by special dice called superiority dice (d8). You have four superiority dice and learn three maneuvers of your choice. You regain all expended superiority dice when you finish a short or long rest.'),
                                 ('Battle Master', 3,  'Student of War',
                                  'You gain proficiency with one type of artisan''s tools of your choice.'),
                                 ('Battle Master', 7,  'Know Your Enemy',
                                  'If you spend at least 1 minute observing or interacting with another creature outside combat, you can learn certain information about its capabilities compared to your own.'),
                                 ('Battle Master', 10, 'Improved Combat Superiority',
                                  'Your superiority dice turn into d10s.'),
                                 ('Battle Master', 15, 'Relentless',
                                  'When you roll initiative and have no superiority dice remaining, you regain 1 superiority die.'),
                                 ('Battle Master', 18, 'Improved Combat Superiority',
                                  'Your superiority dice turn into d12s.');

INSERT INTO subclass_feature VALUES
                                 ('Eldritch Knight', 3,  'Spellcasting',
                                  'You augment your martial prowess with the ability to cast spells. You learn three wizard cantrips and know three 1st-level wizard spells. Intelligence is your spellcasting ability.'),
                                 ('Eldritch Knight', 3,  'Weapon Bond',
                                  'You learn a ritual that creates a magical bond between yourself and one weapon. You can bond up to two weapons at once.'),
                                 ('Eldritch Knight', 7,  'War Magic',
                                  'When you use your action to cast a cantrip, you can make one weapon attack as a bonus action.'),
                                 ('Eldritch Knight', 10, 'Eldritch Strike',
                                  'When you hit a creature with a weapon attack, that creature has disadvantage on the next saving throw it makes against a spell you cast before the end of your next turn.'),
                                 ('Eldritch Knight', 15, 'Arcane Charge',
                                  'You gain the ability to teleport up to 30 feet to an unoccupied space you can see when you use your Action Surge.'),
                                 ('Eldritch Knight', 18, 'Improved War Magic',
                                  'When you use your action to cast a spell, you can make one weapon attack as a bonus action.');

-- ── MONK ─────────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Way of the Open Hand', 3,  'Open Hand Technique',
                                  'Whenever you hit a creature with one of the attacks granted by your Flurry of Blows, you can impose one of the following effects on that target: knock prone, push up to 15 feet, or prevent reaction until end of next turn.'),
                                 ('Way of the Open Hand', 6,  'Wholeness of Body',
                                  'You gain the ability to heal yourself. As an action, you can regain hit points equal to three times your monk level. You must finish a long rest before you can use this feature again.'),
                                 ('Way of the Open Hand', 11, 'Tranquility',
                                  'You can enter a special meditation that surrounds you with an aura of peace. At the end of a long rest, you gain the effect of a sanctuary spell that lasts until the start of your next long rest.'),
                                 ('Way of the Open Hand', 17, 'Quivering Palm',
                                  'You gain the ability to set up lethal vibrations in someone''s body. When you hit a creature with an unarmed strike, you can spend 3 ki points to start these vibrations. You can then use your action on a later turn to end them, forcing a Constitution saving throw (DC = 8 + proficiency bonus + Wisdom modifier). On a failure, the creature is reduced to 0 hit points.');

INSERT INTO subclass_feature VALUES
                                 ('Way of Shadow', 3,  'Shadow Arts',
                                  'You can use your ki to duplicate the effects of certain spells. As an action, you can spend 2 ki points to cast darkness, darkvision, pass without trace, or silence, without providing material components.'),
                                 ('Way of Shadow', 6,  'Shadow Step',
                                  'You gain the ability to step from one shadow into another. When you are in dim light or darkness, as a bonus action you can teleport up to 60 feet to an unoccupied space you can see that is also in dim light or darkness.'),
                                 ('Way of Shadow', 11, 'Cloak of Shadows',
                                  'You have learned to become one with the shadows. When you are in an area of dim light or darkness, you can use your action to become invisible. You remain invisible until you make an attack, cast a spell, or are in an area of bright light.'),
                                 ('Way of Shadow', 17, 'Opportunist',
                                  'You can exploit a creature''s momentary distraction when it is hit by an attack. Whenever a creature within 5 feet of you is hit by an attack made by a creature other than you, you can use your reaction to make a melee attack against that creature.');

-- ── PALADIN ──────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Oath of Devotion', 3,  'Sacred Weapon',
                                  'As an action, you can imbue one weapon that you are holding with positive energy, using your Channel Divinity. For 1 minute, you add your Charisma modifier to attack rolls made with that weapon. The weapon also emits bright light in a 20-foot radius.'),
                                 ('Oath of Devotion', 3,  'Turn the Unholy',
                                  'As an action, you present your holy symbol and speak a prayer censuring fiends and undead, using your Channel Divinity. Each fiend or undead that can see or hear you within 30 feet must make a Wisdom saving throw.'),
                                 ('Oath of Devotion', 7,  'Aura of Devotion',
                                  'You and friendly creatures within 10 feet of you can''t be charmed while you are conscious. At 18th level, the range of this aura increases to 30 feet.'),
                                 ('Oath of Devotion', 15, 'Purity of Spirit',
                                  'You are always under the effects of a protection from evil and good spell.'),
                                 ('Oath of Devotion', 20, 'Holy Nimbus',
                                  'As an action, you can emanate an aura of sunlight. For 1 minute, bright light shines from you in a 30-foot radius, and dim light shines 30 feet beyond that. Enemies that start their turn in the bright light take 10 radiant damage. You can use this feature once per long rest.');

INSERT INTO subclass_feature VALUES
                                 ('Oath of Vengeance', 3,  'Abjure Enemy',
                                  'As an action, you present your holy symbol and speak a prayer of denunciation, using your Channel Divinity. Choose one creature within 60 feet that you can see. That creature must make a Wisdom saving throw, unless it is immune to being frightened.'),
                                 ('Oath of Vengeance', 3,  'Vow of Enmity',
                                  'As a bonus action, you can utter a vow of enmity against a creature you can see within 10 feet of you, using your Channel Divinity. You gain advantage on attack rolls against the creature for 1 minute or until it drops to 0 hit points.'),
                                 ('Oath of Vengeance', 7,  'Relentless Avenger',
                                  'Your supernatural focus helps you close off a foe''s retreat. When you hit a creature with an opportunity attack, you can move up to half your speed immediately after the attack and as part of the same reaction. This movement doesn''t provoke opportunity attacks.'),
                                 ('Oath of Vengeance', 15, 'Soul of Vengeance',
                                  'The authority with which you speak your Vow of Enmity gives you greater power over your foe. When a creature under the effect of your Vow of Enmity makes an attack, you can use your reaction to make a melee weapon attack against that creature if it is within range.'),
                                 ('Oath of Vengeance', 20, 'Avenging Angel',
                                  'You can assume the form of an angelic avenger. Using your action, you undergo a transformation. For 1 hour, you sprout wings and gain a flying speed of 60 feet. Enemies who see you must succeed on a Wisdom saving throw or become frightened of you for 1 minute.');

INSERT INTO subclass_feature VALUES
                                 ('Oath of the Ancients', 3,  'Nature''s Wrath',
                                  'You can use your Channel Divinity to invoke primeval forces to ensnare a foe. As an action, you can cause spectral vines to spring up and reach for a creature within 10 feet of you that you can see.'),
                                 ('Oath of the Ancients', 3,  'Turn the Faithless',
                                  'You can use your Channel Divinity to utter ancient words that are painful for fey and fiends to hear. Each fey or fiend within 30 feet that can hear you must make a Wisdom saving throw.'),
                                 ('Oath of the Ancients', 7,  'Aura of Warding',
                                  'Ancient magic lies so heavily upon you that it forms an eldritch ward. You and friendly creatures within 10 feet of you have resistance to damage from spells.'),
                                 ('Oath of the Ancients', 15, 'Undying Sentinel',
                                  'When you are reduced to 0 hit points and are not killed outright, you can choose to drop to 1 hit point instead. Once you use this ability, you can''t use it again until you finish a long rest.'),
                                 ('Oath of the Ancients', 20, 'Elder Champion',
                                  'You can assume the form of an ancient force of nature, taking on an appearance you choose. For 1 minute, your skin takes on a bark-like quality, grass grows from where you step, and you gain several benefits including regeneration and faster channel divinity recharge.');

-- ── RANGER ───────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Hunter', 3,  'Hunter''s Prey',
                                  'Choose one of the following: Colossus Slayer (extra 1d8 on hurt targets), Giant Killer (reaction attack when Large+ misses), or Horde Breaker (extra attack on adjacent enemy).'),
                                 ('Hunter', 7,  'Defensive Tactics',
                                  'Choose one: Escape the Horde (opportunity attacks have disadvantage), Multiattack Defense (+4 AC against same attacker), or Steel Will (advantage vs. frightened).'),
                                 ('Hunter', 11, 'Multiattack',
                                  'Choose one: Volley (ranged attack at all creatures in 10-ft radius) or Whirlwind Attack (melee attack against all creatures within reach).'),
                                 ('Hunter', 15, 'Superior Hunter''s Defense',
                                  'Choose one: Evasion (Dex save for no damage on success, half on fail) or Stand Against the Tide (redirect missed attack to another creature) or Uncanny Dodge (halve attack damage as reaction).');

INSERT INTO subclass_feature VALUES
                                 ('Beast Master', 3,  'Ranger''s Companion',
                                  'You gain a beast companion that accompanies you on your adventures and is trained to fight alongside you. Choose a beast that is no larger than Medium and has a CR of 1/4 or lower. The beast obeys your commands and acts on your initiative.'),
                                 ('Beast Master', 7,  'Exceptional Training',
                                  'On any of your turns when your beast companion doesn''t attack, you can use a bonus action to command the beast to take the Dash, Disengage, Dodge, or Help action.'),
                                 ('Beast Master', 11, 'Bestial Fury',
                                  'Your beast companion can make two attacks when you command it to use the Attack action.'),
                                 ('Beast Master', 15, 'Share Spells',
                                  'When you cast a spell targeting yourself, you can also affect your beast companion with the spell if the beast is within 30 feet of you.');

-- ── ROGUE ─────────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Thief', 3,  'Fast Hands',
                                  'You can use the bonus action granted by your Cunning Action to make a Dexterity (Sleight of Hand) check, use your thieves'' tools to disarm a trap or open a lock, or take the Use an Object action.'),
                                 ('Thief', 3,  'Second-Story Work',
                                  'You gain the ability to climb faster than normal; climbing no longer costs you extra movement. When you make a running jump, the distance you cover increases by a number of feet equal to your Dexterity modifier.'),
                                 ('Thief', 9,  'Supreme Sneak',
                                  'You have advantage on a Dexterity (Stealth) check if you move no more than half your speed on the same turn.'),
                                 ('Thief', 13, 'Use Magic Device',
                                  'You have learned enough about the workings of magic that you can improvise the use of items even when they are not intended for you. You ignore all class, race, and level requirements on the use of magic items.'),
                                 ('Thief', 17, 'Thief''s Reflexes',
                                  'You have become adept at laying ambushes and quickly escaping danger. You can take two turns during the first round of any combat. You take your first turn at your normal initiative and your second turn at your initiative minus 10.');

INSERT INTO subclass_feature VALUES
                                 ('Assassin', 3,  'Bonus Proficiencies',
                                  'You gain proficiency with the disguise kit and the poisoner''s kit.'),
                                 ('Assassin', 3,  'Assassinate',
                                  'You are at your deadliest when you get the drop on your enemies. You have advantage on attack rolls against any creature that hasn''t taken a turn in the combat yet. In addition, any hit you score against a creature that is surprised is a critical hit.'),
                                 ('Assassin', 9,  'Infiltration Expertise',
                                  'You can unfailingly create false identities for yourself. You must spend seven days and 25 gp to establish the history, profession, and affiliations for an identity.'),
                                 ('Assassin', 13, 'Impostor',
                                  'You gain the ability to unerringly mimic another person''s speech, writing, and behavior. You must spend at least three hours studying these three components of the person''s behavior.'),
                                 ('Assassin', 17, 'Death Strike',
                                  'You become a master of instant death. When you attack and hit a creature that is surprised, it must make a Constitution saving throw (DC = 8 + Dexterity modifier + proficiency bonus). On a failed save, double the damage of your attack against the creature.');

INSERT INTO subclass_feature VALUES
                                 ('Arcane Trickster', 3,  'Spellcasting',
                                  'You gain the ability to cast spells. You know three 1st-level spells, of which two must be from the enchantment and illusion schools of the wizard spell list. Intelligence is your spellcasting ability.'),
                                 ('Arcane Trickster', 3,  'Mage Hand Legerdemain',
                                  'When you cast mage hand, you can make the spectral hand invisible, and you can perform the following additional tasks with it: stow or retrieve an object, use thieves'' tools to pick locks and disarm traps at range.'),
                                 ('Arcane Trickster', 9,  'Magical Ambush',
                                  'If you are hidden from a creature when you cast a spell on it, the creature has disadvantage on any saving throw it makes against the spell this turn.'),
                                 ('Arcane Trickster', 13, 'Versatile Trickster',
                                  'You gain the ability to distract targets with your mage hand. As a bonus action on your turn, you can designate a creature within 5 feet of the spectral hand. Doing so gives you advantage on attack rolls against that creature until the end of the turn.'),
                                 ('Arcane Trickster', 17, 'Spell Thief',
                                  'You gain the ability to magically steal the knowledge of how to cast a spell from another spellcaster. Immediately after a creature casts a spell that targets you or includes you in its area of effect, you can use your reaction to force the creature to make a saving throw with its spellcasting ability modifier.');

-- ── SORCERER ─────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('Draconic Bloodline', 1,  'Dragon Ancestor',
                                  'You choose one type of dragon as your ancestor. The damage type associated with each dragon is used by features you gain later. You can speak, read, and write Draconic. Whenever you make a Charisma check when interacting with dragons, your proficiency bonus is doubled.'),
                                 ('Draconic Bloodline', 1,  'Draconic Resilience',
                                  'As magic flows through your body, it causes physical traits of your dragon ancestors to emerge. Your hit point maximum increases by 1 and increases by 1 again whenever you gain a level in this class. Additionally, parts of your skin are covered by a thin sheen of dragon-like scales: when you aren''t wearing armor, your AC equals 13 + your Dexterity modifier.'),
                                 ('Draconic Bloodline', 6,  'Elemental Affinity',
                                  'When you cast a spell that deals damage of the type associated with your draconic ancestry, you can add your Charisma modifier to that damage roll. At the same time, you can spend 1 sorcery point to gain resistance to that damage type for 1 hour.'),
                                 ('Draconic Bloodline', 14, 'Dragon Wings',
                                  'You gain the ability to sprout a pair of dragon wings from your back, gaining a flying speed equal to your current speed. You can create these wings as a bonus action on your turn.'),
                                 ('Draconic Bloodline', 18, 'Draconic Presence',
                                  'You can channel the dread presence of your dragon ancestor, causing those around you to become awestruck or frightened. As an action, you can spend 5 sorcery points to draw on this power and exude an aura of awe or fear (your choice) to a distance of 60 feet.');

INSERT INTO subclass_feature VALUES
                                 ('Wild Magic', 1,  'Wild Magic Surge',
                                  'Your spellcasting can unleash surges of untamed magic. Immediately after you cast a sorcerer spell of 1st level or higher, the DM can have you roll a d20. If you roll a 1, roll on the Wild Magic Surge table to create a random magical effect.'),
                                 ('Wild Magic', 1,  'Tides of Chaos',
                                  'You can manipulate the forces of chance and chaos to gain advantage on one attack roll, ability check, or saving throw. Once you do so, you must finish a long rest before you can use this feature again.'),
                                 ('Wild Magic', 6,  'Bend Luck',
                                  'You have the ability to twist fate using your wild magic. When another creature you can see makes an attack roll, ability check, or saving throw, you can use your reaction and spend 2 sorcery points to roll 1d4 and apply the number rolled as a bonus or penalty (your choice) to the creature''s roll.'),
                                 ('Wild Magic', 14, 'Controlled Chaos',
                                  'You gain a modicum of control over the surges of your wild magic. Whenever you roll on the Wild Magic Surge table, you can roll twice and use either number.'),
                                 ('Wild Magic', 18, 'Spell Bombardment',
                                  'The harmful energy of your spells intensifies. When you roll damage for a spell and roll the highest number possible on any of the dice, choose one of those dice, roll it again and add that roll to the damage.');

-- ── WARLOCK ──────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('The Fiend', 1,  'Dark One''s Blessing',
                                  'Starting at 1st level, when you reduce a hostile creature to 0 hit points, you gain temporary hit points equal to your Charisma modifier + your warlock level (minimum of 1).'),
                                 ('The Fiend', 6,  'Dark One''s Own Luck',
                                  'Starting at 6th level, you can call on your patron to alter fate in your favor. When you make an ability check or a saving throw, you can use this feature to add a d10 to your roll. You can do so after seeing the initial roll but before any of the roll''s effects occur. Once you use this feature, you can''t use it again until you finish a short or long rest.'),
                                 ('The Fiend', 10, 'Fiendish Resilience',
                                  'Starting at 10th level, you can choose one damage type when you finish a short or long rest. You gain resistance to that damage type until you choose a different one.'),
                                 ('The Fiend', 14, 'Hurl Through Hell',
                                  'Starting at 14th level, when you hit a creature with an attack, you can use this feature to instantly transport the target through the lower planes. The creature disappears and hurtles through a nightmare landscape. At the end of your next turn, the target returns to the space it previously occupied, or the nearest unoccupied space. Unless the creature is a fiend, it takes 10d10 psychic damage from its horrific experience.');

INSERT INTO subclass_feature VALUES
                                 ('The Great Old One', 1,  'Awakened Mind',
                                  'Starting at 1st level, your alien knowledge gives you the ability to touch the minds of other creatures. You can communicate telepathically with any creature you can see within 30 feet of you. You don''t need to share a language with the creature for it to understand your telepathic utterances.'),
                                 ('The Great Old One', 6,  'Entropic Ward',
                                  'At 6th level, you learn to magically ward yourself against attack and to turn an enemy''s failed strike into good luck for yourself. When a creature makes an attack roll against you, you can use your reaction to impose disadvantage on that roll. If the attack misses you, your next attack roll against the creature has advantage if you make it before the end of your next turn.'),
                                 ('The Great Old One', 10, 'Thought Shield',
                                  'Starting at 10th level, your thoughts can''t be read by telepathy or other means unless you allow it. Also, whenever a creature deals psychic damage to you, that creature takes the same amount of damage that you do.'),
                                 ('The Great Old One', 14, 'Create Thrall',
                                  'At 14th level, you gain the ability to infect a humanoid''s mind with the alien magic of your patron. You can use your action to touch an incapacitated humanoid. That creature is then charmed by you until a remove curse spell is cast on it, the charmed condition is removed, or you use this feature again.');

-- ── WIZARD ───────────────────────────────────────────────────────────────────

INSERT INTO subclass_feature VALUES
                                 ('School of Evocation', 2,  'Evocation Savant',
                                  'The gold and time you must spend to copy an evocation spell into your spellbook is halved.'),
                                 ('School of Evocation', 2,  'Sculpt Spells',
                                  'You can create pockets of relative safety within the effects of your evocation spells. When you cast an evocation spell that affects other creatures that you can see, you can choose a number of them equal to 1 + the spell''s level. The chosen creatures automatically succeed on their saving throws against the spell, and they take no damage if they would normally take half damage on a successful save.'),
                                 ('School of Evocation', 6,  'Potent Cantrip',
                                  'Your damaging cantrips affect even creatures that avoid the brunt of the effect. When a creature succeeds on a saving throw against your cantrip, the creature takes half the cantrip''s damage (if any) but suffers no additional effect from the cantrip.'),
                                 ('School of Evocation', 10, 'Empowered Evocation',
                                  'Beginning at 10th level, you can add your Intelligence modifier to one damage roll of any wizard evocation spell you cast.'),
                                 ('School of Evocation', 14, 'Overchannel',
                                  'Starting at 14th level, you can increase the power of your simpler spells. When you cast a wizard spell of 1st through 5th level that deals damage, you can deal maximum damage with that spell. The first time you do so, you suffer no adverse effect. If you use this feature again before you finish a long rest, you take 2d12 necrotic damage for each level of the spell, immediately after you cast it.');

INSERT INTO subclass_feature VALUES
                                 ('School of Abjuration', 2,  'Abjuration Savant',
                                  'The gold and time you must spend to copy an abjuration spell into your spellbook is halved.'),
                                 ('School of Abjuration', 2,  'Arcane Ward',
                                  'Starting at 2nd level, you can weave magic around yourself for protection. When you cast an abjuration spell of 1st level or higher, you can simultaneously use a strand of the spell''s magic to create a magical ward on yourself that lasts until you finish a long rest. The ward has hit points equal to twice your wizard level + your Intelligence modifier.'),
                                 ('School of Abjuration', 6,  'Projected Ward',
                                  'Beginning at 6th level, when a creature that you can see within 30 feet of you takes damage, you can use your reaction to cause your Arcane Ward to absorb that damage. If this damage reduces the ward to 0 hit points, the warded creature takes any remaining damage.'),
                                 ('School of Abjuration', 10, 'Improved Abjuration',
                                  'Beginning at 10th level, when you cast an abjuration spell that requires you to make an ability check as a part of casting that spell, you add your proficiency bonus to that ability check.'),
                                 ('School of Abjuration', 14, 'Spell Resistance',
                                  'Starting at 14th level, you have advantage on saving throws against spells. Furthermore, you have resistance against the damage of spells.');

INSERT INTO subclass_feature VALUES
                                 ('School of Illusion', 2,  'Illusion Savant',
                                  'The gold and time you must spend to copy an illusion spell into your spellbook is halved.'),
                                 ('School of Illusion', 2,  'Improved Minor Illusion',
                                  'When you choose this school at 2nd level, you learn the minor illusion cantrip. If you already know this cantrip, you learn a different wizard cantrip of your choice. When you cast minor illusion, you can create both a sound and an image with a single casting of the spell.'),
                                 ('School of Illusion', 6,  'Malleable Illusions',
                                  'Starting at 6th level, when you cast an illusion spell that has a duration of 1 minute or longer, you can use your action to change the nature of that illusion (using the spell''s normal parameters for the illusion), provided that you can see the illusion.'),
                                 ('School of Illusion', 10, 'Illusory Self',
                                  'Beginning at 10th level, you can create an illusory duplicate of yourself as an instant, almost instinctual reaction to danger. When a creature makes an attack roll against you, you can use your reaction to interpose the illusory duplicate between the attacker and yourself.'),
                                 ('School of Illusion', 14, 'Illusory Reality',
                                  'By 14th level, you have learned the secret of weaving shadow magic into your illusions to give them a semi-reality. When you cast an illusion spell of 1st level or higher, you can choose one inanimate, nonmagical object that is part of the illusion and make that object real.');


-- =============================================================================
-- F. CLASS FEATURE — LEVEL 3–20 ADDITIONS
-- Ergänzt die bestehende class_feature Tabelle mit Level 3–20.
-- Beschreibungen sind bewusst kurz gehalten (max ~200 Zeichen) für die UI.
-- =============================================================================

-- ── BARBARIAN Level 3–20 ────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Barbarian', 3,  'Primal Path',         'Choose a Primal Path that shapes the nature of your rage: Path of the Berserker or Path of the Totem Warrior. Your choice grants features at 3rd, 6th, 10th, and 14th level.'),
                                                                             ('Barbarian', 4,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each. You can''t exceed 20 with this feature.'),
                                                                             ('Barbarian', 5,  'Extra Attack',        'You can attack twice, instead of once, whenever you take the Attack action on your turn.'),
                                                                             ('Barbarian', 5,  'Fast Movement',       'Your speed increases by 10 feet while you aren''t wearing heavy armor.'),
                                                                             ('Barbarian', 7,  'Feral Instinct',      'Your instincts are so honed that you have advantage on initiative rolls. Additionally, if you are surprised at the beginning of combat, you can act normally on your first turn if you enter your rage before doing anything else.'),
                                                                             ('Barbarian', 8,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Barbarian', 9,  'Brutal Critical',     'You can roll one additional weapon damage die when determining the extra damage for a critical hit with a melee attack.'),
                                                                             ('Barbarian',11,  'Relentless Rage',     'Your rage can keep you fighting despite grievous wounds. If you drop to 0 hit points while you''re raging, you can make a DC 10 Constitution saving throw to drop to 1 hit point instead.'),
                                                                             ('Barbarian',12,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Barbarian',13,  'Brutal Critical',     'You can roll two additional weapon damage dice when determining the extra damage for a critical hit with a melee attack.'),
                                                                             ('Barbarian',15,  'Persistent Rage',     'Your rage is so fierce that it ends early only if you fall unconscious or if you choose to end it.'),
                                                                             ('Barbarian',16,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Barbarian',17,  'Brutal Critical',     'You can roll three additional weapon damage dice when determining the extra damage for a critical hit with a melee attack.'),
                                                                             ('Barbarian',18,  'Indomitable Might',   'If your total for a Strength check is less than your Strength score, you can use that score in place of the total.'),
                                                                             ('Barbarian',19,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Barbarian',20,  'Primal Champion',     'You embody the power of the wilds. Your Strength and Constitution scores each increase by 4. Your maximum for those scores is now 24.');

-- ── BARD Level 3–20 ─────────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Bard', 3,  'Expertise',            'Choose two of your skill proficiencies. Your proficiency bonus is doubled for any ability check you make using either of the chosen proficiencies.'),
                                                                             ('Bard', 3,  'Bard College',         'You delve into the advanced techniques of a bard college of your choice: College of Lore or College of Valor. Your choice grants features at 3rd, 6th, and 14th level.'),
                                                                             ('Bard', 4,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Bard', 5,  'Bardic Inspiration (d8)',   'Your Bardic Inspiration die changes to a d8. Font of Inspiration: you now regain your expended uses of Bardic Inspiration on a short or long rest.'),
                                                                             ('Bard', 6,  'Countercharm',         'You gain the ability to use musical notes or words of power to disrupt mind-influencing effects. As an action, you can start a performance that lasts until the end of your next turn.'),
                                                                             ('Bard', 8,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Bard', 9,  'Song of Rest (d8)',     'The extra hit points gained from your Song of Rest increases to 1d8.'),
                                                                             ('Bard',10,  'Bardic Inspiration (d10)',  'Your Bardic Inspiration die changes to a d10. You gain two Magical Secrets: choose two spells from any class. They become bard spells for you.'),
                                                                             ('Bard',10,  'Expertise',            'Choose two more of your skill proficiencies. Your proficiency bonus is doubled for those checks.'),
                                                                             ('Bard',12,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Bard',13,  'Song of Rest (d10)',   'The extra hit points from Song of Rest increases to 1d10.'),
                                                                             ('Bard',14,  'Magical Secrets',      'Choose two spells from any class. They count as bard spells for you and are always prepared.'),
                                                                             ('Bard',15,  'Bardic Inspiration (d12)', 'Your Bardic Inspiration die changes to a d12.'),
                                                                             ('Bard',16,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Bard',17,  'Song of Rest (d12)',   'The extra hit points from Song of Rest increases to 1d12.'),
                                                                             ('Bard',18,  'Magical Secrets',      'Choose two more spells from any class. They count as bard spells for you.'),
                                                                             ('Bard',19,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Bard',20,  'Superior Inspiration', 'When you roll initiative and have no uses of Bardic Inspiration left, you regain one use.');

-- ── CLERIC Level 3–20 ───────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Cleric', 4,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each. Clerics also gain an additional cantrip at this level.'),
                                                                             ('Cleric', 5,  'Destroy Undead (CR 1/2)', 'When an undead of CR 1/2 or lower fails its saving throw against your Turn Undead, it is instantly destroyed.'),
                                                                             ('Cleric', 6,  'Channel Divinity (2/rest)', 'You can use Channel Divinity twice between rests. Your Divine Domain grants an additional use.'),
                                                                             ('Cleric', 8,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Cleric', 8,  'Destroy Undead (CR 1)',     'You can destroy undead of CR 1 or lower with Turn Undead.'),
                                                                             ('Cleric',10,  'Divine Intervention',       'You can call on your deity to intervene on your behalf. Roll percentile dice. If you roll a number equal to or lower than your cleric level, your deity intervenes. You can''t use this feature again for 7 days after a successful intervention.'),
                                                                             ('Cleric',11,  'Destroy Undead (CR 2)',     'You can destroy undead of CR 2 or lower with Turn Undead.'),
                                                                             ('Cleric',12,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Cleric',14,  'Destroy Undead (CR 3)',     'You can destroy undead of CR 3 or lower with Turn Undead.'),
                                                                             ('Cleric',16,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Cleric',17,  'Destroy Undead (CR 4)',     'You can destroy undead of CR 4 or lower with Turn Undead.'),
                                                                             ('Cleric',18,  'Channel Divinity (3/rest)', 'You can use Channel Divinity three times between rests.'),
                                                                             ('Cleric',19,  'Ability Score Improvement', 'You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Cleric',20,  'Divine Intervention Improvement', 'Your call for intervention succeeds automatically — no roll required.');

-- ── DRUID Level 3–20 ────────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Druid', 3,  'Wild Shape Improvement',  'You can use Wild Shape to assume a beast form with a swim speed (no flying). Max CR 1/2.'),
                                                                             ('Druid', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each. You also gain one additional druid cantrip.'),
                                                                             ('Druid', 4,  'Wild Shape Improvement',  'You can assume a beast form with a flying speed. Max CR 1.'),
                                                                             ('Druid', 6,  'Druid Circle Feature',    'Your chosen Druid Circle grants you a feature at 6th level.'),
                                                                             ('Druid', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Druid', 8,  'Wild Shape Improvement',  'Max CR for Wild Shape increases to 1/3 your druid level (rounded down).'),
                                                                             ('Druid',10,  'Druid Circle Feature',    'Your chosen Druid Circle grants you a feature at 10th level.'),
                                                                             ('Druid',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Druid',14,  'Druid Circle Feature',    'Your chosen Druid Circle grants you a feature at 14th level.'),
                                                                             ('Druid',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Druid',18,  'Timeless Body',           'The primal magic that you wield causes you to age more slowly. For every 10 years that pass, your body ages only 1 year.'),
                                                                             ('Druid',18,  'Beast Spells',            'You can cast many of your druid spells in any shape you assume using Wild Shape. You can perform the somatic and verbal components of a druid spell while in a beast shape.'),
                                                                             ('Druid',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Druid',20,  'Archdruid',               'You can use your Wild Shape an unlimited number of times. Additionally, you can ignore the verbal and somatic components of your druid spells, as well as any material components that lack a cost and aren''t consumed by a spell.');

-- ── FIGHTER Level 3–20 ──────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Fighter', 3,  'Martial Archetype',     'Choose an archetype that you strive to emulate: Champion, Battle Master, or Eldritch Knight. Your choice grants features at 3rd, 7th, 10th, 15th, and 18th level.'),
                                                                             ('Fighter', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter', 5,  'Extra Attack',          'You can attack twice whenever you take the Attack action.'),
                                                                             ('Fighter', 6,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter', 9,  'Indomitable',           'You can reroll a saving throw that you fail. If you do so, you must use the new roll. You must finish a long rest before you can use this feature again.'),
                                                                             ('Fighter',11,  'Extra Attack (2)',       'You can attack three times whenever you take the Attack action.'),
                                                                             ('Fighter',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter',13,  'Indomitable (2 uses)',  'You can use Indomitable twice between long rests.'),
                                                                             ('Fighter',14,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter',17,  'Action Surge (2 uses)', 'You can use Action Surge twice between rests. Indomitable can now be used three times between long rests.'),
                                                                             ('Fighter',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Fighter',20,  'Extra Attack (3)',       'You can attack four times whenever you take the Attack action.');

-- ── MONK Level 3–20 ─────────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Monk', 3,  'Monastic Tradition',    'Commit yourself to a monastic tradition: Way of the Open Hand, Way of Shadow, or Way of the Four Elements.'),
                                                                             ('Monk', 3,  'Deflect Missiles',      'You can use your reaction to deflect or catch the missile when you are hit by a ranged weapon attack. The damage is reduced by 1d10 + Dexterity modifier + monk level.'),
                                                                             ('Monk', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Monk', 4,  'Slow Fall',             'You can use your reaction when you fall to reduce any falling damage you take by an amount equal to five times your monk level.'),
                                                                             ('Monk', 5,  'Extra Attack',          'You can attack twice whenever you take the Attack action.'),
                                                                             ('Monk', 5,  'Stunning Strike',       'When you hit another creature with a melee weapon attack, you can spend 1 ki point to attempt a stunning strike. The target must make a Constitution saving throw. On failure, it is stunned until the end of your next turn.'),
                                                                             ('Monk', 6,  'Ki-Empowered Strikes',  'Your unarmed strikes count as magical for the purpose of overcoming resistance and immunity to nonmagical attacks and damage.'),
                                                                             ('Monk', 7,  'Evasion',               'When subjected to an effect that allows a Dexterity saving throw for half damage, you take no damage on success and only half on failure.'),
                                                                             ('Monk', 7,  'Stillness of Mind',     'You can use your action to end one effect on yourself that is causing you to be charmed or frightened.'),
                                                                             ('Monk', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Monk',10,  'Purity of Body',        'Your mastery of the ki flowing through you makes you immune to disease and poison.'),
                                                                             ('Monk',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Monk',13,  'Tongue of the Sun and Moon','You learn to touch the ki of other minds so that you understand all spoken languages and all creatures understand what you say.'),
                                                                             ('Monk',14,  'Diamond Soul',          'Your mastery of ki grants you proficiency in all saving throws. Additionally, whenever you make a saving throw and fail, you can spend 1 ki point to reroll it and take the second result.'),
                                                                             ('Monk',15,  'Timeless Body',         'Your ki sustains you so that you suffer none of the frailty of old age, and you can''t be aged magically.'),
                                                                             ('Monk',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Monk',18,  'Empty Body',            'You can use your action to spend 4 ki points to become invisible for 1 minute. During that time, you also have resistance to all damage but force damage.'),
                                                                             ('Monk',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Monk',20,  'Perfect Self',          'When you roll for initiative and have no ki points remaining, you regain 4 ki points.');

-- ── PALADIN Level 3–20 ──────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Paladin', 3,  'Sacred Oath',           'Swear the oath that binds you as a paladin: Oath of Devotion, Oath of the Ancients, or Oath of Vengeance. Your oath grants features at 3rd, 7th, 15th, and 20th level.'),
                                                                             ('Paladin', 3,  'Divine Health',         'By 3rd level, the divine magic flowing through you makes you immune to disease.'),
                                                                             ('Paladin', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Paladin', 5,  'Extra Attack',          'You can attack twice whenever you take the Attack action.'),
                                                                             ('Paladin', 6,  'Aura of Protection',    'Whenever you or a friendly creature within 10 feet of you must make a saving throw, the creature gains a bonus to the saving throw equal to your Charisma modifier (with a minimum bonus of +1). At 18th level, range increases to 30 feet.'),
                                                                             ('Paladin', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Paladin',10,  'Aura of Courage',       'You and friendly creatures within 10 feet of you can''t be frightened while you are conscious. At 18th level, range increases to 30 feet.'),
                                                                             ('Paladin',11,  'Improved Divine Smite', 'By 11th level, you are so suffused with righteous might that all your melee weapon strikes carry divine power with them. Whenever you hit a creature with a melee weapon, the creature takes an extra 1d8 radiant damage.'),
                                                                             ('Paladin',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Paladin',14,  'Cleansing Touch',       'You can use your action to end one spell on yourself or on one willing creature that you touch. You can use this feature a number of times equal to your Charisma modifier (minimum once).'),
                                                                             ('Paladin',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Paladin',18,  'Aura Improvements',     'The range of your Aura of Protection and Aura of Courage increases to 30 feet.'),
                                                                             ('Paladin',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.');

-- ── RANGER Level 3–20 ───────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Ranger', 3,  'Ranger Archetype',     'Choose an archetype to emulate: Hunter or Beast Master. Your choice grants features at 3rd, 7th, 11th, and 15th level.'),
                                                                             ('Ranger', 3,  'Primeval Awareness',   'You can use your action and expend one ranger spell slot to focus your awareness on the region around you. For 1 minute per level of the spell slot you expend, you can sense whether certain types of creatures are present.'),
                                                                             ('Ranger', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Ranger', 5,  'Extra Attack',         'You can attack twice whenever you take the Attack action.'),
                                                                             ('Ranger', 6,  'Favored Enemy Improvement','You gain one more favored enemy and one more natural environment as a favored terrain.'),
                                                                             ('Ranger', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Ranger', 8,  'Land''s Stride',       'Moving through nonmagical difficult terrain costs no extra movement. You can also pass through nonmagical plants without being slowed or taking damage from them.'),
                                                                             ('Ranger',10,  'Hide in Plain Sight',  'You can spend 1 minute creating camouflage for yourself. Once you are camouflaged in this way, you can try to hide by pressing yourself up against a solid surface that is at least as tall and wide as you are, gaining a +10 bonus to Dexterity (Stealth) checks.'),
                                                                             ('Ranger',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Ranger',14,  'Vanish',               'You can use the Hide action as a bonus action. Also, you can''t be tracked by nonmagical means, unless you choose to leave a trail.'),
                                                                             ('Ranger',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Ranger',18,  'Feral Senses',         'You gain preternatural senses that help you fight creatures you can''t see. When you attack a creature you can''t see, your inability to see it doesn''t impose disadvantage on your attack rolls against it.'),
                                                                             ('Ranger',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Ranger',20,  'Foe Slayer',           'You become an unparalleled hunter of your enemies. Once on each of your turns, you can add your Wisdom modifier to the attack roll or the damage roll of an attack you make against your favored enemy.');

-- ── ROGUE Level 3–20 ────────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Rogue', 3,  'Roguish Archetype',    'You choose an archetype that you emulate in the exercise of your rogue abilities: Thief, Assassin, or Arcane Trickster.'),
                                                                             ('Rogue', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue', 5,  'Uncanny Dodge',        'When an attacker that you can see hits you with an attack, you can use your reaction to halve the attack''s damage against you.'),
                                                                             ('Rogue', 6,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue', 6,  'Expertise',            'Choose two more of your skill proficiencies, or one more proficiency and your thieves'' tools proficiency. Your proficiency bonus is doubled for those checks.'),
                                                                             ('Rogue', 7,  'Evasion',              'When subjected to an effect that allows a Dexterity saving throw for half damage, you take no damage on success and only half on failure.'),
                                                                             ('Rogue', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue',10,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue',11,  'Reliable Talent',      'Whenever you make an ability check that lets you add your proficiency bonus, you can treat a d20 roll of 9 or lower as a 10.'),
                                                                             ('Rogue',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue',14,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue',14,  'Blindsense',           'If you are able to hear, you are aware of the location of any hidden or invisible creature within 10 feet of you.'),
                                                                             ('Rogue',15,  'Slippery Mind',        'You have acquired greater mental strength. You gain proficiency in Wisdom saving throws.'),
                                                                             ('Rogue',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue',18,  'Elusive',              'No attack roll has advantage against you while you aren''t incapacitated.'),
                                                                             ('Rogue',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Rogue',20,  'Stroke of Luck',       'You have an uncanny knack for succeeding when you need to. If your attack misses a target within range, you can turn the miss into a hit. Alternatively, if you fail an ability check, you can treat the d20 roll as a 20.');

-- ── SORCERER Level 3–20 ─────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Sorcerer', 3,  'Metamagic',             'At 3rd level, you gain the ability to twist your spells to suit your needs. You gain two Metamagic options of your choice: Careful Spell, Distant Spell, Empowered Spell, Extended Spell, Heightened Spell, Quickened Spell, Subtle Spell, or Twinned Spell.'),
                                                                             ('Sorcerer', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Sorcerer', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Sorcerer',10,  'Metamagic',             'You learn one additional Metamagic option of your choice.'),
                                                                             ('Sorcerer',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Sorcerer',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Sorcerer',17,  'Metamagic',             'You learn one additional Metamagic option of your choice.'),
                                                                             ('Sorcerer',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Sorcerer',20,  'Sorcerous Restoration', 'You regain 4 expended sorcery points whenever you finish a short rest.');

-- ── WARLOCK Level 3–20 ──────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Warlock', 3,  'Pact Boon',            'Your otherworldly patron bestows a gift upon you for your loyal service: Pact of the Chain, Pact of the Blade, or Pact of the Tome.'),
                                                                             ('Warlock', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Warlock', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Warlock',11,  'Mystic Arcanum (6th)',  'Your patron bestows upon you a magical secret called an arcanum. Choose one 6th-level spell from the warlock spell list as your arcanum. You can cast this spell once without expending a spell slot. Once per long rest.'),
                                                                             ('Warlock',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Warlock',13,  'Mystic Arcanum (7th)',  'Choose one 7th-level spell from the warlock spell list as an additional arcanum.'),
                                                                             ('Warlock',15,  'Mystic Arcanum (8th)',  'Choose one 8th-level spell from the warlock spell list as an additional arcanum.'),
                                                                             ('Warlock',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Warlock',17,  'Mystic Arcanum (9th)',  'Choose one 9th-level spell from the warlock spell list as an additional arcanum.'),
                                                                             ('Warlock',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Warlock',20,  'Eldritch Master',       'When you use your Eldritch Master feature, you can ask your patron to restore all your expended spell slots. You must spend 1 minute entreating your patron. Once you regain spell slots with this feature, you must finish a long rest before you can do so again.');

-- ── WIZARD Level 3–20 ───────────────────────────────────────────────────────
INSERT INTO class_feature (class_name, level, feature_name, description) VALUES
                                                                             ('Wizard', 3,  'Arcane Tradition Feature','Your chosen Arcane Tradition grants a feature at 3rd level (e.g. Savant: halved cost to copy spells of your school; plus one additional school-specific feature).'),
                                                                             ('Wizard', 4,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each. You also gain one additional wizard cantrip.'),
                                                                             ('Wizard', 8,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Wizard',10,  'Arcane Tradition Feature','Your chosen Arcane Tradition grants a feature at 10th level. You also gain one additional wizard cantrip.'),
                                                                             ('Wizard',12,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Wizard',14,  'Arcane Tradition Feature','Your chosen Arcane Tradition grants a feature at 14th level.'),
                                                                             ('Wizard',16,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Wizard',18,  'Spell Mastery',           'You have achieved such mastery over certain spells that you can cast them at will. Choose a 1st-level and a 2nd-level spell from your spellbook. You can cast those spells at their lowest level without expending a spell slot when you have them prepared.'),
                                                                             ('Wizard',19,  'Ability Score Improvement','You can increase one ability score by 2, or two ability scores by 1 each.'),
                                                                             ('Wizard',20,  'Signature Spells',        'You gain mastery over two powerful spells and can cast them with little effort. Choose two 3rd-level wizard spells as your signature spells. You always have these spells prepared, and you can cast each once at 3rd level without expending a spell slot per short rest.');

-- =============================================================================
-- END OF LEVEL UP EXTENSION
-- =============================================================================
-- Zusammenfassung der neuen Objekte:
--   Tables (DDL):  character_level_up, character_asi,
--                  class_asi_levels, class_spells_known, subclass_feature
--   Rows (DML):
--     class_asi_levels      : 62 Zeilen (alle 12 Klassen)
--     class_spells_known    : 140 Zeilen (alle Klassen inkl. Non-Caster)
--     class_feature         : 114 neue Zeilen (Level 3–20, alle 12 Klassen)
--     subclass_feature      : ~130 Zeilen (alle 18 Subklassen im Schema)
-- =============================================================================

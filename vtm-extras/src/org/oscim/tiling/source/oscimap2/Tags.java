/*
 * Copyright 2012 Hannes Janetzek
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.tiling.source.oscimap2;

import org.oscim.core.Tag;

public class Tags {
    public static final int MAX = 628;
    public static final int LIMIT = 1024;

    private static final String s_limited = "limited";
    private static final String s_chain = "chain";
    private static final String s_viaduct = "viaduct";
    private static final String s_department_store = "department_store";
    private static final String s_factory = "factory";
    private static final String s_recreation_ground = "recreation_ground";
    private static final String s_nature_reserve = "nature_reserve";
    private static final String s_apartment = "apartment";
    private static final String s_preserved = "preserved";
    private static final String s_stationery = "stationery";
    private static final String s_gravel = "gravel";
    private static final String s_hill = "hill";
    private static final String s_water_well = "water_well";
    private static final String s_garden = "garden";
    private static final String s_permissive = "permissive";
    private static final String s_deli = "deli";
    private static final String s_industrial_retail = "industrial;retail";
    private static final String s_city_wall = "city_wall";
    private static final String s_artwork = "artwork";
    private static final String s_chapel = "chapel";
    private static final String s_school = "school";
    private static final String s_caravan_site = "caravan_site";
    private static final String s_reservoir_watershed = "reservoir_watershed";
    private static final String s_local_authority = "local_authority";
    private static final String s_miniature_golf = "miniature_golf";
    private static final String s_bus_stop = "bus_stop";
    private static final String s_convenience = "convenience";
    private static final String s_kissing_gate = "kissing_gate";
    private static final String s_subway = "subway";
    private static final String s_cutline = "cutline";
    private static final String s_disused = "disused";
    private static final String s_clothes = "clothes";
    private static final String s_bicycle = "bicycle";
    private static final String s_meadow = "meadow";
    private static final String s_fence = "fence";
    private static final String s_video = "video";
    private static final String s_monorail = "monorail";
    private static final String s_clock = "clock";
    private static final String s_dirt = "dirt";
    private static final String s_border_control = "border_control";
    private static final String s_access = "access";
    private static final String s_public = "public";
    private static final String s_fast_food = "fast_food";
    private static final String s_transportation = "transportation";
    private static final String s_commercial = "commercial";
    private static final String s_water = "water";
    private static final String s_beacon = "beacon";
    private static final String s_trunk = "trunk";
    private static final String s_path = "path";
    private static final String s_bicycle_rental = "bicycle_rental";
    private static final String s_miniature = "miniature";
    private static final String s_car_parts = "car_parts";
    private static final String s_light_rail = "light_rail";
    private static final String s_military = "military";
    private static final String s_bog = "bog";
    private static final String s_hiking = "hiking";
    private static final String s_lift_gate = "lift_gate";
    private static final String s_private = "private";
    private static final String s_county = "county";
    private static final String s_secondary_link = "secondary_link";
    private static final String s_marker = "marker";
    private static final String s_islet = "islet";
    private static final String s_holding_position = "holding_position";
    private static final String s_tertiary = "tertiary";
    private static final String s_water_park = "water_park";
    private static final String s_stream = "stream";
    private static final String s_hospital = "hospital";
    private static final String s_destination = "destination";
    private static final String s_MDF = "MDF";
    private static final String s_sports = "sports";
    private static final String s_vineyard = "vineyard";
    private static final String s_music = "music";
    private static final String s_6 = "6";
    private static final String s_entrance = "entrance";
    private static final String s_beauty = "beauty";
    private static final String s_give_way = "give_way";
    private static final String s_kiosk = "kiosk";
    private static final String s_stone = "stone";
    private static final String s_grass_paver = "grass_paver";
    private static final String s_deciduous = "deciduous";
    private static final String s_train = "train";
    private static final String s_organic = "organic";
    private static final String s_farmyard = "farmyard";
    private static final String s_riverbank = "riverbank";
    private static final String s_doityourself = "doityourself";
    private static final String s_town = "town";
    private static final String s_dog_park = "dog_park";
    private static final String s_village_green = "village_green";
    private static final String s_tunnel = "tunnel";
    private static final String s_car = "car";
    private static final String s_roof = "roof";
    private static final String s_mall = "mall";
    private static final String s_ferry_terminal = "ferry_terminal";
    private static final String s_cave_entrance = "cave_entrance";
    private static final String s_detached = "detached";
    private static final String s_concrete_plates = "concrete:plates";
    private static final String s_public_building = "public_building";
    private static final String s_buffer_stop = "buffer_stop";
    private static final String s_lock = "lock";
    private static final String s_dolphin = "dolphin";
    private static final String s_taxiway = "taxiway";
    private static final String s_hunting_stand = "hunting_stand";
    private static final String s_estate_agent = "estate_agent";
    private static final String s_station = "station";
    private static final String s_car_repair = "car_repair";
    private static final String s_dyke = "dyke";
    private static final String s_hangar = "hangar";
    private static final String s_information = "information";
    private static final String s_1 = "1";
    private static final String s_forest = "forest";
    private static final String s_gate = "gate";
    private static final String s_beach = "beach";
    private static final String s_laundry = "laundry";
    private static final String s_speed_camera = "speed_camera";
    private static final String s_staircase = "staircase";
    private static final String s_farm = "farm";
    private static final String s_stop = "stop";
    private static final String s_bump_gate = "bump_gate";
    private static final String s_motorway = "motorway";
    private static final String s_water_tower = "water_tower";
    private static final String s_abutters = "abutters";
    private static final String s_driving_school = "driving_school";
    private static final String s_natural = "natural";
    private static final String s_orchard = "orchard";
    private static final String s_wheelchair = "wheelchair";
    private static final String s_swimming_pool = "swimming_pool";
    private static final String s_switch = "switch";
    private static final String s_block = "block";
    private static final String s_turnstile = "turnstile";
    private static final String s_camp_site = "camp_site";
    private static final String s_shoes = "shoes";
    private static final String s_reservoir = "reservoir";
    private static final String s_pebblestone = "pebblestone";
    private static final String s_stile = "stile";
    private static final String s_embassy = "embassy";
    private static final String s_postal_code = "postal_code";
    private static final String s_retaining_wall = "retaining_wall";
    private static final String s_bridleway = "bridleway";
    private static final String s_pitch = "pitch";
    private static final String s_agricultural = "agricultural";
    private static final String s_post_office = "post_office";
    private static final String s_parking_fuel = "parking;fuel";
    private static final String s_bureau_de_change = "bureau_de_change";
    private static final String s_mini_roundabout = "mini_roundabout";
    private static final String s_hov = "hov";
    private static final String s_police = "police";
    private static final String s_courthouse = "courthouse";
    private static final String s_raceway = "raceway";
    private static final String s_kindergarten = "kindergarten";
    private static final String s_attraction = "attraction";
    private static final String s_marsh = "marsh";
    private static final String s_reservoir_covered = "reservoir_covered";
    private static final String s_petroleum_well = "petroleum_well";
    private static final String s_silo = "silo";
    private static final String s_toys = "toys";
    private static final String s_apron = "apron";
    private static final String s_halt = "halt";
    private static final String s_dam = "dam";
    private static final String s_golf_course = "golf_course";
    private static final String s_detour = "detour";
    private static final String s_tree_row = "tree_row";
    private static final String s_copyshop = "copyshop";
    private static final String s_milestone = "milestone";
    private static final String s_foot = "foot";
    private static final String s_tourism = "tourism";
    private static final String s_bank = "bank";
    private static final String s_dry_cleaning = "dry_cleaning";
    private static final String s_tram = "tram";
    private static final String s_trolleybus = "trolleybus";
    private static final String s_university = "university";
    private static final String s_hampshire_gate = "hampshire_gate";
    private static final String s_embankment = "embankment";
    private static final String s_rock = "rock";
    private static final String s_crossing = "crossing";
    private static final String s_volcano = "volcano";
    private static final String s_greengrocer = "greengrocer";
    private static final String s_kerb = "kerb";
    private static final String s_waste_disposal = "waste_disposal";
    private static final String s_grave_yard = "grave_yard";
    private static final String s_coniferous = "coniferous";
    private static final String s_house = "house";
    private static final String s_books = "books";
    private static final String s_neighbourhood = "neighbourhood";
    private static final String s_hostel = "hostel";
    private static final String s_alcohol = "alcohol";
    private static final String s_restricted = "restricted";
    private static final String s_motel = "motel";
    private static final String s_sand = "sand";
    private static final String s_fishmonger = "fishmonger";
    private static final String s_fountain = "fountain";
    private static final String s_playground = "playground";
    private static final String s_7 = "7";
    private static final String s_parking_aisle = "parking_aisle";
    private static final String s_protected_area = "protected_area";
    private static final String s_electronics = "electronics";
    private static final String s_Paved = "Paved";
    private static final String s_highway = "highway";
    private static final String s_fine_gravel = "fine_gravel";
    private static final String s_barrier = "barrier";
    private static final String s_hairdresser = "hairdresser";
    private static final String s_post_box = "post_box";
    private static final String s_pub = "pub";
    private static final String s_coastline = "coastline";
    private static final String s_marina = "marina";
    private static final String s_reedbed = "reedbed";
    private static final String s_biergarten = "biergarten";
    private static final String s_dismantled = "dismantled";
    private static final String s_farmland = "farmland";
    private static final String s_yard = "yard";
    private static final String s_route = "route";
    private static final String s_atm = "atm";
    private static final String s_place = "place";
    private static final String s_bus_station = "bus_station";
    private static final String s_retail = "retail";
    private static final String s_industrial = "industrial";
    private static final String s_municipality = "municipality";
    private static final String s_primary = "primary";
    private static final String s_nursing_home = "nursing_home";
    private static final String s_florist = "florist";
    private static final String s_ditch = "ditch";
    private static final String s_national_park = "national_park";
    private static final String s_city = "city";
    private static final String s_confectionery = "confectionery";
    private static final String s_service = "service";
    private static final String s_unknown = "unknown";
    private static final String s_cycle_barrier = "cycle_barrier";
    private static final String s_elevator = "elevator";
    private static final String s_2 = "2";
    private static final String s_car_rental = "car_rental";
    private static final String s_flagpole = "flagpole";
    private static final String s_cabin = "cabin";
    private static final String s_paved = "paved";
    private static final String s_guest_house = "guest_house";
    private static final String s_mobile_phone = "mobile_phone";
    private static final String s_lot = "lot";
    private static final String s_quarry = "quarry";
    private static final String s_train_station = "train_station";
    private static final String s_hotel = "hotel";
    private static final String s_park = "park";
    private static final String s_hut = "hut";
    private static final String s_dentist = "dentist";
    private static final String s_doctors = "doctors";
    private static final String s_greenhouse = "greenhouse";
    private static final String s_11 = "11";
    private static final String s_10 = "10";
    private static final String s_theme_park = "theme_park";
    private static final String s_tree = "tree";
    private static final String s_shower = "shower";
    private static final String s_siding = "siding";
    private static final String s_aeroway = "aeroway";
    private static final String s_emergency_access_point = "emergency_access_point";
    private static final String s_watermill = "watermill";
    private static final String s_college = "college";
    private static final String s_landuse = "landuse";
    private static final String s_tracktype = "tracktype";
    private static final String s_ferry = "ferry";
    private static final String s_bridge = "bridge";
    private static final String s_vacant = "vacant";
    private static final String s_cattle_grid = "cattle_grid";
    private static final String s_brownfield = "brownfield";
    private static final String s_allotments = "allotments";
    private static final String s_alley = "alley";
    private static final String s_pedestrian = "pedestrian";
    private static final String s_borough = "borough";
    private static final String s_bare_rock = "bare_rock";
    private static final String s_motorcycle = "motorcycle";
    private static final String s_bakery = "bakery";
    private static final String s_zoo = "zoo";
    private static final String s_scree = "scree";
    private static final String s_fire_station = "fire_station";
    private static final String s_theatre = "theatre";
    private static final String s_track = "track";
    private static final String s_reinforced_slope = "reinforced_slope";
    private static final String s_slipway = "slipway";
    private static final String s_mangrove = "mangrove";
    private static final String s_aerodrome = "aerodrome";
    private static final String s_byway = "byway";
    private static final String s_metal = "metal";
    private static final String s_swamp = "swamp";
    private static final String s_construction = "construction";
    private static final String s_grassland = "grassland";
    private static final String s_shop = "shop";
    private static final String s_soakhole = "soakhole";
    private static final String s_asphalt = "asphalt";
    private static final String s_social_facility = "social_facility";
    private static final String s_isolated_dwelling = "isolated_dwelling";
    private static final String s_hamlet = "hamlet";
    private static final String s_picnic_table = "picnic_table";
    private static final String s_artificial = "artificial";
    private static final String s_earth = "earth";
    private static final String s_grit_bin = "grit_bin";
    private static final String s_ground = "ground";
    private static final String s_groyne = "groyne";
    private static final String s_office = "office";
    private static final String s_state = "state";
    private static final String s_terminal = "terminal";
    private static final String s_wood = "wood";
    private static final String s_fuel = "fuel";
    private static final String s_8 = "8";
    private static final String s_garden_centre = "garden_centre";
    private static final String s_horse_riding = "horse_riding";
    private static final String s_viewpoint = "viewpoint";
    private static final String s_designated = "designated";
    private static final String s_leisure = "leisure";
    private static final String s_waste_basket = "waste_basket";
    private static final String s_hifi = "hifi";
    private static final String s_hedge = "hedge";
    private static final String s_spur = "spur";
    private static final String s_chimney = "chimney";
    private static final String s_secondary = "secondary";
    private static final String s_rest_area = "rest_area";
    private static final String s_bar = "bar";
    private static final String s_bay = "bay";
    private static final String s_common = "common";
    private static final String s_river = "river";
    private static final String s_ruins = "ruins";
    private static final String s_terrace = "terrace";
    private static final String s_art = "art";
    private static final String s_residental = "residental";
    private static final String s_newsagent = "newsagent";
    private static final String s_turntable = "turntable";
    private static final String s_computer = "computer";
    private static final String s_wetland = "wetland";
    private static final String s_driveway = "driveway";
    private static final String s_parking = "parking";
    private static final String s_compacted = "compacted";
    private static final String s_barn = "barn";
    private static final String s_alpine_hut = "alpine_hut";
    private static final String s_wire_fence = "wire_fence";
    private static final String s_unpaved = "unpaved";
    private static final String s_dormitory = "dormitory";
    private static final String s_mud = "mud";
    private static final String s_3 = "3";
    private static final String s_semi = "semi";
    private static final String s_boundary = "boundary";
    private static final String s_field_boundary = "field_boundary";
    private static final String s_beverages = "beverages";
    private static final String s_supermarket = "supermarket";
    private static final String s_store = "store";
    private static final String s_restaurant = "restaurant";
    private static final String s_region = "region";
    private static final String s_variety_store = "variety_store";
    private static final String s_saltmarsh = "saltmarsh";
    private static final String s_landform = "landform";
    private static final String s_helipad = "helipad";
    private static final String s_railway = "railway";
    private static final String s_greenhouse_horticulture = "greenhouse_horticulture";
    private static final String s_wall = "wall";
    private static final String s_recycling = "recycling";
    private static final String s_passing_place = "passing_place";
    private static final String s_church = "church";
    private static final String s_pharmacy = "pharmacy";
    private static final String s_lighthouse = "lighthouse";
    private static final String s_platform = "platform";
    private static final String s_cinema = "cinema";
    private static final String s_political = "political";
    private static final String s_stadium = "stadium";
    private static final String s_basin = "basin";
    private static final String s_gasometer = "gasometer";
    private static final String s_bicycle_parking = "bicycle_parking";
    private static final String s_bbq = "bbq";
    private static final String s_incline_steep = "incline_steep";
    private static final String s_drinking_water = "drinking_water";
    private static final String s_living_street = "living_street";
    private static final String s_chalet = "chalet";
    private static final String s_narrow_gauge = "narrow_gauge";
    private static final String s_prison = "prison";
    private static final String s_mine = "mine";
    private static final String s_level_crossing = "level_crossing";
    private static final String s_water_works = "water_works";
    private static final String s_street_lamp = "street_lamp";
    private static final String s_main = "main";
    private static final String s_tank = "tank";
    private static final String s_abandoned = "abandoned";
    private static final String s_ski = "ski";
    private static final String s_runway = "runway";
    private static final String s_parking_space = "parking_space";
    private static final String s_dirt_sand = "dirt/sand";
    private static final String s_salt_pond = "salt_pond";
    private static final String s_hedge_bank = "hedge_bank";
    private static final String s_amenity = "amenity";
    private static final String s_telephone = "telephone";
    private static final String s_surface = "surface";
    private static final String s_travel_agency = "travel_agency";
    private static final String s_hardware = "hardware";
    private static final String s_wastewater_plant = "wastewater_plant";
    private static final String s_waterway = "waterway";
    private static final String s_butcher = "butcher";
    private static final String s_surveillance = "surveillance";
    private static final String s_Dirt_Sand = "Dirt/Sand";
    private static final String s_9 = "9";
    private static final String s_windmill = "windmill";
    private static final String s_picnic_site = "picnic_site";
    private static final String s_rail = "rail";
    private static final String s_cement = "cement";
    private static final String s_sauna = "sauna";
    private static final String s_suburb = "suburb";
    private static final String s_waterfall = "waterfall";
    private static final String s_bunker = "bunker";
    private static final String s_ice_cream = "ice_cream";
    private static final String s_culvert = "culvert";
    private static final String s_drain = "drain";
    private static final String s_dock = "dock";
    private static final String s_glasshouse = "glasshouse";
    private static final String s_no = "no";
    private static final String s_well = "well";
    private static final String s_wet_meadow = "wet_meadow";
    private static final String s_concrete = "concrete";
    private static final String s_dismount = "dismount";
    private static final String s_vending_machine = "vending_machine";
    private static final String s_oneway = "oneway";
    private static final String s_taxi = "taxi";
    private static final String s_outdoor = "outdoor";
    private static final String s_proposed = "proposed";
    private static final String s_sally_port = "sally_port";
    private static final String s_photo = "photo";
    private static final String s_plant_nursery = "plant_nursery";
    private static final String s_clinic = "clinic";
    private static final String s_fishing = "fishing";
    private static final String s_yes = "yes";
    private static final String s_turning_circle = "turning_circle";
    private static final String s_toilets = "toilets";
    private static final String s_guard_rail = "guard_rail";
    private static final String s_townhall = "townhall";
    private static final String s_community_centre = "community_centre";
    private static final String s_residential = "residential";
    private static final String s_cemetery = "cemetery";
    private static final String s_survey_point = "survey_point";
    private static final String s_bench = "bench";
    private static final String s_4 = "4";
    private static final String s_bollard = "bollard";
    private static final String s_sports_centre = "sports_centre";
    private static final String s_paving_stones_30 = "paving_stones:30";
    private static final String s_administrative = "administrative";
    private static final String s_Building = "Building";
    private static final String s_customers = "customers";
    private static final String s_emergency = "emergency";
    private static final String s_motorway_junction = "motorway_junction";
    private static final String s_grade1 = "grade1";
    private static final String s_grade3 = "grade3";
    private static final String s_grade2 = "grade2";
    private static final String s_grade5 = "grade5";
    private static final String s_grade4 = "grade4";
    private static final String s_lock_gate = "lock_gate";
    private static final String s_furniture = "furniture";
    private static final String s_place_of_worship = "place_of_worship";
    private static final String s_optician = "optician";
    private static final String s_gift = "gift";
    private static final String s_parking_entrance = "parking_entrance";
    private static final String s_garage = "garage";
    private static final String s_tram_stop = "tram_stop";
    private static final String s_steps = "steps";
    private static final String s_tower = "tower";
    private static final String s_works = "works";
    private static final String s_shed = "shed";
    private static final String s_car_sharing = "car_sharing";
    private static final String s_apartments = "apartments";
    private static final String s_spring = "spring";
    private static final String s_village = "village";
    private static final String s_library = "library";
    private static final String s_emergency_access = "emergency_access";
    private static final String s_home = "home";
    private static final String s_farm_auxiliary = "farm_auxiliary";
    private static final String s_primary_link = "primary_link";
    private static final String s_toll_booth = "toll_booth";
    private static final String s_jewelry = "jewelry";
    private static final String s_pet = "pet";
    private static final String s_veterinary = "veterinary";
    private static final String s_man_made = "man_made";
    private static final String s_motorway_link = "motorway_link";
    private static final String s_offices = "offices";
    private static final String s_power = "power";
    private static final String s_weir = "weir";
    private static final String s_unsurfaced = "unsurfaced";
    private static final String s_tertiary_link = "tertiary_link";
    private static final String s_trunk_link = "trunk_link";
    private static final String s_tyres = "tyres";
    private static final String s_paving_stones = "paving_stones";
    private static final String s_pipeline = "pipeline";
    private static final String s_census = "census";
    private static final String s_incline = "incline";
    private static final String s_footway = "footway";
    private static final String s_drive_through = "drive-through";
    private static final String s_island = "island";
    private static final String s_monitoring_station = "monitoring_station";
    private static final String s_nightclub = "nightclub";
    private static final String s_unclassified = "unclassified";
    private static final String s_aquaculture = "aquaculture";
    private static final String s_mixed = "mixed";
    private static final String s_road = "road";
    private static final String s_greenfield = "greenfield";
    private static final String s_breakwater = "breakwater";
    private static final String s_services = "services";
    private static final String s_railway_crossing = "railway_crossing";
    private static final String s_residentiel1 = "residentiel1";
    private static final String s_canal = "canal";
    private static final String s__1 = "-1";
    private static final String s_ridge = "ridge";
    private static final String s_fabric = "fabric";
    private static final String s_museum = "museum";
    private static final String s_communications_tower = "communications_tower";
    private static final String s_semi_detached = "semi-detached";
    private static final String s_conservation = "conservation";
    private static final String s_way = "way";
    private static final String s_wood_fence = "wood_fence";
    private static final String s_manufacture = "manufacture";
    private static final String s_admin_level = "admin_level";
    private static final String s_building_concrete = "building_concrete";
    private static final String s_bus = "bus";
    private static final String s_collapsed = "collapsed";
    private static final String s_ford = "ford";
    private static final String s_delivery = "delivery";
    private static final String s_garages = "garages";
    private static final String s_funeral_directors = "funeral_directors";
    private static final String s_land = "land";
    private static final String s_interlock = "interlock";
    private static final String s_reef = "reef";
    private static final String s_crane = "crane";
    private static final String s_true = "true";
    private static final String s_storage_tank = "storage_tank";
    private static final String s_official = "official";
    private static final String s_subway_entrance = "subway_entrance";
    private static final String s_mtb = "mtb";
    private static final String s_grass = "grass";
    private static final String s_marketplace = "marketplace";
    private static final String s_rapids = "rapids";
    private static final String s_car_wash = "car_wash";
    private static final String s_general = "general";
    private static final String s_cafe = "cafe";
    private static final String s_locality = "locality";
    private static final String s_glacier = "glacier";
    private static final String s_storage = "storage";
    private static final String s_cycleway = "cycleway";
    private static final String s_forestry = "forestry";
    private static final String s_field = "field";
    private static final String s_5 = "5";
    private static final String s_arts_centre = "arts_centre";
    private static final String s_warehouse = "warehouse";
    private static final String s_chemist = "chemist";
    private static final String s_pier = "pier";
    private static final String s_scrub = "scrub";
    private static final String s_shelter = "shelter";
    private static final String s_emergency_phone = "emergency_phone";
    private static final String s_tidalflat = "tidalflat";
    private static final String s_cobblestone = "cobblestone";
    private static final String s_fell = "fell";
    private static final String s_peak = "peak";
    private static final String s_charging_station = "charging_station";
    private static final String s_cliff = "cliff";
    private static final String s_building = "building";
    private static final String s_fire_hydrant = "fire_hydrant";
    private static final String s_traffic_signals = "traffic_signals";
    private static final String s_heath = "heath";
    private static final String s_landfill = "landfill";
    private static final String s_mast = "mast";
    private static final String s_boutique = "boutique";
    private static final String s_boat_storage = "boat_storage";
    private static final String s_area = "area";
    private static final String s_urban = "urban";

    // only the keys that were imported via osm2pgsql
    // FIXME add whats missing, e.g. wheelchair
    public static final String[] keys = {
            "access",
            "addr:housename",
            "addr:housenumber",
            "addr:interpolation",
            "admin_level",
            "aerialway",
            "aeroway",
            "amenity",
            "area",
            "barrier",
            "bicycle",
            "brand",
            "bridge",
            "boundary",
            "building",
            "construction",
            "covered",
            "culvert",
            "cutting",
            "denomination",
            "disused",
            "embankment",
            "foot",
            "generator:source",
            "harbour",
            "highway",
            "historic",
            "horse",
            "intermittent",
            "junction",
            "landuse",
            "layer",
            "leisure",
            "lock",
            "man_made",
            "military",
            "motorcar",
            "name",
            "natural",
            "oneway",
            "operator",
            "population",
            "power",
            "power_source",
            "place",
            "railway",
            "ref",
            "religion",
            "route",
            "service",
            "shop",
            "sport",
            "surface",
            "toll",
            "tourism",
            "tower:type",
            "tracktype",
            "tunnel",
            "water",
            "waterway",
            "wetland",
            "width",
            "wood"
    };

    // most common tags, ordered by tag count
    public static final Tag[] tags = {
            new Tag(s_building, s_yes, false),
            new Tag(s_highway, s_residential, false),
            new Tag(s_highway, s_service, false),
            new Tag(s_waterway, s_stream, false),
            new Tag(s_highway, s_unclassified, false),
            new Tag(s_highway, s_track, false),
            new Tag(s_oneway, s_yes, false),
            new Tag(s_natural, s_water, false),
            new Tag(s_highway, s_footway, false),
            new Tag(s_access, s_private, false),
            new Tag(s_highway, s_tertiary, false),
            new Tag(s_highway, s_path, false),
            new Tag(s_highway, s_secondary, false),
            new Tag(s_landuse, s_forest, false),
            new Tag(s_bridge, s_yes, false),
            new Tag(s_natural, s_tree, false),
            new Tag(s_surface, s_paved, false),
            new Tag(s_natural, s_wood, false),
            new Tag(s_highway, s_primary, false),
            new Tag(s_landuse, s_grass, false),
            new Tag(s_landuse, s_residential, false),
            new Tag(s_surface, s_unpaved, false),
            new Tag(s_highway, s_bus_stop, false),
            new Tag(s_surface, s_asphalt, false),
            new Tag(s_bicycle, s_yes, false),
            new Tag(s_amenity, s_parking, false),
            new Tag(s_place, s_locality, false),
            new Tag(s_railway, s_rail, false),
            new Tag(s_service, s_parking_aisle, false),
            new Tag(s_boundary, s_administrative, false),
            new Tag(s_building, s_house, false),
            new Tag(s_place, s_village, false),
            new Tag(s_natural, s_coastline, false),
            new Tag(s_tracktype, s_grade2, false),
            new Tag(s_oneway, s_no, false),
            new Tag(s_service, s_driveway, false),
            new Tag(s_highway, s_turning_circle, false),
            new Tag(s_place, s_hamlet, false),
            new Tag(s_natural, s_wetland, false),
            new Tag(s_tracktype, s_grade3, false),
            new Tag(s_waterway, s_river, false),
            new Tag(s_highway, s_cycleway, false),
            new Tag(s_barrier, s_fence, false),
            new Tag(s_building, s_residential, false),
            new Tag(s_amenity, s_school, false),
            new Tag(s_highway, s_crossing, false),
            new Tag(s_admin_level, s_8, false),
            new Tag(s_highway, s_trunk, false),
            new Tag(s_amenity, s_place_of_worship, false),
            new Tag(s_landuse, s_farmland, false),
            new Tag(s_tracktype, s_grade1, false),
            new Tag(s_highway, s_road, false),
            new Tag(s_landuse, s_farm, false),
            new Tag(s_surface, s_gravel, false),
            new Tag(s_landuse, s_meadow, false),
            new Tag(s_highway, s_motorway, false),
            new Tag(s_highway, s_traffic_signals, false),
            new Tag(s_building, s_hut, false),
            new Tag(s_highway, s_motorway_link, false),
            new Tag(s_tracktype, s_grade4, false),
            new Tag(s_barrier, s_gate, false),
            new Tag(s_highway, s_living_street, false),
            new Tag(s_bicycle, s_no, false),
            new Tag(s_leisure, s_pitch, false),
            new Tag(s_tunnel, s_yes, false),
            new Tag(s_surface, s_ground, false),
            new Tag(s_highway, s_steps, false),
            new Tag(s_natural, s_land, false),
            new Tag(s_man_made, s_survey_point, false),
            new Tag(s_tracktype, s_grade5, false),
            new Tag(s_waterway, s_ditch, false),
            new Tag(s_leisure, s_park, false),
            new Tag(s_amenity, s_restaurant, false),
            new Tag(s_barrier, s_wall, false),
            new Tag(s_waterway, s_riverbank, false),
            new Tag(s_amenity, s_bench, false),
            new Tag(s_building, s_garage, false),
            new Tag(s_natural, s_scrub, false),
            new Tag(s_highway, s_pedestrian, false),
            new Tag(s_natural, s_peak, false),
            new Tag(s_building, s_entrance, false),
            new Tag(s_landuse, s_reservoir, false),
            new Tag(s_access, s_yes, false),
            new Tag(s_bicycle, s_designated, false),
            new Tag(s_leisure, s_swimming_pool, false),
            new Tag(s_landuse, s_farmyard, false),
            new Tag(s_railway, s_level_crossing, false),
            new Tag(s_building, s_apartments, false),
            new Tag(s_surface, s_grass, false),
            new Tag(s_wheelchair, s_yes, false),
            new Tag(s_service, s_alley, false),
            new Tag(s_landuse, s_industrial, false),
            new Tag(s_amenity, s_fuel, false),
            new Tag(s_surface, s_dirt, false),
            new Tag(s_highway, s_trunk_link, false),
            new Tag(s_waterway, s_drain, false),
            new Tag(s_barrier, s_hedge, false),
            new Tag(s_amenity, s_grave_yard, false),
            new Tag(s_tourism, s_information, false),
            new Tag(s_shop, s_supermarket, false),
            new Tag(s_highway, s_primary_link, false),
            new Tag(s_wood, s_deciduous, false),
            new Tag(s_leisure, s_playground, false),
            new Tag(s_building, s_roof, false),
            new Tag(s_building, s_industrial, false),
            new Tag(s_amenity, s_post_box, false),
            new Tag(s_waterway, s_canal, false),
            new Tag(s_barrier, s_bollard, false),
            new Tag(s_leisure, s_garden, false),
            new Tag(s_wood, s_mixed, false),
            new Tag(s_landuse, s_cemetery, false),
            new Tag(s_landuse, s_orchard, false),
            new Tag(s_shop, s_convenience, false),
            new Tag(s_access, s_permissive, false),
            new Tag(s_surface, s_concrete, false),
            new Tag(s_surface, s_paving_stones, false),
            new Tag(s_service, s_spur, false),
            new Tag(s_building, s_garages, false),
            new Tag(s_amenity, s_bank, false),
            new Tag(s_tourism, s_hotel, false),
            new Tag(s_access, s_no, false),
            new Tag(s_amenity, s_fast_food, false),
            new Tag(s_man_made, s_pier, false),
            new Tag(s_amenity, s_kindergarten, false),
            new Tag(s_access, s_agricultural, false),
            new Tag(s_surface, s_cobblestone, false),
            new Tag(s_wheelchair, s_no, false),
            new Tag(s_amenity, s_cafe, false),
            new Tag(s_amenity, s_hospital, false),
            new Tag(s_amenity, s_post_office, false),
            new Tag(s_amenity, s_public_building, false),
            new Tag(s_amenity, s_recycling, false),
            new Tag(s_highway, s_street_lamp, false),
            new Tag(s_man_made, s_tower, false),
            new Tag(s_waterway, s_dam, false),
            new Tag(s_amenity, s_pub, false),
            new Tag(s_wood, s_coniferous, false),
            new Tag(s_access, s_destination, false),
            new Tag(s_admin_level, s_6, false),
            new Tag(s_landuse, s_commercial, false),
            new Tag(s_amenity, s_pharmacy, false),
            new Tag(s_railway, s_abandoned, false),
            new Tag(s_service, s_yard, false),
            new Tag(s_place, s_island, false),
            new Tag(s_oneway, s__1, false),
            new Tag(s_landuse, s_quarry, false),
            new Tag(s_landuse, s_vineyard, false),
            new Tag(s_highway, s_motorway_junction, false),
            new Tag(s_railway, s_station, false),
            new Tag(s_landuse, s_allotments, false),
            new Tag(s_barrier, s_lift_gate, false),
            new Tag(s_admin_level, s_10, false),
            new Tag(s_amenity, s_telephone, false),
            new Tag(s_place, s_town, false),
            new Tag(s_man_made, s_cutline, false),
            new Tag(s_place, s_suburb, false),
            new Tag(s_aeroway, s_taxiway, false),
            new Tag(s_wheelchair, s_limited, false),
            new Tag(s_highway, s_secondary_link, false),
            new Tag(s_leisure, s_sports_centre, false),
            new Tag(s_amenity, s_bicycle_parking, false),
            new Tag(s_surface, s_sand, false),
            new Tag(s_highway, s_stop, false),
            new Tag(s_man_made, s_works, false),
            new Tag(s_landuse, s_retail, false),
            new Tag(s_amenity, s_fire_station, false),
            new Tag(s_service, s_siding, false),
            new Tag(s_amenity, s_toilets, false),
            new Tag(s_bench, s_yes, false),
            new Tag(s_oneway, s_1, false),
            new Tag(s_surface, s_compacted, false),
            new Tag(s_landuse, s_basin, false),
            new Tag(s_amenity, s_police, false),
            new Tag(s_railway, s_tram, false),
            new Tag(s_route, s_road, false),
            new Tag(s_natural, s_cliff, false),
            new Tag(s_highway, s_construction, false),
            new Tag(s_aeroway, s_aerodrome, false),
            new Tag(s_entrance, s_yes, false),
            new Tag(s_man_made, s_storage_tank, false),
            new Tag(s_amenity, s_atm, false),
            new Tag(s_tourism, s_attraction, false),
            new Tag(s_route, s_bus, false),
            new Tag(s_shop, s_bakery, false),
            new Tag(s_tourism, s_viewpoint, false),
            new Tag(s_amenity, s_swimming_pool, false),
            new Tag(s_natural, s_beach, false),
            new Tag(s_tourism, s_picnic_site, false),
            new Tag(s_oneway, s_true, false),
            new Tag(s_highway, s_bridleway, false),
            new Tag(s_tourism, s_camp_site, false),
            new Tag(s_abutters, s_residential, false),
            new Tag(s_leisure, s_nature_reserve, false),
            new Tag(s_amenity, s_drinking_water, false),
            new Tag(s_shop, s_clothes, false),
            new Tag(s_natural, s_heath, false),
            new Tag(s_highway, s_mini_roundabout, false),
            new Tag(s_landuse, s_construction, false),
            new Tag(s_amenity, s_waste_basket, false),
            new Tag(s_railway, s_platform, false),
            new Tag(s_amenity, s_townhall, false),
            new Tag(s_shop, s_hairdresser, false),
            new Tag(s_amenity, s_shelter, false),
            new Tag(s_admin_level, s_9, false),
            new Tag(s_building, s_farm_auxiliary, false),
            new Tag(s_amenity, s_library, false),
            new Tag(s_building, s_detached, false),
            new Tag(s_admin_level, s_4, false),
            new Tag(s_landuse, s_village_green, false),
            new Tag(s_barrier, s_stile, false),
            new Tag(s_landuse, s_garages, false),
            new Tag(s_amenity, s_bar, false),
            new Tag(s_railway, s_buffer_stop, false),
            new Tag(s_wetland, s_marsh, false),
            new Tag(s_tourism, s_museum, false),
            new Tag(s_barrier, s_cycle_barrier, false),
            new Tag(s_route, s_bicycle, false),
            new Tag(s_railway, s_tram_stop, false),
            new Tag(s_amenity, s_parking_space, false),
            new Tag(s_barrier, s_retaining_wall, false),
            new Tag(s_landuse, s_recreation_ground, false),
            new Tag(s_amenity, s_university, false),
            new Tag(s_highway, s_tertiary_link, false),
            new Tag(s_building, s_terrace, false),
            new Tag(s_shop, s_car_repair, false),
            new Tag(s_amenity, s_hunting_stand, false),
            new Tag(s_amenity, s_fountain, false),
            new Tag(s_man_made, s_pipeline, false),
            new Tag(s_wetland, s_swamp, false),
            new Tag(s_shop, s_car, false),
            new Tag(s_bench, s_no, false),
            new Tag(s_tunnel, s_culvert, false),
            new Tag(s_building, s_school, false),
            new Tag(s_barrier, s_entrance, false),
            new Tag(s_railway, s_disused, false),
            new Tag(s_railway, s_crossing, false),
            new Tag(s_building, s_church, false),
            new Tag(s_amenity, s_social_facility, false),
            new Tag(s_natural, s_bay, false),
            new Tag(s_shop, s_kiosk, false),
            new Tag(s_amenity, s_vending_machine, false),
            new Tag(s_route, s_hiking, false),
            new Tag(s_natural, s_spring, false),
            new Tag(s_leisure, s_common, false),
            new Tag(s_railway, s_switch, false),
            new Tag(s_waterway, s_rapids, false),
            new Tag(s_admin_level, s_7, false),
            new Tag(s_leisure, s_stadium, false),
            new Tag(s_leisure, s_track, false),
            new Tag(s_place, s_isolated_dwelling, false),
            new Tag(s_place, s_islet, false),
            new Tag(s_waterway, s_weir, false),
            new Tag(s_amenity, s_doctors, false),
            new Tag(s_access, s_designated, false),
            new Tag(s_landuse, s_conservation, false),
            new Tag(s_waterway, s_artificial, false),
            new Tag(s_amenity, s_bus_station, false),
            new Tag(s_leisure, s_golf_course, false),
            new Tag(s_shop, s_doityourself, false),
            new Tag(s_building, s_service, false),
            new Tag(s_tourism, s_guest_house, false),
            new Tag(s_aeroway, s_runway, false),
            new Tag(s_place, s_city, false),
            new Tag(s_railway, s_subway, false),
            new Tag(s_man_made, s_wastewater_plant, false),
            new Tag(s_building, s_commercial, false),
            new Tag(s_railway, s_halt, false),
            new Tag(s_amenity, s_emergency_phone, false),
            new Tag(s_building, s_retail, false),
            new Tag(s_barrier, s_block, false),
            new Tag(s_leisure, s_recreation_ground, false),
            new Tag(s_access, s_forestry, false),
            new Tag(s_amenity, s_college, false),
            new Tag(s_highway, s_platform, false),
            new Tag(s_access, s_unknown, false),
            new Tag(s_man_made, s_water_tower, false),
            new Tag(s_surface, s_pebblestone, false),
            new Tag(s_bridge, s_viaduct, false),
            new Tag(s_shop, s_butcher, false),
            new Tag(s_shop, s_florist, false),
            new Tag(s_boundary, s_landuse, false),
            new Tag(s_aeroway, s_helipad, false),
            new Tag(s_building, s_hangar, false),
            new Tag(s_natural, s_glacier, false),
            new Tag(s_highway, s_proposed, false),
            new Tag(s_shop, s_mall, false),
            new Tag(s_barrier, s_toll_booth, false),
            new Tag(s_amenity, s_fire_hydrant, false),
            new Tag(s_building, s_manufacture, false),
            new Tag(s_building, s_farm, false),
            new Tag(s_surface, s_wood, false),
            new Tag(s_amenity, s_car_wash, false),
            new Tag(s_amenity, s_dentist, false),
            new Tag(s_natural, s_marsh, false),
            new Tag(s_man_made, s_surveillance, false),
            new Tag(s_shop, s_bicycle, false),
            new Tag(s_route, s_foot, false),
            new Tag(s_amenity, s_theatre, false),
            new Tag(s_building, s_office, false),
            new Tag(s_railway, s_light_rail, false),
            new Tag(s_man_made, s_petroleum_well, false),
            new Tag(s_amenity, s_taxi, false),
            new Tag(s_building, s_greenhouse, false),
            new Tag(s_landuse, s_brownfield, false),
            new Tag(s_bicycle, s_permissive, false),
            new Tag(s_admin_level, s_2, false),
            new Tag(s_aeroway, s_apron, false),
            new Tag(s_building, s_cabin, false),
            new Tag(s_amenity, s_cinema, false),
            new Tag(s_access, s_customers, false),
            new Tag(s_tourism, s_motel, false),
            new Tag(s_railway, s_narrow_gauge, false),
            new Tag(s_amenity, s_marketplace, false),
            new Tag(s_shop, s_furniture, false),
            new Tag(s_entrance, s_staircase, false),
            new Tag(s_tourism, s_artwork, false),
            new Tag(s_natural, s_grassland, false),
            new Tag(s_shop, s_books, false),
            new Tag(s_admin_level, s_5, false),
            new Tag(s_man_made, s_groyne, false),
            new Tag(s_waterway, s_lock_gate, false),
            new Tag(s_highway, s_emergency_access_point, false),
            new Tag(s_natural, s_sand, false),
            new Tag(s_landuse, s_military, false),
            new Tag(s_boundary, s_protected_area, false),
            new Tag(s_amenity, s_community_centre, false),
            new Tag(s_barrier, s_kissing_gate, false),
            new Tag(s_highway, s_speed_camera, false),
            new Tag(s_boundary, s_national_park, false),
            new Tag(s_railway, s_subway_entrance, false),
            new Tag(s_man_made, s_silo, false),
            new Tag(s_shop, s_alcohol, false),
            new Tag(s_highway, s_give_way, false),
            new Tag(s_leisure, s_slipway, false),
            new Tag(s_shop, s_electronics, false),
            new Tag(s_bicycle, s_dismount, false),
            new Tag(s_leisure, s_marina, false),
            new Tag(s_entrance, s_main, false),
            new Tag(s_boundary, s_postal_code, false),
            new Tag(s_landuse, s_greenhouse_horticulture, false),
            new Tag(s_highway, s_milestone, false),
            new Tag(s_natural, s_cave_entrance, false),
            new Tag(s_landuse, s_landfill, false),
            new Tag(s_shop, s_chemist, false),
            new Tag(s_shop, s_shoes, false),
            new Tag(s_barrier, s_cattle_grid, false),
            new Tag(s_landuse, s_railway, false),
            new Tag(s_tourism, s_hostel, false),
            new Tag(s_tourism, s_chalet, false),
            new Tag(s_place, s_county, false),
            new Tag(s_shop, s_department_store, false),
            new Tag(s_highway, s_ford, false),
            new Tag(s_natural, s_scree, false),
            new Tag(s_landuse, s_greenfield, false),
            new Tag(s_amenity, s_nursing_home, false),
            new Tag(s_barrier, s_wire_fence, false),
            new Tag(s_access, s_restricted, false),
            new Tag(s_man_made, s_reservoir_covered, false),
            new Tag(s_amenity, s_bicycle_rental, false),
            new Tag(s_man_made, s_MDF, false),
            new Tag(s_man_made, s_water_well, false),
            new Tag(s_landuse, s_field, false),
            new Tag(s_landuse, s_wood, false),
            new Tag(s_shop, s_hardware, false),
            new Tag(s_tourism, s_alpine_hut, false),
            new Tag(s_natural, s_tree_row, false),
            new Tag(s_tourism, s_caravan_site, false),
            new Tag(s_bridge, s_no, false),
            new Tag(s_wetland, s_bog, false),
            new Tag(s_amenity, s_courthouse, false),
            new Tag(s_route, s_ferry, false),
            new Tag(s_barrier, s_city_wall, false),
            new Tag(s_amenity, s_veterinary, false),
            new Tag(s_shop, s_jewelry, false),
            new Tag(s_building, s_transportation, false),
            new Tag(s_amenity, s_arts_centre, false),
            new Tag(s_bicycle, s_official, false),
            new Tag(s_shop, s_optician, false),
            new Tag(s_shop, s_yes, false),
            new Tag(s_building, s_collapsed, false),
            new Tag(s_shop, s_garden_centre, false),
            new Tag(s_man_made, s_chimney, false),
            new Tag(s_man_made, s_mine, false),
            new Tag(s_bench, s_unknown, false),
            new Tag(s_railway, s_preserved, false),
            new Tag(s_building, s_public, false),
            new Tag(s_amenity, s_ferry_terminal, false),
            new Tag(s_highway, s_raceway, false),
            new Tag(s_natural, s_rock, false),
            new Tag(s_tunnel, s_no, false),
            new Tag(s_building, s_university, false),
            new Tag(s_shop, s_beverages, false),
            new Tag(s_amenity, s_waste_disposal, false),
            new Tag(s_building, s_warehouse, false),
            new Tag(s_leisure, s_water_park, false),
            new Tag(s_shop, s_gift, false),
            new Tag(s_place, s_farm, false),
            new Tag(s_wetland, s_tidalflat, false),
            new Tag(s_waterway, s_waterfall, false),
            new Tag(s_man_made, s_dolphin, false),
            new Tag(s_service, s_drive_through, false),
            new Tag(s_amenity, s_nightclub, false),
            new Tag(s_building, s_shed, false),
            new Tag(s_shop, s_greengrocer, false),
            new Tag(s_natural, s_fell, false),
            new Tag(s_wetland, s_wet_meadow, false),
            new Tag(s_aeroway, s_gate, false),
            new Tag(s_shop, s_computer, false),
            new Tag(s_man_made, s_lighthouse, false),
            new Tag(s_wetland, s_reedbed, false),
            new Tag(s_man_made, s_breakwater, false),
            new Tag(s_surface, s_Dirt_Sand, false),
            new Tag(s_barrier, s_ditch, false),
            new Tag(s_barrier, s_yes, false),
            new Tag(s_amenity, s_biergarten, false),
            new Tag(s_shop, s_mobile_phone, false),
            new Tag(s_route, s_mtb, false),
            new Tag(s_amenity, s_grit_bin, false),
            new Tag(s_amenity, s_bbq, false),
            new Tag(s_shop, s_sports, false),
            new Tag(s_barrier, s_wood_fence, false),
            new Tag(s_entrance, s_home, false),
            new Tag(s_shop, s_laundry, false),
            new Tag(s_man_made, s_gasometer, false),
            new Tag(s_barrier, s_embankment, false),
            new Tag(s_shop, s_toys, false),
            new Tag(s_wetland, s_saltmarsh, false),
            new Tag(s_waterway, s_soakhole, false),
            new Tag(s_shop, s_travel_agency, false),
            new Tag(s_man_made, s_water_works, false),
            new Tag(s_route, s_railway, false),
            new Tag(s_amenity, s_prison, false),
            new Tag(s_highway, s_rest_area, false),
            new Tag(s_shop, s_stationery, false),
            new Tag(s_admin_level, s_11, false),
            new Tag(s_building, s_train_station, false),
            new Tag(s_building, s_storage_tank, false),
            new Tag(s_man_made, s_windmill, false),
            new Tag(s_shop, s_beauty, false),
            new Tag(s_building, s_semi, false),
            new Tag(s_highway, s_services, false),
            new Tag(s_bicycle, s_private, false),
            new Tag(s_route, s_ski, false),
            new Tag(s_service, s_emergency_access, false),
            new Tag(s_building, s_factory, false),
            new Tag(s_man_made, s_reinforced_slope, false),
            new Tag(s_amenity, s_car_sharing, false),
            new Tag(s_surface, s_earth, false),
            new Tag(s_shop, s_hifi, false),
            new Tag(s_amenity, s_car_rental, false),
            new Tag(s_barrier, s_hedge_bank, false),
            new Tag(s_shop, s_confectionery, false),
            new Tag(s_aeroway, s_terminal, false),
            new Tag(s_highway, s_passing_place, false),
            new Tag(s_building, s_building, false),
            new Tag(s_man_made, s_dyke, false),
            new Tag(s_building, s_construction, false),
            new Tag(s_building, s_shop, false),
            new Tag(s_natural, s_reef, false),
            new Tag(s_landuse, s_aquaculture, false),
            new Tag(s_shop, s_dry_cleaning, false),
            new Tag(s_amenity, s_embassy, false),
            new Tag(s_shop, s_newsagent, false),
            new Tag(s_landuse, s_salt_pond, false),
            new Tag(s_railway, s_spur, false),
            new Tag(s_wheelchair, s_unknown, false),
            new Tag(s_tourism, s_zoo, false),
            new Tag(s_man_made, s_waterway, false),
            new Tag(s_surface, s_fine_gravel, false),
            new Tag(s_shop, s_motorcycle, false),
            new Tag(s_building, s_Building, false),
            new Tag(s_railway, s_construction, false),
            new Tag(s_place, s_neighbourhood, false),
            new Tag(s_route, s_train, false),
            new Tag(s_building, s_no, false),
            new Tag(s_natural, s_mud, false),
            new Tag(s_place, s_region, false),
            new Tag(s_landuse, s_reservoir_watershed, false),
            new Tag(s_boundary, s_marker, false),
            new Tag(s_man_made, s_beacon, false),
            new Tag(s_shop, s_outdoor, false),
            new Tag(s_access, s_public, false),
            new Tag(s_abutters, s_industrial, false),
            new Tag(s_building, s_barn, false),
            new Tag(s_leisure, s_picnic_table, false),
            new Tag(s_building, s_hospital, false),
            new Tag(s_access, s_official, false),
            new Tag(s_shop, s_variety_store, false),
            new Tag(s_man_made, s_crane, false),
            new Tag(s_amenity, s_parking_fuel, false),
            new Tag(s_route, s_tram, false),
            new Tag(s_tourism, s_theme_park, false),
            new Tag(s_shop, s_pet, false),
            new Tag(s_building, s_kindergarten, false),
            new Tag(s_man_made, s_storage, false),
            new Tag(s_man_made, s_mast, false),
            new Tag(s_amenity, s_parking_entrance, false),
            new Tag(s_amenity, s_clock, false),
            new Tag(s_landuse, s_industrial_retail, false),
            new Tag(s_shop, s_video, false),
            new Tag(s_access, s_delivery, false),
            new Tag(s_amenity, s_driving_school, false),
            new Tag(s_service, s_yes, false),
            new Tag(s_natural, s_bare_rock, false),
            new Tag(s_building, s_chapel, false),
            new Tag(s_natural, s_volcano, false),
            new Tag(s_waterway, s_dock, false),
            new Tag(s_building, s_dormitory, false),
            new Tag(s_amenity, s_boat_storage, false),
            new Tag(s_man_made, s_tank, false),
            new Tag(s_man_made, s_flagpole, false),
            new Tag(s_surface, s_grass_paver, false),
            new Tag(s_shop, s_organic, false),
            new Tag(s_natural, s_landform, false),
            new Tag(s_highway, s_unsurfaced, false),
            new Tag(s_route, s_power, false),
            new Tag(s_surface, s_mud, false),
            new Tag(s_building, s_building_concrete, false),
            new Tag(s_abutters, s_retail, false),
            new Tag(s_building, s_store, false),
            new Tag(s_shop, s_vacant, false),
            new Tag(s_leisure, s_miniature_golf, false),
            new Tag(s_man_made, s_monitoring_station, false),
            new Tag(s_natural, s_waterfall, false),
            new Tag(s_aeroway, s_hangar, false),
            new Tag(s_shop, s_boutique, false),
            new Tag(s_route, s_detour, false),
            new Tag(s_building, s_way, false),
            new Tag(s_railway, s_stop, false),
            new Tag(s_amenity, s_ice_cream, false),
            new Tag(s_building, s_storage, false),
            new Tag(s_shop, s_car_parts, false),
            new Tag(s_natural, s_ridge, false),
            new Tag(s_shop, s_tyres, false),
            new Tag(s_railway, s_dismantled, false),
            new Tag(s_amenity, s_shop, false),
            new Tag(s_landuse, s_plant_nursery, false),
            new Tag(s_building, s_residentiel1, false),
            new Tag(s_barrier, s_field_boundary, false),
            new Tag(s_barrier, s_border_control, false),
            new Tag(s_surface, s_Paved, false),
            new Tag(s_barrier, s_sally_port, false),
            new Tag(s_amenity, s_bureau_de_change, false),
            new Tag(s_leisure, s_fishing, false),
            new Tag(s_amenity, s_charging_station, false),
            new Tag(s_building, s_supermarket, false),
            new Tag(s_highway, s_stile, false),
            new Tag(s_amenity, s_sauna, false),
            new Tag(s_place, s_municipality, false),
            new Tag(s_building, s_hotel, false),
            new Tag(s_surface, s_metal, false),
            new Tag(s_highway, s_incline_steep, false),
            new Tag(s_shop, s_estate_agent, false),
            new Tag(s_natural, s_grass, false),
            new Tag(s_shop, s_pharmacy, false),
            new Tag(s_surface, s_concrete_plates, false),
            new Tag(s_shop, s_copyshop, false),
            new Tag(s_surface, s_paving_stones_30, false),
            new Tag(s_surface, s_interlock, false),
            new Tag(s_access, s_hov, false),
            new Tag(s_highway, s_elevator, false),
            new Tag(s_boundary, s_local_authority, false),
            new Tag(s_man_made, s_communications_tower, false),
            new Tag(s_shop, s_deli, false),
            new Tag(s_barrier, s_turnstile, false),
            new Tag(s_building, s_offices, false),
            new Tag(s_building, s_bunker, false),
            new Tag(s_natural, s_stone, false),
            new Tag(s_railway, s_railway_crossing, false),
            new Tag(s_leisure, s_dog_park, false),
            new Tag(s_building, s_semi_detached, false),
            new Tag(s_man_made, s_watermill, false),
            new Tag(s_route, s_trolleybus, false),
            new Tag(s_admin_level, s_3, false),
            new Tag(s_building, s_block, false),
            new Tag(s_barrier, s_guard_rail, false),
            new Tag(s_bicycle, s_unknown, false),
            new Tag(s_highway, s_abandoned, false),
            new Tag(s_surface, s_dirt_sand, false),
            new Tag(s_barrier, s_chain, false),
            new Tag(s_barrier, s_bump_gate, false),
            new Tag(s_building, s_residental, false),
            new Tag(s_surface, s_cement, false),
            new Tag(s_man_made, s_embankment, false),
            new Tag(s_building, s_ruins, false),
            new Tag(s_highway, s_incline, false),
            new Tag(s_abutters, s_commercial, false),
            new Tag(s_barrier, s_hampshire_gate, false),
            new Tag(s_shop, s_music, false),
            new Tag(s_shop, s_funeral_directors, false),
            new Tag(s_wetland, s_mangrove, false),
            new Tag(s_place, s_borough, false),
            new Tag(s_building, s_apartment, false),
            new Tag(s_boundary, s_census, false),
            new Tag(s_barrier, s_kerb, false),
            new Tag(s_building, s_glasshouse, false),
            new Tag(s_aeroway, s_holding_position, false),
            new Tag(s_shop, s_general, false),
            new Tag(s_building, s_tank, false),
            new Tag(s_railway, s_monorail, false),
            new Tag(s_service, s_parking, false),
            new Tag(s_place, s_state, false),
            new Tag(s_railway, s_proposed, false),
            new Tag(s_shop, s_art, false),
            new Tag(s_natural, s_hill, false),
            new Tag(s_railway, s_turntable, false),
            new Tag(s_tourism, s_cabin, false),
            new Tag(s_shop, s_photo, false),
            new Tag(s_boundary, s_lot, false),
            new Tag(s_shop, s_fishmonger, false),
            new Tag(s_amenity, s_clinic, false),
            new Tag(s_boundary, s_political, false),
            new Tag(s_man_made, s_well, false),
            new Tag(s_highway, s_byway, false),
            new Tag(s_leisure, s_horse_riding, false),
            new Tag(s_service, s_bus, false),
            new Tag(s_building, s_tower, false),
            new Tag(s_entrance, s_service, false),
            new Tag(s_shop, s_fabric, false),
            new Tag(s_railway, s_miniature, false),
            new Tag(s_abutters, s_mixed, false),
            new Tag(s_surface, s_stone, false),
            new Tag(s_access, s_emergency, false),
            new Tag(s_landuse, s_mine, false),
            new Tag(s_amenity, s_shower, false),
            new Tag(s_waterway, s_lock, false),
            new Tag(s_area, s_yes, false),
            new Tag(s_landuse, s_urban, false),
    };
}

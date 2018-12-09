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
package org.oscim.tiling.source.oscimap;

import org.oscim.core.Tag;

public class Tags {
    public static final int MAX = 654;
    public static final int LIMIT = 1024;

    private static final String s_limited = "limited".intern();
    private static final String s_chain = "chain".intern();
    private static final String s_viaduct = "viaduct".intern();
    private static final String s_department_store = "department_store".intern();
    private static final String s_factory = "factory".intern();
    private static final String s_recreation_ground = "recreation_ground".intern();
    private static final String s_nature_reserve = "nature_reserve".intern();
    private static final String s_apartment = "apartment".intern();
    private static final String s_preserved = "preserved".intern();
    private static final String s_stationery = "stationery".intern();
    private static final String s_gravel = "gravel".intern();
    private static final String s_hill = "hill".intern();
    private static final String s_water_well = "water_well".intern();
    private static final String s_garden = "garden".intern();
    private static final String s_permissive = "permissive".intern();
    private static final String s_deli = "deli".intern();
    private static final String s_industrial_retail = "industrial;retail".intern();
    private static final String s_city_wall = "city_wall".intern();
    private static final String s_artwork = "artwork".intern();
    private static final String s_chapel = "chapel".intern();
    private static final String s_school = "school".intern();
    private static final String s_caravan_site = "caravan_site".intern();
    private static final String s_reservoir_watershed = "reservoir_watershed".intern();
    private static final String s_local_authority = "local_authority".intern();
    private static final String s_miniature_golf = "miniature_golf".intern();
    private static final String s_bus_stop = "bus_stop".intern();
    private static final String s_convenience = "convenience".intern();
    private static final String s_kissing_gate = "kissing_gate".intern();
    private static final String s_subway = "subway".intern();
    private static final String s_cutline = "cutline".intern();
    private static final String s_disused = "disused".intern();
    private static final String s_clothes = "clothes".intern();
    private static final String s_bicycle = "bicycle".intern();
    private static final String s_meadow = "meadow".intern();
    private static final String s_fence = "fence".intern();
    private static final String s_video = "video".intern();
    private static final String s_monorail = "monorail".intern();
    private static final String s_clock = "clock".intern();
    private static final String s_dirt = "dirt".intern();
    private static final String s_border_control = "border_control".intern();
    private static final String s_access = "access".intern();
    private static final String s_public = "public".intern();
    private static final String s_fast_food = "fast_food".intern();
    private static final String s_transportation = "transportation".intern();
    private static final String s_commercial = "commercial".intern();
    private static final String s_water = "water".intern();
    private static final String s_beacon = "beacon".intern();
    private static final String s_trunk = "trunk".intern();
    private static final String s_path = "path".intern();
    private static final String s_bicycle_rental = "bicycle_rental".intern();
    private static final String s_miniature = "miniature".intern();
    private static final String s_car_parts = "car_parts".intern();
    private static final String s_light_rail = "light_rail".intern();
    private static final String s_military = "military".intern();
    private static final String s_bog = "bog".intern();
    private static final String s_hiking = "hiking".intern();
    private static final String s_lift_gate = "lift_gate".intern();
    private static final String s_private = "private".intern();
    private static final String s_county = "county".intern();
    private static final String s_secondary_link = "secondary_link".intern();
    private static final String s_marker = "marker".intern();
    private static final String s_islet = "islet".intern();
    private static final String s_holding_position = "holding_position".intern();
    private static final String s_tertiary = "tertiary".intern();
    private static final String s_water_park = "water_park".intern();
    private static final String s_stream = "stream".intern();
    private static final String s_hospital = "hospital".intern();
    private static final String s_destination = "destination".intern();
    private static final String s_MDF = "MDF".intern();
    private static final String s_sports = "sports".intern();
    private static final String s_vineyard = "vineyard".intern();
    private static final String s_music = "music".intern();
    private static final String s_6 = "6".intern();
    private static final String s_entrance = "entrance".intern();
    private static final String s_beauty = "beauty".intern();
    private static final String s_give_way = "give_way".intern();
    private static final String s_kiosk = "kiosk".intern();
    private static final String s_stone = "stone".intern();
    private static final String s_grass_paver = "grass_paver".intern();
    private static final String s_deciduous = "deciduous".intern();
    private static final String s_train = "train".intern();
    private static final String s_organic = "organic".intern();
    private static final String s_farmyard = "farmyard".intern();
    private static final String s_riverbank = "riverbank".intern();
    private static final String s_doityourself = "doityourself".intern();
    private static final String s_town = "town".intern();
    private static final String s_dog_park = "dog_park".intern();
    private static final String s_village_green = "village_green".intern();
    private static final String s_tunnel = "tunnel".intern();
    private static final String s_car = "car".intern();
    private static final String s_roof = "roof".intern();
    private static final String s_mall = "mall".intern();
    private static final String s_ferry_terminal = "ferry_terminal".intern();
    private static final String s_cave_entrance = "cave_entrance".intern();
    private static final String s_detached = "detached".intern();
    private static final String s_concrete_plates = "concrete:plates".intern();
    private static final String s_public_building = "public_building".intern();
    private static final String s_buffer_stop = "buffer_stop".intern();
    private static final String s_lock = "lock".intern();
    private static final String s_dolphin = "dolphin".intern();
    private static final String s_taxiway = "taxiway".intern();
    private static final String s_hunting_stand = "hunting_stand".intern();
    private static final String s_estate_agent = "estate_agent".intern();
    private static final String s_station = "station".intern();
    private static final String s_car_repair = "car_repair".intern();
    private static final String s_dyke = "dyke".intern();
    private static final String s_hangar = "hangar".intern();
    private static final String s_information = "information".intern();
    private static final String s_1 = "1".intern();
    private static final String s_forest = "forest".intern();
    private static final String s_gate = "gate".intern();
    private static final String s_beach = "beach".intern();
    private static final String s_laundry = "laundry".intern();
    private static final String s_speed_camera = "speed_camera".intern();
    private static final String s_staircase = "staircase".intern();
    private static final String s_farm = "farm".intern();
    private static final String s_stop = "stop".intern();
    private static final String s_bump_gate = "bump_gate".intern();
    private static final String s_motorway = "motorway".intern();
    private static final String s_water_tower = "water_tower".intern();
    private static final String s_abutters = "abutters".intern();
    private static final String s_driving_school = "driving_school".intern();
    private static final String s_natural = "natural".intern();
    private static final String s_orchard = "orchard".intern();
    private static final String s_wheelchair = "wheelchair".intern();
    private static final String s_swimming_pool = "swimming_pool".intern();
    private static final String s_switch = "switch".intern();
    private static final String s_block = "block".intern();
    private static final String s_turnstile = "turnstile".intern();
    private static final String s_camp_site = "camp_site".intern();
    private static final String s_shoes = "shoes".intern();
    private static final String s_reservoir = "reservoir".intern();
    private static final String s_pebblestone = "pebblestone".intern();
    private static final String s_stile = "stile".intern();
    private static final String s_embassy = "embassy".intern();
    private static final String s_postal_code = "postal_code".intern();
    private static final String s_retaining_wall = "retaining_wall".intern();
    private static final String s_bridleway = "bridleway".intern();
    private static final String s_pitch = "pitch".intern();
    private static final String s_agricultural = "agricultural".intern();
    private static final String s_post_office = "post_office".intern();
    private static final String s_parking_fuel = "parking;fuel".intern();
    private static final String s_bureau_de_change = "bureau_de_change".intern();
    private static final String s_mini_roundabout = "mini_roundabout".intern();
    private static final String s_hov = "hov".intern();
    private static final String s_police = "police".intern();
    private static final String s_courthouse = "courthouse".intern();
    private static final String s_raceway = "raceway".intern();
    private static final String s_kindergarten = "kindergarten".intern();
    private static final String s_attraction = "attraction".intern();
    private static final String s_marsh = "marsh".intern();
    private static final String s_reservoir_covered = "reservoir_covered".intern();
    private static final String s_petroleum_well = "petroleum_well".intern();
    private static final String s_silo = "silo".intern();
    private static final String s_toys = "toys".intern();
    private static final String s_apron = "apron".intern();
    private static final String s_halt = "halt".intern();
    private static final String s_dam = "dam".intern();
    private static final String s_golf_course = "golf_course".intern();
    private static final String s_detour = "detour".intern();
    private static final String s_tree_row = "tree_row".intern();
    private static final String s_copyshop = "copyshop".intern();
    private static final String s_milestone = "milestone".intern();
    private static final String s_foot = "foot".intern();
    private static final String s_tourism = "tourism".intern();
    private static final String s_bank = "bank".intern();
    private static final String s_dry_cleaning = "dry_cleaning".intern();
    private static final String s_tram = "tram".intern();
    private static final String s_trolleybus = "trolleybus".intern();
    private static final String s_university = "university".intern();
    private static final String s_hampshire_gate = "hampshire_gate".intern();
    private static final String s_embankment = "embankment".intern();
    private static final String s_rock = "rock".intern();
    private static final String s_crossing = "crossing".intern();
    private static final String s_volcano = "volcano".intern();
    private static final String s_greengrocer = "greengrocer".intern();
    private static final String s_kerb = "kerb".intern();
    private static final String s_waste_disposal = "waste_disposal".intern();
    private static final String s_grave_yard = "grave_yard".intern();
    private static final String s_coniferous = "coniferous".intern();
    private static final String s_house = "house".intern();
    private static final String s_books = "books".intern();
    private static final String s_neighbourhood = "neighbourhood".intern();
    private static final String s_hostel = "hostel".intern();
    private static final String s_alcohol = "alcohol".intern();
    private static final String s_restricted = "restricted".intern();
    private static final String s_motel = "motel".intern();
    private static final String s_sand = "sand".intern();
    private static final String s_fishmonger = "fishmonger".intern();
    private static final String s_fountain = "fountain".intern();
    private static final String s_playground = "playground".intern();
    private static final String s_7 = "7".intern();
    private static final String s_parking_aisle = "parking_aisle".intern();
    private static final String s_protected_area = "protected_area".intern();
    private static final String s_electronics = "electronics".intern();
    private static final String s_Paved = "Paved".intern();
    private static final String s_highway = "highway".intern();
    private static final String s_fine_gravel = "fine_gravel".intern();
    private static final String s_barrier = "barrier".intern();
    private static final String s_hairdresser = "hairdresser".intern();
    private static final String s_post_box = "post_box".intern();
    private static final String s_pub = "pub".intern();
    private static final String s_coastline = "coastline".intern();
    private static final String s_marina = "marina".intern();
    private static final String s_reedbed = "reedbed".intern();
    private static final String s_biergarten = "biergarten".intern();
    private static final String s_dismantled = "dismantled".intern();
    private static final String s_farmland = "farmland".intern();
    private static final String s_yard = "yard".intern();
    private static final String s_route = "route".intern();
    private static final String s_atm = "atm".intern();
    private static final String s_place = "place".intern();
    private static final String s_bus_station = "bus_station".intern();
    private static final String s_retail = "retail".intern();
    private static final String s_industrial = "industrial".intern();
    private static final String s_municipality = "municipality".intern();
    private static final String s_primary = "primary".intern();
    private static final String s_nursing_home = "nursing_home".intern();
    private static final String s_florist = "florist".intern();
    private static final String s_ditch = "ditch".intern();
    private static final String s_national_park = "national_park".intern();
    private static final String s_city = "city".intern();
    private static final String s_confectionery = "confectionery".intern();
    private static final String s_service = "service".intern();
    private static final String s_unknown = "unknown".intern();
    private static final String s_cycle_barrier = "cycle_barrier".intern();
    private static final String s_elevator = "elevator".intern();
    private static final String s_2 = "2".intern();
    private static final String s_car_rental = "car_rental".intern();
    private static final String s_flagpole = "flagpole".intern();
    private static final String s_cabin = "cabin".intern();
    private static final String s_paved = "paved".intern();
    private static final String s_guest_house = "guest_house".intern();
    private static final String s_mobile_phone = "mobile_phone".intern();
    private static final String s_lot = "lot".intern();
    private static final String s_quarry = "quarry".intern();
    private static final String s_train_station = "train_station".intern();
    private static final String s_hotel = "hotel".intern();
    private static final String s_park = "park".intern();
    private static final String s_hut = "hut".intern();
    private static final String s_dentist = "dentist".intern();
    private static final String s_doctors = "doctors".intern();
    private static final String s_greenhouse = "greenhouse".intern();
    private static final String s_11 = "11".intern();
    private static final String s_10 = "10".intern();
    private static final String s_theme_park = "theme_park".intern();
    private static final String s_tree = "tree".intern();
    private static final String s_shower = "shower".intern();
    private static final String s_siding = "siding".intern();
    private static final String s_aeroway = "aeroway".intern();
    private static final String s_emergency_access_point = "emergency_access_point"
            .intern();
    private static final String s_watermill = "watermill".intern();
    private static final String s_college = "college".intern();
    private static final String s_landuse = "landuse".intern();
    private static final String s_tracktype = "tracktype".intern();
    private static final String s_ferry = "ferry".intern();
    private static final String s_bridge = "bridge".intern();
    private static final String s_vacant = "vacant".intern();
    private static final String s_cattle_grid = "cattle_grid".intern();
    private static final String s_brownfield = "brownfield".intern();
    private static final String s_allotments = "allotments".intern();
    private static final String s_alley = "alley".intern();
    private static final String s_pedestrian = "pedestrian".intern();
    private static final String s_borough = "borough".intern();
    private static final String s_bare_rock = "bare_rock".intern();
    private static final String s_motorcycle = "motorcycle".intern();
    private static final String s_bakery = "bakery".intern();
    private static final String s_zoo = "zoo".intern();
    private static final String s_scree = "scree".intern();
    private static final String s_fire_station = "fire_station".intern();
    private static final String s_theatre = "theatre".intern();
    private static final String s_track = "track".intern();
    private static final String s_reinforced_slope = "reinforced_slope".intern();
    private static final String s_slipway = "slipway".intern();
    private static final String s_mangrove = "mangrove".intern();
    private static final String s_aerodrome = "aerodrome".intern();
    private static final String s_byway = "byway".intern();
    private static final String s_metal = "metal".intern();
    private static final String s_swamp = "swamp".intern();
    private static final String s_construction = "construction".intern();
    private static final String s_grassland = "grassland".intern();
    private static final String s_shop = "shop".intern();
    private static final String s_soakhole = "soakhole".intern();
    private static final String s_asphalt = "asphalt".intern();
    private static final String s_social_facility = "social_facility".intern();
    private static final String s_isolated_dwelling = "isolated_dwelling".intern();
    private static final String s_hamlet = "hamlet".intern();
    private static final String s_picnic_table = "picnic_table".intern();
    private static final String s_artificial = "artificial".intern();
    private static final String s_earth = "earth".intern();
    private static final String s_grit_bin = "grit_bin".intern();
    private static final String s_ground = "ground".intern();
    private static final String s_groyne = "groyne".intern();
    private static final String s_office = "office".intern();
    private static final String s_state = "state".intern();
    private static final String s_terminal = "terminal".intern();
    private static final String s_wood = "wood".intern();
    private static final String s_fuel = "fuel".intern();
    private static final String s_8 = "8".intern();
    private static final String s_garden_centre = "garden_centre".intern();
    private static final String s_horse_riding = "horse_riding".intern();
    private static final String s_viewpoint = "viewpoint".intern();
    private static final String s_designated = "designated".intern();
    private static final String s_leisure = "leisure".intern();
    private static final String s_waste_basket = "waste_basket".intern();
    private static final String s_hifi = "hifi".intern();
    private static final String s_hedge = "hedge".intern();
    private static final String s_spur = "spur".intern();
    private static final String s_chimney = "chimney".intern();
    private static final String s_secondary = "secondary".intern();
    private static final String s_rest_area = "rest_area".intern();
    private static final String s_bar = "bar".intern();
    private static final String s_bay = "bay".intern();
    private static final String s_common = "common".intern();
    private static final String s_river = "river".intern();
    private static final String s_ruins = "ruins".intern();
    private static final String s_terrace = "terrace".intern();
    private static final String s_art = "art".intern();
    private static final String s_residental = "residental".intern();
    private static final String s_newsagent = "newsagent".intern();
    private static final String s_turntable = "turntable".intern();
    private static final String s_computer = "computer".intern();
    private static final String s_wetland = "wetland".intern();
    private static final String s_driveway = "driveway".intern();
    private static final String s_parking = "parking".intern();
    private static final String s_compacted = "compacted".intern();
    private static final String s_barn = "barn".intern();
    private static final String s_alpine_hut = "alpine_hut".intern();
    private static final String s_wire_fence = "wire_fence".intern();
    private static final String s_unpaved = "unpaved".intern();
    private static final String s_dormitory = "dormitory".intern();
    private static final String s_mud = "mud".intern();
    private static final String s_3 = "3".intern();
    private static final String s_semi = "semi".intern();
    private static final String s_boundary = "boundary".intern();
    private static final String s_field_boundary = "field_boundary".intern();
    private static final String s_beverages = "beverages".intern();
    private static final String s_supermarket = "supermarket".intern();
    private static final String s_store = "store".intern();
    private static final String s_restaurant = "restaurant".intern();
    private static final String s_region = "region".intern();
    private static final String s_variety_store = "variety_store".intern();
    private static final String s_saltmarsh = "saltmarsh".intern();
    private static final String s_landform = "landform".intern();
    private static final String s_helipad = "helipad".intern();
    private static final String s_railway = "railway".intern();
    private static final String s_greenhouse_horticulture = "greenhouse_horticulture"
            .intern();
    private static final String s_wall = "wall".intern();
    private static final String s_recycling = "recycling".intern();
    private static final String s_passing_place = "passing_place".intern();
    private static final String s_church = "church".intern();
    private static final String s_pharmacy = "pharmacy".intern();
    private static final String s_lighthouse = "lighthouse".intern();
    private static final String s_platform = "platform".intern();
    private static final String s_cinema = "cinema".intern();
    private static final String s_political = "political".intern();
    private static final String s_stadium = "stadium".intern();
    private static final String s_basin = "basin".intern();
    private static final String s_gasometer = "gasometer".intern();
    private static final String s_bicycle_parking = "bicycle_parking".intern();
    private static final String s_bbq = "bbq".intern();
    private static final String s_incline_steep = "incline_steep".intern();
    private static final String s_drinking_water = "drinking_water".intern();
    private static final String s_living_street = "living_street".intern();
    private static final String s_chalet = "chalet".intern();
    private static final String s_narrow_gauge = "narrow_gauge".intern();
    private static final String s_prison = "prison".intern();
    private static final String s_mine = "mine".intern();
    private static final String s_level_crossing = "level_crossing".intern();
    private static final String s_water_works = "water_works".intern();
    private static final String s_street_lamp = "street_lamp".intern();
    private static final String s_main = "main".intern();
    private static final String s_tank = "tank".intern();
    private static final String s_abandoned = "abandoned".intern();
    private static final String s_ski = "ski".intern();
    private static final String s_runway = "runway".intern();
    private static final String s_parking_space = "parking_space".intern();
    private static final String s_dirt_sand = "dirt/sand".intern();
    private static final String s_salt_pond = "salt_pond".intern();
    private static final String s_hedge_bank = "hedge_bank".intern();
    private static final String s_amenity = "amenity".intern();
    private static final String s_telephone = "telephone".intern();
    private static final String s_surface = "surface".intern();
    private static final String s_travel_agency = "travel_agency".intern();
    private static final String s_hardware = "hardware".intern();
    private static final String s_wastewater_plant = "wastewater_plant".intern();
    private static final String s_waterway = "waterway".intern();
    private static final String s_butcher = "butcher".intern();
    private static final String s_surveillance = "surveillance".intern();
    private static final String s_Dirt_Sand = "Dirt/Sand".intern();
    private static final String s_9 = "9".intern();
    private static final String s_windmill = "windmill".intern();
    private static final String s_picnic_site = "picnic_site".intern();
    private static final String s_rail = "rail".intern();
    private static final String s_cement = "cement".intern();
    private static final String s_sauna = "sauna".intern();
    private static final String s_suburb = "suburb".intern();
    private static final String s_waterfall = "waterfall".intern();
    private static final String s_bunker = "bunker".intern();
    private static final String s_ice_cream = "ice_cream".intern();
    private static final String s_culvert = "culvert".intern();
    private static final String s_drain = "drain".intern();
    private static final String s_dock = "dock".intern();
    private static final String s_glasshouse = "glasshouse".intern();
    private static final String s_no = "no".intern();
    private static final String s_well = "well".intern();
    private static final String s_wet_meadow = "wet_meadow".intern();
    private static final String s_concrete = "concrete".intern();
    private static final String s_dismount = "dismount".intern();
    private static final String s_vending_machine = "vending_machine".intern();
    private static final String s_oneway = "oneway".intern();
    private static final String s_taxi = "taxi".intern();
    private static final String s_outdoor = "outdoor".intern();
    private static final String s_proposed = "proposed".intern();
    private static final String s_sally_port = "sally_port".intern();
    private static final String s_photo = "photo".intern();
    private static final String s_plant_nursery = "plant_nursery".intern();
    private static final String s_clinic = "clinic".intern();
    private static final String s_fishing = "fishing".intern();
    private static final String s_yes = "yes".intern();
    private static final String s_turning_circle = "turning_circle".intern();
    private static final String s_toilets = "toilets".intern();
    private static final String s_guard_rail = "guard_rail".intern();
    private static final String s_townhall = "townhall".intern();
    private static final String s_community_centre = "community_centre".intern();
    private static final String s_residential = "residential".intern();
    private static final String s_cemetery = "cemetery".intern();
    private static final String s_survey_point = "survey_point".intern();
    private static final String s_bench = "bench".intern();
    private static final String s_4 = "4".intern();
    private static final String s_bollard = "bollard".intern();
    private static final String s_sports_centre = "sports_centre".intern();
    private static final String s_paving_stones_30 = "paving_stones:30".intern();
    private static final String s_administrative = "administrative".intern();
    private static final String s_Building = "Building".intern();
    private static final String s_customers = "customers".intern();
    private static final String s_emergency = "emergency".intern();
    private static final String s_motorway_junction = "motorway_junction".intern();
    private static final String s_grade1 = "grade1".intern();
    private static final String s_grade3 = "grade3".intern();
    private static final String s_grade2 = "grade2".intern();
    private static final String s_grade5 = "grade5".intern();
    private static final String s_grade4 = "grade4".intern();
    private static final String s_lock_gate = "lock_gate".intern();
    private static final String s_furniture = "furniture".intern();
    private static final String s_place_of_worship = "place_of_worship".intern();
    private static final String s_optician = "optician".intern();
    private static final String s_gift = "gift".intern();
    private static final String s_parking_entrance = "parking_entrance".intern();
    private static final String s_garage = "garage".intern();
    private static final String s_tram_stop = "tram_stop".intern();
    private static final String s_steps = "steps".intern();
    private static final String s_tower = "tower".intern();
    private static final String s_works = "works".intern();
    private static final String s_shed = "shed".intern();
    private static final String s_car_sharing = "car_sharing".intern();
    private static final String s_apartments = "apartments".intern();
    private static final String s_spring = "spring".intern();
    private static final String s_village = "village".intern();
    private static final String s_library = "library".intern();
    private static final String s_emergency_access = "emergency_access".intern();
    private static final String s_home = "home".intern();
    private static final String s_farm_auxiliary = "farm_auxiliary".intern();
    private static final String s_primary_link = "primary_link".intern();
    private static final String s_toll_booth = "toll_booth".intern();
    private static final String s_jewelry = "jewelry".intern();
    private static final String s_pet = "pet".intern();
    private static final String s_veterinary = "veterinary".intern();
    private static final String s_man_made = "man_made".intern();
    private static final String s_motorway_link = "motorway_link".intern();
    private static final String s_offices = "offices".intern();
    private static final String s_power = "power".intern();
    private static final String s_weir = "weir".intern();
    private static final String s_unsurfaced = "unsurfaced".intern();
    private static final String s_tertiary_link = "tertiary_link".intern();
    private static final String s_trunk_link = "trunk_link".intern();
    private static final String s_tyres = "tyres".intern();
    private static final String s_paving_stones = "paving_stones".intern();
    private static final String s_pipeline = "pipeline".intern();
    private static final String s_census = "census".intern();
    private static final String s_incline = "incline".intern();
    private static final String s_footway = "footway".intern();
    private static final String s_drive_through = "drive-through".intern();
    private static final String s_island = "island".intern();
    private static final String s_monitoring_station = "monitoring_station".intern();
    private static final String s_nightclub = "nightclub".intern();
    private static final String s_unclassified = "unclassified".intern();
    private static final String s_aquaculture = "aquaculture".intern();
    private static final String s_mixed = "mixed".intern();
    private static final String s_road = "road".intern();
    private static final String s_greenfield = "greenfield".intern();
    private static final String s_breakwater = "breakwater".intern();
    private static final String s_services = "services".intern();
    private static final String s_railway_crossing = "railway_crossing".intern();
    private static final String s_residentiel1 = "residentiel1".intern();
    private static final String s_canal = "canal".intern();
    private static final String s__1 = "-1".intern();
    private static final String s_ridge = "ridge".intern();
    private static final String s_fabric = "fabric".intern();
    private static final String s_museum = "museum".intern();
    private static final String s_communications_tower = "communications_tower".intern();
    private static final String s_semi_detached = "semi-detached".intern();
    private static final String s_conservation = "conservation".intern();
    private static final String s_way = "way".intern();
    private static final String s_wood_fence = "wood_fence".intern();
    private static final String s_manufacture = "manufacture".intern();
    private static final String s_admin_level = "admin_level".intern();
    private static final String s_building_concrete = "building_concrete".intern();
    private static final String s_bus = "bus".intern();
    private static final String s_collapsed = "collapsed".intern();
    private static final String s_ford = "ford".intern();
    private static final String s_delivery = "delivery".intern();
    private static final String s_garages = "garages".intern();
    private static final String s_funeral_directors = "funeral_directors".intern();
    private static final String s_land = "land".intern();
    private static final String s_interlock = "interlock".intern();
    private static final String s_reef = "reef".intern();
    private static final String s_crane = "crane".intern();
    private static final String s_true = "true".intern();
    private static final String s_storage_tank = "storage_tank".intern();
    private static final String s_official = "official".intern();
    private static final String s_subway_entrance = "subway_entrance".intern();
    private static final String s_mtb = "mtb".intern();
    private static final String s_grass = "grass".intern();
    private static final String s_marketplace = "marketplace".intern();
    private static final String s_rapids = "rapids".intern();
    private static final String s_car_wash = "car_wash".intern();
    private static final String s_general = "general".intern();
    private static final String s_cafe = "cafe".intern();
    private static final String s_locality = "locality".intern();
    private static final String s_glacier = "glacier".intern();
    private static final String s_storage = "storage".intern();
    private static final String s_cycleway = "cycleway".intern();
    private static final String s_forestry = "forestry".intern();
    private static final String s_field = "field".intern();
    private static final String s_5 = "5".intern();
    private static final String s_arts_centre = "arts_centre".intern();
    private static final String s_warehouse = "warehouse".intern();
    private static final String s_chemist = "chemist".intern();
    private static final String s_pier = "pier".intern();
    private static final String s_scrub = "scrub".intern();
    private static final String s_shelter = "shelter".intern();
    private static final String s_emergency_phone = "emergency_phone".intern();
    private static final String s_tidalflat = "tidalflat".intern();
    private static final String s_cobblestone = "cobblestone".intern();
    private static final String s_fell = "fell".intern();
    private static final String s_peak = "peak".intern();
    private static final String s_charging_station = "charging_station".intern();
    private static final String s_cliff = "cliff".intern();
    private static final String s_building = "building".intern();
    private static final String s_fire_hydrant = "fire_hydrant".intern();
    private static final String s_traffic_signals = "traffic_signals".intern();
    private static final String s_heath = "heath".intern();
    private static final String s_landfill = "landfill".intern();
    private static final String s_mast = "mast".intern();
    private static final String s_boutique = "boutique".intern();
    private static final String s_boat_storage = "boat_storage".intern();

    public static final Tag[] tags = {

            new Tag(s_building, s_yes, false), new Tag(s_highway, s_residential, false),
            new Tag(s_highway, s_service, false), new Tag(s_waterway, s_stream, false),
            new Tag(s_highway, s_unclassified, false), new Tag(s_highway, s_track, false),
            new Tag(s_oneway, s_yes, false), new Tag(s_natural, s_water, false),
            new Tag(s_highway, s_footway, false), new Tag(s_access, s_private, false),
            new Tag(s_highway, s_tertiary, false), new Tag(s_highway, s_path, false),
            new Tag(s_highway, s_secondary, false), new Tag(s_landuse, s_forest, false),
            new Tag(s_bridge, s_yes, false), new Tag(s_natural, s_tree, false),
            new Tag(s_surface, s_paved, false), new Tag(s_natural, s_wood, false),
            new Tag(s_highway, s_primary, false), new Tag(s_landuse, s_grass, false),
            new Tag(s_landuse, s_residential, false), new Tag(s_surface, s_unpaved, false),
            new Tag(s_highway, s_bus_stop, false), new Tag(s_surface, s_asphalt, false),
            new Tag(s_bicycle, s_yes, false), new Tag(s_amenity, s_parking, false),
            new Tag(s_place, s_locality, false), new Tag(s_railway, s_rail, false),
            new Tag(s_service, s_parking_aisle, false),
            new Tag(s_boundary, s_administrative, false),
            new Tag(s_building, s_house, false), new Tag(s_place, s_village, false),
            new Tag(s_natural, s_coastline, false), new Tag(s_tracktype, s_grade2, false),
            new Tag(s_oneway, s_no, false), new Tag(s_service, s_driveway, false),
            new Tag(s_highway, s_turning_circle, false), new Tag(s_place, s_hamlet, false),
            new Tag(s_natural, s_wetland, false), new Tag(s_tracktype, s_grade3, false),
            new Tag(s_waterway, s_river, false), new Tag(s_highway, s_cycleway, false),
            new Tag(s_barrier, s_fence, false), new Tag(s_building, s_residential, false),
            new Tag(s_amenity, s_school, false), new Tag(s_highway, s_crossing, false),
            new Tag(s_admin_level, s_8, false), new Tag(s_highway, s_trunk, false),
            new Tag(s_amenity, s_place_of_worship, false),
            new Tag(s_landuse, s_farmland, false), new Tag(s_tracktype, s_grade1, false),
            new Tag(s_highway, s_road, false), new Tag(s_landuse, s_farm, false),
            new Tag(s_surface, s_gravel, false), new Tag(s_landuse, s_meadow, false),
            new Tag(s_highway, s_motorway, false),
            new Tag(s_highway, s_traffic_signals, false),
            new Tag(s_building, s_hut, false), new Tag(s_highway, s_motorway_link, false),
            new Tag(s_tracktype, s_grade4, false), new Tag(s_barrier, s_gate, false),
            new Tag(s_highway, s_living_street, false), new Tag(s_bicycle, s_no, false),
            new Tag(s_leisure, s_pitch, false), new Tag(s_tunnel, s_yes, false),
            new Tag(s_surface, s_ground, false), new Tag(s_highway, s_steps, false),
            new Tag(s_natural, s_land, false), new Tag(s_man_made, s_survey_point, false),
            new Tag(s_tracktype, s_grade5, false), new Tag(s_waterway, s_ditch, false),
            new Tag(s_leisure, s_park, false), new Tag(s_amenity, s_restaurant, false),
            new Tag(s_barrier, s_wall, false), new Tag(s_waterway, s_riverbank, false),
            new Tag(s_amenity, s_bench, false), new Tag(s_building, s_garage, false),
            new Tag(s_natural, s_scrub, false), new Tag(s_highway, s_pedestrian, false),
            new Tag(s_natural, s_peak, false), new Tag(s_building, s_entrance, false),
            new Tag(s_landuse, s_reservoir, false), new Tag(s_access, s_yes, false),
            new Tag(s_bicycle, s_designated, false),
            new Tag(s_leisure, s_swimming_pool, false),
            new Tag(s_landuse, s_farmyard, false),
            new Tag(s_railway, s_level_crossing, false),
            new Tag(s_building, s_apartments, false), new Tag(s_surface, s_grass, false),
            new Tag(s_wheelchair, s_yes, false), new Tag(s_service, s_alley, false),
            new Tag(s_landuse, s_industrial, false), new Tag(s_amenity, s_fuel, false),
            new Tag(s_surface, s_dirt, false), new Tag(s_highway, s_trunk_link, false),
            new Tag(s_waterway, s_drain, false), new Tag(s_barrier, s_hedge, false),
            new Tag(s_amenity, s_grave_yard, false),
            new Tag(s_tourism, s_information, false),
            new Tag(s_shop, s_supermarket, false),
            new Tag(s_highway, s_primary_link, false), new Tag(s_wood, s_deciduous, false),
            new Tag(s_leisure, s_playground, false), new Tag(s_building, s_roof, false),
            new Tag(s_building, s_industrial, false),
            new Tag(s_amenity, s_post_box, false), new Tag(s_waterway, s_canal, false),
            new Tag(s_barrier, s_bollard, false), new Tag(s_leisure, s_garden, false),
            new Tag(s_wood, s_mixed, false), new Tag(s_landuse, s_cemetery, false),
            new Tag(s_landuse, s_orchard, false), new Tag(s_shop, s_convenience, false),
            new Tag(s_access, s_permissive, false), new Tag(s_surface, s_concrete, false),
            new Tag(s_surface, s_paving_stones, false), new Tag(s_service, s_spur, false),
            new Tag(s_building, s_garages, false), new Tag(s_amenity, s_bank, false),
            new Tag(s_tourism, s_hotel, false), new Tag(s_access, s_no, false),
            new Tag(s_amenity, s_fast_food, false), new Tag(s_man_made, s_pier, false),
            new Tag(s_amenity, s_kindergarten, false),
            new Tag(s_access, s_agricultural, false),
            new Tag(s_surface, s_cobblestone, false), new Tag(s_wheelchair, s_no, false),
            new Tag(s_amenity, s_cafe, false), new Tag(s_amenity, s_hospital, false),
            new Tag(s_amenity, s_post_office, false),
            new Tag(s_amenity, s_public_building, false),
            new Tag(s_amenity, s_recycling, false),
            new Tag(s_highway, s_street_lamp, false), new Tag(s_man_made, s_tower, false),
            new Tag(s_waterway, s_dam, false), new Tag(s_amenity, s_pub, false),
            new Tag(s_wood, s_coniferous, false), new Tag(s_access, s_destination, false),
            new Tag(s_admin_level, s_6, false), new Tag(s_landuse, s_commercial, false),
            new Tag(s_amenity, s_pharmacy, false), new Tag(s_railway, s_abandoned, false),
            new Tag(s_service, s_yard, false), new Tag(s_place, s_island, false),
            new Tag(s_oneway, s__1, false), new Tag(s_landuse, s_quarry, false),
            new Tag(s_landuse, s_vineyard, false),
            new Tag(s_highway, s_motorway_junction, false),
            new Tag(s_railway, s_station, false), new Tag(s_landuse, s_allotments, false),
            new Tag(s_barrier, s_lift_gate, false), new Tag(s_admin_level, s_10, false),
            new Tag(s_amenity, s_telephone, false), new Tag(s_place, s_town, false),
            new Tag(s_man_made, s_cutline, false), new Tag(s_place, s_suburb, false),
            new Tag(s_aeroway, s_taxiway, false), new Tag(s_wheelchair, s_limited, false),
            new Tag(s_highway, s_secondary_link, false),
            new Tag(s_leisure, s_sports_centre, false),
            new Tag(s_amenity, s_bicycle_parking, false),
            new Tag(s_surface, s_sand, false), new Tag(s_highway, s_stop, false),
            new Tag(s_man_made, s_works, false), new Tag(s_landuse, s_retail, false),
            new Tag(s_amenity, s_fire_station, false), new Tag(s_service, s_siding, false),
            new Tag(s_amenity, s_toilets, false), new Tag(s_bench, s_yes, false),
            new Tag(s_oneway, s_1, false), new Tag(s_surface, s_compacted, false),
            new Tag(s_landuse, s_basin, false), new Tag(s_amenity, s_police, false),
            new Tag(s_railway, s_tram, false), new Tag(s_route, s_road, false),
            new Tag(s_natural, s_cliff, false), new Tag(s_highway, s_construction, false),
            new Tag(s_aeroway, s_aerodrome, false), new Tag(s_entrance, s_yes, false),
            new Tag(s_man_made, s_storage_tank, false), new Tag(s_amenity, s_atm, false),
            new Tag(s_tourism, s_attraction, false), new Tag(s_route, s_bus, false),
            new Tag(s_shop, s_bakery, false), new Tag(s_tourism, s_viewpoint, false),
            new Tag(s_amenity, s_swimming_pool, false), new Tag(s_natural, s_beach, false),
            new Tag(s_tourism, s_picnic_site, false), new Tag(s_oneway, s_true, false),
            new Tag(s_highway, s_bridleway, false), new Tag(s_tourism, s_camp_site, false),
            new Tag(s_abutters, s_residential, false),
            new Tag(s_leisure, s_nature_reserve, false),
            new Tag(s_amenity, s_drinking_water, false), new Tag(s_shop, s_clothes, false),
            new Tag(s_natural, s_heath, false),
            new Tag(s_highway, s_mini_roundabout, false),
            new Tag(s_landuse, s_construction, false),
            new Tag(s_amenity, s_waste_basket, false),
            new Tag(s_railway, s_platform, false), new Tag(s_amenity, s_townhall, false),
            new Tag(s_shop, s_hairdresser, false), new Tag(s_amenity, s_shelter, false),
            new Tag(s_admin_level, s_9, false),
            new Tag(s_building, s_farm_auxiliary, false),
            new Tag(s_amenity, s_library, false), new Tag(s_building, s_detached, false),
            new Tag(s_admin_level, s_4, false), new Tag(s_landuse, s_village_green, false),
            new Tag(s_barrier, s_stile, false), new Tag(s_landuse, s_garages, false),
            new Tag(s_amenity, s_bar, false), new Tag(s_railway, s_buffer_stop, false),
            new Tag(s_wetland, s_marsh, false), new Tag(s_tourism, s_museum, false),
            new Tag(s_barrier, s_cycle_barrier, false), new Tag(s_route, s_bicycle, false),
            new Tag(s_railway, s_tram_stop, false),
            new Tag(s_amenity, s_parking_space, false),
            new Tag(s_barrier, s_retaining_wall, false),
            new Tag(s_landuse, s_recreation_ground, false),
            new Tag(s_amenity, s_university, false),
            new Tag(s_highway, s_tertiary_link, false),
            new Tag(s_building, s_terrace, false), new Tag(s_shop, s_car_repair, false),
            new Tag(s_amenity, s_hunting_stand, false),
            new Tag(s_amenity, s_fountain, false), new Tag(s_man_made, s_pipeline, false),
            new Tag(s_wetland, s_swamp, false), new Tag(s_shop, s_car, false),
            new Tag(s_bench, s_no, false), new Tag(s_tunnel, s_culvert, false),
            new Tag(s_building, s_school, false), new Tag(s_barrier, s_entrance, false),
            new Tag(s_railway, s_disused, false), new Tag(s_railway, s_crossing, false),
            new Tag(s_building, s_church, false),
            new Tag(s_amenity, s_social_facility, false), new Tag(s_natural, s_bay, false),
            new Tag(s_shop, s_kiosk, false), new Tag(s_amenity, s_vending_machine, false),
            new Tag(s_route, s_hiking, false), new Tag(s_natural, s_spring, false),
            new Tag(s_leisure, s_common, false), new Tag(s_railway, s_switch, false),
            new Tag(s_waterway, s_rapids, false), new Tag(s_admin_level, s_7, false),
            new Tag(s_leisure, s_stadium, false), new Tag(s_leisure, s_track, false),
            new Tag(s_place, s_isolated_dwelling, false), new Tag(s_place, s_islet, false),
            new Tag(s_waterway, s_weir, false), new Tag(s_amenity, s_doctors, false),
            new Tag(s_access, s_designated, false),
            new Tag(s_landuse, s_conservation, false),
            new Tag(s_waterway, s_artificial, false),
            new Tag(s_amenity, s_bus_station, false),
            new Tag(s_leisure, s_golf_course, false),
            new Tag(s_shop, s_doityourself, false), new Tag(s_building, s_service, false),
            new Tag(s_tourism, s_guest_house, false), new Tag(s_aeroway, s_runway, false),
            new Tag(s_place, s_city, false), new Tag(s_railway, s_subway, false),
            new Tag(s_man_made, s_wastewater_plant, false),
            new Tag(s_building, s_commercial, false), new Tag(s_railway, s_halt, false),
            new Tag(s_amenity, s_emergency_phone, false),
            new Tag(s_building, s_retail, false), new Tag(s_barrier, s_block, false),
            new Tag(s_leisure, s_recreation_ground, false),
            new Tag(s_access, s_forestry, false), new Tag(s_amenity, s_college, false),
            new Tag(s_highway, s_platform, false), new Tag(s_access, s_unknown, false),
            new Tag(s_man_made, s_water_tower, false),
            new Tag(s_surface, s_pebblestone, false), new Tag(s_bridge, s_viaduct, false),
            new Tag(s_shop, s_butcher, false), new Tag(s_shop, s_florist, false),
            new Tag(s_boundary, s_landuse, false), new Tag(s_aeroway, s_helipad, false),
            new Tag(s_building, s_hangar, false), new Tag(s_natural, s_glacier, false),
            new Tag(s_highway, s_proposed, false), new Tag(s_shop, s_mall, false),
            new Tag(s_barrier, s_toll_booth, false),
            new Tag(s_amenity, s_fire_hydrant, false),
            new Tag(s_building, s_manufacture, false), new Tag(s_building, s_farm, false),
            new Tag(s_surface, s_wood, false), new Tag(s_amenity, s_car_wash, false),
            new Tag(s_amenity, s_dentist, false), new Tag(s_natural, s_marsh, false),
            new Tag(s_man_made, s_surveillance, false), new Tag(s_shop, s_bicycle, false),
            new Tag(s_route, s_foot, false), new Tag(s_amenity, s_theatre, false),
            new Tag(s_building, s_office, false), new Tag(s_railway, s_light_rail, false),
            new Tag(s_man_made, s_petroleum_well, false),
            new Tag(s_amenity, s_taxi, false), new Tag(s_building, s_greenhouse, false),
            new Tag(s_landuse, s_brownfield, false),
            new Tag(s_bicycle, s_permissive, false), new Tag(s_admin_level, s_2, false),
            new Tag(s_aeroway, s_apron, false), new Tag(s_building, s_cabin, false),
            new Tag(s_amenity, s_cinema, false), new Tag(s_access, s_customers, false),
            new Tag(s_tourism, s_motel, false), new Tag(s_railway, s_narrow_gauge, false),
            new Tag(s_amenity, s_marketplace, false), new Tag(s_shop, s_furniture, false),
            new Tag(s_entrance, s_staircase, false), new Tag(s_tourism, s_artwork, false),
            new Tag(s_natural, s_grassland, false), new Tag(s_shop, s_books, false),
            new Tag(s_admin_level, s_5, false), new Tag(s_man_made, s_groyne, false),
            new Tag(s_waterway, s_lock_gate, false),
            new Tag(s_highway, s_emergency_access_point, false),
            new Tag(s_natural, s_sand, false), new Tag(s_landuse, s_military, false),
            new Tag(s_boundary, s_protected_area, false),
            new Tag(s_amenity, s_community_centre, false),
            new Tag(s_barrier, s_kissing_gate, false),
            new Tag(s_highway, s_speed_camera, false),
            new Tag(s_boundary, s_national_park, false),
            new Tag(s_railway, s_subway_entrance, false),
            new Tag(s_man_made, s_silo, false), new Tag(s_shop, s_alcohol, false),
            new Tag(s_highway, s_give_way, false), new Tag(s_leisure, s_slipway, false),
            new Tag(s_shop, s_electronics, false), new Tag(s_bicycle, s_dismount, false),
            new Tag(s_leisure, s_marina, false), new Tag(s_entrance, s_main, false),
            new Tag(s_boundary, s_postal_code, false),
            new Tag(s_landuse, s_greenhouse_horticulture, false),
            new Tag(s_highway, s_milestone, false),
            new Tag(s_natural, s_cave_entrance, false),
            new Tag(s_landuse, s_landfill, false), new Tag(s_shop, s_chemist, false),
            new Tag(s_shop, s_shoes, false), new Tag(s_barrier, s_cattle_grid, false),
            new Tag(s_landuse, s_railway, false), new Tag(s_tourism, s_hostel, false),
            new Tag(s_tourism, s_chalet, false), new Tag(s_place, s_county, false),
            new Tag(s_shop, s_department_store, false), new Tag(s_highway, s_ford, false),
            new Tag(s_natural, s_scree, false), new Tag(s_landuse, s_greenfield, false),
            new Tag(s_amenity, s_nursing_home, false),
            new Tag(s_barrier, s_wire_fence, false),
            new Tag(s_access, s_restricted, false),
            new Tag(s_man_made, s_reservoir_covered, false),
            new Tag(s_amenity, s_bicycle_rental, false), new Tag(s_man_made, s_MDF, false),
            new Tag(s_man_made, s_water_well, false), new Tag(s_landuse, s_field, false),
            new Tag(s_landuse, s_wood, false), new Tag(s_shop, s_hardware, false),
            new Tag(s_tourism, s_alpine_hut, false), new Tag(s_natural, s_tree_row, false),
            new Tag(s_tourism, s_caravan_site, false), new Tag(s_bridge, s_no, false),
            new Tag(s_wetland, s_bog, false), new Tag(s_amenity, s_courthouse, false),
            new Tag(s_route, s_ferry, false), new Tag(s_barrier, s_city_wall, false),
            new Tag(s_amenity, s_veterinary, false), new Tag(s_shop, s_jewelry, false),
            new Tag(s_building, s_transportation, false),
            new Tag(s_amenity, s_arts_centre, false),
            new Tag(s_bicycle, s_official, false), new Tag(s_shop, s_optician, false),
            new Tag(s_shop, s_yes, false), new Tag(s_building, s_collapsed, false),
            new Tag(s_shop, s_garden_centre, false), new Tag(s_man_made, s_chimney, false),
            new Tag(s_man_made, s_mine, false), new Tag(s_bench, s_unknown, false),
            new Tag(s_railway, s_preserved, false), new Tag(s_building, s_public, false),
            new Tag(s_amenity, s_ferry_terminal, false),
            new Tag(s_highway, s_raceway, false), new Tag(s_natural, s_rock, false),
            new Tag(s_tunnel, s_no, false), new Tag(s_building, s_university, false),
            new Tag(s_shop, s_beverages, false),
            new Tag(s_amenity, s_waste_disposal, false),
            new Tag(s_building, s_warehouse, false),
            new Tag(s_leisure, s_water_park, false), new Tag(s_shop, s_gift, false),
            new Tag(s_place, s_farm, false), new Tag(s_wetland, s_tidalflat, false),
            new Tag(s_waterway, s_waterfall, false), new Tag(s_man_made, s_dolphin, false),
            new Tag(s_service, s_drive_through, false),
            new Tag(s_amenity, s_nightclub, false), new Tag(s_building, s_shed, false),
            new Tag(s_shop, s_greengrocer, false), new Tag(s_natural, s_fell, false),
            new Tag(s_wetland, s_wet_meadow, false), new Tag(s_aeroway, s_gate, false),
            new Tag(s_shop, s_computer, false), new Tag(s_man_made, s_lighthouse, false),
            new Tag(s_wetland, s_reedbed, false), new Tag(s_man_made, s_breakwater, false),
            new Tag(s_surface, s_Dirt_Sand, false), new Tag(s_barrier, s_ditch, false),
            new Tag(s_barrier, s_yes, false), new Tag(s_amenity, s_biergarten, false),
            new Tag(s_shop, s_mobile_phone, false), new Tag(s_route, s_mtb, false),
            new Tag(s_amenity, s_grit_bin, false), new Tag(s_amenity, s_bbq, false),
            new Tag(s_shop, s_sports, false), new Tag(s_barrier, s_wood_fence, false),
            new Tag(s_entrance, s_home, false), new Tag(s_shop, s_laundry, false),
            new Tag(s_man_made, s_gasometer, false),
            new Tag(s_barrier, s_embankment, false), new Tag(s_shop, s_toys, false),
            new Tag(s_wetland, s_saltmarsh, false), new Tag(s_waterway, s_soakhole, false),
            new Tag(s_shop, s_travel_agency, false),
            new Tag(s_man_made, s_water_works, false), new Tag(s_route, s_railway, false),
            new Tag(s_amenity, s_prison, false), new Tag(s_highway, s_rest_area, false),
            new Tag(s_shop, s_stationery, false), new Tag(s_admin_level, s_11, false),
            new Tag(s_building, s_train_station, false),
            new Tag(s_building, s_storage_tank, false),
            new Tag(s_man_made, s_windmill, false), new Tag(s_shop, s_beauty, false),
            new Tag(s_building, s_semi, false), new Tag(s_highway, s_services, false),
            new Tag(s_bicycle, s_private, false), new Tag(s_route, s_ski, false),
            new Tag(s_service, s_emergency_access, false),
            new Tag(s_building, s_factory, false),
            new Tag(s_man_made, s_reinforced_slope, false),
            new Tag(s_amenity, s_car_sharing, false), new Tag(s_surface, s_earth, false),
            new Tag(s_shop, s_hifi, false), new Tag(s_amenity, s_car_rental, false),
            new Tag(s_barrier, s_hedge_bank, false),
            new Tag(s_shop, s_confectionery, false), new Tag(s_aeroway, s_terminal, false),
            new Tag(s_highway, s_passing_place, false),
            new Tag(s_building, s_building, false), new Tag(s_man_made, s_dyke, false),
            new Tag(s_building, s_construction, false), new Tag(s_building, s_shop, false),
            new Tag(s_natural, s_reef, false), new Tag(s_landuse, s_aquaculture, false),
            new Tag(s_shop, s_dry_cleaning, false), new Tag(s_amenity, s_embassy, false),
            new Tag(s_shop, s_newsagent, false), new Tag(s_landuse, s_salt_pond, false),
            new Tag(s_railway, s_spur, false), new Tag(s_wheelchair, s_unknown, false),
            new Tag(s_tourism, s_zoo, false), new Tag(s_man_made, s_waterway, false),
            new Tag(s_surface, s_fine_gravel, false), new Tag(s_shop, s_motorcycle, false),
            new Tag(s_building, s_Building, false),
            new Tag(s_railway, s_construction, false),
            new Tag(s_place, s_neighbourhood, false), new Tag(s_route, s_train, false),
            new Tag(s_building, s_no, false), new Tag(s_natural, s_mud, false),
            new Tag(s_place, s_region, false),
            new Tag(s_landuse, s_reservoir_watershed, false),
            new Tag(s_boundary, s_marker, false), new Tag(s_man_made, s_beacon, false),
            new Tag(s_shop, s_outdoor, false), new Tag(s_access, s_public, false),
            new Tag(s_abutters, s_industrial, false), new Tag(s_building, s_barn, false),
            new Tag(s_leisure, s_picnic_table, false),
            new Tag(s_building, s_hospital, false), new Tag(s_access, s_official, false),
            new Tag(s_shop, s_variety_store, false), new Tag(s_man_made, s_crane, false),
            new Tag(s_amenity, s_parking_fuel, false), new Tag(s_route, s_tram, false),
            new Tag(s_tourism, s_theme_park, false), new Tag(s_shop, s_pet, false),
            new Tag(s_building, s_kindergarten, false),
            new Tag(s_man_made, s_storage, false), new Tag(s_man_made, s_mast, false),
            new Tag(s_amenity, s_parking_entrance, false),
            new Tag(s_amenity, s_clock, false),
            new Tag(s_landuse, s_industrial_retail, false),
            new Tag(s_shop, s_video, false), new Tag(s_access, s_delivery, false),
            new Tag(s_amenity, s_driving_school, false), new Tag(s_service, s_yes, false),
            new Tag(s_natural, s_bare_rock, false), new Tag(s_building, s_chapel, false),
            new Tag(s_natural, s_volcano, false), new Tag(s_waterway, s_dock, false),
            new Tag(s_building, s_dormitory, false),
            new Tag(s_amenity, s_boat_storage, false), new Tag(s_man_made, s_tank, false),
            new Tag(s_man_made, s_flagpole, false),
            new Tag(s_surface, s_grass_paver, false), new Tag(s_shop, s_organic, false),
            new Tag(s_natural, s_landform, false), new Tag(s_highway, s_unsurfaced, false),
            new Tag(s_route, s_power, false), new Tag(s_surface, s_mud, false),
            new Tag(s_building, s_building_concrete, false),
            new Tag(s_abutters, s_retail, false), new Tag(s_building, s_store, false),
            new Tag(s_shop, s_vacant, false), new Tag(s_leisure, s_miniature_golf, false),
            new Tag(s_man_made, s_monitoring_station, false),
            new Tag(s_natural, s_waterfall, false), new Tag(s_aeroway, s_hangar, false),
            new Tag(s_shop, s_boutique, false), new Tag(s_route, s_detour, false),
            new Tag(s_building, s_way, false), new Tag(s_railway, s_stop, false),
            new Tag(s_amenity, s_ice_cream, false), new Tag(s_building, s_storage, false),
            new Tag(s_shop, s_car_parts, false), new Tag(s_natural, s_ridge, false),
            new Tag(s_shop, s_tyres, false), new Tag(s_railway, s_dismantled, false),
            new Tag(s_amenity, s_shop, false), new Tag(s_landuse, s_plant_nursery, false),
            new Tag(s_building, s_residentiel1, false),
            new Tag(s_barrier, s_field_boundary, false),
            new Tag(s_barrier, s_border_control, false),
            new Tag(s_surface, s_Paved, false), new Tag(s_barrier, s_sally_port, false),
            new Tag(s_amenity, s_bureau_de_change, false),
            new Tag(s_leisure, s_fishing, false),
            new Tag(s_amenity, s_charging_station, false),
            new Tag(s_building, s_supermarket, false), new Tag(s_highway, s_stile, false),
            new Tag(s_amenity, s_sauna, false), new Tag(s_place, s_municipality, false),
            new Tag(s_building, s_hotel, false), new Tag(s_surface, s_metal, false),
            new Tag(s_highway, s_incline_steep, false),
            new Tag(s_shop, s_estate_agent, false), new Tag(s_natural, s_grass, false),
            new Tag(s_shop, s_pharmacy, false),
            new Tag(s_surface, s_concrete_plates, false),
            new Tag(s_shop, s_copyshop, false),
            new Tag(s_surface, s_paving_stones_30, false),
            new Tag(s_surface, s_interlock, false), new Tag(s_access, s_hov, false),
            new Tag(s_highway, s_elevator, false),
            new Tag(s_boundary, s_local_authority, false),
            new Tag(s_man_made, s_communications_tower, false),
            new Tag(s_shop, s_deli, false), new Tag(s_barrier, s_turnstile, false),
            new Tag(s_building, s_offices, false), new Tag(s_building, s_bunker, false),
            new Tag(s_natural, s_stone, false),
            new Tag(s_railway, s_railway_crossing, false),
            new Tag(s_leisure, s_dog_park, false),
            new Tag(s_building, s_semi_detached, false),
            new Tag(s_man_made, s_watermill, false), new Tag(s_route, s_trolleybus, false),
            new Tag(s_admin_level, s_3, false), new Tag(s_building, s_block, false),
            new Tag(s_barrier, s_guard_rail, false), new Tag(s_bicycle, s_unknown, false),
            new Tag(s_highway, s_abandoned, false), new Tag(s_surface, s_dirt_sand, false),
            new Tag(s_barrier, s_chain, false), new Tag(s_barrier, s_bump_gate, false),
            new Tag(s_building, s_residental, false), new Tag(s_surface, s_cement, false),
            new Tag(s_man_made, s_embankment, false), new Tag(s_building, s_ruins, false),
            new Tag(s_highway, s_incline, false), new Tag(s_abutters, s_commercial, false),
            new Tag(s_barrier, s_hampshire_gate, false), new Tag(s_shop, s_music, false),
            new Tag(s_shop, s_funeral_directors, false),
            new Tag(s_wetland, s_mangrove, false), new Tag(s_place, s_borough, false),
            new Tag(s_building, s_apartment, false), new Tag(s_boundary, s_census, false),
            new Tag(s_barrier, s_kerb, false), new Tag(s_building, s_glasshouse, false),
            new Tag(s_aeroway, s_holding_position, false),
            new Tag(s_shop, s_general, false), new Tag(s_building, s_tank, false),
            new Tag(s_railway, s_monorail, false), new Tag(s_service, s_parking, false),
            new Tag(s_place, s_state, false), new Tag(s_railway, s_proposed, false),
            new Tag(s_shop, s_art, false), new Tag(s_natural, s_hill, false),
            new Tag(s_railway, s_turntable, false), new Tag(s_tourism, s_cabin, false),
            new Tag(s_shop, s_photo, false), new Tag(s_boundary, s_lot, false),
            new Tag(s_shop, s_fishmonger, false), new Tag(s_amenity, s_clinic, false),
            new Tag(s_boundary, s_political, false), new Tag(s_man_made, s_well, false),
            new Tag(s_highway, s_byway, false), new Tag(s_leisure, s_horse_riding, false),
            new Tag(s_service, s_bus, false), new Tag(s_building, s_tower, false),
            new Tag(s_entrance, s_service, false), new Tag(s_shop, s_fabric, false),
            new Tag(s_railway, s_miniature, false), new Tag(s_abutters, s_mixed, false),
            new Tag(s_surface, s_stone, false), new Tag(s_access, s_emergency, false),
            new Tag(s_landuse, s_mine, false), new Tag(s_amenity, s_shower, false),
            new Tag(s_waterway, s_lock, false)
    };
}

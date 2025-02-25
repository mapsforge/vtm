package org.osmdroid.routing.provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.osmdroid.routing.Route;
import org.osmdroid.routing.RouteNode;
import org.osmdroid.routing.RouteProvider;
import org.osmdroid.utils.BonusPackHelper;
import org.osmdroid.utils.HttpConnection;
import org.osmdroid.utils.PolylineEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * get a route between a start and a destination point. It uses OSRM, a free
 * open source routing service based on OpenSteetMap data. <br>
 * See https://github.com/DennisOSRM/Project-OSRM/wiki/Server-api<br>
 * It requests by default the OSRM demo site. Use setService() to request an
 * other (for instance your own) OSRM service. <br>
 * TODO: improve internationalization of instructions
 *
 * @author M.Kergall
 */
public class OSRMRouteProvider extends RouteProvider {

    private static final Logger log = Logger.getLogger(OSRMRouteProvider.class.getName());

    // 1 for 6 digit precision, 10 for 5
    private static final int ENCODING_PRECISION = 1;

    //static final String OSRM_SERVICE = "http://city.informatik.uni-bremen.de:5000/viaroute?";
    //static final String OSRM_SERVICE = "http://city.informatik.uni-bremen.de:5001/viaroute?";
    static final String OSRM_SERVICE = "http://router.project-osrm.org/viaroute?";

    //Note that the result of OSRM is quite close to Cloudmade NavEngine format:
    //http://developers.cloudmade.com/wiki/navengine/JSON_format

    protected String mServiceUrl;
    protected String mUserAgent;

    /**
     * mapping from OSRM directions to MapQuest maneuver IDs:
     */
    static final HashMap<String, Integer> MANEUVERS;

    static {
        MANEUVERS = new HashMap<String, Integer>();
        MANEUVERS.put("0", Integer.valueOf(0)); //No instruction
        MANEUVERS.put("1", Integer.valueOf(1)); //Continue
        MANEUVERS.put("2", Integer.valueOf(6)); //Slight right
        MANEUVERS.put("3", Integer.valueOf(7)); //Right
        MANEUVERS.put("4", Integer.valueOf(8)); //Sharp right
        MANEUVERS.put("5", Integer.valueOf(12)); //U-turn
        MANEUVERS.put("6", Integer.valueOf(5)); //Sharp left
        MANEUVERS.put("7", Integer.valueOf(4)); //Left
        MANEUVERS.put("8", Integer.valueOf(3)); //Slight left
        MANEUVERS.put("9", Integer.valueOf(24)); //Arrived (at waypoint)
        //MANEUVERS.put("10", Integer.valueOf(0)); //"Head" => used by OSRM as the start node
        MANEUVERS.put("11-1", Integer.valueOf(27)); //Round-about, 1st exit
        MANEUVERS.put("11-2", Integer.valueOf(28)); //2nd exit, etc ...
        MANEUVERS.put("11-3", Integer.valueOf(29));
        MANEUVERS.put("11-4", Integer.valueOf(30));
        MANEUVERS.put("11-5", Integer.valueOf(31));
        MANEUVERS.put("11-6", Integer.valueOf(32));
        MANEUVERS.put("11-7", Integer.valueOf(33));
        MANEUVERS.put("11-8", Integer.valueOf(34)); //Round-about, 8th exit
        MANEUVERS.put("15", Integer.valueOf(24)); //Arrived
    }

    //From: Project-OSRM-Web / WebContent / localization / OSRM.Locale.en.js
    // driving directions
    // %s: route name
    // %d: direction => removed
    // <*>: will only be printed when there actually is a route name
    static final HashMap<String, HashMap<String, String>> DIRECTIONS;

    static {
        DIRECTIONS = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> directions;

        directions = new HashMap<String, String>();
        DIRECTIONS.put("en", directions);
        directions.put("0", "Unknown instruction< on %s>");
        directions.put("1", "Continue< on %s>");
        directions.put("2", "Turn slight right< on %s>");
        directions.put("3", "Turn right< on %s>");
        directions.put("4", "Turn sharp right< on %s>");
        directions.put("5", "U-Turn< on %s>");
        directions.put("6", "Turn sharp left< on %s>");
        directions.put("7", "Turn left< on %s>");
        directions.put("8", "Turn slight left< on %s>");
        directions.put("9", "You have reached a waypoint of your trip");
        directions.put("10", "<Go on %s>");
        directions.put("11-1", "Enter roundabout and leave at first exit< on %s>");
        directions.put("11-2", "Enter roundabout and leave at second exit< on %s>");
        directions.put("11-3", "Enter roundabout and leave at third exit< on %s>");
        directions.put("11-4", "Enter roundabout and leave at fourth exit< on %s>");
        directions.put("11-5", "Enter roundabout and leave at fifth exit< on %s>");
        directions.put("11-6", "Enter roundabout and leave at sixth exit< on %s>");
        directions.put("11-7", "Enter roundabout and leave at seventh exit< on %s>");
        directions.put("11-8", "Enter roundabout and leave at eighth exit< on %s>");
        directions.put("11-9", "Enter roundabout and leave at nineth exit< on %s>");
        directions.put("15", "You have reached your destination");

        directions = new HashMap<String, String>();
        DIRECTIONS.put("fr", directions);
        directions.put("0", "Instruction inconnue< sur %s>");
        directions.put("1", "Continuez< sur %s>");
        directions.put("2", "Tournez légèrement à droite< sur %s>");
        directions.put("3", "Tournez à droite< sur %s>");
        directions.put("4", "Tournez fortement à droite< sur %s>");
        directions.put("5", "Faites demi-tour< sur %s>");
        directions.put("6", "Tournez fortement à gauche< sur %s>");
        directions.put("7", "Tournez à gauche< sur %s>");
        directions.put("8", "Tournez légèrement à gauche< sur %s>");
        directions.put("9", "Vous êtes arrivé à une étape de votre voyage");
        directions.put("10", "<Prenez %s>");
        directions.put("11-1", "Au rond-point, prenez la première sortie< sur %s>");
        directions.put("11-2", "Au rond-point, prenez la deuxième sortie< sur %s>");
        directions.put("11-3", "Au rond-point, prenez la troisième sortie< sur %s>");
        directions.put("11-4", "Au rond-point, prenez la quatrième sortie< sur %s>");
        directions.put("11-5", "Au rond-point, prenez la cinquième sortie< sur %s>");
        directions.put("11-6", "Au rond-point, prenez la sixième sortie< sur %s>");
        directions.put("11-7", "Au rond-point, prenez la septième sortie< sur %s>");
        directions.put("11-8", "Au rond-point, prenez la huitième sortie< sur %s>");
        directions.put("11-9", "Au rond-point, prenez la neuvième sortie< sur %s>");
        directions.put("15", "Vous êtes arrivé");

        directions = new HashMap<String, String>();
        DIRECTIONS.put("pl", directions);
        directions.put("0", "Nieznana instrukcja<w %s>");
        directions.put("1", "Kontynuuj jazdę<na %s>");
        directions.put("2", "Skręć lekko w prawo<w %s>");
        directions.put("3", "Skręć w prawo<w %s>");
        directions.put("4", "Skręć ostro w prawo<w %s>");
        directions.put("5", "Zawróć<na %s>");
        directions.put("6", "Skręć ostro w lewo<w %s>");
        directions.put("7", "Skręć w lewo<w %s>");
        directions.put("8", "Skręć lekko w lewo<w %s>");
        directions.put("9", "Dotarłeś do punktu pośredniego");
        directions.put("10", "<Jedź %s>");
        directions.put("11-1", "Wjedź na rondo i opuść je pierwszym zjazdem<w %s>");
        directions.put("11-2", "Wjedź na rondo i opuść je drugim zjazdem<w %s>");
        directions.put("11-3", "Wjedź na rondo i opuść je trzecim zjazdem<w %s>");
        directions.put("11-4", "Wjedź na rondo i opuść je czwartym zjazdem<w %s>");
        directions.put("11-5", "Wjedź na rondo i opuść je piątym zjazdem<w %s>");
        directions.put("11-6", "Wjedź na rondo i opuść je szóstym zjazdem<w %s>");
        directions.put("11-7", "Wjedź na rondo i opuść je siódmym zjazdem<w %s>");
        directions.put("11-8", "Wjedź na rondo i opuść je ósmym zjazdem<w %s>");
        directions.put("11-9", "Wjedź na rondo i opuść je dziewiątym zjazdem<w %s>");
        directions.put("15", "Dotarłeś do celu podróży");
    }

    public OSRMRouteProvider() {
        super();
        mServiceUrl = OSRM_SERVICE;
        mUserAgent = BonusPackHelper.DEFAULT_USER_AGENT; //set user agent to the default one.
    }

    /**
     * allows to request on an other site than OSRM demo site
     *
     * @param serviceUrl ...
     */
    public void setService(String serviceUrl) {
        mServiceUrl = serviceUrl;
    }

    /**
     * allows to send to OSRM service a user agent specific to the app, instead
     * of the default user agent of OSMBonusPack lib.
     *
     * @param userAgent ...
     */
    public void setUserAgent(String userAgent) {
        mUserAgent = userAgent;
    }

    protected String getUrl(List<GeoPoint> waypoints) {
        StringBuffer urlString = new StringBuffer(mServiceUrl);
        for (int i = 0; i < waypoints.size(); i++) {
            GeoPoint p = waypoints.get(i);
            urlString.append("&loc=" + geoPointAsString(p));
        }
        urlString.append(mOptions);
        return urlString.toString();
    }

    @Override
    public Route getRoute(List<GeoPoint> waypoints) {
        String url = getUrl(waypoints);
        log.fine("OSRMRouteManager.getRoute:" + url);

        //String jString = BonusPackHelper.requestStringFromUrl(url);
        HttpConnection connection = new HttpConnection();
        connection.setUserAgent(mUserAgent);
        connection.doGet(url);
        String jString = connection.getContentAsString();
        connection.close();

        if (jString == null) {
            log.severe("OSRMRouteManager::getRoute: request failed.");
            return new Route(waypoints);
        }
        Locale l = Locale.getDefault();
        HashMap<String, String> directions = DIRECTIONS.get(l.getLanguage());
        if (directions == null)
            directions = DIRECTIONS.get("en");
        Route route = new Route();
        try {
            JSONObject jObject = new JSONObject(jString);
            String route_geometry = jObject.getString("route_geometry");
            route.routeHigh = PolylineEncoder.decode(route_geometry, ENCODING_PRECISION);
            JSONArray jInstructions = jObject.getJSONArray("route_instructions");
            int n = jInstructions.length();
            RouteNode lastNode = null;
            for (int i = 0; i < n; i++) {
                JSONArray jInstruction = jInstructions.getJSONArray(i);
                RouteNode node = new RouteNode();
                int positionIndex = jInstruction.getInt(3);
                node.location = route.routeHigh.get(positionIndex);
                node.length = jInstruction.getInt(2) / 1000.0;
                node.duration = jInstruction.getInt(4); //Segment duration in seconds.
                String direction = jInstruction.getString(0);
                String routeName = jInstruction.getString(1);
                if (lastNode != null && "1".equals(direction) && "".equals(routeName)) {
                    //node "Continue" with no route name is useless, don't add it
                    lastNode.length += node.length;
                    lastNode.duration += node.duration;
                } else {
                    node.maneuverType = getManeuverCode(direction);
                    node.instructions = buildInstructions(direction, routeName, directions);
                    //log.debug(direction+"=>"+node.mManeuverType+"; "+node.mInstructions);
                    route.nodes.add(node);
                    lastNode = node;
                }
            }
            JSONObject jSummary = jObject.getJSONObject("route_summary");
            route.length = jSummary.getInt("total_distance") / 1000.0;
            route.duration = jSummary.getInt("total_time");
        } catch (JSONException e) {
            e.printStackTrace();
            return new Route(waypoints);
        }
        if (route.routeHigh.size() == 0) {
            //Create default route:
            route = new Route(waypoints);
        } else {
            route.buildLegs(waypoints);
            BoundingBox bb = new BoundingBox(route.routeHigh);
            //Correcting osmdroid bug #359:
            route.boundingBox = bb;
            //    new BoundingBox(
            //    bb.getLatSouthE6(), bb.getLonWestE6(), bb.getLatNorthE6(), bb.getLonEastE6());
            route.status = Route.STATUS_OK;
        }
        log.fine("OSRMRouteManager.getRoute - finished");
        return route;
    }

    protected int getManeuverCode(String direction) {
        Integer code = MANEUVERS.get(direction);
        if (code != null)
            return code.intValue();

        return 0;
    }

    protected String buildInstructions(String direction, String routeName,
                                       HashMap<String, String> directions) {
        if (directions == null)
            return null;
        direction = directions.get(direction);
        if (direction == null)
            return null;
        String instructions = null;
        if (routeName.equals(""))
            //remove "<*>"
            instructions = direction.replaceFirst("<[^>]*>", "");
        else {
            direction = direction.replace('<', ' ');
            direction = direction.replace('>', ' ');
            instructions = String.format(direction, routeName);
        }
        return instructions;
    }
}

package org.osmdroid.location;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.Tag;
import org.oscim.core.osm.OsmData;
import org.oscim.core.osm.OsmNode;
import org.oscim.utils.osmpbf.OsmPbfReader;
import org.osmdroid.utils.HttpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OverpassPOIProvider implements POIProvider {

    static final Logger log = LoggerFactory
            .getLogger(OverpassPOIProvider.class);

    public static final String TAG_KEY_WEBSITE = "website".intern();

    @Override
    public List<POI> getPOIInside(BoundingBox boundingBox, String query,
                                  int maxResults) {
        HttpConnection connection = new HttpConnection();
        boundingBox.toString();

        String q = "node[\"amenity\"~\"^restaurant$|^pub$\"]("
                + boundingBox.format() + ");out 100;";
        String url = "http://city.informatik.uni-bremen.de/oapi/pbf?data=";
        String encoded;
        try {
            encoded = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        log.debug("request " + url + encoded);
        connection.doGet(url + encoded);
        OsmData osmData = OsmPbfReader.process(connection.getStream());
        ArrayList<POI> pois = new ArrayList<POI>(osmData.getNodes().size());

        for (OsmNode n : osmData.getNodes()) {
            POI p = new POI(POI.POI_SERVICE_4SQUARE);
            p.id = Long.toString(n.id);

            p.location = new GeoPoint(n.lat, n.lon);
            Tag t;

            if ((t = n.tags.get(Tag.KEY_NAME)) != null)
                p.description = t.value;

            if ((t = n.tags.get(Tag.KEY_AMENITY)) != null)
                p.type = t.value;

            if ((t = n.tags.get(TAG_KEY_WEBSITE)) != null) {
                log.debug(p.description + " " + t.value);
                p.url = t.value;
            }
            pois.add(p);
        }
        return pois;
    }
}

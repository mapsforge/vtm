/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016 devemux86
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
package org.oscim.utils.overpass;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.oscim.core.Tag;
import org.oscim.core.TagSet;
import org.oscim.core.osm.Bound;
import org.oscim.core.osm.OsmData;
import org.oscim.core.osm.OsmElement;
import org.oscim.core.osm.OsmMember;
import org.oscim.core.osm.OsmNode;
import org.oscim.core.osm.OsmRelation;
import org.oscim.core.osm.OsmWay;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class OverpassAPIReader {
    private static final String OVERPASS_API = "http://city.informatik.uni-bremen.de/oapi/interpreter";
    private static final int RESPONSECODE_OK = 200;

    /**
     * The timeout we use for the HttpURLConnection.
     */
    private static final int TIMEOUT = 15000;

    /**
     * The base url of the server. Defaults to.
     * "http://www.openstreetmap.org/api/0.5".
     */
    private final String myBaseUrl = OVERPASS_API;

    /**
     * The http connection used to retrieve data.
     */
    private HttpURLConnection myActiveConnection;

    /**
     * The stream providing response data.
     */
    private InputStream responseStream;

    private final String query;

    /**
     * Creates a new instance with the specified geographical coordinates.
     *
     * @param left    The longitude marking the left edge of the bounding box.
     * @param right   The longitude marking the right edge of the bounding box.
     * @param top     The latitude marking the top edge of the bounding box.
     * @param bottom  The latitude marking the bottom edge of the bounding box.
     * @param baseUrl (optional) The base url of the server (eg.
     *                http://www.openstreetmap.org/api/0.5).
     */
    public OverpassAPIReader(final double left, final double right,
                             final double top, final double bottom, final String baseUrl,
                             final String query) {

        String bbox = "(" + Math.min(top, bottom) + "," + Math.min(left, right)
                + "," + Math.max(top, bottom) + "," + Math.max(left, right)
                + ")";

        this.query = query.replaceAll("\\{\\{bbox\\}\\}", bbox);

    }

    /**
     * Open a connection to the given url and return a reader on the input
     * stream from that connection.
     *
     * @param pUrlStr The exact url to connect to.
     * @return An reader reading the input stream (servers answer) or
     * <code>null</code>.
     * @throws IOException on io-errors
     */
    private InputStream getInputStream(final String pUrlStr) throws IOException {
        URL url;
        int responseCode;
        String encoding;

        url = new URL(pUrlStr);
        myActiveConnection = (HttpURLConnection) url.openConnection();

        myActiveConnection.setRequestProperty("Accept-Encoding",
                "gzip, deflate");

        responseCode = myActiveConnection.getResponseCode();

        if (responseCode != RESPONSECODE_OK) {
            String message;
            String apiErrorMessage;

            apiErrorMessage = myActiveConnection.getHeaderField("Error");

            if (apiErrorMessage != null) {
                message = "Received API HTTP response code " + responseCode
                        + " with message \"" + apiErrorMessage
                        + "\" for URL \"" + pUrlStr + "\".";
            } else {
                message = "Received API HTTP response code " + responseCode
                        + " for URL \"" + pUrlStr + "\".";
            }

            throw new IOException(message);
        }

        myActiveConnection.setConnectTimeout(TIMEOUT);

        encoding = myActiveConnection.getContentEncoding();

        responseStream = myActiveConnection.getInputStream();
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            responseStream = new GZIPInputStream(responseStream);
        } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
            responseStream = new InflaterInputStream(responseStream,
                    new Inflater(true));
        }

        return responseStream;
    }

    class TmpRelation {
        Long id;
        String type;
        String role;
    }

    private final List<Bound> bounds = new ArrayList<Bound>();
    private Map<Long, OsmNode> nodesById = new HashMap<Long, OsmNode>();
    private Map<Long, OsmWay> waysById = new HashMap<Long, OsmWay>();
    private Map<Long, OsmRelation> relationsById = new HashMap<Long, OsmRelation>();
    private Map<OsmRelation, List<TmpRelation>> relationMembersForRelation =
            new HashMap<OsmRelation, List<TmpRelation>>();

    private final Collection<OsmNode> ownNodes = new ArrayList<OsmNode>(10000);
    private final Collection<OsmWay> ownWays = new ArrayList<OsmWay>(1000);
    private final Collection<OsmRelation> ownRelations = new ArrayList<OsmRelation>(
            100);

    public void parse(InputStream in) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        try {
            JsonParser jp = jsonFactory.createParser(in);

            JsonToken t;
            while ((t = jp.nextToken()) != null) {
                if (t == JsonToken.START_OBJECT) {
                    jp.nextToken();

                    String name = jp.getCurrentName();
                    jp.nextToken();

                    if ("type".equals(name)) {
                        String type = jp.getText();

                        if ("node".equals(type))
                            parseNode(jp);

                        else if ("way".equals(type))
                            parseWay(jp);

                        else if ("relation".equals(type))
                            parseRelation(jp);
                    }
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    private void parseNode(JsonParser jp) throws JsonParseException,
            IOException {

        long id = 0;
        double lat = 0, lon = 0;
        TagSet tags = null;

        while (jp.nextToken() != JsonToken.END_OBJECT) {

            String name = jp.getCurrentName();
            jp.nextToken();

            if ("id".equals(name))
                id = jp.getLongValue();

            else if ("lat".equals(name))
                lat = jp.getDoubleValue();

            else if ("lon".equals(name))
                lon = jp.getDoubleValue();

            else if ("tags".equals(name))
                tags = parseTags(jp);

        }

        // log("node: "+id + " "+ lat + " " + lon);
        OsmNode node = new OsmNode(lat, lon, tags, id);
        ownNodes.add(node);
        nodesById.put(Long.valueOf(id), node);
    }

    private void parseWay(JsonParser jp) throws JsonParseException, IOException {

        long id = 0;
        TagSet tags = null;
        ArrayList<OsmNode> wayNodes = new ArrayList<OsmNode>();

        while (jp.nextToken() != JsonToken.END_OBJECT) {

            String name = jp.getCurrentName();
            jp.nextToken();

            if ("id".equals(name))
                id = jp.getLongValue();

            else if ("nodes".equals(name)) {
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    Long nodeId = Long.valueOf(jp.getLongValue());

                    OsmNode node = nodesById.get(nodeId);
                    if (node != null)
                        // log("missing node " + nodeId);
                        // else
                        wayNodes.add(node);
                }
            } else if ("tags".equals(name))
                tags = parseTags(jp);
        }

        // log("way: "+ id + " " + wayNodes.size());
        OsmWay way = new OsmWay(tags, id, wayNodes);
        ownWays.add(way);
        waysById.put(Long.valueOf(id), way);
    }

    private void parseRelation(JsonParser jp) throws JsonParseException,
            IOException {

        long id = 0;
        TagSet tags = null;
        ArrayList<TmpRelation> members = new ArrayList<TmpRelation>();

        while (jp.nextToken() != JsonToken.END_OBJECT) {

            String name = jp.getCurrentName();
            jp.nextToken();

            if ("id".equals(name))
                id = jp.getLongValue();

            else if ("members".equals(name)) {
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    TmpRelation member = new TmpRelation();

                    while (jp.nextToken() != JsonToken.END_OBJECT) {
                        name = jp.getCurrentName();
                        jp.nextToken();

                        if ("type".equals(name))
                            member.type = jp.getText();

                        else if ("ref".equals(name))
                            member.id = Long.valueOf(jp.getLongValue());

                        else if ("role".equals(name))
                            member.role = jp.getText();
                    }
                    members.add(member);
                }
            } else if ("tags".equals(name))
                tags = parseTags(jp);
        }

        OsmRelation relation = new OsmRelation(tags, id, members.size());
        ownRelations.add(relation);
        relationsById.put(Long.valueOf(id), relation);
        relationMembersForRelation.put(relation, members);
    }

    private static TagSet parseTags(JsonParser jp) throws JsonParseException,
            IOException {

        TagSet tags = null;

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String key = jp.getCurrentName();
            jp.nextToken();
            String val = jp.getText();
            if (tags == null)
                tags = new TagSet(4);

            tags.add(new Tag(key, val, false));

        }

        return tags;
    }

    private static void log(String msg) {
        System.out.println(msg);
    }

    public OsmData getData() {

        String encoded;
        try {
            encoded = URLEncoder.encode(this.query, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        System.out.println(myBaseUrl + "?data=" + encoded);

        InputStream inputStream = null;

        try {
            inputStream = getInputStream(myBaseUrl + "?data=[out:json];" + encoded);

            parse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //...
                }
            inputStream = null;
        }

        for (Entry<OsmRelation, List<TmpRelation>> entry : relationMembersForRelation
                .entrySet()) {

            OsmRelation relation = entry.getKey();

            for (TmpRelation member : entry.getValue()) {

                OsmElement memberObject = null;

                if ("node".equals(member)) {
                    memberObject = nodesById.get(member.id);
                } else if ("way".equals(member)) {
                    memberObject = waysById.get(member.id);
                } else if ("relation".equals(member)) {
                    memberObject = relationsById.get(member.id);
                } else {
                    // log("missing relation " + member.id);
                    continue;
                }

                if (memberObject != null) {
                    OsmMember ownMember = new OsmMember(member.role,
                            memberObject);

                    relation.relationMembers.add(ownMember);
                }
            }
        }
        log("nodes: " + ownNodes.size() + " ways: " + ownWays.size()
                + " relations: " + ownRelations.size());

        // give up references to original collections
        nodesById = null;
        waysById = null;
        relationsById = null;
        relationMembersForRelation = null;

        return new OsmData(bounds, ownNodes, ownWays, ownRelations);
    }
}

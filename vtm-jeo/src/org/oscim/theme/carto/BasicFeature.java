package org.oscim.theme.carto;

/* Copyright 2013 The jeo project. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.jeo.util.Util;
import io.jeo.vector.Feature;
import io.jeo.vector.Field;
import io.jeo.vector.Schema;
import io.jeo.vector.SchemaBuilder;

import org.osgeo.proj4j.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Basic feature implementation.
 *
 * @author Justin Deoliveira, Boundless
 *
 * Adapted by Jan Lippert to the changes made in https://github.com/jeo/jeo/commit/e28695ccc8530ae6d1a2dff1fbd1e11449ef949f
 */
public class BasicFeature implements Feature {

    /**
     * feature identifier
     */
    protected String id;

    /**
     * Underlying feature storage.
     */
    protected Storage storage;

    /**
     * Constructor taking a feature identifier.
     *
     * @param id The feature id, if <code>null</code> an identifier will be generated.
     */
    public BasicFeature(String id) {
        this(id, (Schema) null);
    }

    /**
     * Constructor taking a feature identifier and an explicit schema object.
     *
     * @param id The feature id, if <code>null</code> an identifier will be generated.
     * @param schema The feature schema, if <code>null</code> the schema will be always be derived
     */
    public BasicFeature(String id, Schema schema) {
        this(id, (Storage)
                (schema != null ? new ListStorage(null, schema) : new MapStorage(null, schema)));
    }

    /**
     * Constructs a feature from an identifier and a list of values.
     *
     * @param id The feature id, if <code>null</code> an identifier will be generated.
     * @param values The feature values.
     */
    public BasicFeature(String id, List<Object> values) {
        this(id, values, null);
    }

    /**
     * Constructs a feature from an identifier, a list of values, and a schema.
     *
     * @param id The feature id, if <code>null</code> an identifier will be generated.
     * @param values The feature values.
     * @param schema The feature schema, if <code>null</code> the schema will be always be derived
     */
    public BasicFeature(String id, List<Object> values, Schema schema) {
        this(id, new ListStorage(values, schema));
    }

    /**
     * Constructs a feature from an identifier and a map of values.
     *
     * @param id The feature id, if <code>null</code> an identifier will be generated.
     * @param values The feature values.
     */
    public BasicFeature(String id, Map<String,Object> values) {
        this(id, values, null);
    }

    /**
     * Constructs a feature from an identifier, a map of values, and a schema.
     *
     * @param id The feature id, if <code>null</code> an identifier will be generated.
     * @param values The feature values.
     * @param schema The feature schema, if <code>null</code> the schema will be always be derived
     */
    public BasicFeature(String id, Map<String,Object> values, Schema schema) {
        this(id, new MapStorage(values, schema));
    }

    /**
     * Constructor taking an identifier and feature storage object directly.
     * <p>
     * This constructor is typically only used for subclasses that need to implement custom feature
     * storage.
     * </p>
     */
    protected BasicFeature(String id, Storage storage) {
        this.id = id != null ? id : Util.uuid();
        this.storage = storage;
    }

    @Override
    public String id() {
        return id;
    }

    //    @Override
//    public CoordinateReferenceSystem getCRS() {
//        return crs;
//    }
//
    public BasicFeature crs(CoordinateReferenceSystem crs) {
        storage.crs(crs);
        return this;
    }

    @Override
    public boolean has(String key) {
        return storage.has(key);
    }

    @Override
    public Object get(String key) {
        return storage.get(key);
    }

    @Override
    public BasicFeature put(String key, Object val) {
        storage.put(key, val);
        return this;
    }

    @Override
    public BasicFeature put(Geometry g) {
        //TODO:optimize before triggering schema creation
        Field gf = storage.schema(true).geometry();
        if (gf == null) {
            throw new IllegalArgumentException("Feature schema has no geometry");
        }

        return put(gf.name(), g);
    }


    @Override
    public Geometry geometry() {
        return storage.geometry();
    }

    @Override
    public Map<String,Object> map() {
        return storage.map();
    }

    @Override
    public String toString() {
        return new StringBuilder(id).append(map()).toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((storage == null) ? 0 : storage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BasicFeature other = (BasicFeature) obj;
        if (id == null) {
            return other.id == null;
        }

        return id.equals(other.id);
    }



    protected static abstract class Storage {
        Schema schema;
        CoordinateReferenceSystem crs;

        protected Storage(Schema schema) {
            this.schema = schema;
        }

        protected Storage crs(CoordinateReferenceSystem crs) {
            this.crs = crs;
            return this;
        }

        protected Schema schema() {
            return schema(true);
        }

        protected Schema schema(boolean derive) {
            if (schema == null && derive) {
                schema = buildSchema();

                // hack to apply crs override
                if (crs != null && schema.crs() == null) {
                    schema = SchemaBuilder.crs(schema, crs);
                }
            }
            return schema;
        }

        protected Geometry geometry() {
            if (schema != null) {
                Field f = schema.geometry();
                if (f != null) {
                    return (Geometry) get(f.name());
                }
            }

            return findGeometry();
        }

        protected CoordinateReferenceSystem crs() {
            if (crs != null) {
                return crs;
            }

            if (schema != null) {
                return schema.crs();
            }
            return null;
        }

        /**
         * Method for subclasses to implement to build a schema for the feature from its underlying
         * attributes.
         */
        protected abstract Schema buildSchema();

        /**
         * Method for subclasses to implement in order to find a geometry object when no schema
         * information is available.
         */
        protected abstract Geometry findGeometry();

        protected abstract Object get(String key);

        protected abstract Object get(int index);

        protected abstract void put(String key, Object value);

        protected abstract void set(int index, Object value);

        protected abstract List<Object> list();

        protected abstract Map<String,Object> map();

        protected abstract boolean has(String key);
    }

    static class ListStorage extends Storage {

        List<Object> list;

        ListStorage(List<Object> values, Schema schema) {
            super(schema);
            this.list = pad(values, schema);
        }

        List<Object> pad(List<Object> values, Schema schema) {
            //copy list passed in
            values = values != null ? new ArrayList<Object>(values) : new ArrayList<Object>();

            //expand up to size of schema if necessary
            if (schema != null) {
                while(values.size() < schema.size()) {
                    values.add(null);
                }
            }
            return values;
        }

        @Override
        protected Geometry findGeometry() {
            for (Object o : list) {
                if (o instanceof Geometry) {
                    return (Geometry) o;
                }
            }

            return null;
        }

        @Override
        protected Schema buildSchema() {
            List<Field> fields = new ArrayList<Field>();
            int i = 0;
            boolean g = false;
            for (Object o : list) {
                if (o instanceof Geometry && !g) {
                    //first geometry
                    fields.add(new Field("geometry", o.getClass()));
                    g = true;
                }
                else {
                    //regular field
                    fields.add(new Field(String.format(Locale.ROOT,"field%d", i++), o != null ? o.getClass():null));
                }
            }
            return new Schema("feature", fields);
        }

        @Override
        protected boolean has(String key) {
            return schema().indexOf(key) >= 0;
        }

        @Override
        protected Object get(int i) {
            return list.get(i);
        }

        @Override
        protected void set(int i, Object value) {
            list.set(i,  value);
        }

        @Override
        protected Object get(String key) {
            int i = schema().indexOf(key);
            return i != -1 ? get(i) : null;
        }

        @Override
        protected void put(String key, Object val) {
            int i = schema().indexOf(key);
            if (i == -1) {
                throw new IllegalArgumentException("No such key " + key);
            }
            set(i, val);
        }

        @Override
        protected List<Object> list() {
            return Collections.unmodifiableList(list);
        }

        @Override
        protected Map<String, Object> map() {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            for (Field f : schema()) {
                map.put(f.name(), get(f.name()));
            }
            return map;
        }
    }

    static class MapStorage extends Storage {

        Map<String,Object> map;

        MapStorage(Map<String,Object> values, Schema schema) {
            super(schema);
            this.map = values != null ?
                    new LinkedHashMap<String,Object>(values) : new LinkedHashMap<String,Object>();
        }

        @Override
        protected Schema buildSchema() {
            List<Field> fields = new ArrayList<Field>();
            for (Map.Entry<String, Object> e : map.entrySet()) {
                fields.add(new Field(e.getKey(), e.getValue() != null ? e.getValue().getClass() : null));
            }

            return new Schema("feature", fields);
        }

        @Override
        protected Geometry findGeometry() {
            for (Object obj : map.values()) {
                if (obj instanceof Geometry) {
                    return (Geometry) obj;
                }
            }

            return null;
        }

        @Override
        protected boolean has(String key) {
            return map.containsKey(key);
        }

        @Override
        protected Object get(String key) {
            return map.get(key);
        }

        @Override
        protected void put(String key, Object val) {
            if (!map.containsKey(key)) {
                //new field, clear cached schema
                schema = null;
            }
            map.put(key, val);
        }

        @Override
        protected Object get(int index) {
            return Util.get(map, index);
        }

        @Override
        protected void set(int index, Object value) {
            Util.set(map, index, value);
        }

        @Override
        protected List<Object> list() {
            List<Object> list = new ArrayList<Object>();
            for (Field f : schema()) {
                list.add(get(f.name()));
            }
            return list;
        }

        @Override
        protected Map<String,Object> map() {
            return Collections.unmodifiableMap(map);
        }
    }
}

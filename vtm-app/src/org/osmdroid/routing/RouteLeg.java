package org.osmdroid.routing;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Road Leg is the portion of the route between 2 waypoints (intermediate points
 * requested)
 *
 * @author M.Kergall
 */
public class RouteLeg implements Parcelable {
    //static final Logger log = LoggerFactory.getLogger(RouteLeg.class);

    /**
     * in km
     */
    public double length;
    /**
     * in sec
     */
    public double duration;
    /**
     * starting node of the leg, as index in nodes array
     */
    public int startNodeIndex;
    /**
     * and ending node
     */
    public int endNodeIndex;

    public RouteLeg() {
        length = duration = 0.0;
        startNodeIndex = endNodeIndex = 0;
    }

    public RouteLeg(int startNodeIndex, int endNodeIndex,
                    List<RouteNode> nodes) {
        this.startNodeIndex = startNodeIndex;
        this.endNodeIndex = endNodeIndex;
        length = duration = 0.0;

        for (int i = startNodeIndex; i <= endNodeIndex; i++) {
            RouteNode node = nodes.get(i);
            length += node.length;
            duration += node.duration;
        }
        //log.debug("Leg: " + startNodeIndex + "-" + endNodeIndex
        //        + ", length=" + length + "km, duration=" + duration + "s");
    }

    //--- Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(length);
        out.writeDouble(duration);
        out.writeInt(startNodeIndex);
        out.writeInt(endNodeIndex);
    }

    public static final Parcelable.Creator<RouteLeg> CREATOR = new Parcelable.Creator<RouteLeg>() {
        @Override
        public RouteLeg createFromParcel(Parcel in) {
            RouteLeg rl = new RouteLeg();
            rl.length = in.readDouble();
            rl.duration = in.readDouble();
            rl.startNodeIndex = in.readInt();
            rl.endNodeIndex = in.readInt();
            return rl;
        }

        @Override
        public RouteLeg[] newArray(int size) {
            return new RouteLeg[size];
        }
    };
}

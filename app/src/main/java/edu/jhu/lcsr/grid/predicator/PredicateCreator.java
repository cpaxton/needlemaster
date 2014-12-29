package edu.jhu.lcsr.grid.predicator;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;

import edu.jhu.lcsr.grid.needlegame.Needle;

/**
 * Created by cpaxton on 12/29/14.
 */
public class PredicateCreator {

    final int iter = 1000;
    PathMeasure pm;

    Needle needle;

    public PredicateCreator(Needle needle) {
        this.needle = needle;
        pm = new PathMeasure();
    }


    /*********************************************************************************************/

    /**
     * get the distance from the needle to some surface
     * @param surface
     * @return
     */
    double getNeedleDistance(Path surface) {
        return getDistance(needle.getRealX(), needle.getRealY(), surface);
    }

    /**
     * Get distance from a point to a path
     * @param x point x (real coordinates)
     * @param y point y (real coordinates)
     * @param surface path (real coordinates)
     * @return distance
     */
    double getDistance(float x, float y, Path surface) {
        float dist = Float.MAX_VALUE;
        pm.setPath(surface, true);

        float[] pos = {0f, 0f};
        float[] tan = {0f, 0f};

        RectF tmp = new RectF();
        surface.computeBounds(tmp, true);
        Region region = new Region();
        region.setPath(surface, new Region((int) tmp.left, (int) tmp.top, (int) tmp.right, (int) tmp.bottom));

        if (region.contains((int)x, (int)y)) {
            return 0f;
        } else {

            float len = pm.getLength();
            for (int i = 0; i < iter; i++) {
                float pathDist = (float) i / iter * len;
                pm.getPosTan(pathDist, pos, tan);

                float tempDist = (float) Math.sqrt((pos[0] * pos[0]) + (pos[1] * pos[1]));
                if (tempDist < dist) dist = tempDist;
            }
            return dist;
        }
    }

}

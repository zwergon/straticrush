/* 
 * Copyright 2017 lecomtje.
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
package stratifx.canvas.graphics;


/**
 * A rectangle defined by its upper left (included) and lower right (not included) corners.
 *
 * <pre>
 *   1##############
 *   ###############
 *   ###############
 *                  2
 * </pre>
 *
 * This corresponds to a Rect of width = x2 - x1 and height = y2 - y1.
 * <p>
 * Rect and Box represents the same concept, but their different definition makes them suitable for
 * use in different situations.
 *
 * @author <a href="mailto:jacob.dreyer@geosoft.no">Jacob Dreyer</a>
 */
public class GBox implements Cloneable {
    public int x1;

    public int x2;

    public int y1;

    public int y2;

    /**
     * Create an empty box.
     */
    public GBox() {
        set(0, 0, 0, 0);
    }

    /**
     * Create a new box as a copy of the specified box.
     *
     * @param box Box to copy.
     */
    public GBox(GBox box) {
        set(box.x1, box.y1, box.x2, box.y2);
    }

    /**
     * Create a new box with specified coordinates. The box includes the (x1,y1) as upper left
     * corner. The lower right corner (x2,y2) is just outside the box.
     *
     * @param x1 X of upper left corner (inclusive).
     * @param y1 Y of upper left corner (inclusive).
     * @param x2 X of lower right corner (not inclusive)
     * @param y2 Y of lower right corner (not inclusive).
     */
    public GBox(int x1, int y1, int x2, int y2) {
        set(x1, y1, x2, y2);
    }

    /**
     * Create a new box based on the specified rectangle.
     *
     * @param rectangle Rectangle to copy.
     */
    public GBox(GRect rectangle) {
        x1 = rectangle.x;
        y1 = rectangle.y;
        x2 = rectangle.x + rectangle.width;
        y2 = rectangle.y + rectangle.height;
    }

    /**
     * Copy the specified box.
     *
     * @param box Box to copy.
     */
    public void copy(GBox box) {
        set(box.x1, box.y1, box.x2, box.y2);
    }

    /**
     * Clone this box.
     *
     * @return Clone of this box.
     */
    @Override
    public Object clone() {
        try {
            GBox clone = (GBox) super.clone();
            clone.set(x1, y1, x2, y2);
            return clone;
        }
        catch (CloneNotSupportedException e) {
            return new GBox(x1, y1, x2, y2);
        }
    }

    /**
     * Set the parameters of this box.
     *
     * @param x1 X coordinate of upper left corner of box.
     * @param y1 Y coordinate of upper left corner of box.
     * @param x2 X coordinate of lower right corner of box.
     * @param y2 Y coordinate of lower right corner of box.
     */
    public final void set(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Check if the specified point is ionside this box.
     *
     * @param x X coordinate of point to check.
     * @param y Y coordinate of point to check.
     * @return True if the point is inside this box, false otherwise.
     */
    public boolean isInside(int x, int y) {
        return x >= x1 && x < x2 && y >= y1 && y < y2;
    }

    /**
     * Return true if this box is inside the specified box.
     *
     * @param box Box to check if this is inside of.
     * @return True if this box in inside the specified box, false otherwise.
     */
    public boolean isInsideOf(GBox box) {
        return x1 >= box.x1 && y1 >= box.y1 && x2 <= box.x2 && y2 <= box.y2;
    }

    /**
     * Return true if this box overlaps the specified box.
     *
     * @param box Box to check if this is inside of.
     * @return True if this box overlaps the specified box, false otherwise.
     */
    public boolean isOverlapping(GBox box) {
        return x2 > box.x1 && y2 > box.y1 && x1 < box.x2 && y1 < box.y2;
    }

    /**
     * Return true if this box overlaps the specified rectangle.
     *
     * @param rectangle Rectnagle to check if this is inside of.
     * @return True if this box overlaps the specified rectangle, false otherwise.
     */
    public boolean isOverlapping(GRect rectangle) {
        return x2 > rectangle.x && x1 < rectangle.x + rectangle.width && y2 > rectangle.y
                && y1 < rectangle.y + rectangle.height;
    }

    /**
     * Offset this box a specified distance in x and y direction.
     *
     * @param dx Offset in x direction.
     * @param dy Offset in y direction.
     */
    public void offset(int dx, int dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
    }

    /**
     * Return a string representation of this box.
     *
     * @return String representation of this box.
     */
    @Override
    public String toString() {
        return "Box: " + "y1=" + y1 + " y2=" + y2 + " x1=" + x1 + " x2=" + x2;
    }
}

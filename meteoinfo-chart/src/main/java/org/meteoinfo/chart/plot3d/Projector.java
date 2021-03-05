/*----------------------------------------------------------------------------------------*
 * Projector.java version 1.7                                                  Nov  8 1996 *
 * Projector.java version 1.71                                                May 14 1997 *
 *                                                                                        *
 * Copyright (c) Yanto Suryono <yanto@fedu.uec.ac.jp>                                     *
 *                                                                                        *
 * This program is free software; you can redistribute it and/or modify it                *
 * under the terms of the GNU Lesser General Public License as published by the                  *
 * Free Software Foundation; either version 2 of the License, or (at your option)         *
 * any later version.                                                                     *
 *                                                                                        *
 * This program is distributed in the hope that it will be useful, but                    *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or          *
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for               *
 * more details.                                                                          *
 *                                                                                        *
 * You should have received a copy of the GNU Lesser General Public License along                *
 * with this program; if not, write to the Free Software Foundation, Inc.,                *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA                                  *
 *                                                                                        *
 *----------------------------------------------------------------------------------------*/
package org.meteoinfo.chart.plot3d;

import org.meteoinfo.common.MIMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The class <code>Projector</code> projects points in 3D space to 2D space.
 *
 * @author Yanto Suryono
 */
public final class Projector {

    private float scale_x, scale_y, scale_z;       // 3D scaling factor
    private float distance;                        // distance to object
    private float _2D_scale_x, _2D_scale_y;        // 2D scaling factor
    private float rotation, elevation;             // rotation and elevation angle
    private float sin_rotation, cos_rotation;      // sin and cos of rotation angle
    private float sin_elevation, cos_elevation;    // sin and cos of elevation angle
    private int _2D_trans_x, _2D_trans_y;        // 2D translation
    private int x1, x2, y1, y2;                  // projection area 
    private int center_x, center_y;              // center of projection area           

    private int trans_x, trans_y;
    private float factor_x, factor_y;
    private float sx_cos, sy_cos, sz_cos;
    private float sx_sin, sy_sin, sz_sin;

    private final float DEGTORAD = (float) Math.PI / 180;

    //was static in SurfaceVertex ! now Dynamic in Projector
    float zmin, zmax;
    float zfactor;

    /**
     * The constructor of <code>Projector</code>.
     */
    public Projector() {
        setScaling(1);
        setRotationAngle(0);
        setElevationAngle(0);
        setDistance(10);
        set2DScaling(1);
        set2DTranslation(0, 0);
    }

    /**
     * Sets the projection area.
     *
     * @param r the projection area
     */
    public void setProjectionArea(Rectangle r) {
        x1 = r.x;
        x2 = x1 + r.width;
        y1 = r.y;
        y2 = y1 + r.height;
        center_x = (x1 + x2) / 2;
        center_y = (y1 + y2) / 2;

        trans_x = center_x + _2D_trans_x;
        trans_y = center_y + _2D_trans_y;
        
        this.update2DScaling();
    }

    /**
     * Sets the rotation angle.
     *
     * @param angle the rotation angle in degrees
     */
    public void setRotationAngle(float angle) {
        rotation = angle;
        sin_rotation = (float) Math.sin(angle * DEGTORAD);
        cos_rotation = (float) Math.cos(angle * DEGTORAD);

        sx_cos = -scale_x * cos_rotation;
        sx_sin = -scale_x * sin_rotation;
        sy_cos = -scale_y * cos_rotation;
        sy_sin = scale_y * sin_rotation;                
    }

    /**
     * Gets current rotation angle.
     *
     * @return the rotation angle in degrees.
     */
    public float getRotationAngle() {
        return rotation;
    }

    /**
     * Gets the sine of rotation angle.
     *
     * @return the sine of rotation angle
     */
    public float getSinRotationAngle() {
        return sin_rotation;
    }

    /**
     * Gets the cosine of rotation angle.
     *
     * @return the cosine of rotation angle
     */
    public float getCosRotationAngle() {
        return cos_rotation;
    }

    /**
     * Sets the elevation angle.
     *
     * @param angle the elevation angle in degrees
     */
    public void setElevationAngle(float angle) {
        elevation = angle;
        sin_elevation = (float) Math.sin(angle * DEGTORAD);
        cos_elevation = (float) Math.cos(angle * DEGTORAD);
        sz_cos = scale_z * cos_elevation;
        sz_sin = scale_z * sin_elevation;
    }

    /**
     * Gets current elevation angle.
     *
     * @return the elevation angle in degrees.
     */
    public float getElevationAngle() {
        return elevation;
    }

    /**
     * Gets the sine of elevation angle.
     *
     * @return the sine of elevation angle
     */
    public float getSinElevationAngle() {
        return sin_elevation;
    }

    /**
     * Gets the cosine of elevation angle.
     *
     * @return the cosine of elevation angle
     */
    public float getCosElevationAngle() {
        return cos_elevation;
    }

    /**
     * Sets the projector distance.
     *
     * @param new_distance the new distance
     */
    public void setDistance(float new_distance) {
        distance = new_distance;
        factor_x = distance * _2D_scale_x;
    }

    /**
     * Gets the projector distance.
     *
     * @return the projector distance
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Sets the scaling factor in x direction.
     *
     * @param scaling the scaling factor
     */
    public void setXScaling(float scaling) {
        scale_x = scaling;
        sx_cos = -scale_x * cos_rotation;
        sx_sin = -scale_x * sin_rotation;
    }

    /**
     * Gets the scaling factor in x direction.
     *
     * @return the scaling factor
     */
    public float getXScaling() {
        return scale_x;
    }

    /**
     * Sets the scaling factor in y direction.
     *
     * @param scaling the scaling factor
     */
    public void setYScaling(float scaling) {
        scale_y = scaling;
        sy_cos = -scale_y * cos_rotation;
        sy_sin = scale_y * sin_rotation;
    }

    /**
     * Gets the scaling factor in y direction.
     *
     * @return the scaling factor
     */
    public float getYScaling() {
        return scale_y;
    }

    /**
     * Sets the scaling factor in z direction.
     *
     * @param scaling the scaling factor
     */
    public void setZScaling(float scaling) {
        scale_z = scaling;
        sz_cos = scale_z * cos_elevation;
        sz_sin = scale_z * sin_elevation;
    }

    /**
     * Gets the scaling factor in z direction.
     *
     * @return the scaling factor
     */
    public float getZScaling() {
        return scale_z;
    }

    /**
     * Sets the scaling factor in all direction.
     *
     * @param x the scaling factor in x direction
     * @param y the scaling factor in y direction
     * @param z the scaling factor in z direction
     */
    public void setScaling(float x, float y, float z) {
        scale_x = x;
        scale_y = y;
        scale_z = z;

        sx_cos = -scale_x * cos_rotation;
        sx_sin = -scale_x * sin_rotation;
        sy_cos = -scale_y * cos_rotation;
        sy_sin = scale_y * sin_rotation;
        sz_cos = scale_z * cos_elevation;
        sz_sin = scale_z * sin_elevation;
    }

    /**
     * Sets the same scaling factor for all direction.
     *
     * @param scaling the scaling factor
     */
    public void setScaling(float scaling) {
        scale_x = scale_y = scale_z = scaling;

        sx_cos = -scale_x * cos_rotation;
        sx_sin = -scale_x * sin_rotation;
        sy_cos = -scale_y * cos_rotation;
        sy_sin = scale_y * sin_rotation;
        sz_cos = scale_z * cos_elevation;
        sz_sin = scale_z * sin_elevation;
    }

    /**
     * Sets the 2D scaling factor.
     *
     * @param scaling the scaling factor
     */
    public void set2DScaling(float scaling) {
        _2D_scale_x = scaling;
        _2D_scale_y = scaling;
        factor_x = distance * _2D_scale_x;
        factor_y = distance * _2D_scale_y;
    }
    
    /**
     * Sets the x 2D scaling factor.
     *
     * @param scaling the x scaling factor
     */
    public void setX2DScaling(float scaling) {
        _2D_scale_x = scaling;
        factor_x = distance * _2D_scale_x;
    }
    
    /**
     * Sets the 2D scaling factor.
     *
     * @param scaling the scaling factor
     */
    public void setY2DScaling(float scaling) {
        _2D_scale_y = scaling;
        factor_y = distance * _2D_scale_y;
    }

    /**
     * Gets the x 2D scaling factor.
     *
     * @return the x scaling factor
     */
    public float getX2DScaling() {
        return _2D_scale_x;
    }
    
    /**
     * Gets the x 2D scaling factor.
     *
     * @return the x scaling factor
     */
    public float getY2DScaling() {
        return _2D_scale_y;
    }

    /**
     * Sets the 2D translation.
     *
     * @param x the x translation
     * @param y the y translation
     */
    public void set2DTranslation(int x, int y) {
        _2D_trans_x = x;
        _2D_trans_y = y;

        trans_x = center_x + _2D_trans_x;
        trans_y = center_y + _2D_trans_y;
    }

    /**
     * Sets the 2D x translation.
     *
     * @param x the x translation
     */
    public void set2D_xTranslation(int x) {
        _2D_trans_x = x;
        trans_x = center_x + _2D_trans_x;
    }

    /**
     * Gets the 2D x translation.
     *
     * @return the x translation
     */
    public int get2D_xTranslation() {
        return _2D_trans_x;
    }

    /**
     * Sets the 2D y translation.
     *
     * @param y the y translation
     */
    public void set2D_yTranslation(int y) {
        _2D_trans_y = y;
        trans_y = center_y + _2D_trans_y;
    }

    /**
     * Gets the 2D y translation.
     *
     * @return the y translation
     */
    public int get2D_yTranslation() {
        return _2D_trans_y;
    }

    /**
     * Projects 3D points.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return Projected point
     */
    public final Point project(float x, float y, float z) {
        float temp;

        // rotates
        temp = x;
        x = x * sx_cos + y * sy_sin;
        y = temp * sx_sin + y * sy_cos;

        // elevates and projects
        //float temp_x = factor_x / (x * sin_elevation - z * sz_sin + distance);
        float temp_x = factor_x / (y * cos_elevation - z * sz_sin + distance);
        float temp_y = factor_y / (y * cos_elevation - z * sz_sin + distance);
        return new Point((int) (Math.round(x * temp_x) + trans_x),
                (int) (Math.round((y * sin_elevation + z * sz_cos) * -temp_y) + trans_y));
    }
    
    /**
     * Project angle and length from two points
     * @param x1 Point 1 x
     * @param y1 Point 1 y
     * @param z1 Point 1 z
     * @param x2 Point 2 x
     * @param y2 Point 2 y
     * @param z2 Point 2 z
     * @return Angle and length
     */
    public double[] projectAL(float x1, float y1, float z1, float x2, float y2, float z2){
        Point p1 = project(x1, y1, z1);
        Point p2 = project(x2, y2, z2);
        float u = p2.x - p1.x;
        float v = p2.y - p1.y;
        return MIMath.getDSFromUV(u, v);
    }
    
    /**
     * Projects 3D points without scaling.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return Projected point
     */
    public final Point project_noScale(float x, float y, float z) {
        float temp;

        // rotates
        temp = x;
        x = x * sx_cos + y * sy_sin;
        y = temp * sx_sin + y * sy_cos;

        // elevates and projects
        return new Point((int) (Math.round(x) + trans_x),
                (int) (Math.round((y * sin_elevation + z * sz_cos)) + trans_y));
    }

    public void setZRange(float zmin, float zmax) {
        this.zmin = zmin;
        this.zmax = zmax;
        this.zfactor = 20 / (zmax - zmin);
    }
    
    /**
     * Get bounds
     * @return Bounds rectangle
     */
    public Rectangle getBounds(){
        List<Point> ps = new ArrayList<>();
        ps.add(this.project(10, 10, 10));        
        ps.add(this.project(-10, 10, 10));
        ps.add(this.project(10, -10, 10));
        ps.add(this.project(-10, -10, 10));
        ps.add(this.project(10, 10, -10));
        ps.add(this.project(-10, -10, -10));
        ps.add(this.project(-10, 10, -10));
        ps.add(this.project(10, -10, -10));
        int i = 0;
        int minx =0, miny = 0, maxx = 0, maxy = 0;
        for (Point p : ps){
            if (i == 0){
                minx = p.x;
                maxx = p.x;
                miny = p.y;
                maxy = p.y;
            } else {
                if (minx > p.x)
                    minx = p.x;
                else if (maxx < p.x)
                    maxx = p.x;
                if (miny > p.y)
                    miny = p.y;
                else if (maxy < p.y)
                    maxy = p.y;
            }
            i += 1;
        }
        
        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }
    
    /**
     * Get bounds without scale
     * @return Bounds rectangle
     */
    public Rectangle getBounds_noScale(){
        List<Point> ps = new ArrayList<>();
        ps.add(this.project_noScale(10, 10, 10));        
        ps.add(this.project_noScale(-10, 10, 10));
        ps.add(this.project_noScale(10, -10, 10));
        ps.add(this.project_noScale(-10, -10, 10));
        ps.add(this.project_noScale(10, 10, -10));
        ps.add(this.project_noScale(-10, -10, -10));
        ps.add(this.project_noScale(-10, 10, -10));
        ps.add(this.project_noScale(10, -10, -10));
        int i = 0;
        int minx =0, miny = 0, maxx = 0, maxy = 0;
        for (Point p : ps){
            if (i == 0){
                minx = p.x;
                maxx = p.x;
                miny = p.y;
                maxy = p.y;
            } else {
                if (minx > p.x)
                    minx = p.x;
                else if (maxx < p.x)
                    maxx = p.x;
                if (miny > p.y)
                    miny = p.y;
                else if (maxy < p.y)
                    maxy = p.y;
            }
            i += 1;
        }
        
        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }
    
    /**
     * Update 2D scaling
     */
    public void update2DScaling(){
        Rectangle rect = this.getBounds();
        float s1 = (float)((x2 - x1) / rect.getWidth());
        float s2 = (float)((y2 - y1) / rect.getHeight());
        s1 = s1 * this._2D_scale_x;
        s2 = s2 * this._2D_scale_y;
        this.setX2DScaling(s1);
        this.setY2DScaling(s2);
    }

}

 /* Copyright 2012 - Yaqiang Wang,
 * yaqiang.wang@gmail.com
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 */
package org.meteoinfo.data.meteodata.bandraster;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.meteoinfo.data.GridArray;
import org.meteoinfo.data.GridData;
import org.meteoinfo.data.dimarray.Dimension;
import org.meteoinfo.data.dimarray.DimensionType;
import org.meteoinfo.data.mapdata.geotiff.GeoTiff;
import org.meteoinfo.data.meteodata.MeteoDataType;
import org.meteoinfo.data.meteodata.DataInfo;
import org.meteoinfo.ndarray.*;
import org.meteoinfo.data.meteodata.IGridDataInfo;
import org.meteoinfo.data.meteodata.Variable;
import org.meteoinfo.data.meteodata.Attribute;

 /**
  *
  * @author yaqiang
  */
 public class GeoTiffDataInfo extends DataInfo implements IGridDataInfo {

     // <editor-fold desc="Variables">
     private GeoTiff geoTiff;
     private int bandNum;

     // </editor-fold>
     // <editor-fold desc="Constructor">
     /**
      * Constructor
      */
     public GeoTiffDataInfo() {
         this.setDataType(MeteoDataType.GEOTIFF);
     }
     // </editor-fold>
     // <editor-fold desc="Get Set Methods">
     // </editor-fold>
     // <editor-fold desc="Methods">

     @Override
     public boolean isValidFile(RandomAccessFile raf) {
         return false;
     }

     @Override
     public void readDataInfo(String fileName) {
         try {
             this.setFileName(fileName);

             geoTiff = new GeoTiff(fileName);
             geoTiff.read();
             List<double[]> xy = geoTiff.readXY();
             double[] X = xy.get(0);
             double[] Y = xy.get(1);
             Dimension xDim = new Dimension(DimensionType.X);
             xDim.setValues(X);
             this.setXDimension(xDim);
             this.addDimension(xDim);
             Dimension yDim = new Dimension(DimensionType.Y);
             yDim.setValues(Y);
             yDim.setReverse(true);
             this.setYDimension(yDim);
             this.addDimension(yDim);
             this.bandNum = this.geoTiff.getBandNum();
             Dimension bDim = null;
             if (this.bandNum > 1){
                 bDim = new Dimension(DimensionType.OTHER);
                 bDim.setValues(new double[this.bandNum]);
                 this.addDimension(bDim);
             }

             List<Variable> variables = new ArrayList<>();
             Variable aVar = new Variable();
             aVar.setName("var");
             aVar.addDimension(yDim);
             aVar.addDimension(xDim);
             if (this.bandNum > 1){
                 aVar.addDimension(bDim);
             }
             variables.add(aVar);
             this.setVariables(variables);

             this.setProjectionInfo(geoTiff.readProj());
         } catch (IOException ex) {
             Logger.getLogger(GeoTiffDataInfo.class.getName()).log(Level.SEVERE, null, ex);
         }
     }

     /**
      * Get global attributes
      * @return Global attributes
      */
     @Override
     public List<Attribute> getGlobalAttributes(){
         return new ArrayList<>();
     }

     @Override
     public String generateInfoText() {
         String dataInfo = "Data Type: GeoTiff";
         dataInfo += System.getProperty("line.separator") + super.generateInfoText();

         return dataInfo;
     }

     /**
      * Read array data of a variable
      *
      * @param varName Variable name
      * @return Array data
      */
     @Override
     public Array read(String varName){
         Array r = null;
         try {
             r = this.geoTiff.readArray();
         } catch (IOException ex) {
             Logger.getLogger(GeoTiffDataInfo.class.getName()).log(Level.SEVERE, null, ex);
         }

         return r;
     }

     /**
      * Read array data of the variable
      *
      * @param varName Variable name
      * @param origin The origin array
      * @param size The size array
      * @param stride The stride array
      * @return Array data
      */
     @Override
     public Array read(String varName, int[] origin, int[] size, int[] stride) {
         try {
             Section section = new Section(origin, size, stride);
             int rangeIdx = 0;
             Range yRange = section.getRange(rangeIdx++);
             Range xRange = section.getRange(rangeIdx);
             Array array = this.geoTiff.readArray(yRange, xRange);
             return array;
         } catch (InvalidRangeException | IOException ex) {
             Logger.getLogger(GeoTiffDataInfo.class.getName()).log(Level.SEVERE, null, ex);
             return null;
         }
     }

     private void readXY(Range yRange, Range xRange, IndexIterator ii) {
         int[][] data = this.geoTiff.readData();
         for (int y = yRange.first(); y <= yRange.last();
                 y += yRange.stride()) {
             for (int x = xRange.first(); x <= xRange.last();
                     x += xRange.stride()) {
                 ii.setFloatNext(data[y][x]);
             }
         }
     }

     /**
      * Get grid data
      *
      * @param varName Variable name
      * @return Grid data
      */
     @Override
     public GridArray getGridArray(String varName) {
         return null;
     }

     @Override
     public GridData getGridData_LonLat(int timeIdx, String varName, int levelIdx) {
         GridData gdata = this.geoTiff.getGridData_Value();
         gdata.setXArray(this.getXDimension().getValues());
         gdata.setYArray(this.getYDimension().getValues());
         gdata.setProjInfo(this.getProjectionInfo());

         return gdata;
     }

     @Override
     public GridData getGridData_TimeLat(int lonIdx, String varName, int levelIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_TimeLon(int latIdx, String varName, int levelIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_LevelLat(int lonIdx, String varName, int timeIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_LevelLon(int latIdx, String varName, int timeIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_LevelTime(int latIdx, String varName, int lonIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_Time(int lonIdx, int latIdx, String varName, int levelIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_Level(int lonIdx, int latIdx, String varName, int timeIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_Lon(int timeIdx, int latIdx, String varName, int levelIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }

     @Override
     public GridData getGridData_Lat(int timeIdx, int lonIdx, String varName, int levelIdx) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }
     // </editor-fold>
 }

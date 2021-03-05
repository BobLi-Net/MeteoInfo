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
package org.meteoinfo.geo.projection.info;

import org.meteoinfo.geo.projection.ProjectionNames;
import org.locationtech.proj4j.CoordinateReferenceSystem;

 /**
  *
  * @author Yaqiang Wang
  */
 public class LongLat extends ProjectionInfo {
     // <editor-fold desc="Variables">
     // </editor-fold>
     // <editor-fold desc="Constructor">
     /**
      * Construction
      * @param crs Coorinate reference system
      */
     public LongLat(CoordinateReferenceSystem crs) {
         this.crs = crs;
     }
     // </editor-fold>
     // <editor-fold desc="Get Set Methods">
     /**
      * Get projection name
      *
      * @return Projection name
      */
     @Override
     public ProjectionNames getProjectionName() {
         return ProjectionNames.LongLat;
     }
     // </editor-fold>
     // <editor-fold desc="Methods">
     // </editor-fold>
 }

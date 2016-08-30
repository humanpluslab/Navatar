''' --------------------------------
 Name:         GISToXml.py
 Purpose:      Create XML file from GIS files
 Author:       Rohit Patil
 Created       11/04/2016
 Python:       2.7
-------------------------------- '''
# Import the required python modules
import arcpy
import os
import sys
import re

# Data base connection to ArcGIS Server
database_conn = "ehsprod2.ehs.unr.edu.sde"
#Output location to create the intermediate files and output protobuffer files
outputPath = r"D:\Box Sync\Projects\Eelke\navatar\MapCreator\Output"
# The output projected co-ordinate system
outSpatialReference = "NAD 1983 UTM Zone 11N"
# The current building to process
buildings = ["sde.BUILDING.scrugham_engineering_mines"]
#Features from building dataset to process
features = ["rooms", "doors"]
#The maximum distance that will be used to combine adjecent features for navigable space
aggregationDist = "4 Feet"
# Query used to find open spaces
rooms_query = "(UPPER([ROOM_TYPE]) LIKE '%CORRIDOR%' OR UPPER([ROOM_TYPE]) LIKE '%VESTIBULE%' OR  UPPER([ROOM_TYPE]) LIKE '%LOBBY%')"
#Query used to find the elevators and stairs on a floor
stairs_query = "(UPPER([ROOM_TYPE]) LIKE '%ELEVATOR%' OR UPPER([ROOM_TYPE]) LIKE '%STAIRS%')"



# set arcpy environment variable for overwriting database outputs
arcpy.env.overwriteOutput = True

#start the processing

try:
  # temporary database path
  tempDBPath = outputPath + os.sep + 'temp.gdb'

  #delete the existing temporary database
  arcpy.Delete_management(tempDBPath)
  arcpy.CreateFileGDB_management(outputPath, 'temp.gdb')

  # set arcpy environment variable for database connection
  arcpy.env.workspace = database_conn

  # get a list of buildings in the current ArcGIS database
  building_list = arcpy.ListDatasets()
  #Iterate for each building
  for building in buildings:
    #get index of the current building
    index = building_list.index(building)
    # if the current building matches the building in the list to be processed
    if index <> -1:
      currentBldg = building_list[index]

      building_name = currentBldg[currentBldg.rfind(".") + 1: len(currentBldg)]
      fds_path = tempDBPath + os.sep + building_name
      arcpy.CreateFeatureDataset_management(tempDBPath, building_name)
      arcpy.DefineProjection_management(fds_path, arcpy.SpatialReference(outSpatialReference)) #arcpy.Describe(currentBldg).spatialReference)

      #create the protobuffer file
      protobuff_file = outputPath + os.sep + building_name


      # list all feature classes in the building dataset
      fclist = arcpy.ListFeatureClasses('', '', currentBldg)

      # get the feature class for rooms
      fc_rooms = [x for x in fclist if re.search('rooms', x)]

      # get the feature class for doors
      fc_doors = [x for x in fclist if re.search('doors', x)]

      # if both rooms and doors feature classes are found then proceed
      if len(fc_rooms) > 0 and len(fc_doors) > 0:
        #desc = arcpy.Describe(fc_rooms[0])

        # get floors list from the building
        values = [row[0] for row in arcpy.da.SearchCursor(fc_rooms[0], 'FLOOR')]
        floors = set(values)

        #iterate through each floor
        for floor_num in floors:
          # ignore basements and floor 0
          if floor_num >= 0:
            ######### Processing open space #########

            # query navigable space for current floor
            condition = "[FLOOR] = " + str(floor_num) + " AND " + rooms_query

            # create file path for output
            fc_name = currentBldg[currentBldg.rfind(".") + 1:len(currentBldg)]
            fc_file = fc_name + "_FLR" + str(floor_num) + "_OS"
            arcpy.Delete_management(fds_path + os.sep + fc_file)

            # set the output spatial reference
            arcpy.env.outputCoordinateSystem = arcpy.SpatialReference(outSpatialReference)

            # save the query results to feature class
            arcpy.FeatureClassToFeatureClass_conversion(fc_rooms[0], fds_path, 'OS_temp', condition)
            OSFeatureClass = fds_path + os.sep + fc_file

            # dissolve multiple features into single
            arcpy.Dissolve_management(fds_path + os.sep + 'OS_temp', fds_path + os.sep + 'OS_temp2')
            # dissolve boundaries to make a continuous polygon for navigable space
            arcpy.AggregatePolygons_cartography(fds_path + os.sep + 'OS_temp2', OSFeatureClass, aggregationDist,
                                                "0 SquareFeet", "0 SquareFeet", "NON_ORTHOGONAL", "#")
            #arcpy.Delete_management(OSFeatureClass)
            arcpy.Delete_management(fds_path + os.sep + 'OS_temp')
            arcpy.Delete_management(fds_path + os.sep + 'OS_temp2')

             ######### Processing landmarks #########
            condition = "[FLOOR] = " + str(floor_num)
            arcpy.MakeFeatureLayer_management(fc_doors[0], 'DR_lyr', condition)

            if (arcpy.Exists(OSFeatureClass)):
              arcpy.SelectLayerByLocation_management('DR_lyr', "WITHIN_A_DISTANCE", OSFeatureClass, '2 Feet',
                                                     "NEW_SELECTION")
            print arcpy.GetCount_management('DR_lyr')
            count = arcpy.GetCount_management('DR_lyr')
            if (int(count.getOutput(0)) <> 0):
              fc_file = fc_name + "_FLR" + str(floor_num) + "_DR"
              DRFeatureClass = fds_path + os.sep + fc_file

              doors_temp = fds_path + os.sep + 'temp_doors'
              arcpy.FeatureToPoint_management('DR_lyr', doors_temp, point_location="CENTROID")

              rooms_condition = "[FLOOR] = " + str(floor_num) + " AND NOT " + rooms_query
              arcpy.FeatureClassToFeatureClass_conversion(fc_rooms[0], fds_path, 'rooms_temp', rooms_condition)

              arcpy.SpatialJoin_analysis(doors_temp, fds_path + os.sep + 'rooms_temp', DRFeatureClass, "#", "#", "#",
                                         "CLOSEST")
              #arcpy.Delete_management(DRFeatureClass)
              arcpy.Delete_management(fds_path + os.sep + 'rooms_temp')
              arcpy.Delete_management(doors_temp)

  print "********************************Processing complete.......************************************************"
  os.chdir("c:")

  # **************************************** Elevator and stairs **********************************************

  '''condition = "[FLOOR] = " + str(floor_num) + " AND " + stairs_query
  # fc_name = currentBldg[currentBldg.rfind(".")+1:len(currentBldg)]
  fc_file = fc_name + "_FLR" + str(floor_num) + "_SE"
  arcpy.Delete_management(tempDBPath + os.sep + fc_file)

  arcpy.FeatureClassToFeatureClass_conversion(fc_rooms[0], tempDBPath, fc_file, condition)
  SEFeatureClass = tempDBPath + os.sep + fc_file
  count = arcpy.GetCount_management(SEFeatureClass)
  if (int(count.getOutput(0)) == 0):
    arcpy.Delete_management(SEFeatureClass)
    # GenerateShapesXML(SEFeatureClass, outputXMLFile, 2)
    '''
  # ************************************ doors **************************************************************


except IOError as (errno, strerror):
  print "I/O error({0}): {1}".format(errno, strerror)
  arcpy.AddError("I/O error({0}): {1}".format(errno, strerror))

except ValueError:
  print "Could not convert data to an integer."
  arcpy.AddError("Could not convert data to an integer.")
except:
  print "Unexpected error:" + str(sys.exc_info()[0])
  arcpy.AddError("Unexpected error:" + str(sys.exc_info()[0]))
  raise

'''
def add_features(fc, navigable_space, xmltype):
  # xmltype 1 = OS, 2 = SE
  desc = arcpy.Describe(fc)

  if desc.ShapeType == "Point":
    root = "point"
    root = "line"
  else:
    root = "polygon"

  rows = arcpy.SearchCursor(fc)
  row = rows.next()

  while row != None:
    feat = row.shape
    # strOutput = '\t\t<' + root + ' id = "' + str(row.getValue("OBJECTID"))
    if xmltype == 1:
      strOutput += '" type="OpenSpace">\n'
    elif xmltype == 2:
      strOutput += '" type="' + str(row.getValue("ROOM_TYPE")) + '">\n'
    elif xmltype == 3:
      strOutput += '" type="Door" room_num="' + str(row.getValue("ROOM_NUM")) + '">\n'

    outfile.write(strOutput)
    for partIndex in range(feat.partCount):
      currentPart = feat.getPart(partIndex)
      outfile.write('\t\t\t<part id = "' + str(partIndex + 1) + '">\n')
      for pointIndex in range(currentPart.count):
        currentPoint = currentPart.getObject(pointIndex)
        if str(currentPoint) == 'None':
          outfile.write('\t\t\t<ring id = "' + str(pointIndex + 1) + '"/>\n')
        else:
          x_coord = round(float(currentPoint.X), 5)
          y_coord = round(float(currentPoint.Y), 5)
          outfile.write('\t\t\t\t<point id = "' + str(pointIndex + 1) + '"  X = "' + str(x_coord) + '" Y = "' + str(
            y_coord) + '"/> \n')
      outfile.write('\t\t\t</part>\n')
    outfile.write('\t\t</' + root + '>\n')
    row = rows.next()
    del feat, currentPoint, currentPart
  del row, rows
  return



              for row in arcpy.SearchCursor(OSFeatureClass):
                partnum = 0
                for part in row[1]:
                  ring_start = False
                  for pnt in part:
                    if pnt:
                      a = b
                    else:
                      ring_start = True
                      ring_parts = navigable_space.rings.add()

                    if ring_start == True:
                      point = ring_parts.polygon.add()
                      point.x = round(float(pnt.X), 5)
                      point.y = round(float(pnt.Y), 5)
                    else:
                      point = navigable_space.outerBoundary.add()
                      point.x = round(float(pnt.X), 5)
                      point.y = round(float(pnt.Y), 5)
                  partnum += 1
                del part, pnt, point
              del row
'''
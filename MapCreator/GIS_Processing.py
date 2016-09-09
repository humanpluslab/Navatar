''' --------------------------------
 Name:         GIS_Processing.py
 Purpose:      Extract GIS features(rooms, doors, coridors, stairs, etc.) from GIS file
 Author:       Rohit Patil
 Created       8/31/2016
 Python:       2.7
-------------------------------- '''
# Import the required python modules
import arcpy
import os
import sys
import re

############################  VARIABLES  ####################################
# Data base connection to ArcGIS Server containing floor maps for buildings
database_conn = "ehsprod2.ehs.unr.edu.sde"
# The current building to process
buildings = ["sde.BUILDING.ansari_business_building"]

############################  CONSTANTS  ####################################
# Output location to create the intermediate files and output protobuffer files
current_working_dir = os.getcwd()
outputPath = current_working_dir + os.sep + "\Output"
# The output projected co-ordinate system
outSpatialReference = "NAD 1983 UTM Zone 11N"
# Features from building dataset to process
features = ["rooms", "doors"]
# The maximum distance that will be used to combine adjecent features for navigable space
aggregationDist = "4 Feet"
# Query used to find open spaces
rooms_query = "(UPPER([ROOM_TYPE]) LIKE '%CORRIDOR%' OR UPPER([ROOM_TYPE]) LIKE '%VESTIBULE%' OR  UPPER([ROOM_TYPE]) LIKE '%LOBBY%')"
# Query used to find the elevators and stairs on a floor
stairs_query = "(UPPER([ROOM_TYPE]) LIKE '%ELEVATOR%' OR UPPER([ROOM_TYPE]) LIKE '%STAIRS%')"
# set arcpy environment variable for overwriting database outputs
arcpy.env.overwriteOutput = True
#############################################################################

try:
    # temporary database path
    tempDBPath = outputPath + os.sep + 'temp.gdb'

    # delete the existing temporary database
    arcpy.Delete_management(tempDBPath)
    arcpy.CreateFileGDB_management(outputPath, 'temp.gdb')

    # set arcpy environment variable for database connection
    arcpy.env.workspace = database_conn

    # get a list of buildings in the current ArcGIS database
    building_list = arcpy.ListDatasets()
    # Iterate for each building
    for building in buildings:
        # get index of the current building
        index = building_list.index(building)
        # if the current building matches the building in the list to be processed
        if index <> -1:
            currentBldg = building_list[index]

            building_name = currentBldg[currentBldg.rfind(".") + 1: len(currentBldg)]
            fds_path = tempDBPath + os.sep + building_name
            arcpy.CreateFeatureDataset_management(tempDBPath, building_name)
            arcpy.DefineProjection_management(fds_path, arcpy.SpatialReference(
                outSpatialReference))  # arcpy.Describe(currentBldg).spatialReference)

            # create the protobuffer file
            protobuff_file = outputPath + os.sep + building_name

            # list all feature classes in the building dataset
            fclist = arcpy.ListFeatureClasses('', '', currentBldg)

            # get the feature class for rooms
            fc_rooms = [x for x in fclist if re.search('rooms', x)]

            # get the feature class for doors
            fc_doors = [x for x in fclist if re.search('doors', x)]

            # if both rooms and doors feature classes are found then proceed
            if len(fc_rooms) > 0 and len(fc_doors) > 0:
                # desc = arcpy.Describe(fc_rooms[0])

                # get floors list from the building
                values = [row[0] for row in arcpy.da.SearchCursor(fc_rooms[0], 'FLOOR')]
                floors = set(values)

                # iterate through each floor
                for floor_num in floors:
                    # ignore basements and floor 0
                    if floor_num >= 0:

                        # **************** Extract Open space *****************
                        # query navigable space on current floor
                        condition = "[FLOOR] = " + str(floor_num) + " AND " + rooms_query

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
                        arcpy.AggregatePolygons_cartography(fds_path + os.sep + 'OS_temp2', OSFeatureClass,
                                                            aggregationDist,
                                                            "0 SquareFeet", "0 SquareFeet", "NON_ORTHOGONAL", "#")
                        # arcpy.Delete_management(OSFeatureClass)
                        arcpy.Delete_management(fds_path + os.sep + 'OS_temp')
                        arcpy.Delete_management(fds_path + os.sep + 'OS_temp2')

                        #**************** Extract Doors *****************
                        # query doors on current floor
                        condition = "[FLOOR] = " + str(floor_num)
                        arcpy.MakeFeatureLayer_management(fc_doors[0], 'DR_lyr', condition)

                        if (arcpy.Exists(OSFeatureClass)):
                            arcpy.SelectLayerByLocation_management('DR_lyr', "WITHIN_A_DISTANCE", OSFeatureClass,
                                                                   '2 Feet',
                                                                   "NEW_SELECTION")
                        print arcpy.GetCount_management('DR_lyr')
                        count = arcpy.GetCount_management('DR_lyr')
                        if (int(count.getOutput(0)) <> 0):
                            fc_file = fc_name + "_FLR" + str(floor_num) + "_DR"
                            DRFeatureClass = fds_path + os.sep + fc_file

                            doors_temp = fds_path + os.sep + 'temp_doors'
                            arcpy.FeatureToPoint_management('DR_lyr', doors_temp, point_location="CENTROID")

                            rooms_condition = "[FLOOR] = " + str(floor_num) + " AND NOT " + rooms_query
                            arcpy.FeatureClassToFeatureClass_conversion(fc_rooms[0], fds_path, 'rooms_temp',
                                                                        rooms_condition)

                            arcpy.SpatialJoin_analysis(doors_temp, fds_path + os.sep + 'rooms_temp', DRFeatureClass,
                                                       "#", "#", "#",
                                                       "CLOSEST")
                            arcpy.Delete_management(DRFeatureClass)
                            arcpy.Delete_management(fds_path + os.sep + 'rooms_temp')
                            arcpy.Delete_management(doors_temp)

                        # **************************************** Elevator and stairs **********************************************

                        #condition = "[FLOOR] = " + str(floor_num) + " AND " + stairs_query
                        ## fc_name = currentBldg[currentBldg.rfind(".")+1:len(currentBldg)]
                        #fc_file = fc_name + "_FLR" + str(floor_num) + "_SE"
                        #arcpy.Delete_management(tempDBPath + os.sep + fc_file)

                        #arcpy.FeatureClassToFeatureClass_conversion(fc_rooms[0], tempDBPath, fc_file, condition)
                        #SEFeatureClass = tempDBPath + os.sep + fc_file
                        #count = arcpy.GetCount_management(SEFeatureClass)
                        #if (int(count.getOutput(0)) == 0):
                        #  arcpy.Delete_management(SEFeatureClass)
                        #  # GenerateShapesXML(SEFeatureClass, outputXMLFile, 2)


    print "********************************Processing complete.......************************************************"
    os.chdir("c:")


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

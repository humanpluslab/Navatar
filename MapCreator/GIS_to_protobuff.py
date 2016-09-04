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

# Import proto buffer module
import libs.BuildingMapProto_pb2

############################  VARIABLES  ####################################
# Set the input location for GIS database that contains the extracted GIS features
# and output location for the protobuff file
current_working_dir = os.getcwd()
outputPath = current_working_dir + os.sep + "\Output"


############################  CONSTANTS  ####################################
# Set the path and name of the GIS database created in Step 1
arcpy.env.workspace = outputPath + os.sep + "temp.gdb"

# set arcpy environment variable for overwriting database outputs
arcpy.env.overwriteOutput = True

try:
    # loop through all the GIS files with required features extracted
    for fds in arcpy.ListDatasets("*", "Feature"):
        building_map = libs.BuildingMapProto_pb2.BuildingMap()
        # Create a protobuf file for building
        protobuff_file = outputPath + os.sep + fds
        building_map.name = fds
        # Get the extent of the features and add it to protobuf
        building_extent = arcpy.Describe(fds).extent
        building_map.minCoordinates.x = building_extent.XMin
        building_map.minCoordinates.y = building_extent.YMin
        building_map.maxCoordinates.x = building_extent.XMax
        building_map.maxCoordinates.y = building_extent.YMax

        # crate a set for floors available for a building
        temp_floors = []
        for fc in arcpy.ListFeatureClasses(feature_dataset=fds):
            temp_floors.append(fc[fc.find("FLR") + 3:fc.find("FLR") + 4])

        floors = set(temp_floors)
        floors_sort = sorted(floors)

        # iterate through each floor and generate the floor protobuffer object
        for floor_num in floors_sort:
            # add floor object in protobuffer
            floor = building_map.floors.add()

            # set the floor number to current floor
            floor.number = int(floor_num) + 1

            for fc in arcpy.ListFeatureClasses("*FLR" + floor_num + "*", feature_dataset=fds):

                # create navigable space object
                if (fc.find("OS") > 0):
                    navigable_space = floor.navigableSpaces.add()

                    # query navigable space for current floor
                    count = arcpy.GetCount_management(fc)
                    # if open spaces are found then loop through them
                    if int(count.getOutput(0)) != 0:
                        desc = arcpy.Describe(fc)
                        # get the features in the open space
                        rows = arcpy.SearchCursor(fc)
                        for row in rows:
                            #create open space protobuf and add points
                            feat = row.shape
                            ring_start = False
                            for partIndex in range(feat.partCount):
                                currentPart = feat.getPart(partIndex)
                                for pointIndex in range(currentPart.count):
                                    currentPoint = currentPart.getObject(pointIndex)
                                    if str(currentPoint) == 'None':
                                        # ring starts
                                        ring_start = True
                                        # if ring_start == 0:
                                        ring_parts = navigable_space.rings.add()
                                    else:
                                        if ring_start == True:
                                            point = ring_parts.polygon.add()
                                            point.x = round(float(currentPoint.X), 3)
                                            point.y = round(float(currentPoint.Y), 3)
                                        else:
                                            point = navigable_space.outerBoundary.add()
                                            point.x = round(float(currentPoint.X), 3)
                                            point.y = round(float(currentPoint.Y), 3)
                            del feat, currentPoint, currentPart
                        del row, rows
                        # Delete the GIS file when exported to protobuffer
                        # arcpy.Delete_management(fc)
                else:
                    count = arcpy.GetCount_management(fc)
                    if int(count.getOutput(0)) != 0:
                        desc = arcpy.Describe(fc)

                        rows = arcpy.SearchCursor(fc)

                        for row in rows:
                            feat = row.shape
                            currentPoint = feat.getPart(0)
                            floor_landmark = floor.landmarks.add()
                            floor_landmark.location.x = round(float(currentPoint.X), 3)
                            floor_landmark.location.y = round(float(currentPoint.Y), 3)
                            floor_landmark.name = row.ROOM_NUM
                            # check if landmark is stairs
                            if row.ROOM_NUM.startswith("S"):
                                floor_landmark.type = 3
                            # check if landmark is elevator
                            elif row.ROOM_NUM.startswith("ELE"):
                                floor_landmark.type = 4  # Change this to dynamically set the type
                            #check if the landmark id intersection
                            elif row.ROOM_NUM.startswith("INT"):
                                floor_landmark.type = 2
                            # else landmark is open space
                            else:
                                floor_landmark.type = 1

                    del feat, currentPoint
                    del row, rows

                    # arcpy.Delete_management(fc)
    # Save the generated protobuffer to file
    f = open(protobuff_file, "wb")
    f.write(building_map.SerializeToString())
    f.close()

    # Code to read the protobuf file for testing
    #f = open(protobuff_file, "rb")
    #building_map.ParseFromString(f.read())
    #f.close()

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


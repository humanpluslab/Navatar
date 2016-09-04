The two scripts "GIS_Processing.py" and "GIS_to_Protobuffer.py" can be used to extract required features form the GIS files and then save them into protobuffer file

The conversion of GIS input files to map protobuffer consists of two steps

Step 1. GIS_Processing.py script
    This step consists of extracting the needed features like rooms, doors, corridors, stairs, etc. from GIS blueprint file/databases
    and then saving them as GIS files

    Variables in this file
    1. database_conn - This variable contains the ArcSDE database connection to database containing the blueprints for all buildings.
        e.g. for UNR it is "ehsprod2.ehs.unr.edu.sde"
    2. buildings - This variable should contain names of building/s datasets to be included in the processing as found in GIS database
        e.g. ["sde.BUILDING.scrugham_engineering_mines"] is the
    3. output_gis_delete - This variable can be used to either save or delete the intermediate GIS files.
        e.g. if set to 1 the intermediate files will be deleted, if set to 0 the intermediate files will not be deleted

    Steps to Execute:
    1. After the variables are set run the python script. The output GIS database will be stored in the


Step 2. GIS_Processing.py script
    This step reads the extracted GIS features and saves them into the protobuffer

    Variables in this file
    1. outputPath - This variable contains the path to directory where the GIS database with extracted features is created in
        step one.
        e.g. for e.g. "\Output"

    Steps to Execute:
    1. After the variables are set run the python script. The protobuff file generated will be stored in the location set
        in outputPath variable



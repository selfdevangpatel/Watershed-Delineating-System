# Watershed-Delineating-System
A basic JavaFX desktop application to delineate a watershed region from an input Digital Elevation Model (DEM) raster file.

## Implementation
The system uses the [ArcGIS Runtime API for Java](https://developers.arcgis.com/java/) to load the map data and
implement other functionalities such as adding a raster/feature layer onto the map, applying different raster renderer
to raster layer, sketching on map, etc.

The tools required for delineating a watershed region are implemented from [WhiteboxTools](https://whiteboxgeo.com/manual/wbt_book/intro.html)
which is an advanced geospatial data analysis platform developed by Prof. John Lindsay.

## Setup

### Database:
Create a MongoDB database with the following details:
 - Name: wds
 - Collection: users
 - Host: localhost
 - Port: 27017

### System:
Setting up the system in IntelliJ IDEA:  

Open IntelliJ IDEA. If you are on the Welcome Screen, click on 'Get from VCS' button. If you have a project opened
already, you can do the same action from the File > New > Project from Version Control menu.

Enter the GitHub repository URL and Click on 'Clone' button to clone the GitHub repository to the directory you
selected. IntelliJ IDEA will pick up the common build tools like Gradle or Maven, and automatically download the
required dependencies and build the project.

After the IntelliJ IDEA successfully builds the project, make sure you have selected appropriate Gradle JVM and project
bytecode version. Follow these 2 steps:  
Step 1) Go to File > Settings > Build, Execution, Deployment > Build Tools > Gradle and select appropriate Gradle JVM.  
Step 2) Go to File > Settings > Build, Execution, Deployment > Compiler > Java Compiler and select appropriate Project
bytecode version.

Set up the Run configuration to run the system. Follow these steps:  
Step 1) Go to Run > Edit Configurations. Click on + icon and select 'Application' to add a new configuration.  
Step 2) Type "LaunchApp" in the Name.  
Step 3) Specify appropriate module(JDK or JVM) and classpath.  
Step 4) Type "launch.LaunchApp" in Main class.  
Step 5) Click on 'Modify options' and select 'Add VM options'. Add VM options as follows:  
For Windows users:
```shell
--module-path "\path\to\javafx-sdk-17.0.1\lib" --add-modules javafx.controls,javafx.fxml
```
Modify the above VM options to specify your path to JavaFX SDK library. Click on 'Apply' button and then OK.

Optional: For the purpose of validating OTP through E-mail for resetting user's password, specify the sender's E-mail
address and password in EmailUtil.java of 'email' package. Make sure you have turned on 'Less secure app access'
setting from your Google account.

An API key is required to enable access to services, web maps, and web scenes hosted in ArcGIS Online. If you don't
have an API key, go to https://developers.arcgis.com/dashboard/ to get it. After you get an API key, specify it in
MainApp.java in 'mainModule' package.

Finally, run the 'LaunchApp' configuration to launch the system.
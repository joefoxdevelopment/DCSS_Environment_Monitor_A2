# DCSS_Environment_Monitor_A2
## U1454236 Joseph Fox
### Set up instructions

Run the following commands in the project root in order to set the application up to be run.
1) `./build_idl.sh`
2) `./build_java.sh`

Start the CORBA naming service. There is a script `./start_naming_service.sh` that starts it and binds it to port 1050.

Now to start the application. It is recommeneded that you use Terminator or tmux for this for convenience. Ensure that you are in the `java/` folder. Once in the dir, you can run the following commands to start the application components. There must be an environmental centre before there is a regional centre, and there must be a regional centre before there can be a monitoring station.

`java com.joefox.runners.StartEnvironmentalCentre <ENVIRONMENTAL_CENTRE_NAME> -ORBInitialPort <PORT>`

`java com.joefox.runners.StartRegionalCentre <REGIONAL_CENTRE_NAME> <ENVIRONMENTAL_CENTRE_NAME> -ORBInitialPort <PORT>`

`java com.joefox.runners.StartMonitoringStation <MONITORING_STATION_NAME> <MONITORING_STATION_LOCATION> <REGIONAL_CENTRE_NAME> -ORBInitialPort <PORT>`

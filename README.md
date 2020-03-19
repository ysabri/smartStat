# smartStat

Smart home automation thermostat project that runs on a Raspberry Pi. It uses two servo motors along with a temprature sensor
to flip a switch. It also integrates with Google Home.

* Here is an album showing how it looks like: https://imgur.com/a/Tp7i25f and a video showing how it works with Google Home: https://youtu.be/3-etgQS_Go0
* This only runs on Linux and assumes certain configurations for the servos and temp sensor
* The oauth code is not intended for any serious use, meaning it is not secure by any means, it doesn’t persist any refresh tokens, and very possibly has other issues. The config properties for it are left out of the project for obvious reasons
* The REST endpoints Google Home talks to are exposed using ngrok in my setup of the project
* The thermostat is setup to use Fahrenheit exact values, it probes the temperature every 20 minutes. This behavior can be overridden by explicitly calling the on and off endpoints. Calling the setTemp endpoint will turn off the override and trigger a check for every call

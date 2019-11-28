import Adafruit_DHT
import argparse, sys


# read the wanted data argument
parser = argparse.ArgumentParser()
parser.add_argument("--wantedData", "-D", help="Choose either T or H for temperature or humidity")
wantedData = parser.parse_args().wantedData

# setup constants for sensor
DHT_SENSOR = Adafruit_DHT.DHT22
DHT_PIN = 3

humidity, temp = Adafruit_DHT.read_retry(DHT_SENSOR, DHT_PIN)

if wantedData == "T":
    print("{0:0.1f}".format((temp * 1.8) + 32))
#    print("Temp={0:0.1f}*C  Humidity={1:0.1f}%".format((temperature * 1.8) + 32, humidity))
elif wantedData == "H":
    print("{0:0.1f}".format(humidity))
else:
    print("Failed to retrieve data from sensor")
    sys.exit(2)

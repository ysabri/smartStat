import os
import glob

# Init the pins
os.system('modprobe w1-gpio')  # Turns on the GPIO module
os.system('modprobe w1-therm') # Turns on the Temperature module
 
# Finds probe slave
base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'

# Read the two output lines
tempFile = open(device_file, 'r')
tempLines = tempFile.read()
tempFile.close()

# Parse the temp out
secondLine = tempLines.split("\n")[1]
tempRaw = secondLine.split(" ")[9]
cTemp = float(tempRaw[2:]) / 1000
fTemp = (cTemp * 1.8) + 32

print(fTemp)

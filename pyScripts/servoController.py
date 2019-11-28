import RPi.GPIO as GPIO
import time
import argparse, sys

# direction signal powers
RIGHT_VAL = 1.5
UP_VAL = 6.5
LEFT_VAL = 11.5

# read the direction argument
parser = argparse.ArgumentParser()
parser.add_argument("--direction", "-D", help="Set servo to given direction")
direction = parser.parse_args().direction

# setup the servo
GPIO.setmode(GPIO.BOARD)
GPIO.setup(7, GPIO.OUT)
pmw = GPIO.PWM(7, 50)

# start the signal based on given argument
if direction == "L":
    pmw.start(LEFT_VAL)
elif direction == "U":
    pmw.start(UP_VAL)
elif direction == "R":
    pmw.start(RIGHT_VAL)
else:
    GPIO.cleanup()
    sys.exit(2)


time.sleep(.5)
GPIO.cleanup()

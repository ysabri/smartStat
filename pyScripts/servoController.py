import RPi.GPIO as GPIO
import time
import argparse, sys

# direction signal powers
RIGHT_VAL = 1.5
UP_VAL = 5.1
LEFT_VAL = 11.5

# read the direction argument
parser = argparse.ArgumentParser()
parser.add_argument("--direction", "-D", help="Set servo to given direction")
parser.add_argument("--servoPin", "-P", help="Input the servo's pin")
direction = parser.parse_args().direction
pin = int(parser.parse_args().servoPin)

# setup the servo
GPIO.setmode(GPIO.BOARD)
GPIO.setup(pin, GPIO.OUT)
pmw = GPIO.PWM(pin, 50)

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

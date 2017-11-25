#!/bin/bash
/etc/init.d/pi-lightcontrol stop
/etc/init.d/pi-lightcontrol start &

echo "All transferred. Press Crtl+C to close this program"
echo "(bug, should actually do it automatically)"

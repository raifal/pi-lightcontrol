#!/bin/bash
#
# Location: /usr/local/bin/
#

pid=`ps aux | grep pi-lightcontrol | awk '{print $2}'`
sudo kill -9 $pid

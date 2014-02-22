#! /bin/sh

if [ -d ~/Dropbox/CS4152\ Assets ] ; then
    ASSETS=~/Dropbox/CS4152\ Assets
else
    echo "Where is your CS4152 Assets folder?"
    read ASSETS
fi

ln -s "$ASSETS" android/assets

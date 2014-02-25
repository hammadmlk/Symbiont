#! /bin/sh

if [ -d android/assets/non-git ] ; then
    echo already set up
    exit 0
fi

if [ -L android/assets ] ; then
    rm android/assets
fi

if [ -d ~/Dropbox/CS4152\ Assets ] ; then
    ASSETS=~/Dropbox/CS4152\ Assets
else
    echo "Where is your CS4152 Assets folder?"
    read ASSETS
fi

mkdir -p android/assets
ln -s "$ASSETS" android/assets/non-git

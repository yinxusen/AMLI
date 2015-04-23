#!/usr/bin/env bash

set -e

FWDIR="$(cd `dirname $0`; pwd)"
DISTDIR="$FWDIR/dist"
mkdir -p $DISTDIR/model

echo "Building binary distribution for Inspeed"

cd $FWDIR

pio build --sbt-extra assembly

cp -r target $DISTDIR
cp engine.json $DISTDIR

echo "Inspeed binary distribution created at $DISTDIR"

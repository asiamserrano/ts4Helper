#!/bin/bash
git add commit.sh
git add */src/*
git add */pom.xml
git commit -m "${1:-updates}"  
git push
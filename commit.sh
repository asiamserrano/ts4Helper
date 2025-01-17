#!/bin/bash
git add TS4Downloader/src/*
git commit -m "${1:-updates}"  
git push
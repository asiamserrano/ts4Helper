#!/bin/bash
git add commit.sh
git add *.java
git add *.log
git add *.avsc
git add */pom.xml
git add */logback.xml
git add */application.yml
git commit -m "${1:-updates}"  
git push
git status
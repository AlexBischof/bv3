#!/usr/bin/env bash

scp -P 28987 target/buchungstool.war bischofa@hosteurope:/home/bischofa/;
ssh -t -p 28987 hosteurope "/opt/wildfly/bin/jboss-cli.sh --connect --controller=localhost:10090 --user=admin --password=test --command=\"deploy --force /home/bischofa/buchungstool.war\""
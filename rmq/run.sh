#!/bin/bash

gnome-terminal -e "java -cp ./build/libs/rmq-1.0-SNAPSHOT-all.jar org.maciejmarczak.ds.rmq.Doctor Marek"

gnome-terminal -e "java -cp ./build/libs/rmq-1.0-SNAPSHOT-all.jar org.maciejmarczak.ds.rmq.Doctor Tomek"

gnome-terminal -e "java -cp ./build/libs/rmq-1.0-SNAPSHOT-all.jar org.maciejmarczak.ds.rmq.Technician Adam knee ankle"

gnome-terminal -e "java -cp ./build/libs/rmq-1.0-SNAPSHOT-all.jar org.maciejmarczak.ds.rmq.Technician Tadek ankle elbow"

gnome-terminal -e "java -cp ./build/libs/rmq-1.0-SNAPSHOT-all.jar org.maciejmarczak.ds.rmq.Administrator Julek"

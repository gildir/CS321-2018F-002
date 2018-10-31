#! /bin/sh
#now takes a world file as an argument!
rmiregistry & java -Djava.security.policy=game.policy GameServer localhost $1

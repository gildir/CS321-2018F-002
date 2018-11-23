#! /bin/sh
#now takes a world file as an argument!
rmiregistry & java -cp ".:../lib/*" -Djava.security.policy=game.policy GameServer localhost $1

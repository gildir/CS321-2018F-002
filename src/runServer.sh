#! /bin/sh
rmiregistry &; java -Djava.security.policy=game.policy GameServer localhost

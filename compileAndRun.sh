#!/bin/bash
EXPECTED_ARGS=2
E_BADARGS=65

if [ $# -ne $EXPECTED_ARGS ]; then
	echo "Usage: `basename $0` {port} {board}"
	exit $E_BADARGS
else
	cd code
	javac *.java

	java Client 130.237.218.85 $1 $2
fi

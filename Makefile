zip:
	cp -r src/ code/
	zip sokoban-solver-E-SEA.zip compileAndRun.sh README.txt code/* report-ai10-groupE-SEA.pdf
	rm -r code/

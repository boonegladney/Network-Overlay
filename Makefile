all: compile
	@echo -e '[INFO] Done!'
clean:
	@echo -e '[INFO] Cleaning Up..'	
	@-rm -rf cs455/overlay/node/*.class
	@-rm -rf cs455/overlay/transport/*.class
	@-rm -rf cs455/overlay/wireformats/*.class
	@-rm -rf cs455/overlay/dijkstra/*.class
	@-rm -rf cs455/overlay/util/*.class

compile: 
	@echo -e '[INFO] Compiling the Source..'
	@javac -d . cs455/overlay/node/*.java
	@javac -d . cs455/overlay/transport/*.java
	@javac -d . cs455/overlay/wireformats/*.java
	@javac -d . cs455/overlay/util/*.java
	@javac -d . cs455/overlay/dijkstra/*.java
	


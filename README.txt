The following are breif descriptions about each class used in my project:
- Graph -- Used to create a model of my overlay so that shortest paths can
			be calculated from it and communicated back to the nodes in my
			overlay.
- DijkstraNode -- Used by the Graph to represent a node in the overlay.
- DijkstraEdge --  Used by the Graph to represent a connection in the overlay.
- NodeComparator -- Used as a comparator that is passed to a priority queue so
			that DijkstraNodes can be poped off the queue in order of
			their distance from the source node.
- ShortestPath -- This class takes in a Graph and will compute the shortest paths
			to each node in the graph from the source node, store these
			paths in each DijkstraNode and store references to these nodes
			for future reference of their shortest path from the source.
- MessagingNode -- This is obviously a class representing a MessagingNode and will
			perform many of the necessary computations and procedures in
			connecting the node to the overlay.
- Node -- This class is actually pointless in this project. I had planned on using
			it as an interface, but that never became necessary. Not sure why I left
			it in, but it may be useful for future implementations.
- Registry -- This class performs many of the major functionalities necessary for
			the overlay construction. It communicates with the MessagingNodes
			and allows them to coordinate in the overlay construction.
- Connection -- Used by both the Registry and MessagingNode classes to store info
			about specific connections. This class contains the Sender object
			for the connection, as well as a receiverThread.
- TCPReceiverThread -- This thread processes incoming data from other nodes in the
			overlay
- TCPSender -- This class is used to send data to other nodes in the overlay
- TCPServerThread -- this thread accepts incoming connections and will instantiate
			a connections object with the socket returned from the serverSocket's
			accept() method.
ConsoleInputReader -- This thread processes input from the console. It is used by
			both the Registry and MessagingNode classes.
Link -- This class stores information about a link in the system. Specifically the
		host and portnumber of both nodes in the connection, as well as the link
		weight.
RegistrationForm -- Used by the Registry to store registration information for each
			messaging node connecting to the overlay.
Deregistered -- A wireformat used to inform MessagingNodes that they have successfully
		been deregistered from the registry.
DeregisterRequest -- A wireformat send to the registry as a request from a MessagingNode
			to be removed from the registry.
Event -- An interface that each wireformat must implement.
EventFactory -- used to easily retreive the correct wireformat based on the eventType.
HandShake -- A wireformat used by two connecting nodes so that the can share information
		about who they are (hostname/portnumber).
LinkWeightMessage -- A wireformat used by the registry to inform all MessagingNodes about
			all of the links in the overlay.
Message -- A wireformat that communicates the payload from one node to another.
MessaginNodeslist -- A wireformat used to inform MessagingNodes what other nodes they
			need to initiate a connection with.
Protocol -- A wireformat that contains information that a MessagingNode can use to relay
			a Message to the correct node. This wireformat also stores the payload Message.
PullTrafficSummary -- A wireformat used by the registry to request that MessagingNodes send
			their traffic reports.
RegisterRequest -- A wireformat that a MessagingNode sends to the registry in order to register
			itself.
StartTask -- A wireformat used by the registry to inform the nodes in the overlay to start
			sending messages, and includes the number of rounds expected.
TaskComplete -- A wireformat used by the MessagingNodes to communicate to the registry that
			they have completed their task.
TrafficSummary -- A wireformat used by the MessagingNodes to communicate their traffic report data
			to the registry.



IMPORTANT NOTES: 
--- Within my program, all nodes are identified by their hostname and the port that the server thread was bound to, and is 
represented by a string of format <hostname>:<serverPort>. This enabled me to have multiple nodes running locally on the same machine.

--- When exit-overlay is called for a MessagingNode it will ALWAYS throw a SocketException. This is because upon shutdown, the server
must interrupt it's accept() blocking call, which will then always throw the exception.

--- I noticed that occasionally my program would take a considerable ammount of time to complete than previous attempts reguardless of
how similar the conditions. Seriously, I lost hope many times just to find out that the program actually ran correctly after a long long time.
For a while I thought that my program was failing, but it seems that eventually the program will complete the
goal. Not sure if its my program or the computers I am running it on, but in any case I have no idea what the cause is.

--- command line arguments ---
-- list-messaging-nodes
-- list-weights
-- send-overlay-link-weights
-- setup-overlay
-- start <numOfRounds>
-- shutdown

-- print-shortest-path
-- exit-overlay
------------------------------


I have all my .java files in their respective directories inside of my tar, and a makefile/README.txt (this thing youre reading).
I excluded all class files so compilation will be required before you run the program.




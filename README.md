# dcoverlay
This is a standalone Java application which can be used to build an overlay network.

We can make necessary changes in the properties file and run the application.

The information required in the properties file are
  * Bootstrap server IP
  * Bootstrap server port
  * Local IP
  * Local port
  * Time to live (ttl) value of a message between nodes
  * Username
	
After setting these values and run the application it will connect to a provided bootstrap server
and form the overlay.

Respective IP, port should be given to different different nodes and can form an overlay.

Once the overlay is formed, we can upload files to nodes and search the specific keywords
from the search option provided. 

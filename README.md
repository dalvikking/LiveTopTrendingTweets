# LiveTopTweetsFeed

About :

Web application shows the list of top trending topic on twitter of India.
Selecting the topic will stream the live tweets feed of the selected topic.

Application is currently hosted on Google Cloud. If not accessible please contact.

Link : 104.196.217.74:9000/webui/

Technology Used:

Java for server side.
Tomcat for Servlet Container with Embedded Tomcat. No need to install tomcat on the host machine. Just install java.
Gradle for building war and jar.
Jersey for Rest API’s
AngularJs, HTML, CSS, JS for frontend.
Twitter4j -- Java wrapper for Twitter API.
Eclipse IDE.

How to launch server :

Extract zip. It contains Two folder Server & WebUI.
Navigate to Server folder -- execute cmd - java -jar server.jar -- It will launch the server.
Navigate to WebUI folder -- execute cmd - java -jar server.jar -- It will launch the frontend.
config.props file contains the configuration required by the tomcat to launch the war file like name of war file , port , context name.
server.jar contains the code for embedded tomcat.

Assumptions:

Hard Coded value for India Location woeid is used.
Access Token and Consumer Keys are also hard coded.
Only one websocket connection is made with twitter server due to rate limitation.
Top 45 trending topics are fetched from the server after every 15 min.
Streaming connection is made after 15 min , by killing the old connection in order to update the trend filter.
Client connected to Server using websocket receives the tweets of selected topic after every 5 sec if server have live tweets available.
In some cases client may not be able to fetch live stream of certain topics. This can happen because we can’t control the data coming from the actual twitter stream. So you have to be patient for some topics.
Application caches 100 tweets of each trend in a BlockingQueue.


Architecture :

Application is designed using the concept of microservices.
We can launch any number of instances of server and frontend on the bass of load we have. 

Link to Image :  https://docs.google.com/drawings/d/12OBFdH7RzJkfukozH0rvoHFL4mcJm1YjyadJENDn3tk/edit?usp=sharing

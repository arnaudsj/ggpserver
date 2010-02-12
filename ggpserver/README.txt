# install tomcat, some webapps and the admin webapp, some java stuff
sudo apt-get install tomcat5.5 tomcat5.5-webapps tomcat5.5-admin libcommons-lang-java

# add a manager user in /etc/tomcat5.5/tomcat-users.xml e.g.:
<?xml version='1.0' encoding='utf-8'?>
<tomcat-users>
  <role rolename="manager"/>
  <role rolename="tomcat"/>
  <role rolename="admin"/>
  <user username="tomcat" password="tomcat" roles="tomcat"/>
  <user username="admin" password="???" roles="tomcat,admin,manager"/>
  <user username="flux" password="???" roles="tomcat,manager"/>
</tomcat-users>

# restart tomcat
sudo /etc/init.d/tomcat5.5 restart

# install mysql
sudo apt-get install mysql-server
# enter some sql root password

# ========================================================================================

# Tomcat needs the files mysql-connector-java.jar (MySQL driver), commons-lang.jar (some helper classes) and jstl.jar / standard.jar (JSTL standard taglib).

# Steps to install under Ubuntu:

sudo apt-get install libmysql-java libcommons-lang-java
sudo ln -s /usr/share/java/mysql-connector-java.jar /usr/share/tomcat5.5/common/lib/
sudo ln -s /usr/share/java/commons-lang.jar /usr/share/tomcat5.5/common/lib/

# libraries for <%@ taglib ...
sudo ln -s /usr/share/tomcat5.5-webapps/jsp-examples/WEB-INF/lib/standard.jar /usr/share/tomcat5.5/common/lib/
sudo ln -s /usr/share/tomcat5.5-webapps/jsp-examples/WEB-INF/lib/jstl.jar /usr/share/tomcat5.5/common/lib/



# ========================================================================================

# install phpmyadmin to admin database (can usually be reached at: http://127.0.0.1/phpmyadmin/)
sudo apt-get install phpmyadmin

# not necessary for ubuntu intrepid or better
sudo ln -s /etc/phpmyadmin/apache.conf /etc/apache2/conf.d
sudo apache2ctl restart

# First execute 01_create_database.sql, then 02_add_db_user.sql .
mysql -u root -p < 01_create_database.sql
mysql -u root -p < 02_add_db_user.sql
mysql -u root -p < 03_add_games.sql
# enter the matching sql root password

# ========================================================================================

# Add the following lines to the first grant section of the /etc/tomcat5.5/policy.d/04webapps.policy file:

// The permissions granted to the balancer WEB-INF/classes and WEB-INF/lib directory
grant codeBase "file:/usr/share/tomcat5.5/webapps/ggpserver/-" {
        permission java.net.SocketPermission "*", "connect,resolve";  // required for database access to 127.0.0.1:3306 and for connecting to the players
        permission java.util.PropertyPermission "file.encoding", "read";
        permission java.util.PropertyPermission "java.io.tmpdir", "read";
        permission java.util.PropertyPermission "org.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER", "read";
        permission java.util.logging.LoggingPermission "control";
        permission java.io.FilePermission "${catalina.base}${file.separator}webapps${file.separator}ggpserver${file.separator}WEB-INF${file.separator}classes${file.separator}logging.properties", "read";
        permission java.io.FilePermission "${catalina.base}${file.separator}logs", "read, write";
        permission java.io.FilePermission "${catalina.base}${file.separator}logs${file.separator}*", "read, write";
        permission java.io.FilePermission "${java.io.tmpdir}${file.separator}*", "read, write, delete"; // required for exporting tournaments/matches
};

# change the codeBase according to your tomcat installation

# ========================================================================================

# Edit the file /etc/default/tomcat5.5 to give Tomcat more memory, like this:

JAVA_OPTS="-Djava.awt.headless=true -Xmx256M"

# If you want to trigger a heap dump if an OutOfMemoryError occurs, also add the following options:
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/lib/tomcat5.5/logs

# ========================================================================================

# Export the WAR file. There are two ways to do this:

# --- OPTION 1 (the Eclipse way) ---
# 1. Install the following Eclipse plugins:
#      - J2EE Standard Tools (JST) Project
#      - Web Standard Tools (WST) Project
#    Depending on your version of Eclipse, you may have to click "Add Required" to automatically add additional dependencies, such as:
#      - Eclipse Modeling Framework (EMF) Runtime
#      - EMF Service Data Objects (SDO) Runtime
#      - Graphical Editing Framework
#      - Visual Editor (Java EMF Model)
   
# 2. Right-click on the ggpserver project --> Export --> WAR file


# --- OPTION 2 (the Ant way) ---

sudo apt-get install ant
ant -f my-build.xml cleanall war

# BTW, the file build.xml can be re-generated from Eclipse by selecting Export -> Ant Buildfiles. The file my-build.xml is a modified version of that.

# ========================================================================================

# Start the Tomcat application server and log in to the Tomcat Manager with the manager user
# (usually at http://127.0.0.1:8180/manager/html)

# If there is an old instance of ggpserver running, click "remove".

# Upload your newly created ggpserver.war file.

========================================================================================

# the GGPServer should now be reachable at http://hostname:8180/ggpserver/
# After starting the web application, log in as user "admin", password "eu4uo5Ha" and start the round robin scheduler.

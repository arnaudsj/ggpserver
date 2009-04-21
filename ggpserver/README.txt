# required:
# - tomcat 5.5
sudo apt-get install tomcat5.5

# - mysql
sudo apt-get install mysql-server
# enter some sql root password

# ========================================================================================

# Tomcat needs the files mysql-connector-java.jar (MySQL driver) and jstl.jar / standard.jar (JSTL standard taglib).

# Steps to install under Ubuntu:

sudo apt-get install libmysql-java
sudo ln -s /usr/share/java/mysql-connector-java.jar /usr/share/tomcat5.5/common/lib/

# libraries for <%@ taglib ...
sudo apt-get install tomcat5.5-webapps
sudo ln -s /usr/share/tomcat5.5-webapps/jsp-examples/WEB-INF/lib/standard.jar /usr/share/tomcat5.5/common/lib/
sudo ln -s /usr/share/tomcat5.5-webapps/jsp-examples/WEB-INF/lib/jstl.jar /usr/share/tomcat5.5/common/lib/

# ========================================================================================

# install phpmyadmin to admin database
sudo apt-get install phpmyadmin

# not necessary for ubuntu intrepid or better
sudo ln -s /etc/phpmyadmin/apache.conf /etc/apache2/conf.d
sudo apache2ctl restart

# First execute 01_create_database.sql, then 02_add_db_user.sql .
mysql -u root -p < 01_create_database.sql
mysql -u root -p < 02_add_db_user.sql
# enter the matching sql root password

# ========================================================================================

Add the following lines to the first grant section of the /etc/tomcat5.5/policy.d/04webapps.policy file:

permission java.net.SocketPermission "*", "connect,resolve";  // required for database access to 127.0.0.1:3306 and for connecting to the players
permission java.util.PropertyPermission "file.encoding", "read";
permission java.util.PropertyPermission "org.apache.jasper.runtime.BodyContentImpl.LIMIT_BUFFER", "read";
permission java.util.logging.LoggingPermission "control";
permission java.io.FilePermission "${catalina.base}${file.separator}webapps${file.separator}ggpserver${file.separator}WEB-INF${file.separator}classes${file.separator}logging.properties", "read";

========================================================================================

Edit the file /etc/default/tomcat5.5 to give Tomcat more memory, like this:

JAVA_OPTS="-Djava.awt.headless=true -Xmx256M"

The more, the better. Most memory is needed for the games. 128MB should be okay for now, but you never know...

========================================================================================

# After starting the web application, log in as user "admin", password "admin" and start the round robin scheduler.


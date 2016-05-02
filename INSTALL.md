# vefa-srest

### Install MySQL with database oxalis_test
- install mysql
- create database `oxalis_test` with one user `skrue` using password `vable`
````
    create database oxalis_test;
    create user 'skrue' identified by 'vable';
    use oxalis_test;
    grant all on oxalis_test.* to 'skrue';
````
- run the script in `/server/src/main/sql/create-oxalis-dbms.sql`
Given that your are located in the root source directory of the code, this
MySQL command should do the trick:
````
source /server/src/main/sql/create-oxalis-dbms.sql
````

### Install Java 8 and Tomcat 9
- create new folder .../rest-server/
- unzip JDK 8 in that folder .../rest-server/jdk1.8.0_66
- unzip Tomcat 9 in that folder .../rest-server/apache-tomcat-9.0.0.M4
- make start / stop scripts runnable chmod +x .../rest-server/apache-tomcat-9.0.0.M4/bin/*.sh
- create .../rest-server/apache-tomcat-9.0.0.M4/bin/setenv.sh file pointing to your local JDK
```
# Linux put this in setenv.sh
export JAVA_HOME=.../rest-server/jdk1.8.0_66

# Windows put this is setenv.bat
set JAVA_HOME=C:/.../rest-server/jdk1.8.0_66
```

### Compile code
- mvn clean test            <== unit tests
- mvn clean verify          <== integration tests (needs database and network)
- mvn clean install -Pprod  <== build runnable artifacts for production
- mvn clean install -Ptest  <== build runnable artifacts for test

### Install build artifacts for REST interface
- Copy ROOT.war to tomcat/webapps as vefa.war
- Copy MySQL jdbc driver file mysql-connector-java-5.1.38-bin.jar to tomcat/lib
- Copy Ringo Realm file ringo-tomcat-realm-1.1.28-SNAPSHOT-jar-with-dependencies.jar to tomcat/lib
- Add the jdbc datasource resource into GlobalNamingResources in tomcat/conf/server.xml
```
<Resource name="jdbc/oxalis"
        auth="Container"
        type="javax.sql.DataSource"
        maxActive="100"
        maxIdle="30"
        maxWait="10000"
        username="skrue"
        password="vable"
        driverClassName="com.mysql.jdbc.Driver"
        url="jdbc:mysql://localhost:3306/oxalis_test"
        removeAbandoned="true"
        removeAbandonedTimeout="60"
        logAbandoned="true"
        validationQuery="select now()"
    />
```
- Configure security realm in tomcat/conf/server.xml
```
<Realm className="ringo.realm.RingoDataSourceRealm"
        dataSourceName="jdbc/oxalis"
        userTable="account"
        userNameCol="username"
        userCredCol="password"
        userRoleTable="account_role"
        roleNameCol="role_name"
    />
```
- Add resource link from GlobalNamingResources to the WebApp Context in tomcat/conf/context.xml
```
<ResourceLink name="jdbc/oxalis" global="jdbc/oxalis" type="javax.sql.DataSource"/>
```
- Start tomcat/bin/startup.sh
- Make sure there are no errors in tocat/logs/catalina.out
- Verify REST response using a browser or command line curl
```
curl -i http://localhost:8080/vefa/statistics -u username:password
```
- Stop tomcat/bin/shutdown.sh
- Tweak tomcat/conf/server.xml to optimize furher,  turn off 8443 redirects, turn off https and ajp etc
 ```
 <Connector port="8080" protocol="HTTP/1.1"
            connectionTimeout="20000" />
            <!--
            redirectPort="8443" />
            -->
 ```

# vefa-srest

Install MySQL or use the supplied H2 database. Follow the instructions below.

### Install MySQL with database oxalis_test
- install mysql
- create database `oxalis_test` with one user `skrue` using password `vable`

  ```sql    
    create database oxalis_test;
    create user 'skrue' identified by 'vable';
    use oxalis_test;
    grant all on oxalis_test.* to 'skrue';
  ```        
- run the script in `/server/src/main/sql/create-oxalis-dbms.sql`
Given that your are located in the root source directory of the code, this
MySQL command should do the trick:

  ```sql
  source /server/src/main/sql/create-oxalis-dbms.sql
  ```


### Install Java 8 and Tomcat 9
- create new folder .../rest-server/
- unzip JDK 8 in that folder .../rest-server/jdk1.8.0_66
- unzip Tomcat 9 in that folder .../rest-server/apache-tomcat-9.0.0.M4
- make start / stop scripts runnable chmod +x .../rest-server/apache-tomcat-9.0.0.M4/bin/*.sh
- create .../rest-server/apache-tomcat-9.0.0.M4/bin/setenv.sh file pointing to your local JDK:

  ```sh
  # Linux put this in setenv.sh
  export JAVA_HOME=.../rest-server/jdk1.8.0_66

  # Windows put this is setenv.bat
  set JAVA_HOME=C:/.../rest-server/jdk1.8.0_66
  ```

### Compiling the code

The code may be compiled using the following commands:

Command                     | Comment
--------------------------- | ---------------------------
`mvn clean test`            | unit tests
`mvn clean verify`          | integration tests (needs database and network)
`mvn clean install -Pprod`  | build runnable artifacts for production in `server/target/ROOT.war`
`mvn clean install -Ptest`  | build runnable artifacts for test (fake SMP lookup) in `server/target/test.war`.
`mvn clean install`         | same as above


### Install build artifacts for REST interface

1. Copy `server/target/ROOT.war` to $TOMCAT_HOME/webapps as `ROOT.war`
1. Copy MySQL jdbc driver file mysql-connector-java-5.1.38-bin.jar to `tomcat/lib`
1. Copy Ringo Realm file ringo-tomcat-realm-1.1.28-SNAPSHOT-jar-with-dependencies.jar to `tomcat/lib`
1. Add the jdbc datasource resource into `<GlobalNamingResources>` in `tomcat/conf/server.xml`:
   ```xml
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
1. Configure security realm in `tomcat/conf/server.xml` inside the `<Engine>` element, below the `UserDatabaseRealm` (nested inside the `LockOutRealm`):

  ```xml
  <Engine name="Catalina" defultHost="localhost">

        <Realm className="org.apache.catalina.realm.LockOutRealm">

            <Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase" />
    
                  <Realm className="ringo.realm.RingoDataSourceRealm"
                        dataSourceName="jdbc/oxalis"
                        userTable="account"
                        userNameCol="username"
                        userCredCol="password"
                        userRoleTable="account_role"
                        roleNameCol="role_name"
                    />
```
1. Add resource link from GlobalNamingResources to the WebApp Context in `${TOMCAT_HOME}/conf/context.xml`:

  ```
<ResourceLink name="jdbc/oxalis" global="jdbc/oxalis" type="javax.sql.DataSource"/>
  ```
  
1. Start Tomcat using `${TOMCAT_HOME}/bin/startup.sh`
1. Make sure there are no errors in `$TOMCAT_HOME/logs/catalina.out`
1. Verify REST response using a browser or command line _curl_:

  ```
  curl -i http://localhost:8080/vefa-srest/statistics -u username:password
  ```
  
1. Stop Tomcat: `${TOMCAT_HOME}/bin/shutdown.sh`
1. Tweak `${TOMCAT_HOME}/conf/server.xml` to optimize further,  
  turn off 8443 redirects, turn off "https", "ajp" etc
 ```
 <Connector port="8080" protocol="HTTP/1.1"
            connectionTimeout="20000" />
            <!--
            redirectPort="8443" />
            -->
 ```

1. Execute the client and enqueue the eligible documents for transmission:

  ```
  bin/upload sr ringo c:/temp/ringo/outbox c:/temp/ringo/archive CH1
  ```

1. Execute the vefa-srest standalone client, which will fetch all enqued outbound messages
  and transmit them:
  
  ```
  java -jar target\ringo-standalone.jar -d oxalis_test -h localhost \
  -k /C:/Users/soc/Dropbox/DIFI/oxalis/difi-test-cert-ok-2015/difi-keystore.jks \
  -p vable -t ALL -u skrue -s true
  ```

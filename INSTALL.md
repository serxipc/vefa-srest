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

### Install Java and server
- create new folder .../rest-server/
- unzip JDK 8 in that folder .../rest-server/jdk1.8.0_66
- unzip Tomcat 7 in that folder .../rest-server/apache-tomcat-7.0.42

### Compile code
- mvn clean test            <== unit tests
- mvn clean verify          <== integration tests (needs database and network)
- mvn clean install -Pprod  <== build runnable artifacts for production
- mvn clean install -Ptest  <== build runnable artifacts for test

### Install build artifacts
- Copy ROOT.war to tomcat/webapps
- Copy realm file ringo-tomcat-realm-x.y.zz-jar-with-dependencies.jar to tomcat/lib
- Copy MySQL jdbc driver file to tomcat/lib
- Configure jdbc datasource resource in tomcat/conf/server.xml
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
- Start tomcat/bin/startup.sh
- Stop tomcat/bin/shutdown.sh
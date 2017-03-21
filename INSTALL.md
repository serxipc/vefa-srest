vefa-srest a.k.a. "Ringo" - REST based API for Oxalis based PEPPOL access points
================================================================================

"vefa-srest" is the REST API for Oxalis

[TOC]: #
# Table of Contents
- [Prerequisites](#prerequisites)
- [Downloading and compiling the code from source](#downloading-and-compiling-the-code-from-source)
- [Installing the compiled binary artifacts](#installing-the-compiled-binary-artifacts)
- [Installing and configuring the DBMS](#installing-and-configuring-the-dbms)
    - [Configuring BASIC authentication](#configuring-basic-authentication)
- [Optional configuration of Tomcat](#optional-configuration-of-tomcat)
- [Verification of installation](#verification-of-installation)
- [Verifying the installation](#verifying-the-installation)
- [Notes on BASIC authentication](#notes-on-basic-authentication)


***

## Prerequisites

- Oxalis version 4.x has been installed and verified.

These installation instructions assumes that you are using Tomcat 9.x with Oxalis installed.

## Downloading and compiling the code from source

There is currently no binary distribution available.

The source code may be compiled and packaged using the following command:

    mvn -Dmaven.test.skip clean verify  

Once this command has completed, you will find a complete binary distribution in the following
directory:

    ringo-distribution/target/ringo-distribution-${version}-bin.zip

## Installing the compiled binary artifacts

1. Create a home directory for Ringo, we recommend `~/.ringo` and create an environment variable
   referencing the the directory. The following command is an example showing how to do this on
   Linux systems:

        export RINGO_HOME=~/.ringo

2. Unpack the contents of the binary distribution in a separate directory like for instance
   `~/opt/`:

   ```shell
   mkdir ~/opt/ringo-distribution-${version}
   cp ringo-distribution/target/ringo-distribution-${version}-bin.zip ~/opt/ringo-distribution-${version} 
   cd ~/opt/ringo-distribution-${version}
   unzip ringo-distribution-${version}-bin.zip 
   ```

3. Copy `war/ringo-server-${version}.war` to `$TOMCAT_HOME/webapps/vefa-srest.war`

4. Create and edit the configuration file `~/.ringo/ringo.conf`:

   ```
   # ringo.conf - example file, edit to your liking
   
   # Where to store uploaded outbound files
   ringo.payload.basedir = /var/peppol
   
   
   # Uncomment to use a plugin for rewriting URIs
   # ringo.blob.uri.handler = plugin
   
   # Directory in which our plugins are located
   ringo.plugin.path = ringo-plugin
   
   # Includes the JDBC configuration from a separate shared file 
   include "/Users/steinar/.spiralis/jdbc.conf"    
   ```

5. Copy the executable `ringo-standalone-${version}.jar` into a suitable directory from where
   you will execute it later.

## Installing and configuring the DBMS

The distribution includes the H2 SQL DBMS, which is sufficient for testing. However in
production you might want to consider replacing this with a database of your choice. MySQL is a
recommended choice and has been thoroughly tested.

1. Create the database and populate the scheme by running the SQL script, which is appropriate
   for your kind of DBMS. For H2 and MySQL, use
   [create-ringo-dbms-h2.sql](ringo-persistence-jdbc-impl/src/main/resources/create-ringo-dbms-h2.sql).

   Note: consult the documentation for H2 to install the DBMS before you run the SQL scripts.

2. Here is a sample `jdbc.conf` file for your convenience.

   ```
   // Default configuration uses H2, which should be on the classpath as it is supplied as part of the 
   // software distribution
   jdbc {
   	connection.uri : "jdbc:h2:~/.oxalis/ap;AUTO_SERVER=TRUE"
   	driver.class {
   	    path : ""
   	    name : "org.h2.Driver"
   	}
   	user: "SA"
   	password : ""
   	validation.query : "select now()"
   }
   ```

### Configuring BASIC authentication

1. Copy the the .jar-file of the JDBC driver to `$TOMCAT_HOME/lib`

2. Modify the contents of `$TOMCAT_HOME/conf/Catalina/localhost/vefa-srest.xml` to make the
   security realm of Tomcat point to your database. This file is automagically created the first
   time you copy the `.war`-file into the `$TOMCAT_HOME/webapps` directory:

```
<!--  Establishes a JNDI DataSource made available in -->
<!--  java:comp/env as jdbc/oxalis -->
<Resource name="jdbc/oxalis"
          auth="Container"
          type="javax.sql.DataSource"
          maxActive="100"
          maxIdle="30"
          maxWait="10000"
          username="sa"                     <<<< Change this
          password=""                       <<<< Change this
          driverClassName="org.h2.Driver"   <<< Change to class name of your driver
          url="jdbc:h2:~/.oxalis/ap;AUTO_SERVER=true" <<<< Change to your DBMS URL
          removeAbandoned="true"
          removeAbandonedTimeout="60"
          logAbandoned="true"
          validationQuery="select now()"
/>
```

## Optional configuration of Tomcat

1. Tweak `${TOMCAT_HOME}/conf/server.xml` to optimize further,  
   turn off 8443 redirects, turn off "https", "ajp" etc

   ```
   <Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000" />
           <!--
           redirectPort="8443" />
           -->
   ```

## Verification of installation

1. Execute the client and enqueue the eligible documents for transmission:

   ```
   bin/upload sr ringo1 c:/temp/ringo/outbox c:/temp/ringo/archive CH1
   ```

2. Execute the vefa-srest standalone client, which will fetch all enqued outbound messages and
   transmit them:

```
    java -jar target\ringo-standalone.jar -t ALL -s true
```

## Verifying the installation

1. Start Tomcat using `${TOMCAT_HOME}/bin/startup.sh`

2. Make sure there are no errors in `$TOMCAT_HOME/logs/catalina.out`

3. Verify REST response using a browser or command line _curl_:

   ```
   curl -i http://localhost:8080/vefa-srest/statistics -u username:password
   ```

4. Stop Tomcat: `${TOMCAT_HOME}/bin/shutdown.sh`

## Notes on BASIC authentication

BASIC authentication is used to verify credentials of users and associating them with the
correct account. Using BASIC authentication over https is considered safe.

The password is stored in the SQL database `account.password` for each user.

The password is generated using the _PBKDF2WithHmacSHA1_ algorithm, which is considered being
one of the best ways of handling passwords these days. There is really no simple way to reverse
the password. If you are really interested in this topic, may I suggest
[this article from CrackStation](https://crackstation.net/hashing-security.htm)

Basically this means you must use the `$TOMCAT_HOME/bin/digest.sh|.bat` script or write your own
little Java program.

This command will generate a hashed password for cleartext "ringo1". Basically the hashed
password will be different every time you execute it:

```
    bin/digest.sh -a PBKDF2WithHmacSHA1 -h org.apache.catalina.realm.SecretKeyCredentialHandler ringo1
```

The output looks like this: ```
ringo1:197ce6e3955f4d4c24e8e35ae15be74f6cd5e5bfe7a19bc8201cfaad7629fdec$20000$bb48ce2c6e389b5eb2fdbb07671d8fb516aeaed5
``` Take away the `ringo1:` (don't forget the colon) and what remains is the encrypted password
to be stored into `account.password`

The `bin/digest` command is explained in the Tomcat documentation.

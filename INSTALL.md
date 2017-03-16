vefa-srest a.k.a. "Ringo" - REST based API for Oxalis based PEPPOL access points
================================================================================

"vefa-srest" is the REST API for Oxalis

[TOC]: #
# Table of Contents
- [Prerequisites](#prerequisites)
- [Compiling the code](#compiling-the-code)
- [Installation of artifacts for the REST interface](#installation-of-artifacts-for-the-rest-interface)
- [Configuration of BASIC authentication](#configuration-of-basic-authentication)
- [Replacing the SQL DBMS](#replacing-the-sql-dbms)


***

## Prerequisites

- Oxalis version 4.x has been installed and verified.

These installation instructions assumes that you are using Tomcat 9.x
with Oxalis installed.

## Compiling the code

The code may be compiled using the following commands:

| Command             | Comment                                                                                 |
|:--------------------|:----------------------------------------------------------------------------------------|
| `mvn clean test`    | unit tests                                                                              |
| `mvn clean verify`  | integration tests (needs database and network)                                          |
| `mvn clean install` | builds runnable artifacts for production in `server/target/ringo-server-${version}.war` |

This installation guide assumes that you have a binary distribution,
which you have either built using the maven commands listed above or
downloaded.


## Installation of artifacts for the REST interface

1. Create a home directory for Ringo, we recommend `~/.ringo` and create an environment variable referencing the
   the directory:

   ```
   export RINGO_HOME=~/.ringo
   ```

2. Unpack the contents of the binary distribution in a separate
   directory:

   ```shell
   mkdir ~/opt/ringo-distribution-${version}
   cd ~/opt/ringo-distribution-${version}
   unzip ringo-distribution-${version}-bin.zip 
   ```

3. Copy `war/ringo-server-${version}.war` to
   `$TOMCAT_HOME/webapps/vefa-srest.war`

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

5. Here is a sample `jdbc.conf` file for your convenience.
   Please consult [Replacing the SQL DBMS](#replacing-the-sql-dbms) if you intend to use a different DBMS than the one
   supplied (H2) with the distribution:

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
   
6. Create the database and populate the scheme by running the SQL script, which is appropriate for your kind of DBMS.
   For H2, use [create-ringo-dbms-h2.sql](ringo-persistence-jdbc-impl/src/main/resources/create-ringo-dbms-h2.sql).

   Note: consult the documentation for H2 to install the DBMS before you run the SQL scripts.

7. Start Tomcat using `${TOMCAT_HOME}/bin/startup.sh`

8. Make sure there are no errors in `$TOMCAT_HOME/logs/catalina.out`

9. Verify REST response using a browser or command line _curl_:

   ```
   curl -i http://localhost:8080/vefa-srest/statistics -u username:password
   ```

10. Stop Tomcat: `${TOMCAT_HOME}/bin/shutdown.sh`

11. Tweak `${TOMCAT_HOME}/conf/server.xml` to optimize further,  
   turn off 8443 redirects, turn off "https", "ajp" etc

   ```
   <Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000" />
           <!--
           redirectPort="8443" />
           -->
   ```

12. If you are going to use a DBMS of your own choice, you must modify
    the security realm. Please consult the section
    [Replacing the SQL DBMS](#replacing-the-sql-dbms) for detailed
    instructions on how to do this.

13. Start Tomcat again

14. Execute the client and enqueue the eligible documents for
    transmission:

    ```
    bin/upload sr ringo1 c:/temp/ringo/outbox c:/temp/ringo/archive CH1
    ```

15. Execute the vefa-srest standalone client, which will fetch all enqued
    outbound messages and transmit them:

    ```
    java -jar target\ringo-standalone.jar -d oxalis_test -h localhost \
-k /C:/Users/soc/Dropbox/DIFI/oxalis/difi-test-cert-ok-2015/difi-keystore.jks \
-p vable -t ALL -u skrue -s true
    ```


## Configuration of BASIC authentication

BASIC authentication is used to verify credentials of users and
associating them with the correct account. Using BASIC authentication
over https is considered safe.

The password is stored in the SQL database `account.password` for each
user.

The password is generated using the _PBKDF2WithHmacSHA1_ algorithm,
which is considered being one of the best ways of handling passwords
these days. There is really no simple way to reverse the password. If
you are really interested in this topic, may I suggest
[this article from CrackStation](https://crackstation.net/hashing-security.htm)

Basically this means you must use the `$TOMCAT_HOME/bin/digest.sh|.bat`
script or write your own little Java program.

This command will generate a hashed password for cleartext "ringo1".
Basically the hashed password will be different every time you execute
it:

```
    bin/digest.sh -a PBKDF2WithHmacSHA1 -h org.apache.catalina.realm.SecretKeyCredentialHandler ringo1
```

The output looks like this: ```
ringo1:197ce6e3955f4d4c24e8e35ae15be74f6cd5e5bfe7a19bc8201cfaad7629fdec$20000$bb48ce2c6e389b5eb2fdbb07671d8fb516aeaed5
``` Take away the `ringo1:` (don't forget the colon) and what remains is
the encrypted password to be stored into `account.password`

The `bin/digest` command is explained in the Tomcat documentation.

## Replacing the SQL DBMS

The standard distribution comes preconfigured with support for H2.

If you wish to use a different database:

1. Copy the the .jar-file of the JDBC driver to `$TOMCAT_HOME/lib`

2. Modify the contents of
   `$TOMCAT_HOME/conf/Catalina/localhost/vefa-srest.xml` to make the
   security realm of Tomcat point to your database:

   ```
   
   ```

<!--  Establishes a JNDI DataSource made available in java:comp/env as jdbc/oxalis -->

        <Resource name="jdbc/oxalis"
                  auth="Container"
                  type="javax.sql.DataSource"
                  maxActive="100"
                  maxIdle="30"
                  maxWait="10000"
                  username="sa"                     <<<< Change tthis
                  password=""                       <<<< Change this
                  driverClassName="org.h2.Driver"   <<< Change to class name of your driver
                  url="jdbc:h2:~/.oxalis/ap;AUTO_SERVER=true" <<<< Change to your DBMS URL
                  removeAbandoned="true"
                  removeAbandonedTimeout="60"
                  logAbandoned="true"
                  validationQuery="select now()"
        />
    ```

1. Verify the contents of the table `account`. Hint: look at the
   database creation script found in your Oxalis distribution.
2. Restart Tomcat and you should be able to login using for example
   username `sr` with password `ringo1`.

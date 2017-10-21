# pentaho-dynamicschemaprocessor

Example DynamicSchemaProcessor (DSP) for Pentaho that uses a JNDI connection. 
Depending on the userrole and username it gives full, restrictet (to certain tenants) or no access to the user. 

## What does it need?

### Copy resulting jar to Pentaho server
`...\pentaho-server-ee\pentaho-server\tomcat\webapps\pentaho\WEB-INF\lib`

### JNDI connection parameters in pentaho server
`.../pentaho-server/tomcat/conf/context.xml`
```xml
<Context>
    ...
    ...
    <Resource 
    	name="jdbc/ExampleJNDI" 
    	auth="Container" type="javax.sql.DataSource"
    	factory="org.apache.commons.dbcp.BasicDataSourceFactory" 
    	maxActive="20" 
    	maxIdle="5"
    	maxWait="10000" 
    	username="myuser" 
    	password="mypassword" 
    	driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
    	url="jdbc:sqlserver://localhost:1433;DatabaseName=myDatabaseName"
    />
</Context>
```

`.../pentaho-server/tomcat/webapps/pentaho/web-inf/web.xml`
```xml
<web-app>
    ...
    ...
    <resource-ref>
    	<description>My Connection</description>
    	<res-ref-name>jdbc/ExampleJNDI</res-ref-name>
    	<res-type>javax.sql.DataSource</res-type>
    	<res-auth>Container</res-auth>
    </resource-ref>
    ...
</web-app>
```


###  Add SQL security where clause to Mondrian Schema
This clause in the schema will be replaced from the DSP during runtime depending on the role/user.
```xml
...
<Cube name="....">
    <Table name="t_Example" schema="dbo" alias="Example">
      <SQL dialect="generic">
        <![CDATA[SECURITY_PATTERN#Example.idTenant IN (%TENANT_IDS%)#]]>
      </SQL>
    </Table>
    ...
</Cube>
```

###  If you want so see the logs from the DSP in pentaho.xml
Set the loglevel for your namespace in log4j.xml
```xml
...
<category name="org.migaman.pentaho.example.dsp">
    <priority value="INFO"/>
</category>
...
```


## Can't find Pentaho libraries?
Pentaho Maven Repository  
http://nexus.pentaho.org/content/groups/omni

~/.m2/settings.xml example  
https://github.com/pentaho/maven-parent-poms/blob/master/maven-support-files/settings.xml
# Configuration file
All available settings are located in the [application.yml](application.yml) file. The file in this directory should be used as a template and appropriate values should be populated into a custom file.

## How to use custom file
* Ensure that the custom configuration file is in the same directory as the final jar file
```
cd target
ls
application.yml  skip-period-tool.jar
```
* Set path to the custom configuration file as a parameter in the following way:
```
-Dspring.config.location=config/tool/application.yml
```
## spring section
```
spring:
    main:
        banner-mode: (OFF|CONSOLE|LOG)
    batch:
        job:
            enabled: boolean
```
In this section you can provide standard spring properties that could be read by [Spring](https://spring.io/) library. Currently the template sets two things:
* the Spring banner will be written only to log file. In other words when a user run the application there will be no Spring banner on default output. There will be addictional file **log** in which there will be more details messages.
* By default we disabled a Spring Batch job that would execute ETL process. For now the job is manually executed by the tool and this property should be set to **false**
## tool section
```
tool:
    parameters:
    configuration:
```
This section is used only by the tool and it is devided into smaller sections
* parameters - parameters that would be used during process.
* configuration - this section contains details about OpenLMIS databases and additionally Spring Batch settins

#### parameters section
```
tool:
    parameters:
        startDate: YYYY-MM-DD
        endDate: YYYY-MM-DD
        creator: string
        timeZone: string
        facilities:
            - string
            - string
        programs:
            - string
            - string
```
The following list explains all settings in the *parameters* section:
* start/end date - set from/to what period periods should be skipped. By default the start date is set to today date minus 5 years and with first day of month. The end date is set to today date with last day of month. The format for dates is the following: YYYY-MM-DD
* creator - when a requisition is created we need to set who create/skipped. This field should contain a username.
* timeZone - set time zone for dates
* facilities - set for what facilities periods should be skipped.
* programs - set for what programs periods should be skipped.

#### configuration section
```
tool:
    configuration:
        olmis:
            dialect: string
            showSql: boolean
            dataSource:
                connectionProperties:
                    string: string
                driverClass: string
                host: string
                port: integer
                database: string
                username: string
                password: string
        batch:
            chunk: integer
            skipPolicy: string
            skipPeriods: boolean
```
Before the tool can skip periods in OpenLMIS system, it needs to know where is a OpenLMIS database. This section also contains addictional Spring Batch settings.

* olmis - provide details about OpenLMIS database
  * dialect - select what dialect should by used by Hibernate
  * showSql - define if SQL statements should be visible on output
  * dataSource - define settings of OpenLMIS database like address, port, name of database and so on.
* batch - this small configuration sections contains addictional Spring Batch settings
  * chunk - after how much requisitions the Spring Batch should execute the commit command.
  * skipPolicy - set the skip policy. By default if there will be any issue the appropriate error message will be displayed but the tool will continue the work. The class must implement the ```org.springframework.batch.core.step.skip.SkipPolicy``` interface.
  * skipPeriods - set whether the tool should insert skipped requisitions.

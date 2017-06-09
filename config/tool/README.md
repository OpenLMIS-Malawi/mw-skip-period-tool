# Configuration file
All available settings are located in the [application.yml](application.yml) file. The file in this directory should be used as a template and appropriate values should be populated into a custom file.

## How to use custom file
* Ensure that the custom configuration file is in the same directory as the final jar file
```
cd target
ls
application.yml  migration-tool.jar
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
    tableNames:
    parameters:
    configuration:
    mapping:
```
This section is used only by the tool and it is devided into smaller sections
* tableNames - set appropriate table name for tables that wre used by the tool.
* parameters - parameters that would be used during ETL process.
* configuration - this section contains details about SCM/OpenLMIS databases and additionally Spring Batch settins
* mapping - in this section a user can change the way SCM data will be handled. For example if SCM facility code need to be changed before it will be used then the user should add single line which will say that the given facility code should changed to another facility code.

#### parameters section
```
tool:
    parameters:
        startDate: YYYY-MM-DD
        endDate: YYYY-MM-DD
        numberOfPeriodsToAverage: integer
        creator: string
        requestedQuantityExplanation: string
        timeZone: string
        orderNumberConfiguration:
            orderNumberPrefix: string
            includeOrderNumberPrefix: boolean
            includeProgramCode: boolean
            includeTypeSuffix: boolean
```
The following list explains all settings in the *parameters* section:
* start/end date - set from what period data should be migrated into OpenLMIS system. By default the start date is set to today date minus 5 years and with first day of month. The end date is set to today date with last day of month. The format for dates is the following: YYYY-MM-DD
* numberOfPeriodsToAverage - set number of periods to calculate value for average consumption in the requisition template
* creator - when a requisition is created we need to set who create/submit/authorize/approve and so on. This field should contain a username.
* requestedQuantityExplanation - the value of this field will be populated into *Requested Quantity Explanation* column in each requisition.
* timeZone - set time zone for dates
* orderNumberConfiguration - set how order code should be generated. By default a order code has the following pattern ```O${requsition_id}```.

#### configuration section
```
tool:
    configuration:
        createRequisitionTemplate: boolean
        accessFile: string
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
```
Before the tool can migrate data from supply manager into OpenLMIS system, it needs to know where SCM database file is located and where is a OpenLMIS database. This section also contains addictional Spring Batch settings.

* createRequisitionTemplate - decide if the tool should create requisition template before migration process. If the property is set to **false**, there must be templates in database.
* accessFile - defaine where is SCMgr database file. You can set absolute path like ```/home/user/data.mdb``` or relate path like ```../../data.mdb```.
* olmis - provide details about OpenLMIS database
  * dialect - select what dialect should by used by Hibernate
  * showSql - define if SQL statements should be visible on output
  * dataSource - define settings of OpenLMIS database like address, port, name of database and so on.
* batch - this small configuration sections contains addictional Spring Batch settings
  * chunk - after how much requisitions the Spring Batch should execute the commit command.
  * skipPolicy - set the skip policy. By default if there will be any issue with migrating a single Product Tracking form the appropriate error message will be displayed but the tool will continue the work. The class must implement the ```org.springframework.batch.core.step.skip.SkipPolicy``` interface.
  
#### mapping section
```
tool:
    mapping:
        programs:
            - code: string
              categories:
                  - string
                  - string
                  - ...
              warehouses:
                  - geographicZone: string
                    code: string
        facilities:
            string: string
        products:
            string: string
        stockAdjustmentReasons:
            string: string
        categoryProductJoins:
            integer: integer
```
The following list presents available mapping:

* programs - define a program code and what categories are related with this program. Also what warehouse should be selected when a requisition is converted into an order. Addictional if there is a value in the ```geographicZone``` field, warehouse will be selected based by the geographic zone value. Otherwise the geographic zone of facility will be ignored.
* facilities - define a key-value pair for each SCM facility code to find a proper OpenLMIS facility.
* products - define a key-value pair for each SCM product name/code to find a proper OpenLMSI product by code.
* stockAdjustmentReasons - define a key-value pair for each SCM stock adjustment reason to find a proper OpenLMIS stock adjustment reason.
* categoryProductJoins - define a key-value pair of Category Product Join IDs. This mapping will be used only if the tool will not be able to find correct CPJ instance based on data from the Item table. If the CPJ exists, the tool will not use the related mapping.

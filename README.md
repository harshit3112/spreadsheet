# spreadsheet

# spreadsheet


Prompt 1 -
Can you setup a java springboot project for me with maven build system and containing the
following packages
1) spreadsheet-controller

2) spreadsheet-service
this will house the sheetService and sheetDataService  which will have the following methods in the service :

    

3) spreadsheet-model

4) spreadsheet-repository
this package shall house all the db layer connections and entitites
for this project 


ignore:
we shall be using mongo db in the local so i would want you to set it
up on the local and create a mongo config and add the config in application.yaml file in the resources of controller where the application is present



GET, PUT API
Multi user read/write
sheets
Sharing of permissions

Sheet APIs and Sheet Data APIs

Read/Write seperation
Evaluator - Normal +,-
What to be locked, 
find dependent cells - Topological
Locking via REDIS - handle in sheets
User service as well
Simulation - functional testing, running NFR, 
Docker integration
Production code
RDS , shard implement
Small UI for demo
limits, code quality, workflow used to create it
dependents


vikas.s@cred.club
anuj.jalan@cred.club

7829076356



Cell{
    int row;
    int column;
    CellType cellType;
    Object expression;
    Object value;
}

PUT API:
Request{
    int row;
    int column;
    Object value;

}




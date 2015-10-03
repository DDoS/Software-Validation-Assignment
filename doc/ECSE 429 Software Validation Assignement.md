ECSE 429 Software Validation Assignement
====

course : ECSE 429 
Term : Fall 2015
Group # : 14
Team Members : Sapon-Cousineau, Aleksi ; Chen, Yuechuan

-----
##TOC
[TOC]

##Running the Source
1. Download the zipped assignment and unzip to a directory 
2. open Intelliji idea 
3. on Menu : select `New` --> `Project from existing source...` option
4. on the project import dialog : navigate to the project directory and select `Software-Validation-Assignment.iml` to import the project.
5. ![Alt text](./Selection_040.png), once the project is imported into the IDE, Click the dropdown menu on the toolbar and select `Edit configurations`
6. ![Alt text](./Selection_039.png), make sure the main class is selected to `ca.mcgill.ecse429.conformancetest.Main`. The Program arguments should be the state machine definition (xml) located in the project root directory.
7. Give a name to the configuration and `save`.
8.  ![Alt text](./Selection_041.png) , press green triangle to generate the tests. The generated classes will be placed in the folder corresponding to the package path. 

##State machine description 
The state machine depends heavily on two other modules : the `State` class and the `Transition` class. 

###Transition class
Is a collection of an `event`(eg. vend), `condition`(eg. curQrts==2) , `action` (eg. curQtrs+=1) and two other states `from` (the state transfered from ) and `to` (the state transferring to ).

###State class
A wrapper class that contains the name of the state

###State Machine class
Contains a list of all valid `States` as well as a list of all valid `Transitions`. It also contains attributes such as the initial state, the class and package names of that state machine.

This simple state machine convers all transitional behaviors of the CCoinbox SM as well as the `legislation` state machine and is specific enough for (mostly hands free) code generation . 

##Manual changes

Although Code generated is pretty solid, we still need to make a few alterations to the generated code. 
Inside the method block of `conformanceTest[7..9]` in the GeneratedTestCCoinBox class. We have to throw an `UnsupportedOperationExcaption` to indicate that the generated tests are incomplete and needed manual fix. This is beacuse our implementation cannot deduce from the state machine diagram the complex steps required to take in order to meet a condition. 

For example, in `conformanceTest9` we are able to test if  `curQtrs` is equal to 3, but when it does not meet such requirement, the prorgram will not know what to do to bring the `curQtrs` value to 3. Hence, human intervention is necessary :

```java
// generated
while (machine.getCurQtrs() <= 3) {
    throw new UnsupportedOperationException("Manually add code here for reaching condition: curQtrs > 3");
}

//fix 
while (machine.getCurQtrs() <= 3) {
    machine.addQtr();
}
```


##Defects of CCoinBox implementation 
- list defects 
- how to fix each 

##Main challenges to automate sneak path generation from a SM conforming to the metamodel in Fig.1


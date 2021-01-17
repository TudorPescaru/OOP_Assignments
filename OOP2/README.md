# Energy System Project

## About

Object Oriented Programming, Series CA, CD 2020-2021

<https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa1>

<https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa2>

Student: Pescaru Tudor-Mihai, 321CA

## Running tests

Test#main Class
  * runs the solution on the tests from checker/, comparing the results to 
  the reference ones
  * runs checkstyle

Details about tests: checker/README

Necessary libraries for implementation:
* Jackson Core 
* Jackson Databind 
* Jackson Annotations
* json.simple

## Implementation

### Entities

Packages and classes:

* **database**
    * _Database_: this class represents the core of the implementation, it 
    runs the entire simulation and stores the data used in the simulation.
* **entities**
    * _Entity_: this interface describes the main characteristic of an entity 
    in the simulation, the processMonth() method.
    * _EntityFactory_: this class represents an implementation of the factory 
    pattern that will serve in creating Consumer, Distributor and Producer 
    objects.
    * _Consumer_: this class implements the consumer entity and tracks all 
    data about a specific consumer as well as providing methods that perform 
    consumer-specific actions, this class interacts with the distributor 
    class via contracts.
    * _Distributor_: this class implements the distributor entity and tracks 
    all data about a specific distributor as well as providing methods that 
    perform distributor-specific actions, this class interacts with the 
    consumer class via contracts and with the producer class via a producer 
    list and via the observer pattern.
    * _Producer_: this class implements the producer entity and tracks 
    all data about a specific producer as well as providing methods that 
    perform producer-specific actions, this class interacts with the 
    distributor class via a distributor list and via the observer pattern.
    * _Contract_: this class models the relationship between a consumer and a 
    distributor, it has a length and cost associated to it and it is issued 
    by a distributor to a consumer.
* **strategies**
    * _EnergyChoiceStrategy_: this interface describes the generic way a 
    distributor will pick his producers, which is done through the 
    pickProducers() method.
    * _EnergyChoiceStrategyFactory_: this class represents an implementation of 
    the factory pattern that will serve in creating a strategy implementation 
    object based on requested type.
    * _*Strategy_: these classes represent the implementations of the green, 
    price and quantity strategies, each with its own algorithm.
* **fileio**
    * _Input_: this class represents a collection of objects containing raw 
    data created based on the given input, it will be given to the database 
    for further processing.
    * _InputLoader_: this class will parse the input from a given JSON file 
    using the json.simple library and will create the objects which wiil be 
    stored in the final input object.
    * _*InputData_: these classes will represent a method of storing parsed 
    input data for Consumers, Distributors and Producers.
    * *ChangesInputData: these classes will represent a method of storing 
    parsed input data for monthly changes for both Distributors and Producers
    * _Writer_: this class will write all the data in the database to a given 
    output file, in JSON format, using the Jackson library, at the end of the 
    simulation.
* **utils**
    * _Utils_: this class contains a collection of static methods used in 
    calculating various values via mathematical formulas, as well as 
    converting strings to their corresponding enum value.
    * _Constants_: this class contains a collection of string constants used 
    in reading and writing JSON as well as number constants used in the 
    formulas from the methods in the Utils class.
* _Main_: this class represents the entry point of the implementation, it 
handles starting the input parsing, the simulation and the output writing.

### Flow

The implementation starts out in Main. From here, the InputLoader is called 
and the input parsing takes place. The result of this will be an Input object 
which is then given to the database to process. After the input hase been 
processed by the database and it has been populated with objects for all the 
entities that will take part in the simulation, the simulation can begin. 
The database uses maps to store all the objects for quick access to objects 
based on ID when needed.

Firstly, the database will run the initial round. During this, the 
distributors, in order of ID, will apply their strategy to pick initial 
producers by calling the _pickProducers()_ method from their strategy, then 
they will calculate their initial production cost and profit and then generate 
the contract rate for the contracts they will offer this round. Picking 
producers involves sorting all producers in the database by a certain set of 
parameters and picking the producers who will offer enough energy to satisfy 
the distributor's needs. Both the Distributor and Producer will keep track of 
each other through the use of a list. In addition to this, the distributor will 
be added as an observer of the producer for the notifying functionality used 
later. Next, all the consumers will process the month by collecting their 
monthly income, picking a distributor and getting a contract from them and 
finally paying the distributor the contract rate for the month. Paying the 
distributor involves subtracting the cost from the budget and giving it to the 
distributor by using a method from that distributor. After this, the 
distributors will process their month. This involves decrementing the remaining 
length of all contracts issued, paying all monthly costs and removing contracts 
that have expired. Both consumers and distributors will check for bankruptcy 
during the processing of the month and declare bankruptcy if necessary. Finally 
the producers process the month. This envolves storing the list of distributors 
in another list which will be used for keeping track of monthly stats.

Running the rest of the rounds will require an updates object from which new 
consumers will be added and distributor and producer costs will be updated. 
Firstly the new consumers are added and distributor costs are updated. After 
this, all distributors will recalculate contract prices. Next, consumers will 
process their month, performing the aforementioned actions and getting a new 
contract if necessary. Distributors will process their month next. After this, 
producers will have their costs updated and will notify all of their 
distributors that there has been a change. The distributors who have been 
notified will reapply their strategies and recalculate their production cost 
in order of ID. Lastly, all producers will record the distributors they have 
in the monthly stats list. This process will be repeated as many times as 
specified in the input or until all distributors go bankrupt. Distributors and 
consumers who go bankrupt are out of the simulation and no longer process 
months. After the simulation has ended, the Writer will be called and all the 
data from the database will be written to the given output file in JSON format.

### OOP Design elements

In my implementation, encapsulation was used as the core principle when 
designing the Consumer, Distributor and Producer entities. Each entity is 
represented by its own Class which contains all of the data associated with 
that entity as well as the methods that perform all of the specific operations 
for that entity. In this implementation, abstraction is used within the 
factory pattern as well as within the strategy pattern since certain complex 
implementation elements such as creating objects and performing the different 
producer picking strategies are hidden from the "user", whether the user is a 
distributor or the database itself.

### Design patterns

Design patterns used:
* **Singleton**: the singleton pattern has been used in creating the database 
since it has to be a globally accessible, persistent instance. The variation 
of Singleton used is the one outlined by Bill Pugh since it is an easy to 
implement, efficient and thread-safe variation of the Singleton Pattern.
* **Factory**: this pattern was used in creating both the Entity and Strategy 
factories since it allows for the easy creation of objects based on a generic 
interface.
* **Strategy**: this pattern was used in creating the various ways a 
distributor picks his producers. The distributor has access to an 
EnergyChoiceStrategy object which is an instance of the Green, Price or 
Quantity Strategy classes. These clases represent the actual implementations 
of the various producer picking algorithms and are generalised by the 
EnergyChoiceStrategy interface.
* **Observer**: this pattern was used in facilitating the other interaction 
between Distributor and Producer. In this implementation, the Distributors 
are the Observers and the Producers are the Observables. The Distributors 
will observe modifications that happen to their producers and reapply their 
strategies to pick new Producers. One Distributor will observe multiple 
Producers, while one Producer will be observed by multiple Distributors.

## Feedback, comments

A very interesting assignment that was very fun to implement and that allowed 
me to showcase my knowledge of the Java programming language and OOP concepts 
and Design Patterns. The given documentation for this assignment was very 
informative and well structured.

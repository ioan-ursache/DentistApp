# About Dentist Appointment Management System

*The purpose of this app is to illustrate the use of Java and JavaFX for creating a GUI application.*

The use case is as follows: a dentist has to manage the appointments of patients.
Notes about the app:
- The app may work in both console mode or a GUI mode.
- Data is stored in multiple formats: text, binary, JSON, XML and relational database (MySQL).
- The app is solely designed for educational purposes. For that reason, certain shortcomings are noticeable (such as the design used in the Repository classes)


# Laboratory 1

<!--
**Lab assignment**

Write a Java program that computes the sum of all the integer numbers given as command-line
parameters.

---

**Home assignment**
-->

1. Provide solutions for the following problems:
- Write a Java program that prints the prime numbers among the integers numbers given
  as command-line parameters.
- Write a Java program that prints the maximum value from all the double numbers given
  as command-line parameters.
- Write a Java program that prints the greatest common divisor of all integer numbers
  given as command-line parameters.

---

2. Choose one of the problems below and provide a layered architecture solution (in Java). Requirements necessary for the next lab:
- Create the classes necessary for just one entity in the problem requirement (the first mentioned entity).
-	The layers should be: Domain, Repository, service, UI. Use Java packages.
-	Add at least 5 entities in your memory Repository (from source code).
-	All entities should be identifiable (use a generic superclass/interface Identifiable) and unique.
-	The UI must allow CRUD operations for the used entity.

1. Design and implement a Java solution for managing the appointments to a dentist. The program should allow CRUD operations for Patients and Appointments. Each Appointment is linked to a Patient using the unique patient identifier. Each Patient should thus be characterised (at least) by an ID, name, email and telephone number. Each Appointment should be characterised (at least) by an ID, the patient ID, the date and time.

2. Design and implement a Java solution for managing the training sessions of multiple clients of a personal trainer, in a gym. The program should allow CRUD operations for Clients and Sessions. Each Client should thus be characterised (at least) by an ID, name, email and telephone number. Each Session should be characterised (at least) by an ID, the client ID, the date and time and workout description.

3. Design and implement a Java solution for managing the reservations for car rentals. The program should allow CRUD operations for Cars and Reservations. Reservations are linked to cars using the unique car identifier.  Each cake should thus be characterised (at least) by an ID, make, model, rental price. Each Reservation should be characterised (at least) by an ID, the car ID, the customer name, and reservation start and end dates.

# Laboratory 2

<!--
**Lab assignment**

A.	Write a Java class with at least three instance attributes of different types. For example, Car with the following attributes: a manufacturer, a model, maximum speed, a price and a manufacturing year. This is only an example, please choose your own type. Implement the constructors, setters and getters, and other required methods (e.g. toString(), equals()).

B.	Create a repository that contains objects having the type defined at A and provide an iterator for the repository. Write a program that displays the list of all objects and then computes different kinds of information. For example, for a collection of cars, the program prints the following information:
-	the cheapest car;
-	the fastest car;
-	the models manufactured by a given manufacturer.

For testing, you will create some objects in the main function and add them to the repository.


---

**Home assignment**

-->

For your chosen problem implement the requirements below:
-	All entities should be identifiable (use a superclass/interface Identifiable) and unique.
-	Provide a generic interface for the repository and an implementation for a generic memory repository which stores identifiable objects in a Map (HashMap, TreeMap), where the objects identifiers are the keys and the values are the actual objects (please see the UML diagram in the given image).
-	Filter your entities by various criteria (2 criteria for each entity). Use a generic AbstractFilter interface and implement this interface in various classes, according to the required filters. Define a generic FilteredRepositoy which can use any filtering strategy and then extend this for each of your entities (please see the UML diagram in the given image).
-	Add basic data validation and use the exception mechanism in Java for exceptional situations. Show messages in case of such situations.
-	The UI must allow CRUD operations for both entities.

# Laboratory 3

<!--
**Lab assignment**

For the class implemented for Lab assignment 2, design and implement an in-memory repository using Java generics. The repository should contain the CRUD operations. Please see the attached diagram as an example. Write a test program for the in-memory repository that will:
-	Add 5 elements to the repository;
-	Print all the elements, sorted by a given criterion (only one is sufficient);
-	Search for an element;
-	Delete an element;
-	Update an element;

---

**Home assignment**
-->

Continue designing and implementing the problem you have chosen. For the next lab you must:
-	Implement classes in the repository that allow storing and retrieving data in two formats: text files and binary files (using the Java serialization mechanism). The program must work identically using text and binary file repositories. The decision of which repositories are employed, as well as the location of the repository input files will be made available via the program’s **settings.properties** file and the Java *Properties* class. See an example of this file below:

> Repository = binary \
> Patients = “patients.bin” \
> Appointments = “appointments.bin”

-	Provide tests using JUnit. The test coverage for at least one entity (throughout all layers except UI) must be more than 95%.

**Bonus (0.2p)** \
Create and use custom Validator classes to validate your inputs. Provide validator objects to your service(s) and make sure validation is performed. For the bonus points create at least one Validator class for each entity.

# Laboratory 4

Continue the implementation of the problem you have chosen for home assignment 2. For the next lab you must:
-	Implement classes in the repository that allow storing and retrieving data to/from a relational database. The decision of which repositories are employed, as well as the location of the repository input files / database will be made available via the program’s **settings.properties** file and the Java *Properties* class. See an example is below:

> Repository = database \
> Location = data \
> Patients = patients \
> Appointments = appointments

-	Provide various reports, using Java 8 streams. You should create at least 5 different reports. See some examples below:

    o	all the appointments for a certain patient (and their status); \
    o	the problems of a certain patient; \
    o	the phone number of a certain patient (given by id); \
    o	all sessions of a given client between 2 given dates; \
    o	all sessions for a client involving certain given exercises (considering the description); \
    o	the name of the persons who booked a certain car; \
    o	all cars rented by a certain person.


**Bonus (0.2p)** \
Allow storing/retrieving your data to/from JSON (**0.2p**) and XML (**0.2p**) files. The decision of which type of file to use and the necessary paths are given in the same configuration file (the program’s **settings.properties** file).


# Laboratory 5

Design and implement a JavaFX UI for the application you have been working on for your home assignment. The classes in layers except the UI should not be modified. The GUI should allow all operations that your console UI provided (basically, your application should offer functionalities for all requirements in your previous assignments). 


**Bonus (0.4p)** \
Add a multiple undo/redo functionality for your application. Implement this functionality using **inheritance and polymorphism**, as illustrated in the attached figure. The performed operations (actions) can be memorized in undo/redo stacks.

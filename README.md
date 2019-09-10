Word Chain
==========

Internal development challenge: build a solution to calculate the shortest chain between two words, changing only 
one letter at a time, with every element in the chain a valid word from the supplied dictionary.

To run within gradle:
```
./gradlew run --args='<filename>'
```

where `<filename>` is a properly formatted input file.




To build:
```
./gradlew jar
```

and then

```
java -jar build/libs/ash-1.0-SNAPSHOT.jar <filename>
```

you can also specify the `-t` flag to get extra timings for each chain:

```
java -jar build/libs/ash-1.0-SNAPSHOT.jar <filename> -t
```

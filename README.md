# Pretchasid

## Master
javac -cp ../../External/json-20201115.jar *.java
java -classpath .:../../External/json-20201115.jar Master 0

## Client
javac -cp ../../External/json-20201115.jar *.java
java -classpath .:../../External/json-20201115.jar Client <Server Port>

## Slave
javac -cp ../../External/json-20201115.jar *.java
java -classpath .:../../External/json-20201115.jar Slave <Server Port> <Name>

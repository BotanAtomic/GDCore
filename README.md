<p align="center"><IMG SRC="https://i.gyazo.com/760ac25569c32430a3d1817a77e0fd6e.png"></p>

![Build Status](https://img.shields.io/badge/Jooq-3.8.5-yellow.svg?style=flat)
![Build Status](https://img.shields.io/badge/Guice-4.1.0-blue.svg?style=flat)
![Build Status](https://img.shields.io/badge/MySql-6.0.5-green.svg?style=flat)
![Build Status](https://img.shields.io/badge/Slf4j-1.7.21-yellow.svg?style=flat)
![Build Status](https://img.shields.io/badge/Hikari CP-2.5.1-blue.svg?style=flat)
![Build Status](https://img.shields.io/badge/Mina Apache-2.0.15-red.svg?style=flat)

#What is it ?

Graviton (GDCore) is a 1.29 dofus emulator, developed in Java. He's supported by gradle and is separated in 2 projects

Server : it's the login server, manages connections

Game   : it's the game server, manages the in-game

#Dependencies

Graviton (GDCore)  contains a lot of util dependencies

- <b>Guice</b> by Google: for dependency injections

- <b>MySQL-Connector</b> by Apache: for JDBC mysql

- <b>Slf4j</b> & Logback : for loggers

- <b>Lombok</b> : for meta-data

- <b>JOOQ</b> : for typesafe SQL query construction and execution.

- <b>Mina Apache</b> : for network.

MuJava-AUM
===========

Original MuJava README:
-------------------------
Mutation system for Java programs, including OO mutation operators.

Please see muJava's homepage for detail: http://cs.gmu.edu/~offutt/mujava

-------------------------------------------------------------------------

MuJava-AUM -- version 0.0.1
----------------
MuJava-AUM is similar to original MuJava, but with support to Avoid Useless Mutants.
Please check https://sites.google.com/view/useless-mutants/


Requirements
----------------
 - Compatible only with JDK and JRE 1.8 (Java 8)

Getting started
----------------
#### Generating muJava executable from sources
1. Clone MuJava-AUM:
    - `git clone https://github.com/easy-software-ufal/muJava-AUM.git`

2. Generate jar:
    - `cd MuJava-AUM`
    - `mvn package`
    
Jar files with dependencies and without (`.jar`) should be available under `target` folder (inside muJava-AUM folder).

#### Using MuJava-AUM
5. Create a file named mujava.config and add one line in following fashion, to point out to examples/session1/ folder (or other subject). This file should be located under your current path. For instance, for UNIX systems, it should be under the folder the output of `pwd` command points to.

    - `MuJava_HOME=<absolute-path>/muJava-AUM/examples/session1`

For examples on how to provide muJava-friendly subjects, please see https://github.com/pmop/muJava-AUM-DummyCode.
Notice that if you're on Windows, paths are separated by '\' instead of '/'.

6. Compile the source files from examples/session1/src/ directory
    - `javac examples/session1/src/*.java -d examples/session1/classes/`
7. Execute muJava:
    - `java -jar muJava.jar`
    - Notice that if you have generated the Jar file from sources, you should be using the -with-dependencies version, for example `java -jar muJava-0.0.1-SNAPSHOT-jar-with-dependencies.jar`

Publications
------------------
* "Avoiding Useless Mutants" - 
    Leonardo Fernandes, Márcio Ribeiro, Luiz Carvalho, Rohit Gheyi, Melina Mongiovi, André L. Santos, Ana Cavalcanti, Fabiano Ferrari, José Carlos Maldonado
    (GPCE 2017) [[link]][gpce17].

[gpce17]: https://conf.researchr.org/event/gpce-2017/gpce-2017-gpce-2017-avoiding-useless-mutants

Documentation
--------------------
Detailed documentation ...
[html documentation][htmldocs].

[htmldocs]: https://github.com/Nimrod-Easy-Lab/muJava-AUM


Obs.
----------------------


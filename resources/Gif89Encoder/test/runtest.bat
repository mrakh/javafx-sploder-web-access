@echo off
::------------------------------------------------------------------------------
::::edit the following line to point to your JRE installation
set JREROOT=c:\jdk11
::::the next two are ok under the standard JDK 1.1.x installation, other
::::Java runtime installations will vary
set JRELIB=%JREROOT%\lib\classes.zip
set JRERUN=%JREROOT%\bin\java
::------------------------------------------------------------------------------
%JRERUN% -classpath ..\lib\classes.jar;%JRELIB% net.jmge.gif.Gif89Encoder in.txt
ren gif89out.gif out_movie.gif
%JRERUN% -classpath ..\lib\classes.jar;%JRELIB% net.jmge.gif.Gif89Encoder in.jpg
ren gif89out.gif out_static.gif
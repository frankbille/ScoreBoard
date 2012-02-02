ScoreBoard
==========

ScoreBoard is a web based system, to keep track of game results and player stats for small office games. It is initially designed to keep track of [Foosball][foosball] scores.

Todo
----
* ~~The basic stuff, like ability to enter game results and which players were on it~~
* Player management
* [Better performance. Right now the daily game page does N+1 queries][nplusone]
* [Ranking of players][ranking]

Build instructions
----
* Install [Eclipse IDE for Java Developers][eclipse]
* Install [Eclipse EGit plugin][egit] in Eclipse
* Install [maven]
* Setup [Maven in Eclipse][mavensetup]
* Start Eclipse
* Select File -> Import -> Git -> Projects from Git and click Next
* Click Clone
* Paste the URI https://\<username\>@github.com/frankbille/ScoreBoard.git
* Enter your GitHub password and click Next
* Select "master" and click Next and Finish
* Select the ScoreBoard repository and click Next
* Select "Import existing projects" and click Next and Finish
* Open a commandline at the git/ScoreBoard dir
* Run the command "mvn eclipse:eclipse -DdownloadSources=true"
* Refresh the project in Eclipse

[foosball]: http://en.wikipedia.org/wiki/Table_football
[ranking]: https://github.com/frankbille/ScoreBoard/issues/2
[nplusone]: https://github.com/frankbille/ScoreBoard/issues/4
[eclipse]: http://www.eclipse.org/downloads/
[egit]: http://eclipse.org/egit/download/
[maven]: http://maven.apache.org/
[mavensetup]: http://maven.apache.org/guides/mini/guide-ide-eclipse.html

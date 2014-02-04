ScoreBoard [![Build Status](https://secure.travis-ci.org/frankbille/ScoreBoard.png)](http://travis-ci.org/frankbille/ScoreBoard) [![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/frankbille/scoreboard/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
==========

ScoreBoard is a web based system, to keep track of game results and player stats for small office games. It is initially designed to keep track of [Foosball][foosball] scores.

Todo
----
* See the [issue list][issues] for details of what is needed to be done.

License
-------

Licensed under the [GNU General Public License, Version 3.0][license]


Build instructions (for developers using IntelliJ IDEA)
-------------------------------------------------------

 1. Install [IntelliJ IDEA Community Edition][idea]
 2. When started up, checkout sourcecode from GitHub from within IDEA
 3. Say "yes" to open the project.
 4. From the runner menu, there are two shared run configurations:
    1. __Run application__ - Run the application against a local MySQL server running on port 3306 with username "root"
       and no password.
    2. __All Tests__ - Run all unit tests found in the ScoreBoard project.


Build instructions (for developers using Eclipse)
-------------------------------------------------

 1. Install [Eclipse IDE for Java Developers][eclipse]
 2. Start Eclipse
 3. Select File -> Import -> Git -> Projects from Git and click Next
 4. Select URI and Next
 5. Paste the URI git@github.com:frankbille/ScoreBoard.git
 7. Select the branches you want (normally just all of them) and click Next
 8. Select where the local clone should be stored and click Next
 9. Select "Import existing projects" and Finish
10. Since it is a maven project, dependencies will be downloaded and
    it will be automatically built,

[foosball]: http://en.wikipedia.org/wiki/Table_football
[eclipse]: http://www.eclipse.org/downloads/
[issues]: https://github.com/frankbille/ScoreBoard/issues
[license]: http://www.gnu.org/licenses/gpl.html
[idea]: http://www.jetbrains.com/idea/

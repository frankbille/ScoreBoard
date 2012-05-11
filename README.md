ScoreBoard [![Build Status](https://secure.travis-ci.org/frankbille/ScoreBoard.png)](http://travis-ci.org/frankbille/ScoreBoard)
==========

ScoreBoard is a web based system, to keep track of game results and player stats for small office games. It is initially designed to keep track of [Foosball][foosball] scores.

Todo
----
* See the [issue list][issues] for details of what is needed to be done.

License
-------

Licensed under the [GNU General Public License, Version 3.0][license]


Build instructions (for developers using Eclipse)
-------------------------------------------------

1. Install [Eclipse IDE for Java Developers][eclipse]
2. Install [Eclipse EGit plugin][egit] in Eclipse
3. Install [Eclipse Maven integration][maven]
5. Start Eclipse
6. Select File -> Import -> Git -> Projects from Git and click Next
7. Click Clone
8. Paste the URI https://\<username\>@github.com/frankbille/ScoreBoard.git
9. Enter your GitHub password and click Next
10. Select "master" and click Next and Finish
11. Select the ScoreBoard repository and click Next
12. Select "Import existing projects" and click Next and Finish
13. Open a commandline at the git/ScoreBoard dir
14. Run the command "mvn eclipse:eclipse -DdownloadSources=true"
15. Refresh the project in Eclipse

[foosball]: http://en.wikipedia.org/wiki/Table_football
[ranking]: https://github.com/frankbille/ScoreBoard/issues/2
[nplusone]: https://github.com/frankbille/ScoreBoard/issues/4
[eclipse]: http://www.eclipse.org/downloads/
[egit]: http://eclipse.org/egit/download/
[maven]: http://www.eclipse.org/m2e/
[issues]: https://github.com/frankbille/ScoreBoard/issues
[license]: http://www.gnu.org/licenses/gpl.html

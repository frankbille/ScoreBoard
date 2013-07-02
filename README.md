# ScoreBoard [![Build Status](https://drone.io/github.com/frankbille/ScoreBoard/status.png)](https://drone.io/github.com/frankbille/ScoreBoard/latest)


ScoreBoard is a web based system, to keep track of game results and player stats
for small office games. It is initially designed to keep track of
[Foosball][foosball] scores.


## Google App Engine Edition

This branch contains a rewrite of the Java+Wicket+MySQL implementation, into a
[Google App Engine][gae] application, using [Go language][golang] on the backend,
to provide data to the frontend, through a REST api. On the frontend
[AngularJS][angularjs] is used, so most of the user interface is rendered in
the browser, to minimize the hit on the server.


### Why a rewrite?

Basically because of the hassle of deploying Java applications, when you when
cheap/free hosting. This app is not meant for any commercial use but only for
fun/recreational spare time in office spaces.

Maybe also because I wanted to work with some new technologies...
but this is just rumors. 


### Why [App Engine][gae]?

I think the idea of having a platform that automatically scales up and down,
and handles the infrastructure for you, is very welcoming. It has some very
strict rules that must be followed, but when you do a lot of best practices
will also follow, making your application faster and more scalable.


### Why [Go][golang]?

With the rewrite I had two criterias: Google App Engine and a `JavaScript` frontend.
A third criteria quickly popped up, which was that new App Engine instances should
start *fast*. With `Java` and `Python`, a cold boot is actually very slow, so the first
request from a user will take 10-30 seconds, depending on how much needing to be
done on startup. `Go` on the other hand, is a fully compiled binary, so a new instance
is avaliable in <500ms (I haven't seen it so slow yet, normally arund 50-100ms).

So by choosing `Go`, I can deliver a page with data, within a second even if no
instances is running. This is pretty amazing and is worth a try-out. I say this
because I haven't decided 100% that I will use Go. It is a newer language, with
syntax similar to `C` and with very few mature frameworks compared to `Java` or
`Python`.

### How to develop on the code?

This is still so new technologies for me, so I haven't figured out what the best
stack is to develop this. I basically use a text editor (TextMate2 in this case)
to edit the `JavaScript` and `Go` files, and then run the Google App Engine 
development server to build/host the files, so I can continuously develop and test
in a browser, without having to restart all the time.

To get started you minimally need the tools for it:

1. [The Go Programming Language][golang]
   1. Go to the website and download go for your operating system
   2. Read as much as you can about Go, if you don't know it already. 
      For a quick start to actually be able to compile things, read [this document][goinstalldoc].
   3. Make sure that you read http://golang.org/doc/code.html#Workspaces and http://golang.org/doc/code.html#GOPATH
      from the above guide, as they are quite essential.
   4. Open a command prompt and `cd` to the folder where you have cloned the ScoreBoard
      code to on your computer.
   5. Execute the two following commands when Go is set up correctly:
      * `go get`
	  * This will download dependencies and build them.
2. [Google App Engine][gae]
   1. Download and install [Google Appengine SDK for Go][gaeinstall]
   2. Open a command prompt and `cd` to the folder where you have cloned the ScoreBoard
      code to on your computer.
   3. Run this command, which will start the App Engine development server:
      * `PATH_TO_APPENGINE/dev_appserver.py --clear_datastore true .`
   4. Open a browser and point it to http://localhost:8080
      * This is the ScoreBoard application, and the code behind it will be reloaded
        automatically when you change it.
   5. Open a browser and point it to http://localhost:8000
      * This is the App Engine admin console, where you can see various aspects of the
        application, like data storage, memcache etc.
3. Code away!


## Todo

See the [issue list][issues] for details of what is needed to be done.


## License

Licensed under the [GNU General Public License, Version 3.0][license]


[foosball]: http://en.wikipedia.org/wiki/Table_football
[issues]: https://github.com/frankbille/ScoreBoard/issues
[license]: http://www.gnu.org/licenses/gpl.html
[gae]: http://developers.google.com/appengine
[gaeinstall]: https://developers.google.com/appengine/downloads#Google_App_Engine_SDK_for_Go
[golang]: http://golang.org
[goinstalldoc]: http://golang.org/doc/install
[angularjs]: http://angularjs.org

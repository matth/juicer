# Juicer

Juicer is a web API for extracting text, meta data and named entities from HTML "article" type pages.

For more info visit: http://juicer.herokuapp.com/

### Install 

### On debian / ubuntu

Install from our PPA

    sudo add-apt-repository ppa:juicer-server/juicer
    sudo apt-get update
    sudo apt-get install juicer

This will start juicer running as a service on port 9000, try
http://localhost:9000 

    sudo service juicer start|stop|restart|status

You can run the server directly also:

    usage: juicer-server [options]

    options:
      -h, --help         Print this message and exit
      --version          Print program version and exit
      --port       PORT  Port to run on, default 9000
      --pidfile    FILE  Write process pid to pidfile
      --logfile    FILE  Write logs to logfile
      --daemonize        Daemonize and exit

See also `man juicer-server`

### On Heroku

* Clone the repo
* Install the Heroku tools; be sure heroku is on your path
  * see http://devcenter.heroku.com/articles/heroku-command
* Type these commands inside the application's git clone:
  * `heroku create -s cedar --buildpack http://github.com/heroku/heroku-buildpack-scala.git`
  * `git push heroku master`
  * `heroku open`

### Using a fat jar

Build a fat jar for use anywhere ...

* Run `sbt assembly`
* Run `java -Xmx1g -jar juicer-web/target/scala-2.9.1/juicer-web-assembly-1.0.jar`

## Running the app (Developers)

* Install sbt `brew install sbt`
* Run `sbt test` to test the app (sbt must be sbt 0.11, not 0.7)
* Run `sbt stage` to stage the app
* Run `JAVA_OPTS="$JAVA_OPTS -Xmx1g" juicer-web/target/start` to run the server
* Now open `http://localhost:8080` in a browser


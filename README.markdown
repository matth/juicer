# Juicer

Juicer is a web API for extracting text, meta data and named entities from HTML "article" type pages.

For more info visit: http://juicer.herokuapp.com/

## Running the app (Developers)

### Locally

* Run `sbt test` to test the app (sbt must be sbt 0.11, not 0.7)
* Run `sbt stage` to stage the app
* Run `juicer-web/target/start` to run the server
* Now open `http://localhost:8080` in a browser

### On Heroku

- Clone the repo
- Install the Heroku tools; be sure heroku is on your path
  - see http://devcenter.heroku.com/articles/heroku-command
- Type these commands inside the application's git clone:
  - `heroku create -s cedar --buildpack http://github.com/heroku/heroku-buildpack-scala.git`
  - `git push heroku master`
  - `heroku open`



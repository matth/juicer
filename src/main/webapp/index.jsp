<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Meta Extraction API</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script type="text/javascript" src="/js/bootstrap-modal.js"></script>
  </head>

  <body>
    <div class="container">
      <section>
        <div class="page-header">
          <h1>Entity extraction <small>extract named entities from a block of text</small></h1>
        </div>

        <h3>URL</h3>
        <p><code>POST /api/entities</code></p>

        <h3>Parameters</h3>

        <p><code>text</code> - A string of text you want to analyse</p>

        <h3>Response</h3>

        <p>A JSON response, showing entity text, frequency and type (one of Location, Person, Organization)</p>

        <pre>
          {
            "entities" : [
              {
                "text"      : "Damascus",
                "type"      : "Location",
                "frequency" : 2
              },
              {
                "text"      : "Bashar al-Assad",
                "type"      : "Person",
                "frequency" : 1
              },
              {
                "text"      : "UN",
                "type"      : "Organization",
                "frequency" : 1
              }
            ]
          }
        </pre>

        <h3>Example</h3>

        <p>Try it out using the form below &hellip;</p>

        <form id="entity_example">
          <fieldset>
            <div class="clearfix">
              <div class="input">
                <textarea id="textarea" class="xxlarge" rows="10" name="textarea">
This year was the second warmest on record for the UK, the Met Office says.

Provisional figures show that only 2006, with an average temperature of 9.73C (49.5F), was warmer than 2011's average temperature of 9.62C (49.3F).

This year saw high temperatures for lengthy periods; including the warmest April and spring on record, the second warmest autumn and the warmest October day.

Early figures suggest 2011 is ending with a "close to average" December.

The Met Office said its figures were a mean temperature taken over day and night.

The mean temperature for the first 28 days of December was 4.7C (40.5F); a big swing from 2010, says the Met Office, when temperatures were 5C below average for the coldest December on record.

The BBC weather centre is predicting another "very mild" day for New Year's Eve with highs of 13C (55F). Forecasters say it will be mostly cloudy and windy, with perhaps a few brighter spells in the north and east of the UK and the odd outbreak of mainly light rain or drizzle.

John Prior, national climate manager at the Met Office, said: "While it may have felt mild for many so far this December, temperatures overall have been close to what we would expect.
                </textarea>
              </div>
            </div>
            <div class="actions">
              <input class="btn primary" type="submit" value="Extract">
            </div>
          </fieldset>
        </form>

        <div id="extracted-entites" class="modal hide fade scroll">
          <div class="modal-header">
            <a href="#" class="close">&times;</a>
            <h3>Extracted Entities </h3>
          </div>
          <div class="modal-body">heheheh</div>
        </div>

      </section>
    </div>

    <script type="text/javascript" src="/js/script.js"></script>
  </body>

</html>

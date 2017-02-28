# WebOligos
A web service frontend for the oligo library design software. The software designs optimal libraries of oligonucleotides to minimize the amount of sequence to create gene variants expressing certain codons at different levels. For more information on the science, see [Ryan and Papamichail 2013](https://www.ncbi.nlm.nih.gov/pubmed/23654273).

## Building
To produce a standalone executable version, use `bin/activator dist`. This will create a zip file with all required libraries in `target/universal` called `web-oligos-$version.zip`.
Because the underlying oligo designer uses some libraries of its own, this zip file needs to be extracted to the `run` directory, so that `bin`, `conf`, and `lib` are on the same level as `data` and `libs`.

If you just need to test the application in a dev environment, run `bin/activator run`.

## Usage

Before launching a production server, change the `play.crypto.secret` key in `conf/application.conf` to any random string. Play will *not* launch without this being changed.

From the `run` directory, running `bin/web-oligos` will start the Play app server. It listens to `http://localhost:9000/weboligos/` by default, although can be changed with `-Dhttp.port=0000`.

This software *requires* a Python 2.7 installation on the path, and *optionally* requires a Perl installation to calculate codon pair bias.

To test the application, visit `http://localhost:9000/weboligos/test` for some sample parameters and hit submit.

### API

The application has API endpoints for job submission and viewing. The API returns results as JSON objects.

The API can be accessed via the paths
`/api/submit`
`/api/view/<jobid>`

The submit path takes an encoded form (using all the same fields as a normal POST request via the UI), either as HTTP Encoded data, or as JSON.
It will return either a list of errors in the form, or, if successful, a success message and job id.

The view path takes the id as part of the URL, and returns the job status, parameters, and results (if completed) as JSON.

## Known Issues

Assuming all the libraries loaded correctly, there should be no uncaught exceptions in program execution at this time.

## Plans

The UI is still missing some features that the original Swing interface had, especially with the display of the results.

See the [issue tracker](https://github.com/ottj3/weboligos/issues).

## Project Structure

The directory structure follows the [Play Framework's](https://www.playframework.com/documentation/2.5.x/Anatomy).

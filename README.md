# WebOligos
A web service frontend for the oligo library design software. The software designs optimal libraries of oligonucleotides to minimize the amount of sequence to create gene variants expressing certain codons at different levels. For more information on the science, see [Ryan and Papamichail 2013](https://www.ncbi.nlm.nih.gov/pubmed/23654273).

## Building
To produce a standalone executable version, use `bin/activator dist`. This will create a zip file with all required libraries in `target/universal` called `web-oligos-$version.zip`.
Because the underlying oligo designer uses some libraries of its own, this zip file needs to be extracted to the `run` directory, so that `bin`, `conf`, and `lib` are on the same level as `data` and `libs`.

If you just need to test the application in a dev environment, run `bin/activator run -Djava.library.path=libs` (see note in Usage section about libs).

## Usage

Before launching a production server, change the `play.crypto.secret` key in `conf/application.conf` to any random string. Play will *not* launch without this being changed.

From the `run` directory, running `bin/web-oligos -Djava.library.path=libs` will start the Play app server. It listens to `http://localhost:9000/` by default, although can be changed with `-Dhttp.port=0000`.

Note that if you encounter an `UnsatisfiedLinkError` while running the program on Linux (especially on non-Oracle JREs), you may additionally need to specify `LD_PRELOAD=/full/path/to/run/libs/libjep.so` before `bin/web-oligos`. We apologize for the inconvenience and are looking on removing this dependency from the upstream project soon.

To test the application, visit `http://localhost:9000/test` for some sample parameters and hit submit.

## Known Issues

Assuming all the libraries loaded correctly, there should be no uncaught exceptions in program execution at this time.

## Plans

The UI is still missing some features that the original Swing interface had, especially with the display of the results.

See the [issue tracker](https://github.com/ottj3/weboligos/issues).

## Project Structure

The directory structure follows the [Play Framework's](https://www.playframework.com/documentation/2.5.x/Anatomy).

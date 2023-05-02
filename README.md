# LookupLab: A Story Discovery Tool

LookupLab is a query matching tool that can be configured with domain-specific knowledge.

The tool leverages [Postgres Full Text Search](https://www.postgresql.org/docs/current/textsearch.html) and uses a combination of lexical-based and graph-based techniques. By adjusting the configuration, you can inform the tool of semantic nuances within your domain such as:

* equivalent terms / abbreviations _(e.g. "immunization" and "vaccination", "prescription" and "rx")_
* equivalent queries _(e.g. "I need a prescription renewal" and "I've run out of medication")_
* important keywords _(e.g. "I want to **follow up** on a previously discussed **medical concern**")_


## Prerequisites

You will need the following to compile and run the application.

* [PostgreSQL](https://www.postgresql.org/download/) 14 or greater
* [JDK](https://www.azul.com/downloads/) 11 or greater is required. 
* [Clojure](https://clojure.org/guides/install_clojure)
* [Babashka](https://github.com/babashka/babashka#installation) (optional)


## Development

### Setup
1. Clone this repo 
1. Run `./dev-setup.sh`

This will: 
1. Generate a dfd.ths file and copy it into the appropriate location
1. Generate a dfd.syn file and copy it into the appropriate location
1. Create a new local database `dfd` with user/pass `dfd/dfd`.

### Running
1. Start a REPL from the command line with `clj -M:dev:nrepl` (or `bb nrepl`) 
2. Start the server by running `(go)` in the REPL.

Any pending migrations will be ran when the server is started. The tables will also be populated with the data in the `search_data` directory.

Once the server starts, the default API is available under http://localhost:3000/api. System configuration is available under `resources/system.edn`.

To reload changes, run `(reset)` in the REPL.


## Packaging / Deployment

### Setup
To configure production database credentials, run `./prod-setup.sh`. 

This will generate a new DB password and place it into `db.env` (used in `docker-compose.yml`) and `.db-connection.edn` (used in the `:prod` profile in `resources/system.edn`).

You should also already have a `search_data/dfd.ths` and a `search_data/dfd.syn` file. If you do not, you can generate one by running `clj -T:build ths` (or `bb gen-ths`) or create your own from scratch.

### Running
Run `docker compose up` (add `-d` for detached mode).

## License 

Copyright &copy; 2022-2023 [Magenta Health Inc](https://www.magentahealth.ca/).<br>
Authored by [Carmen La](https://carmen.la/).

This software is distributed under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

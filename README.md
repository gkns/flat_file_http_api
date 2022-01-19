Assumptions:
---
File IO based search is eliminated because:
    1. Consumes a lot of time for each API call.
    2. We cannot make use of the timestamp format for effective querying, unless the data is in-memory.
    3. If we preprocess, create index and search using binary/n-ary search, it is similar to the strategy used in databases.

Why Derby embedded DB:
    Ideally for range queries, we should go with a timeseries DB, or DBs which have efficient implementations supporting range queries.
    Mongo DB could be used, But this will mean a separate service, avoiding in this preliminary implementation in the interest of time.

How to scale this:
    Derby is not the right choice of database.
    We can split the database at a million record or such, But ideally we will run a timeseries database and make camel-route output data into that.

Design
---

On a high level, we have two main tasks in this exercise:
    1. Injesting data from the text files and store it in a structured format, for easy querying.
    2. Expose HTTP endpoint for accessing the injested data.

[1] is taken care in this preliminary implementation in a more robust way, than [2]
This choice is explained in : 'Why Derby embedded DB'

Implementation sepcifics:
---
* This is a springboot web application.
* File component of apache camel library is used for injesting the data from text files.
* Camel runs continuously in a multi-threaded mode, injesting data from the files placed under %input_files_folder% in application.properties,
and unmarshall each record-line from the text file to a model and diverts this structured data to the DB.



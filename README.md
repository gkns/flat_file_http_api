Assumptions:
---
There is always the scope for improvement :), That being the maxim.

**Why use Apache camel and not something from scratch:**
Camel is purpose built for applications like this,
Where data needs to flow from one system to another.
And designing the file watching+parsing+transforming by hand, is almost like a part of re-implementing camel.

**File IO based search is eliminated because:**
1. Consumes a lot of time for each API call.
2. We cannot make use of the timestamp format for effective querying, unless the data is in-memory.
3. If we preprocess, create index and search using binary/n-ary search, it is similar to the strategy used in databases.

**Why Derby embedded DB:**
Ideally for range queries, we should go with a timeseries DB, or DBs which have efficient implementations supporting range queries.
    Mongo DB could be used, But this will mean a separate service, avoiding in this preliminary implementation.

How to scale this:
---
    Derby is not the right choice of database.
    For existing implementation, We can split the database at a million record or such,
    But ideally we will run a timeseries database and make camel-route output data into that.

Design
---

On a high level, we have two main tasks in this application:
1. Injesting data from the text files and store it in a structured format, for easy querying.
2. Expose HTTP endpoint for accessing the injested data.

[1] Is taken care in this preliminary implementation in a more robust way, In that:
- Incoming data-files are processed in a continuous and memory efficient way (streaming)
- Data files can be added when the application is running, and they will be processed and added to the DB.
- New record formats can be easily plugged using Camel components.
- New data sources (other than file) can be easily plugged.

[2] Can be optimized, explained in : **Why Derby embedded DB**

Implementation specifics:
---
* This is a springboot web application.
* File component of apache camel library is used for injesting the data from text files.
* Camel runs continuously in a multi-threaded mode, injesting data from the files placed under **input_files_folder** in application.properties,
and unmarshall each record-line from the text file to a model and diverts this structured data to the DB.
* Unmarshalling is aided by the Camel Bindy Data format, which readily parses the records
* Camel uses multiple threads (no. of avaliable cores on the system).
* There is an index created on the "eventTime" column to speed up queries.

How to run this:
---
Remember to set the property: **input_files_folder** in **application.properties**
to an appropriate directory in your system containing the sample\*.txt which contains the recoreds in the given format:
Eg: `2000-01-01T17:25:49Z dedric_strosin@adams.co.uk dfad33e7-f734-4f70-af29-c42f2b467142`

**Run command:**

`./mvnw spring-boot:run`

**Note: ** First run might take time, to download dependencies and build.

Screenshots:
---

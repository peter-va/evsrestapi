# EVSRESTAPI

Information on the build and deployment process for the EVSRESTAPI project

### Prerequisites

* Install Docker and ensure it is configured to allow
    * Memory = 6G
    * Swap = 1G
* Clone the project - [https://github.com/NCIEVS/evsrestapi](https://github.com/NCIEVS/evsrestapi)
* Choose a local directory $dir (e.g. c:/evsrestapi)
* Make directories $dir/elasticsearch/data
* Download an NCI Thesaurus file as "Thesaurus.owl" to $dir/ (see [https://evs.nci.nih.gov/evs-download/thesaurus-downloads](https://evs.nci.nih.gov/evs-download/thesaurus-downloads))

### Steps for Loading Data and Indexes Locally

* Launch Stardog and load NCI Thesaurus data - (see [Stardog Resources](STARDOG.md))
* Launch Elasticsearch docker container 
In a terminal/Cygwin window, run the following to have an elasticsearch instance running. Keep this window open to keep the server running.

      docker pull docker.elastic.co/elasticsearch/elasticsearch:6.7.0
      # Choose a directory for your elasticsearch data to live
      dir=c:/evsrestapi/elasticsearch/data
      docker run -p 9200:9200 -v "$dir":/usr/share/elasticsearch/data  -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms3g -Xmx4g"  docker.elastic.co/elasticsearch/elasticsearch:6.7.0


* Load/Compute Indexes

      # From the root of cloned https://github.com/NCIEVS/evsrestapi
      # see
      ./gradlew clean build -x test
      SPRING_PROFILES_ACTIVE=local java -jar build/libs/evsrestapi-1.1.1.RELEASE.jar -t ncit_20.03d
      


### Steps for Building and Running EVSRESTAPI locally

* Launch Stardog and Elasticsearch (as described above)
    * If loaded properly, the loaded artifacts should be persistent and you can take down and restart the docker processes and the data will still be there.
    * NOTE: both services must be loaded and running for the application tests to run properly
* Configure application
    * see `src/main/resources/application-local.yml` file for local setup (these settings should be suitable for local deployment)
* Build the application (MUST DO BEFORE RUNNING if using “external tools configuration”)
    * `SPRING_PROFILES_ACTIVE=local ./gradlew clean build -x test` (without tests)
    * `SPRING_PROFILES_ACTIVE=local ./gradlew clean build` (with tests)
    * Executable war file present in build/libs

* Run application in Eclipse (SpringBoot)
    * Click "Run" → "External Tools" → "External Tools Configurations"
    * Create a new entry under “Program” and configure it as follows:
        * location = <path to java executable, e.g. `C:/Program Files/Java/jdk1.8.0_191/bin/java.exe`>
        * working dir = <path to project, e.g. `C:/Users/bcarl/Desktop/workspace/evsrestapi`>
        * Arguments = command line args
            * `-Xmx4096M` - ensure enough memory usage
            * `-Dspring.profiles.active=local` - make sure to use application-local.yml
            * `-jar *.war` - point to the war file

    * Test that it’s up by looking for swagger docs: [http://localhost:8080/swagger-ui.html#/](http://localhost:8080/swagger-ui.html#/)


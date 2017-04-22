# File Downloader

Download file from multiple sources and protocols to local disk based on [Akka](http://akka.io) and [sshj](https://github.com/hierynomus/sshj) for ssh client.

Supported Protocols: 
* http
* https
* file
* jar
* ftp
* sftp

---

### Requirements
* Scala 2.12.1
* sbt 0.13.15

### Configuration file
`src/main/resources/application.conf`

* URL credential (username, password)
* Download location

### Run application
To run application, call:
```
$ sbt "run <url1 url2 url3 ...>"
```

**Example**

running in sbt batch mode
```
$ sbt "run https://github.com/scala/scala/archive/v2.12.2.tar.gz ftp://speedtest:speedtest@ftp.otenet.gr/test1Mb.db sftp://test.rebex.net/readme.txt"
```
or running in sbt
```
$ sbt
> run https://github.com/scala/scala/archive/v2.12.2.tar.gz ftp://speedtest:speedtest@ftp.otenet.gr/test1Mb.db sftp://test.rebex.net/readme.txt
```

**Note**

For FTP and SFTP, credentials are set in configuration file

### Run tests
To run tests, call:
```
$ sbt test
```

### Contact
Patipol Sittiyanon (Boom), patipol.st@gmail.com


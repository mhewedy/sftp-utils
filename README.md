# sftp-utils

 * mkdirp  
Create directory structure recursively (from mkdir -p command)   

```java
mkdirp(sftpChannel, "/path/to/dir"); //absolute
mkdirp(sftpChannel, "another/path/to/dir"); // relative
```

 * doInChannel    
 Manage the open and close of session and channel objects

```java
Object isDir = doInChannel("host", 22 /*port*/, "username", "password", channelSftp -> {
      try {
          return channelSftp.lstat("/").isDir();
      } catch (SftpException e) {
          return false;
      }
  });
System.out.println(isDir);       // true

```
Also
```java
// Cache this session Object, as Spring Bean for example
JSch jsch = new JSch();
Session session = jsch.getSession("username", "host", 22 /*port*/);
Properties config = new Properties();
config.setProperty("StrictHostKeyChecking", "no");
session.setConfig(config);
session.setPassword("password");
session.connect();

// Then use it like this:
String result = doInChannel(session, channel -> {
    try {
        return channel.pwd();
    } catch (SftpException e) {
        throw new RuntimeException(e);
    }
});
System.out.println(result);
```

### Usage:
```xml
  <dependency>
    <groupId>com.github.mhewedy</groupId>
    <artifactId>sftp-utils</artifactId>
    <version>1.0.3</version>
  </dependency>
  ```

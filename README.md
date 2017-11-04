# sftp-utils

 * mkdirp  
Create directory structure recursively (from mkdir -p command)   

```java
mkdirp(sftpChannel, "/path/to/dir"); //absolute
mkdirp(sftpChannel, "another/path/to/dir"); // relative
```

 * execute
 Execute sftp commands giving a managed ChannelSftp

```java
JSch jsch = new JSch();
session = jsch.getSession("mhewedy", "192.168.1.10", 22 /*port*/);
Properties config = new Properties();
config.setProperty("StrictHostKeyChecking", "no");
session.setConfig(config);
session.setPassword("system");
session.connect();

Vector result = (Vector) SftpUtils.execute(session, channel -> {
    System.out.println("pwd: " + channel.pwd());
    SftpUtils.mkdirp(channel, "path/to/new/file");
    return channel.ls("path");
});
result.forEach(System.out::println);

boolean findFound = SftpUtils.execute(session, channel -> {
    System.out.println("pwd: " + channel.pwd());
    try{
        channel.lstat("non_found_find");
    }catch (SftpException ex){  // only handel exception when needed
        if (ex.id == ChannelSftp.SSH_FX_NO_SUCH_FILE){
            return false;
        }
    }
    return true;
});
System.out.println("find found: " + findFound);
```

### Usage:
```xml
  <dependency>
    <groupId>com.github.mhewedy</groupId>
    <artifactId>sftp-utils</artifactId>
    <version>1.0.4</version>
  </dependency>
```

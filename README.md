# sftp-utils

 * mkdirp  
Create directory structure recursively (from mkdir -p command)   

```java
SftpUtils.mkdirp(sftpChannel, "/path/to/dir"); //absolute
SftpUtils.mkdirp(sftpChannel, "another/path/to/dir"); // relative
```

 * execute
 Execute sftp commands giving a managed ChannelSftp

```java
SftpUtils.execute(session, channel -> {
    SftpUtils.mkdirp(channel, "path/to/new/file");
});

boolean findFound = SftpUtils.execute(session, channel -> {
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

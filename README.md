# sftp-utils

 ### mkdirp  
Create directory structure recursively (from mkdir -p command)   

```java
SftpUtils.mkdirp(sftpChannel, "/path/to/dir"); //absolute
SftpUtils.mkdirp(sftpChannel, "another/path/to/dir"); // relative
```
### rmr    
Removes dirs and files recursivly, starting from a dir name (same as rm -r command)

```java
SftpUtils.rmr(sftpChannel, "/existence/path/to/remove");  // removes all files and dirs inside the "remove" directory
SftpUtils.rmr(sftpChannel, "/existence/path/"); // removes all files and dirs insdie the "path" directory
```

 ### execute   
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

Copy File and print its content from SFTP after copy:

```java
InputStream transfer = Files.newInputStream(Paths.get("/Users/mhewedy/Work/Code/sftp-utils/README.md"));
String ftpPath = "path/to/new/file/";
String ftpFileName = "README.md2";

execute(session, channel -> {
    SftpUtils.mkdirp(channel, ftpPath);
    channel.put(transfer, ftpPath + ftpFileName);
});

String readmeFile = execute(session, channel -> {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    channel.get(ftpPath + ftpFileName, baos);
    byte[] bytes = baos.toByteArray();
    return new String(bytes);
});
```

### Usage:
```xml
  <dependency>
    <groupId>com.github.mhewedy</groupId>
    <artifactId>sftp-utils</artifactId>
    <version>1.0.6</version>
  </dependency>
```

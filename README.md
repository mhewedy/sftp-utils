# sftp-utils

 ### doInSession   
 Execute sftp commands giving a managed ChannelSftp
 
 Complete example:
 ```java
 // Create SessionFactory instance using supplied SimpleSessionFactory class
 
Properties properties = new Properties();
properties.setProperty("username", "mhewedy");
properties.setProperty("host", "192.168.1.10");
properties.setProperty("port", "22");
properties.setProperty("password", "system");

SessionFactory sessionFactory = new SessionFactory.SimpleSessionFactory(properties);

String fileDir = "test/files/goes/here";
byte[] bytes = "Test me\n".getBytes("utf8");

// create dir recursive:

SftpUtils.doInSession(sessionFactory, channel -> {
    SftpUtils.mkdirp(channel, fileDir);
});

// write a file:

String filePath = SftpUtils.doInSession(sessionFactory, channel -> {
    String path = String.format("%s/%s.%s", fileDir, System.currentTimeMillis(), "txt");
    SftpUtils.mkdirp(channel, fileDir);
    channel.put(new ByteArrayInputStream(bytes), path);
    return path;
});

System.out.println("file written at: " + filePath);

// download the file:

String fileContents = SftpUtils.doInSession(sessionFactory, channel -> {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    channel.get(filePath, baos);
    return new String(baos.toByteArray());
});

System.out.println("file contents from the server: " + fileContents);

// delete the file:

SftpUtils.doInSession(sessionFactory, channel -> {
    channel.rm(filePath);
});

// delete the empty directory:

SftpUtils.doInSession(sessionFactory, channel -> {
    SftpUtils.rmr(channel, fileDir.split("/")[0]);
});

 ```

Check file is found:
```java
SftpUtils.doInSession(sessionFactory, channel -> {
    SftpUtils.mkdirp(channel, "path/to/new/file");
});

boolean findFound = SftpUtils.doInSession(sessionFactory, channel -> {
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

SftpUtils.doInSession(sessionFactory, channel -> {
    SftpUtils.mkdirp(channel, ftpPath);
    channel.put(transfer, ftpPath + ftpFileName);
});

String readmeFile = SftpUtils.doInSession(sessionFactory, channel -> {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    channel.get(ftpPath + ftpFileName, baos);
    byte[] bytes = baos.toByteArray();
    return new String(bytes);
});
```

 ### mkdirp  
Create directory structure recursively (from mkdir -p command)   

```java
SftpUtils.mkdirp(sftpChannel, "/path/to/dir"); //absolute
SftpUtils.mkdirp(sftpChannel, "another/path/to/dir"); // relative
```
### rmr    
Removes dirs and files recursivly, starting from a dir name (same as rm -r command)

```java
SftpUtils.rmr(sftpChannel, "/existence/path/to/remove");  // removes all files and dirs inside the "remove" directory, including "remove" directory it self.
SftpUtils.rmr(sftpChannel, "/existence/path/"); // removes all files and dirs insdie the "path" directory, including "path" directory it self.
```

### Usage:
```xml
  <dependency>
    <groupId>com.github.mhewedy</groupId>
    <artifactId>sftp-utils</artifactId>
    <version>1.0.8</version>
  </dependency>
```

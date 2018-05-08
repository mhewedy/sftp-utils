# sftp-utils

 ### doInSession   
 Execute sftp commands giving a managed ChannelSftp
 
 Set server properties:
 ```java
java.util.Properties props = new java.util.Properties(); 
props.setProperty("username", "ftpuser");
props.setProperty("host", "ftpserver");
props.setProperty("port", 22);
props.setProperty("password", "very secure password");
 ```
 
 Write, Read and Delete file:
 ```java

 private String writeToSftp(byte[] bytes, String fileDir, String ext) {
 	return SftpUtils.doInSession(props, channel -> {
 	    String filePath = String.format("%s/%s.%s", fileDir, System.currentTimeMillis(), ext);
 		SftpUtils.mkdirp(channel, fileDir);
 		channel.put(new ByteArrayInputStream(bytes), filePath);
 		return filePath;
 	});
 }
 
 private byte[] readFromSftp(String filePath) {
 	return SftpUtils.doInSession(props, channel -> {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream();
 		channel.get(filePath, baos);
 		return baos.toByteArray();
 	});
 }
 
 private void deleteFromSftp(String filePath) {
 	SftpUtils.doInSession(props, channel -> {
 		channel.rm(filePath);
 	});
 }
 ```

Check file is found:
```java
SftpUtils.doInSession(props, channel -> {
    SftpUtils.mkdirp(channel, "path/to/new/file");
});

boolean findFound = SftpUtils.doInSession(props, channel -> {
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

SftpUtils.doInSession(props, channel -> {
    SftpUtils.mkdirp(channel, ftpPath);
    channel.put(transfer, ftpPath + ftpFileName);
});

String readmeFile = SftpUtils.doInSession(props, channel -> {
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

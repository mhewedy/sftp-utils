# sftp-utils

 * mkdirp  
Create directory structure recursively (from mkdir -p command)   

```java
SftpUtils.mkdirp(sftpChannel, "/path/to/dir"); //absolute
SftpUtils.mkdirp(sftpChannel, "another/path/to/dir"); // relative
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

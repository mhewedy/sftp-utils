# sftp-utils

 * mkdirp  
Create directory structure recursively (from mkdir -p command)   
```
SftpUtils.mkdirp(sftpChannel, "/path/to/dir"); //absolute
SftpUtils.mkdirp(sftpChannel, "another/path/to/dir"); // relative
```

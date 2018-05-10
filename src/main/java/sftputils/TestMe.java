package sftputils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class TestMe {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Properties properties = new Properties();
        properties.setProperty("username", "mhewedy");
        properties.setProperty("host", "192.168.1.10");
        properties.setProperty("port", "22");
        properties.setProperty("password", "system");

        SessionFactory sessionFactory = new SessionFactory.SimpleSessionFactory(properties);

        String fileDir = "test/files/goes/here";
        byte[] bytes = "Test me\n".getBytes("utf8");

        // create dir recursive:
        SftpUtils.execute(sessionFactory, channel -> {
            SftpUtils.mkdirp(channel, fileDir);
        });

        // write a file:
        String filePath = SftpUtils.execute(sessionFactory, channel -> {
            String path = String.format("%s/%s.%s", fileDir, System.currentTimeMillis(), "txt");
            SftpUtils.mkdirp(channel, fileDir);
            channel.put(new ByteArrayInputStream(bytes), path);
            return path;
        });

        System.out.println("file written at: " + filePath);

        // download the file:
        String fileContents = SftpUtils.execute(sessionFactory, channel -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            channel.get(filePath, baos);
            return new String(baos.toByteArray());
        });

        System.out.println("file contents from the server: " + fileContents);

        // delete the file:
        SftpUtils.execute(sessionFactory, channel -> {
            channel.rm(filePath);
        });

        // delete the empty directory:

        SftpUtils.execute(sessionFactory, channel -> {
            SftpUtils.rmr(channel, fileDir.split("/")[0]);
        });
    }
}

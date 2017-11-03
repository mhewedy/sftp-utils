package sftputils;

import com.jcraft.jsch.*;
import org.junit.*;
import software.sham.sftp.MockSftpServer;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class SFtpUtilsTest {
    static MockSftpServer server;
    static Session session;

    @BeforeClass
    public static void initSftp() throws IOException {
        server = new MockSftpServer(9022);
        server.getBaseDirectory();
    }

    @BeforeClass
    public static void initSshClient() throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession("tester", "localhost", 9022);
        Properties config = new Properties();
        config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword("testing");
        session.connect();
    }

    @AfterClass
    public static void stopSftp() throws IOException {
        server.stop();
    }

    @Test
    public void mkdirpUsingAbsolutePath() throws JSchException, IOException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        SftpUtils.mkdirp(sftpChannel, "/path/to/dir");
        SftpATTRS lstat = sftpChannel.lstat("/path/to/dir");
        assertTrue(lstat.isDir());

        sftpChannel.disconnect();
    }

    @Test
    public void mkdirpUsingAbsolutePath2() throws JSchException, IOException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        SftpUtils.mkdirp(sftpChannel, "/path/to/dir");
        SftpATTRS lstat = sftpChannel.lstat("/path/to/dir");
        assertTrue(lstat.isDir());

        sftpChannel.disconnect();
    }

    @Test
    public void mkdirpUsingRelativePath() throws JSchException, IOException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        SftpUtils.mkdirp(sftpChannel, "another/path/to/dir");
        SftpATTRS lstat = sftpChannel.lstat("/another/path/to/dir");
        assertTrue(lstat.isDir());

        sftpChannel.disconnect();
    }
}

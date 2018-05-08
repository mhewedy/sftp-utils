package sftputils;

import com.jcraft.jsch.*;

import java.util.Properties;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_FAILURE;
import static com.jcraft.jsch.ChannelSftp.SSH_FX_NO_SUCH_FILE;

class Ops {
    static void mkdir(ChannelSftp channelSftp, String part) throws SftpException {
        try {
            channelSftp.mkdir(part);
        } catch (SftpException ex) {
            error("mkdir", channelSftp, part);
            throw ex;
        }
    }

    static void cd(ChannelSftp channelSftp, String part) throws SftpException {
        try {
            channelSftp.cd(part);
        } catch (SftpException ex) {
            error("cd", channelSftp, part);
            throw ex;
        }
    }

    static void rm(ChannelSftp channelSftp, String file) throws SftpException {
        try {
            channelSftp.rm(file);
        } catch (SftpException ex) {
            error("cd", channelSftp, file);
            throw ex;
        }
    }

    static void rmdir(ChannelSftp channelSftp, String dir) throws SftpException {
        try {
            channelSftp.rmdir(dir);
        } catch (SftpException ex) {
            error("cd", channelSftp, dir);
            throw ex;
        }
    }

    static boolean notExists(ChannelSftp channelSftp, String part) throws SftpException {
        try {
            SftpATTRS attrs = channelSftp.lstat(part);
            if (attrs != null && !attrs.isDir()) {
                throw new SftpException(SSH_FX_FAILURE, "object exists with the same name");
            }
            return false;
        } catch (SftpException ex) {
            if (ex.id == SSH_FX_NO_SUCH_FILE) {
                return true;
            }
            error("notExists", channelSftp, part);
            throw ex;
        }
    }

    static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    static Session createSession(Properties properties) {
        try {
            String username = properties.getProperty("username");
            String host = properties.getProperty("host");
            int port = Integer.parseInt(properties.getProperty("port"));
            String password = properties.getProperty("password");

            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            return session;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static void closeSession(Session session) {
        if (session != null) {
            session.disconnect();
        }
    }

    private static void error(String ops, ChannelSftp channelSftp, String path) {
        try {
            System.err.println("Operation: " + ops + ", pwd:" + channelSftp.pwd() + ", path: " + path);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}

package sftputils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SftpUtils {

    /**
     * Create dir recursively, similar to shell command mkdir -p
     *
     * @param channelSftp
     * @param path        absolute or relative path
     * @throws SftpException
     */
    public static void mkdirp(ChannelSftp channelSftp, String path) throws SftpException {
        if (isBlank(path)) {
            throw new IllegalArgumentException("path cannot be blank");
        }

        String[] parts = path.split("/");
        if (isBlank(parts[0])) {
            parts[0] = "/";
        }

        for (String part : parts) {
            if (notExists(channelSftp, part)) {
                mkdir(channelSftp, part);
            }
            cd(channelSftp, part);
        }
    }

    private static boolean notExists(ChannelSftp channelSftp, String part) throws SftpException {
        try {
            SftpATTRS attrs = channelSftp.lstat(part);
            if (attrs != null && !attrs.isDir()) {
                throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "object exists with the same name");
            }
            return false;
        } catch (SftpException ex) {
            if (ex.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return true;
            }
            error("notExists", channelSftp, part);
            throw ex;
        }
    }

    private static void mkdir(ChannelSftp channelSftp, String part) throws SftpException {
        try {
            channelSftp.mkdir(part);
        } catch (SftpException ex) {
            error("mkdir", channelSftp, part);
            throw ex;
        }
    }

    private static void cd(ChannelSftp channelSftp, String part) throws SftpException {
        try {
            channelSftp.cd(part);
        } catch (SftpException ex) {
            error("cd", channelSftp, part);
            throw ex;
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void error(String ops, ChannelSftp channelSftp, String path) {
        try {
            System.err.println("Operation: " + ops + ", pwd:" + channelSftp.pwd() + ", path: " + path);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}

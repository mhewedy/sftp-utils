package sftputils;

import com.jcraft.jsch.*;

import java.util.Arrays;
import java.util.List;

import static sftputils.Ops.*;

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

        String pwd = channelSftp.pwd();

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
        // return to original path before recursively create the new path
        cd(channelSftp, pwd);
    }

    public static <T> void execute(Session session, final Action0 block) {
        execute(session, new Action1<Void>() {
            public Void execute(ChannelSftp channelSftp) throws SftpException {
                block.execute(channelSftp);
                return null;
            }
        });
    }

    public static <T> T execute(Session session, Action1<T> block) {
        ChannelSftp sftpChannel = null;
        try {
            if (!session.isConnected()) {
                session.connect();
            }

            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            return block.execute(sftpChannel);

        } catch (JSchException ex) {
            throw new RuntimeException(ex);
        } catch (SftpException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
        }
    }

    /**
     * remove dir recursively , similar to shell command rm -r (but works for dirs only)
     * @param channelSftp
     * @param path directory to remove and all its sub content
     * @throws SftpException
     */
    public static void rmr(ChannelSftp channelSftp, String path) throws SftpException {
        if (isBlank(path)) {
            throw new IllegalArgumentException("path cannot be blank");
        }
        List<ChannelSftp.LsEntry> files = channelSftp.ls(path);

        for (ChannelSftp.LsEntry entry : files) {
            if (entry.getAttrs().isDir()) {
                if (!Arrays.asList(".", "..").contains(entry.getFilename())) {
                    rmr(channelSftp, path + "/" + entry.getFilename());
                }
            } else {
                rm(channelSftp, path + "/" + entry.getFilename());
            }
        }
        rmdir(channelSftp, path);
    }

    public interface Action0 {
        void execute(ChannelSftp channelSftp) throws SftpException;
    }

    public interface Action1<T> {
        T execute(ChannelSftp channelSftp) throws SftpException;
    }
}

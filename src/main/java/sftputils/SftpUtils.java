package sftputils;

import com.jcraft.jsch.*;

import java.util.Arrays;
import java.util.List;

import static sftputils.Ops.*;

public class SftpUtils {

    public static void execute(SessionFactory sessionFactory, Action0 block) {
        execute(sessionFactory, (Action1<Object>) channelSftp -> {
            block.doInChannel(channelSftp);
            return (Void) null;
        });
    }

    public static <T> T execute(SessionFactory sessionFactory, Action1<T> block) {

        final Session session = sessionFactory.createSession();
        ChannelSftp sftpChannel = null;

        try {
            if (!session.isConnected()) session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            return block.doInChannel(sftpChannel);

        } catch (JSchException | SftpException ex) {
            throw new RuntimeException(ex);
        } finally {
            sessionFactory.closeSession(session);
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
        }
    }

    /**
     * Create dir recursively, similar to shell command mkdir -p
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

    /**
     * remove dir recursively , similar to shell command rm -r (but works for dirs only)
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
        void doInChannel(ChannelSftp channel) throws SftpException;
    }

    public interface Action1<T> {
        T doInChannel(ChannelSftp channel) throws SftpException;
    }
}

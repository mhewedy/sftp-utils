package sftputils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * Thread safe class, better to cache instance of it. (e.g. as a Spring Bean)
 */
public interface SessionFactory {

    Session createSession();

    default void closeSession(Session session) {
        if (session != null) {
            session.disconnect();
        }
    }

    class SimpleSessionFactory implements SessionFactory {

        private final Properties properties;

        public SimpleSessionFactory(Properties properties) {
            this.properties = properties;
        }

        @Override
        public Session createSession() {
            try {
                String username = properties.getProperty("username");
                String host = properties.getProperty("host");
                int port = Integer.parseInt(properties.getProperty("port"));
                String password = properties.getProperty("password");

                JSch jsch = new JSch();
                Session session = jsch.getSession(username, host, port);
                session.setPassword(password);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                return session;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

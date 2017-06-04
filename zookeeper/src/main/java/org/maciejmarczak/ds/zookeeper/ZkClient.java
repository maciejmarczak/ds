package org.maciejmarczak.ds.zookeeper;

import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ZkClient implements Watcher, AsyncCallback.StatCallback, Runnable {

    private final ClientConfig clientConfig;
    private final ProcessManager processManager;
    private ZooKeeper zk;

    private ZkClient(String watchedZnode, String connectionUrl,
                     String runCmd) {
        clientConfig = new ClientConfig(watchedZnode, connectionUrl);
        processManager = new ProcessManager(runCmd);
    }

    private void start() throws IOException {
        zk = new ZooKeeper(clientConfig.connectionUrl,
                3000, this);

        zk.exists(clientConfig.watchedZnode, this, this, null);
        getChildren("/", false);
    }

    private void stop() {
        try {
            zk.close();
        } catch (InterruptedException ignored) {}
        System.exit(0);
    }

    private void getChildren(String znode, boolean sync) {
        try {
            if (sync) {
                System.out.format("%s has %d children\n\n", znode,
                        zk.getChildren(znode, true).size());
            } else {
                zk.getChildren(znode, this);
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String printTree(String znode) {
        StringBuilder result = new StringBuilder();

        try {
            List<String> children = zk.getChildren(znode, false);

            for (String s : children) {
                result.append("[").append(printTree(znode + "/" + s)).append("]");
            }

        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        return "[" + znode + result.toString() + "]";
    }

    @Override
    public void process(WatchedEvent event) {
        final String path = event.getPath();
        final String znode = clientConfig.watchedZnode;

        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected: break;
                case Expired: case Disconnected: stop();
            }
        } else if (path != null) {
            zk.exists(znode, this, this, null);
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        switch (rc) {
            case KeeperException.Code.Ok: {
                if (!processManager.isRunning()) {
                    processManager.startProcess();
                }
                getChildren(clientConfig.watchedZnode, true);
                break;
            }
            case KeeperException.Code.NoNode: {
                if (processManager.isRunning()) {
                    processManager.stopProcess();
                }
                break;
            }
            default:
                zk.exists(clientConfig.watchedZnode, true, this, null);
        }
    }

    @Override
    public void run() {
        try {
            start();

            Scanner sc = new Scanner(System.in);

            String in = sc.nextLine();
            while (!"q".equals(in)) {
                if ("print".equals(in)) {
                    System.out.println(printTree(clientConfig.watchedZnode));
                }
                in = sc.nextLine();
            }

        } catch (IOException e) {
            // log it somewhere
            System.out.println("Fatal error: " + e.getMessage());
        }
    }

    private class ClientConfig {
        final String watchedZnode;
        final String connectionUrl;

        ClientConfig(String watchedZnode, String connectionUrl) {
            this.watchedZnode = watchedZnode;
            this.connectionUrl = connectionUrl;
        }
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        if (args.length != 3) {
            System.out.println("Usage: ./program [WATCHED_ZNODE] " +
                    "[CONNECTION_URL] [RUN_CMD]");
            System.exit(-1);
        }

        new ZkClient(args[0], args[1], args[2]).run();
    }
}

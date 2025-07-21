package agh.distrib;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ZDWatcher implements Watcher {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZDWatcher watcher = new ZDWatcher(args);

        for(;;) {
            Thread.sleep(100);
        }
    }

    private final ZooKeeper zk;
    private String tree;
    private Process externalApp;
    private String[] args;

    public ZDWatcher(String[] args) throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(args[0], 3000, this);
        this.args = Arrays.stream(args).skip(1).toArray(String[]::new);
        if(makeTree()) {
            runExternalApp();
        }
        System.out.println(tree);
    }

    private void runExternalApp() throws IOException {
        File executable = new File(args[0]);
        File executableDir = executable.getParentFile();
        ProcessBuilder pb = new ProcessBuilder(executable.getAbsolutePath());
        pb.directory(executableDir);
        externalApp = pb.start();
    }

    private void clearConsole() {
        int lines = tree.split("\\n").length;
        System.out.print("\033[%dF".formatted(lines));
        System.out.println(tree.replaceAll("[^\\n]", " "));
        System.out.print("\033[%dF".formatted(lines));
    }

    private static String level(int i) {
        StringBuilder sb = new StringBuilder();
        while(i-- != 0) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private Pair<String, Integer> getChildrenTree(String path, int lvl) throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(path, true);
        if(children.size() == 0) {
            return new Pair<>(" (brak dzieci)", 0);
        }

        String result = "";
        int descendants = children.size();
        for(String child : children) {
            Pair<String, Integer> pair = getChildrenTree("%s/%s".formatted(path, child), lvl + 1);
            descendants += pair.second;
            result += "\n%s%s%s".formatted(level(lvl), child, pair.first);
        }

        return new Pair<>(" (%d potomków):%s".formatted(descendants, result), descendants);
    }

    private boolean makeTree() throws InterruptedException, KeeperException {
        if (zk.exists("/a", true) == null) {
            tree = "/a nie istnieje";
            return false;
        }
        tree = "/a:%s".formatted(getChildrenTree("/a", 1).first);
        return true;
    }

    void printTree() {
        try {
            clearConsole();
            makeTree();
            System.out.println(tree);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getPath() == null){
            return;
        }

        var eventType = watchedEvent.getType();
        switch (eventType) {
            case NodeChildrenChanged:
                printTree();
                break;
            case NodeCreated:
                if(watchedEvent.getPath().equals("/a")) {
                    printTree();
                    try {
                        runExternalApp();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            case NodeDeleted:
                if(watchedEvent.getPath().equals("/a")) {
                    printTree();
                    externalApp.destroy();
                    externalApp = null;
                }
                break;
        }
    }
}
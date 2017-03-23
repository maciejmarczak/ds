package org.maciejmarczak.ds.jgroupschat;

import java.util.Scanner;

class TerminalUtils {
    private static final String PROMPT = ": > ";

    static String readLine(String msg) {
        System.out.print(msg + PROMPT);
        return readLine();
    }

    static String readLine() {
        try (Scanner sc = new Scanner(System.in)) {
            return sc.nextLine();
        }
    }
}

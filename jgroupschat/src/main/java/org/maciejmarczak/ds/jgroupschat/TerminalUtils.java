package org.maciejmarczak.ds.jgroupschat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class TerminalUtils {
    private static final String PROMPT = ": > ";
    private static final BufferedReader IN =
            new BufferedReader(new InputStreamReader(System.in));

    static String readLine(String msg) throws IOException {
        System.out.print(msg + PROMPT);
        return readLine();
    }

    static String readLine() throws IOException {
        return IN.readLine();
    }
}

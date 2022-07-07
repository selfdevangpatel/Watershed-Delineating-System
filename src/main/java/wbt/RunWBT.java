package wbt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunWBT {

    public static boolean runScript(String toolName, String flag1, String flag2, String flag3, String flag4,
                                    String flag5, String flag6) {

        Process process;
        String wbtPath = "src/main/resources/wbt/executable/whitebox_tools.exe";
        String args = wbtPath + toolName + flag1 + flag2 + flag3 + flag4 + flag5 + flag6;
        System.out.println(args);
        boolean success = false;

        try {

            process = Runtime.getRuntime().exec(args);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) System.out.println(line);

            process.destroy();
            success = process.exitValue() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }
}
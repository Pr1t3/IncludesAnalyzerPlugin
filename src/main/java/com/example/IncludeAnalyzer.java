package com.example;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncludeAnalyzer {

    public static List<String> getIncludes(VirtualFile virtualFile) {
        List<String> includes = new ArrayList<>();
        if (virtualFile != null && virtualFile.isValid()) {
            try {
                String content = new String(virtualFile.contentsToByteArray());
                Pattern pattern = Pattern.compile("#include\\s+[\"<](.*?)[\">]");
                Matcher matcher = pattern.matcher(content);

                while (matcher.find()) {
                    includes.add(matcher.group(1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return includes;
    }
}

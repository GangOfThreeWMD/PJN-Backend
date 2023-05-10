package org.GoT;

import jep.*;
import jep.MainInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Algorithm {
    private boolean firstRun;

    public Algorithm() throws IOException {
        String pythonFolder = System.getenv("LD_LIBRARY_PATH");
        if (pythonFolder == null) {
            throw new UnsatisfiedLinkError("Wrong setup path to Python library");
        }
        String jepPath = pythonFolder + "/jep/libjep.jnilib"; // for OS X
        if (!Files.exists(Path.of(jepPath))){
            jepPath = pythonFolder + "/jep/libjep.so"; // for Linux
        } if (!Files.exists(Path.of(jepPath))) {
            jepPath = pythonFolder + "\\jep\\jep.dll"; // For Windows
        } if (!Files.exists(Path.of(jepPath))) {
            throw new UnsatisfiedLinkError("No found jep library. Install jep via pip");
        }

        //create the interpreter for python executing
        MainInterpreter.setJepLibraryPath(jepPath);
        jep.JepConfig jepConf = new JepConfig();
        File javaDirectory = new File("").getCanonicalFile();
        jepConf.addIncludePaths(javaDirectory.getAbsolutePath()  + "/PythonModule");
        jepConf.addIncludePaths(pythonFolder);
        jepConf.redirectStdout(System.out);
        jepConf.redirectStdErr(System.err);
        SharedInterpreter.setConfig(jepConf);

        this.firstRun = true;
    }

    public String getSummarize(String text, SummarizeProperties properties) throws JepException{
        int max_sentences = properties.maxSentences();
        int min_length = properties.min_length();

        try(SharedInterpreter subInterp = new SharedInterpreter()){
            subInterp.eval("import summarizer as sum");
            if(firstRun) {
                subInterp.eval("sum.init()");
                firstRun = false;
            }
            subInterp.set("textToSummarize", text);
            subInterp.set("max_sentences", max_sentences);
            subInterp.set("min_length", min_length);
            subInterp.eval("""
                    output = sum.generate_summary(text=textToSummarize,
                    top_n=max_sentences,
                    min_length=min_length)
                    """);
            return subInterp.getValue("output", String.class);
        }
    }

    public static void main(String[] args) throws IOException {
        Algorithm algorithm = new Algorithm();
        System.out.println(algorithm.getSummarize("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum placerat sit amet velit porttitor rutrum. Quisque turpis risus, egestas id eros at, viverra ultricies dolor. Fusce dictum dui id justo ultricies, in lobortis erat viverra. Donec sed purus ipsum. Nulla condimentum ut lorem vel mattis. Donec lacinia velit ac quam commodo placerat. Nam vehicula auctor eros at mattis. Donec pharetra est a metus viverra volutpat at ac lacus.", SummarizeProperties.getDefault()));
    }
}
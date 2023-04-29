package org.GoT;

import jep.*;
import jep.MainInterpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Algorithm {

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
    }

    public String getSummarize(String text) {
        try(SharedInterpreter subInterp = new SharedInterpreter()){
            // run each function from the .py doc I
            subInterp.eval("import summarizer as sum");
            subInterp.eval("import pathlib");
            subInterp.eval("path = pathlib.Path().resolve()");
            var path = subInterp.getValue("path", String.class);
            System.out.println(path);
            subInterp.set("textToSummarize", text);
            subInterp.eval("output = sum.generate_summary(textToSummarize)");
            return subInterp.getValue("output", String.class);
        } catch (JepException ex) {
            throw new JepException("Problem with Python side: "  + ex);
        }
    }

    public static void main(String[] args) throws IOException {
        Algorithm algorithm = new Algorithm();
        System.out.println(algorithm.getSummarize("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum placerat sit amet velit porttitor rutrum. Quisque turpis risus, egestas id eros at, viverra ultricies dolor. Fusce dictum dui id justo ultricies, in lobortis erat viverra. Donec sed purus ipsum. Nulla condimentum ut lorem vel mattis. Donec lacinia velit ac quam commodo placerat. Nam vehicula auctor eros at mattis. Donec pharetra est a metus viverra volutpat at ac lacus."));
    }
}
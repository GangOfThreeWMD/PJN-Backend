# ClipSum
Project contain 4 module:
1. PythonModule - contain python code for summarize text
2. SummarizeAlgorith - Java code use Python algorithm and return summarize text
3. Scraper - Program for Scrapping news from web
4. Summarizer - Main program to get news

## Instruction installation with Intellij <a href="https://www.jetbrains.com/help/idea/getting-started.html"><img src="https://cdn3.emoji.gg/emojis/5827_intellij.png" width="20px" height="20px" alt="intellij logo"></a>:
1. Install plugin Python [plugin install](https://www.jetbrains.com/help/idea/managing-plugins.html) , [Python support](https://www.jetbrains.com/help/idea/plugin-overview.html)
2. Go to "File -> Project Structure -> Project Settings -> Modules"
3. Click Add symbol "+"
4. Select *New Module*
5. Type *Name* as **PythonModule** - ignore warning about dirrectory is not empty
6. Select *Language* as **Python**
7. Select *Inherit global site-packages*
8. Select *Base interpreter" the same as your default Python from console. For check which Python interpreter use as default, run this command from terminal/console:
    ```bash
    python -c "import os, sys; print(os.path.dirname(sys.executable))"
    ```
    This instruction print a path of directory where is python executable file.
9. Set environment to *dist-packages* (or *site-packages*) folder. Go to "Run -> Edit Configurations -> select Spring Boot configuration -> Modify options -> in a section *Operating system* select **Environment variables** -> In a new textbox provide:
    * For Windows:
        ```PowerShell
        LD_LIBRARY_PATH=C:\Users\<user name>\AppData\Roaming\Python\<python version>\site-packages        
        ```
    * For Linux:
        ```bash
        LD_LIBRARY_PATH="/usr/local/lib/python<python version>/dist-packages"
        ```
    For display your path to this file, should run this command:
    ```bash
    python -m site
    ```

    More info: [How do I fix Unsatisfied Link Error: no jep in java.library.path?](https://github.com/ninia/jep/wiki/FAQ#how-do-i-fix-unsatisfied-link-error-no-jep-in-javalibrarypath)

10. Next step you should install all neccessary library for Python script:
    ```bash
    pip install jep
    pip install nltk
    pip install numpy
    pip install networkx
    pip install scipy
    ```

11. Now you can run Spring Boot project

## For run project without Intellij:
1. You should set environment:
```bash
export LD_LIBRARY_PATH="/usr/local/lib/python<python version>/dist-packages"$LD_LIBRARY_PATH
```
2. install library for Python (step 10 for Intellij instruction)

## Documentations:
- [Project page for library JEP](https://github.com/ninia/jep)
- [Java documentation for Library JEP](https://ninia.github.io/jep/javadoc/4.1/)


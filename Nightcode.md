# Getting started with Nightcode
Nightcode is a clojure IDE that comes bundled with other tools in the
clojure development stack. 

![Nightcode IDE](https://sekao.net/nightcode/screenshot.png)
# Install Nightcode
Download and install Java 7 or Java 8

Download Nightcode from https://sekao.net/nightcode/ into a tools folder like `~/tools/nightcode`

Open Nightcode by double-clicking the downloaded jar or selecting the downloaded jar, right-click and select the "Open" command. 

Once Nightcode is loaded, verify that you have the complete clojure stack by typing command in the Clojure REPL.
# Clojure REPL

# New project cloned from git
  Follow the steps below to clone a new project from a git repository. For this example we will clone the git repository at https://github.com/clojurebridge-minneapolis/clojure-koans.git
  

 1. Click `New Project`
 2. Select a folder location for your project, type in your project name (say CBB) in the `File Name` form and click `Save`.
 3. On the `Specify Project Type` screen, click `Download`. In the `Git address` field, type in the address *my address* and click `Clone Project`

The git project is now cloned and available for editing in Nightcode.

# Clojure Koans
Create a new Nightcode project cloned from the Clojure Koans git project at https://github.com/clojurebridge-minneapolis/clojure-koans.git 

To run the Koans, do the following

 1. Click `Run with REPL`
 2. In the Nightcode project files panel on the left, select the file *src/koans/01_equalities.clj*. This will open the file in the editing panel.
 3. Click `Eval`. You will see an assertion failure like the one below
 ```
 ExceptionInfo We shall contemplate truth by testing reality, via equality
(= __ true)  clojure.core/ex-info (core.clj:4327)
 ```
 4. Modify the code in the file as below, save and click `Eval` again
 ```
 "We shall contemplate truth by testing reality, via equality"
  (= true true)
 ```
 You will now see that this assertion passed successfully and you a failure on the next assertion.
 5. Modify the code in the next assertion to make it pass and continue on to all the assertion int he file by clicking `Eval` every time you modify the code.# Getting started with Nightcode
Nightcode is a clojure IDE that comes bundled with other tools in the
clojure development stack. 
# Install Nightcode
Download and install Java 7 or Java 8

Download Nightcode from https://sekao.net/nightcode/ into a tools folder like `~/tools/nightcode`

Open Nightcode by double-clicking or selecting the jar, right-click and select the "Open" command.
# Clojure REPL

# New project cloned from git
  Follow the steps below to clone a new project from a git repository. For this example we will clone the git repository at https://github.com/clojurebridge-minneapolis/clojure-koans.git
  
# Clojure Koans

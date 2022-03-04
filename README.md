# ASTChangeAnalyzer

A modulizable code differencing and AST extracting tool through mining Git commits.

## Description

ASTChangeAnalyzer is a tool that mines commit IDs from a designated URL or a local path using JGIt to extract two ASTs (Abstract Syntax Tree) of source codes from the commit and generate change information. The tool is designed to collect huge size of change information.

ASTChangeAnalyzer mines repositories using JGit and parse codes using GumTree and LAS

(Find the details of tools : GumTree(https://github.com/GumTreeDiff/gumtree), LAS(https://github.com/zackcglee/LAS)

It has the following features:
* mining diff commits and extract source code
* converting a source file into a language-agnostic tree format (Java, Python, c, and c++ supported)
* compute the differences between the trees
* visualize these differences in different abstract levels
* storing unique change patterns for collecting mass change data

## Documentation

### Execution

* Required Options :
1. `-p` option : provide a local path, an URL, or a file containing either list of local paths or URLs for the repository (absolute path if local path)

2. `-java`, `-python`, `c` options : choose 'java', 'python', 'c', or 'c++(cpp in command) and name the code differencing tool (default: GumTree).

3. `-gitClone` : this just clones a github repo/ or a repos from .csv listing urls taken from `-p` option (path statically set).

4. `-changeCount` : this gives you the total number of changes from the given path from `-p` option.

5. `-save` option : this option provides 3 things
     
                     first, it clones (if not cloned yet), diffs, and produces `.chg` binary file per project at the path given as argument
                     second, it provides `Statistics.txt` file for further analysis.
                     third, it updates or creates `index.csv` file that has hascode lists and project names mapped to index from `.chg` files.

6. `-combine` option : this combines multiple `.chg` files into one `.chg` file


Example : `-p https://github.com/centic9/jgit-cookbook -java las -save`





* graph.py :

we have simple graph.py file using plotext library to generate graph as a terminal output
following commands at the root directory with the .txt file will produce a graph if there exists `Statistics.txt` file generated form `-save` option.

`pip3 install plotext`

`python3 graph.py`


![Screenshot from 2022-02-18 00-30-25](https://user-images.githubusercontent.com/83571012/154514625-5d32b1df-d2c5-4e3c-9d48-f9debf4a9b10.png)
![Screenshot from 2022-02-18 00-25-46](https://user-images.githubusercontent.com/83571012/154513828-4e0877be-0c36-4515-9543-4c1efc337332.png)

Example of `Statistics.txt` and `graph.py`



* Dependencies :

Required installation is internally done - no need!

(Currently, Gumtree execution file does not run on Windows environment) 




Yeawon Na, Zack CG Lee from ISEL

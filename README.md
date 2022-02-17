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
* storing unique change patterns for mass change data

## Documentation

### Execution

* Required Options :
1. `-p` option : provide a local path, an URL, or a file containing either list of local paths or URLs for the repository (absolute path if local path)

2. `-java`, `-python`, `c` options : choose 'java', 'python', 'c', or 'c++(cpp in command) and name the code differencing tool (default: GumTree).

3. `-gitClone`, `-changeMine`, `-analysis` options : gitClone option just clones the repos, and changeMine option gives total number of changes based on the path given with -p option. 

Finally, analysis options produce .txt file and .chg binary file that has statistics about change analysis and hash-encoded binary information. 
The `analysis` option requires an argument as integer which becomes the statistical increments for the records in .txt file

Example : `-p https://github.com/centic9/jgit-cookbook -java las -analysis 10`

![Screenshot from 2022-02-18 00-25-46](https://user-images.githubusercontent.com/83571012/154513828-4e0877be-0c36-4515-9543-4c1efc337332.png)



* graph.py :

we have simple graph.py file using plotext library to generate graph as a terminal output
following command at the root directory with the .txt file will produce a graph
`pip3 install plotext`
`python3 graph.py`
![Screenshot from 2022-02-18 00-30-25](https://user-images.githubusercontent.com/83571012/154514625-5d32b1df-d2c5-4e3c-9d48-f9debf4a9b10.png)




* Dependencies :

Required installation is internally done - no need!

(Currently, Gumtree execution file does not run on Windows environment) 




Yeawon Na, Zack CG Lee from ISEL

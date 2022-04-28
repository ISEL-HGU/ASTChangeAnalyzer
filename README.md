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
1. `-p` option : provide an argument of a local path, an URL, or a file containing either list of local paths or URLs for the repository (absolute path if local path)

2. `-java`, `-python`, `c` options : choose 'java', 'python', 'c', or 'c++(cpp in command) and name the code differencing tool (default: GumTree).

3. `-gitClone` : this just clones a github repo/ or a repos from .csv listing urls taken from `-p` option (path statically set).

4. `-changeCount` : this gives you the total number of changes from the given path from `-p` option.

5. `-save` option : this option provides 3 things
     
                     first, it clones (if not cloned yet), diffs, and produces `.chg` binary file per project at the path given as an argument
                     second, it provides `Statistics.txt` file for further analysis.
                     third, it updates or creates `index.csv` file that has hascode lists and project names mapped to index from `.chg` files.

6. `-combine` option : this combines multiple `.chg` files into one `.chg` file (not implemented yet)

7. `-sample` option : takes an absolute path of `index.csv` to provide 20 samples based on the median value.

8. `-hashcode` option : takes an hashcode along with `-sample` option

Example : `-p https://github.com/centic9/jgit-cookbook -java las -save`


* Dependencies :

Required installation is internally done - no need!

(Currently, Gumtree execution file does not run on Windows environment) 




Yeawon Na, Zack CG Lee from ISEL

# ASTChangeAnalyzer

A modulizable code differencing and AST extracting tool through mining Git commits.

## Description

ASTChangeAnalyzer is a tool that mines commit IDs from a designated URL or a local path to extract two ASTs (Abstract Syntax Tree) of before and after the point of the commit.
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

3. `-level` : adding this option will not only stop printing the trees but also stores file level and hunk level change patterns.

Example : `-p https://github.com/ISEL-HGU/ASTChangeAnalyzer/ -java las`

* Dependencies :

Required installation is internally done - no need!

(Currently, Gumtree execution file does not run on Windows environment) 




Yeawon Na, Zack CG Lee from ISEL

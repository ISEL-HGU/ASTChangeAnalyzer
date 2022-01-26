# ASTChangeAnalyzer

A modulizable code differencing and AST extracting tool through mining Git commits.

## Description

ASTChangeAnalyzer is a tool that mines commit IDs from a designated URL or a local path to extract two ASTs (Abstract Syntax Tree) of before and after the point of the commit.
ASTChangeAnalyzer mines repositories using JGit and parse codes using GumTree

It has the following features:
* mining diff commits and extract source code
* converting a source file into a language-agnostic tree format (Java and Python supported)
* compute the differences between the trees
* visualize these differences graphically

## Documentation

* Execution

Required Options :
1. `-p` option : provide a local path for the repository (absolute path)
                        or
   `-u` option : provide a URL to create a clone of the repository (root/Desktop)

2. `-lang` option : choose java or python



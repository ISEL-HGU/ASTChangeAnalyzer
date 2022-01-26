# ASTChangeAnalyzer

A modulizable code differencing and AST extracting tool through mining Git commits.

## Description

ASTChangeAnalyzer is a tool that mines commit IDs from a designated URL or a local path to extract two ASTs (Abstract Syntax Tree) of before and after the point of the commit.
ASTChangeAnalyzer mines repositories using JGit and parse codes using GumTree

It has the following features:
* mining diff commits and extract source code
* converting a source file into a language-agnostic tree format (Java and Python supported)
* compute the differences between the trees
* visualize these differences in different abstract levels

## Documentation

* Execution

Required Options :
1. `-p` option : provide a local path for the repository (absolute path)
                        or
   `-u` option : provide a URL to create a clone of the repository (root/Desktop)

2. `-lang` option : choose 'java' or 'python'

Example : -u https://github.com/ISEL-HGU/ASTChangeAnalyzer/ -lang python

Dependencies :

Required installation is internally done - no need!


User Guide :
1. If you already have a cloned repository at the path given in the command line input, the program let you choose to rewrite the repository or exit the program. Example as below.
<img width="598" alt="KakaoTalk_20220126_152536726" src="https://user-images.githubusercontent.com/83571012/151114015-25f4f160-f4b2-4264-8e79-1511e3172201.png">

2. Otherwise, or if you do not already have a cloned repository, it generates change information based on each commits that the repository contains. Example as below.
<img width="361" alt="KakaoTalk_20220126_152820033" src="https://user-images.githubusercontent.com/83571012/151114470-378d84ac-72ce-476c-99f0-be739f8b5e3b.png">




Yeawon Na, Zack CG Lee from ISEL

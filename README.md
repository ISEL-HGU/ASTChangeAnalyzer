# ASTChangeAnalyzer

An awesome code differencing tool through mining Git commits.

## Status

![Build and Test GumTree](https://github.com/GumTreeDiff/gumtree/workflows/Build,%20Test%20and%20Deploy%20GumTree/badge.svg?branch=main)

## Description

ASTChangeAnalyzer is a tool to deal with source code mined in Git/Github as trees and compute differences between them. 
It has the following features:
* mining diff commits and extract source code
* converting a source file into a language-agnostic tree format
* export the produced trees in various formats
* compute the differences between the trees
* visualize these differences graphically

## Documentation

To use ASTChangeAnalyzer, you can start by consulting the [Getting Started](https://github.com/GumTreeDiff/gumtree/wiki/Getting-Started) page from our [wiki](https://github.com/GumTreeDiff/gumtree/wiki).

## Screenshots

### The directory diff viewer

![Directory comparator view](https://github.com/GumTreeDiff/gumtree/raw/main/doc/screenshots/screenshot-0.png)

### The file diff viewer

![Diff view on a CSS file](https://github.com/GumTreeDiff/gumtree/raw/main/doc/screenshots/screenshot-1.png)

![Diff view on a Java file](https://github.com/GumTreeDiff/gumtree/raw/main/doc/screenshots/screenshot-2.png)

## Supported languages

We already deal with a wide range of languages: Java, Python. Click [here](https://github.com/GumTreeDiff/gumtree/wiki/Languages) for more details about the language we support.

More languages are coming soon, if you want to help contact [me](http://www.labri.fr/perso/falleri).

## Citing GumTree

We are researchers, therefore if you use GumTree in an academic work we would be really glad if you cite our seminal paper using the following bibtex:

```
@inproceedings{DBLP:conf/kbse/FalleriMBMM14,
  author    = {Jean{-}R{\'{e}}my Falleri and
               Flor{\'{e}}al Morandat and
               Xavier Blanc and
               Matias Martinez and
               Martin Monperrus},
  title     = {Fine-grained and accurate source code differencing},
  booktitle = {{ACM/IEEE} International Conference on Automated Software Engineering,
               {ASE} '14, Vasteras, Sweden - September 15 - 19, 2014},
  pages     = {313--324},
  year      = {2014},
  url       = {http://doi.acm.org/10.1145/2642937.2642982},
  doi       = {10.1145/2642937.2642982}
}
```
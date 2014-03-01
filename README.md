Language Definition Framework
=============================

## Objective

This project aims to provide a set of tools for defining computer languages
(typically, [context-free languages][CFL]).

It should enable one to easily define [DSL][]s or alter the syntax of an
existing language (and use it in their own programs), if they think it
would be a good idea to temporarily extend its syntax, in order to more
expressively approach a certain kind of problems. One scenario would even
allow a (downloadable) framework to extend a target language in order to
integrate some new features.

## Sought Features

+ An elegant syntax for writing grammar files (which, ideally, are
  independent of the actual parser generator that ends up being used)
+ Support for detecting the classes to which a context-free language belongs
  ([DCFL][], [LL][], [LR][], [LALR][], [SLR][], [GLR][], etc.)
+ Support for rewriting grammar rules, such that the higher-level constructs
  (or syntactic sugar provided by LDF) get converted into basic [BNF][]
  automatically
+ The ability to "extend" a grammar, very much like one would extend a class
  and override some of its methods
+ The ability to combine two languages into one (as well as a few other
  operations on grammars)
+ A scripting language for grammar actions, with rigorous type checking
+ Support for working with [AST][]s and common elements of a programming
  language (eg: symbol tables)
+ The ability to freely choose the parser generator to be used on a grammar
+ A few sample (skeleton) languages, provided as starting point for a new
  language (be it imperative, functional, declarative, etc.)

[CFL]: http://en.wikipedia.org/wiki/Context-free_language
[DSL]: http://en.wikipedia.org/wiki/Domain-specific_language "Domain-Specific Language"
[BNF]: http://en.wikipedia.org/wiki/Backus%E2%80%93Naur_Form "Backus-Naur Form"
[DCFL]: http://en.wikipedia.org/wiki/DCFL "Deterministic context-free languages"
[LL]: http://en.wikipedia.org/wiki/LL_parser "Left to right, Leftmost derivation"
[LR]: http://en.wikipedia.org/wiki/LR_parser "Left to right, Rightmost derivation"
[LALR]: http://en.wikipedia.org/wiki/LALR_parser "Look-Ahead LR"
[SLR]: http://en.wikipedia.org/wiki/SLR_parser "Simple LR"
[GLR]: http://en.wikipedia.org/wiki/GLR_parser "Generalized LR"
[AST]: http://en.wikipedia.org/wiki/Abstract_syntax_tree "Abstract syntax tree"
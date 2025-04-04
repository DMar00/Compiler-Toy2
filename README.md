# Toy2 Compiler

This project is a **compiler for a custom programming language called _Toy2_**.  

## ✨ Language Features
- **Strong static typing**
- **Variable declarations**, with or without initialization
- **Functions with multiple return values**, using a comma-separated list
- **Procedures** supporting:
  - `out` parameters for returning values
  - `@` parameters for pass-by-reference semantics
- **Input/output primitives**:
  - `-->` for printing expressions
  - `-->!` for printing with a newline
  - `<--` for reading from standard input
- **Control flow constructs**:
  - `if`, `elseif`, `else` blocks
  - `while` loops
- **Expressions**:
  - Arithmetic: `+`, `-`, `*`, `/`, `%`
  - Logical: `&&`, `||`, `!`
  - Relational: `==`, `!=`, `<`, `<=`, `>`, `>=`
- **Return statements** with one or multiple expressions
- **Syntactic sugar** for formatting in output using `$(...)`
- **Support for nested blocks** and scoped variable declarations
- **Comments** using `#`


## 📄 Specifications
The full lexical and syntactic specification of the Toy2 language can be found in the  
`Documentation` folder.



# Compiler Project

## Introduction
This project is a basic compiler that processes source code files from a designated `code` folder and generates output in an `output` folder. The compiler expects the user to specify a particular code file to compile by setting a variable in the code. If the code has any errors, they will be reported in the console, while successful runs generate three output files.
This project has been developed by Héctor Borreguero, David Morilla Sorlí and Sebastián Salom Fluxa.
## Table of Contents
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Errors](#errors)
- [Troubleshooting](#troubleshooting)
- [Maintainers](#maintainers)

## Requirements
- Java Development Kit (JDK) 8 or later
- Recommended: An IDE like IntelliJ IDEA or Eclipse for easier development and testing.
  
## Installation
1. Clone the repository or download the source code.
   ```bash
   git clone <repository-url>
   ```
2. Ensure you have JDK 8+ installed on your machine. You can verify the installation using:
   ```bash
   java -version
   ```
3. Compile the project:
   ```bash
   javac -d bin src/com/grupo22/compiler/model/Compiler.java
   ```
   Make sure the directory structure is properly organized according to the package.

## Configuration
1. **Prepare input files**: Place the code files you want to compile in the `code` folder. The files should follow the naming convention `codeN.txt` where `N` is a positive integer (e.g., `code1.txt`, `code2.txt`).
   
2. **Set the file to compile**: Inside the `Compiler.java` file, set the `CODE_FILE_NUMBER` variable to the number corresponding to the file you want to compile (e.g., `1` for `code1.txt`).

3. **Run the compiler**: After setting the file number, run the program using:
   ```bash
   java -cp bin com.grupo22.compiler.model.Compiler
   ```

## Errors

The compiler provides detailed feedback on any errors found in the code. The errors fall into the following categories:

### 1. **Syntax Errors**
   - **Description**: These errors occur when the source code doesn't adhere to the expected syntax rules. Common causes include missing semicolons, unmatched parentheses, or improperly declared variables.
   - **Example**: 
     ```
     Syntax Error: Expected ';' at line 5
     ```

### 2. **Lexical Errors**
   - **Description**: Lexical errors happen when the compiler encounters invalid tokens. This may be due to illegal characters or improperly formed identifiers.
   - **Example**: 
     ```
     Lexical Error: Invalid token at line 8, character '@'
     ```

### 3. **Semantic Errors**
   - **Description**: These errors arise when the meaning of the code is incorrect, even though it might be syntactically valid. This includes operations on incompatible types or undeclared variables.
   - **Example**: 
     ```
     Semantic Error: Variable 'x' not declared before use at line 12
     ```

### 4. **Compilation Errors**
   - **Description**: General errors that prevent the code from being compiled successfully. These could be due to a variety of factors, including incorrect package imports, unresolved dependencies, or other issues that stop the compilation process.
   - **Example**: 
     ```
     Compilation Error: Unable to resolve symbol 'foo' at line 16
     ```

### 5. **Runtime Errors**
   - **Description**: Errors that occur during the execution of compiled code. These errors won't be caught at compile time but will cause the program to crash or behave unexpectedly during runtime.
   - **Example**: 
     ```
     Runtime Error: Division by zero at line 20
     ```

### 6. **Compilation Warnings**
   - **Description**: Warnings are less severe than errors and do not prevent the code from compiling. However, they indicate potential issues in the code that should be addressed to avoid future errors or logical mistakes.
   - **Example**: 
     ```
     Warning: Variable 'y' declared but not used at line 25
     ```

## Troubleshooting

- **Problem**: Compiler throws a `FileNotFoundException` when trying to access the `code` file.
  - **Solution**: Ensure that the input files are correctly named following the `codeN.txt` convention and placed in the `code` directory.

- **Problem**: Compilation fails due to missing dependencies or unresolved symbols.
  - **Solution**: Verify that all necessary imports and package structures are correctly defined and that all external libraries, if any, are included.

- **Problem**: The output files are not generated after successful compilation.
  - **Solution**: Ensure that the `output` directory exists and that the program has the necessary permissions to write to this folder.

## Maintainers
This project is maintained by David Morilla Sorlí. For any queries, you can contact me via [email](mailto:contactodavidmorilla@gmail.com).

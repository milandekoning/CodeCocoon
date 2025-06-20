# CodeCocoon

**CodeCocoon** is a tool made for applying **semantic-preserving** (metamorphic) and **naturalness-preserving** transformations to code snippets.
For example:
```java
public int test() {
    int value = 1;
    return value;
}
```
Can be transformed to:
```java
public int test() {
    int result = 1;
    return result;
}
```
Such a transformation preserves the functionality of the code while making it appear differently.

These transformations can be used for testing the robustness of LLMs for coding tasks and mitigate data leakage in LLMs. We found that the median success rate of ChatGPT-4o repairing bugs in the [Defects4J](https://github.com/rjust/defects4j) dataset can drop by up to 20% when testing with transformed snippets.



At this point in time, it supports only Java functions as code snippets.
The supported transformations are shown and explained [here](docs/Transformations.md).
Explanations and details about the design of CodeCocoon can be found [here](docs/Design.md).

## Setup 

1. Clone the repository and navigate to the root directory.
2. To install dependencies, compile, test and package use `mvn clean install`.
3. Make sure that the openai api key is added as an environment variable `OPENAI_API_KEY`.

## Usage

To run the system under the configuration in `config.yaml`, run the following command:

``mvn clean compile exec:java``

If the default configuration is used, a new file with the transformed version of the 
Chart-1 bug from Defects4j should appear under `datasets`

More explanation about the configuration can be found [here](docs/Config.md). 


To run the tests, use `mvn clean test` 

To build a standalone jar, run `mvn clean install`. The jar should appear under `target`.
The input and output file locations can either be defined in config.yaml or provided as command line arguments.
For example:

``java -jar codecocoon.jar config.yaml datasets/input.json datasets/output.json``

will use the command line arguments, even if they are also defined in config.yaml



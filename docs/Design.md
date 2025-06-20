# Design

The system loads code snippets, transforms the snippets, and saves the transformed snippets. Snippets are transformed 
with Transformers. Each Transformer is designed to apply one specific kind of transformation to a code snippet if 
possible. All transformations are defined and explained [here](Transformations.md). At this point in time, a Transformer
applies its transformation at every valid position.

## Visitor pattern for transformers

The transformers use the Visitor pattern to apply transformations. The idea is that we perform a traversal of the
Abstract Syntax Tree and modify some of the nodes we visit. Each transformer defines a `visit()` method specifically for
the kind of node that should be modified. 

For example, say we want to add `else break;` to each if statement, we would define the following method in a 
transformer:

```java
    @Override
    public Visitable visit(IfStmt ifStatement, Snippet snippet) {
        // Set the else statement to be a new break statement
        ifStatement.setElseStmt(new BreakStmt());
        
        // Continue the traversal into the newly generated statement.
        return super.visit(ifStatement, context);
    }
```

This method is called when the AST traversal visits an if statement. We can modify the if statement and continue the 
traversal into the body of the if statement by calling the default visit method from the superclass, which visits both
the if and else blocks. In case we did not want to continue the traversal to a deeper level, we could've used 
`return ifStatement;`. Then, the traversal continues with the next node on the same level as the if statement.

The traversal pattern also allows us to provide a state object, which is passed through in the traversal. This contains some information about the current code snippet, which is used for 
context-aware synonym generation and keeping track of the transformations. This is what the `Snippet snippet` parameter refers to.

More in-detail explanations of the visitor pattern and how it should be used can be found in the 
[JavaParser documentation](https://leanpub.com/javaparservisited).

## Synonym generation

There are multiple ways to generate synonyms for variable names. Currently, we can generate synonyms with a lexical 
dictionary (WordNet), or use an LLM to generate synonyms given the context (ChatGPT-4o-mini).

### Caching

To reduce the costs of querying LLMs for generating synonyms, a wrapper LLM implementation called LLMCacheWrapper was 
implemented. This class makes sure that prompts and responses are stored. If a prompt has already been used, the 
response is fetched from the cache instead of the LLM.

**NOTE:** The cache only works for identical prompts. If you apply other transformations to a code snippet before 
renaming variable names, the LLM will be queried even if synonyms for these variables were already generated for the
non-transformed code snippet. The order of applying transformations matters for this!
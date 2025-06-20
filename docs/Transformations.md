# Transformations

Currently, the following transformations are supported:

### Local variable renaming:
Rename local variables to context synonyms. It does so by querying ChatGPT4o-mini via the Openai API.
Example:
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

### Function renaming:
Rename a function with a synonym. It does so by querying ChatGPT-4o-mini via the Openai API.
Example:
```java
public int add(int a, int b) {
    return a + b;
}
```
Can be transformed to:
```java
public int sum(int a, int b) {
    return a + b;
}
```

### Parameter renaming:
Renames a parameters with a synonym. It does so by querying ChatGPT-4o-mini via the Openai API.
Example:
```java
public int add(int a, int b) {
    return a + b;
}
```
Can be transformed to:
```java
public int add(int val1, int val2) {
    return val1 + val2;
}
```

### For loop to while loop:
Transforms a for loop to a while loop. This transformation can only be applied if there are no variables
with the same name as the for loop variable in the same scope, as this transformation lifts the variable 
declaration to a higher scope.

Example:
```java
public int sum(int[] arr) {
    int sum = 0;
    for (int i = 0; i < arr; i++) {
        sum += arr[i];
    }
    return sum;
}
```
Is transformed to:
```java
public int test() {
    int sum = 0;
    int i = 0;
    while(i < arr) {
        sum += arr[i];
        i++;
    }
    return sum;
}
```

### Nest an else-if
Transforms an else if statement into an if statement nested in an else statement.

Example:
```java
public int test(int i) {
    if (i == 1) {
        return 1;
    } else if (i == 2) {
        return 2;
    }
    return 3;
}
```
Is transformed to:
```java
public int test(int i) {
    if (i == 1) {
        return 1;
    } else {
        if (i == 2) {
            return 2;
        }
    }
    return 3;
}
```

### Reverse if-else blocks
Transforms if-else statements by negating the condition and the then and else blocks.

Example:
```java
public int test(int i) {
    if (i == 1) {
        return 1;
    } else {
        return 2;
    }
}
```
Is transformed to:
```java
public int test(int i) {
    if (i != 1) {
        return 2;
    } else {
        return 1;
    }
}
```

### Swap relational operands expression
Transforms relational expressions by swapping operands and inverting the operator.

Example:
```java
public int test(int i) {
    if (i > 1) {
        return 1;
    } else {
        return 2;
    }
}
```
Is transformed to:
```java
public int test(int i) {
    if (1 < i) {
        return 2;
    } else {
        return 1;
    }
}
```

### Expand Unary Increment/Decrement
Transforms unary increments/decrements by expanding them.

Example:
```java
public int test(int i) {
    i++;
    return i;
}
```
Is transformed to:
```java
public int test(int i) {
    i += 1;
    return i;
}
```

### Swap == operands
Transforms == and != expressions by switching the operands.

Example:
```java
public int test(int i) {
    if (i == 1) {
        return 1;
    } else {
        return 2;
    }
}
```
Is transformed to:
```java
public int test(int i) {
    if (1 == i) {
        return 2;
    } else {
        return 1;
    }
}
```

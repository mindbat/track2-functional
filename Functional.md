
# Clojure as a functional language

Clojure is a functional language. Functions are first class citizens just like
any other value type you have already encountered (ints/floats/objects etc.).
This means that we can pass functions as arguments to other functions, returning
functions from functions, and even manipulating functions.

# Syntax

Compared to other languages, Clojure does function calls slightly differently than one
might expect. To give a quick example:

```java
myFunction("foo", "bar")
```

In Clojure this might look like the following:

```clojure
(my-function "foo" "bar")
```

There are a couple differences here, first thing to note is that we are using a different
case style. In Clojure we don't use camel case but instead lisp case which is dash seperated.

Another thing is that we've placed the function inside the parenthesis. In Clojure a function
call is always an open paren followed by the function we want to call, followed by any arguments
we are passing to the function, followed by a closing paren.

Lastly another thing is that we aren't separating the arguments with a comma anymore. In Clojure
commas are simply treated as whitespace so we don't need them.

## Common types

Clojure mostly just uses Java classes under the hood. Lets examine some common types using
the `type` function.

```clojure
user=> (type 2015)
java.lang.Long
user=> (type 3.14)
java.lang.Double
user=> (type "clojure")
java.lang.String
user=> (type true)
java.lang.Boolean
```

## Keywords

TODO: Not sure how best to explain this.

## Symbols

TODO: Not sure if we should have this.

## Common data structures

When working in Clojure you'll encounter 3 common types of data structures.

### Lists

A list in Clojure is just a linked list. There are a couple of ways we can create them, the
first one being the `list` function:

```clojure
user=> (list)
()
```

This gave us an empty list, but we can also supply it with a variable number of arguments to
initialize the list with:

```clojure
user=> (list 1 2 3)
(1 2 3)
```

We can efficiently add to the beginning of a linked list using the `conj` function:

```clojure
user=> (conj (list 1 2 3) 0)
(0 1 2 3)
```

Accessing the head or tail of the list is possible using the `first` and `rest` functions.
We can also access a specific element using the `nth` function.

```clojure
user=> (first (list 1 2 3))
1
user=> (rest (list 1 2 3))
(2 3)
user=> (nth (list 1 2 3) 0)
1
```

Finally Clojure has a shorthand for defining lists, instead of calling the list function we can
use the following:

```clojure
user=> '(1 2 3)
(1 2 3)
user=> `()
()
```

### Vectors

A vector in Clojure is a lot like an array in other languages. That means we can efficiently access
the index of a vector compared to a list, but adding elements might be a bit more expensive than on
a list.

To create a vector we can use the vector function:

```clojure
user=> (vector 1 2 3)
[1 2 3]
user=> (vector)
[]
```

We can perform a lot of the same operations on vectors as we can on lists:

```clojure
user=> (first (vector 1 2 3))
1
user=> (rest (vector 1 2 3))
(2 3)
user=> (nth (vector 1 2 3) 0)
1
```

Lets try using the `conj` function from earlier:

```clojure
user=> (conj (vector 1 2 3) 0)
[1 2 3 0]
```

Wait, this isn't what we expect. With a list using `conj` added the new element to the front,
not to the back. This is because the `conj` function adds elements where it is most efficient
for the datastructure.

In a list it is cheapest to add an element to the front, we don't
need to traverse the whole list to get to the end to add the element.

In a vector it is cheapest to add an element to the end. Because vectors are arrays if we were to
add to the front, we would need to move every element that was already there down a slot to make
room for the new element.

Finally like lists vectors have a shorthand way to create them quickly:

```clojure
user=> [1 2 3]
[1 2 3]
user=> []
[]
```

### Hash Maps

TODO

## Exercises

TODO

# Thinking functionally

## Recursion

## Map

## Filter

## Reduce









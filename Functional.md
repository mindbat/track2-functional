# Clojure as a functional language

Clojure is a functional language - that means that functions are first class citizens just like
any other value type you have already encountered (ints/floats/objects etc.).
This means that we can pass functions as arguments to other functions, returning
functions from functions, and even manipulating functions.

# Koans

Periodically we'll mention exercises from the Clojure Koans. These are a series of exercises
designed to teach Clojure.

Each one will be an assertion such as the following:

```clojure
(= true __)
```

The purpose of these exercises is to alter the `__` to make the assertion pass.

```clojure
(= true true)
```

The koans are done incrementally, as you complete each one it will then move on to the next koan.
Feel free to go through these at your own pace.

To get the koans all setup and running
visit: http://clojurekoans.com

You should have everything you need installed from the Install Fest, so hop down to the
`Install the Koans` section to get started.

# Syntax

Compared to other languages, Clojure does function calls slightly differently than one
might expect. To give a quick example:

```java
myFunction("hi", "bye")
```

In Clojure this might look like the following:

```clojure
(my-function "hi" "bye")
```

There are a couple differences here, first thing to note is that we are using a different
case style. In Clojure we don't use camel case but instead lisp case which is dash separated.

Another thing is that we've placed the function inside the parenthesis. In Clojure a function
call is always an open paren followed by the function we want to call, followed by any arguments
we are passing to the function, followed by a closing paren.

Lastly another thing is that we aren't separating the arguments with a comma anymore. In Clojure
commas are simply treated as whitespace so we don't need them.

## Common functions

### Math

In Clojure everything is a function, there is no notion of operators. Additionally all of the
math operators take a variable number of arguments.

```clojure
user=> (+ 1 1)
2
user=> (+ 1 2 3)
6
user=> (- 1 1)
0
user=> (- 5)
-5
user=> (* 2 3)
6
user=> (/ 6 2)
3
```

### Equality

We can use the `=` function to test whether its arguments are all equal to each other.
Like the math functions the `=` function also takes multiple arguments.

```clojure
user=> (= "hello" "hello")
true
user=> (= 1 1 1)
true
user=> (= 1 2)
false
```

## `nil`

`nil` is a special value in Clojure, it means "nothing". It is commonly used when a desired value is not found. We will see examples of its use later. For now, just note that it is not equal to any other value:

```clojure
user=> (= nil false)
false
user=> (= nil true)
false
user=> (= nil 0)
false
```
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

From the Clojure docs:
`Keywords are symbolic identifiers that evaluate to themselves.
They provide very fast equality tests.`
So at a high level a keyword is a constant literal, just like the integer `1234` or
the string constant `"hello"`. Keywords look and often behave a lot like strings
with a couple exceptions.

* They begin with a `:` so `:hi` is an example keyword, while `"hi"` is an example string.
* They can't have spaces in them. `"hi there"` may be a valid string but it's not a valid
keyword.
* Keywords get special treatment in Clojure: they act like a function when looking up a value 
in a hash-map - we'll introduce hash-maps later.

## Exercises

Try to complete the first set of Koans `01_equalities.clj`

# Common data structures

When working in Clojure you'll encounter 3 common types of data structures.

## Lists

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
Note that while `first` returns an element, `rest` returns a list. 
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

### Exercises

Try to complete the second set of Koans `02_lists.clj`.

## Vectors

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

Let's try using the `conj` function from earlier:

```clojure
user=> (conj (vector 1 2 3) 0)
[1 2 3 0]
```

Wait, this isn't what we expect. With a list using `conj` added the new element to the front,
not to the back. This is because the `conj` function adds elements where it is most efficient
for the data structure.

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

### Exercises

Try to complete the third set of Koans `03_vectors.clj`.

## Sets

Sets are a data-structure that can only contain unique elements. We construct a set with
the `set` function which takes in some other sequence.

```clojure
user=> (set [1 2 3 4])
#{1 4 3 2}
user=> (set [1 1 2 3])
#{1 3 2}
```

So using this method of constructing a set will guarantee us a set of unique elements if we happen
to have duplicates they just get thrown away.

Finally Clojure has a shorthand way to create sets.

```clojure
user=> #{1 2 3}
#{1 3 2}
```

But using the shorthand we can't initialize it with duplicate elements.

```clojure
user=> #{1 1 2 3}

IllegalArgumentException Duplicate key: 1  clojure.lang.PersistentHashSet.createWithCheck (PersistentHashSet.java:68)
```

### Exercises

Try to complete the fourth set of Koans `04_sets.clj`.

## Hash Maps

Hash-maps are just like maps or dictionaries in other languages. They store key/value pairs
and we can efficiently access values in the hash-map by their keys.

We can create hash-maps with the `hash-map` function. This function takes an even number
of arguments, which is of the format key followed by pair.

```clojure
(hash-map "blue" 30 "red" 100)
```

So this will create a map, with keys "blue" and "red" each with an integer as its value.

While you can use any Clojure elements as keys, most commonly keywords are used for this purpose since lookup by keywords is very fast:

```clojure
(hash-map :blue 30 :red 100)
```

To access values from the keys we can use the `get` function which takes a map as its first
argument and the key for the value we want to retrieve as the second.

```clojure
user-> (get (hash-map :blue 30 :red 100) :blue)
30
```

Note that a map is itself a function that can be applied to look up a value for a key: 

```clojure
user-> ((hash-map :blue 30 :red 100) :blue)
30
```

A keyword is also a function that takes a map as a parameter and returns a value associated with this key:

```clojure
user-> (:blue (hash-map :blue 30 :red 100))
30
```

If a key doesn't appear in a map, all three ways of lookup will return a special value `nil`:

```clojure
user=>  (get (hash-map :blue 30 :red 100) :green)
nil
user=> ((hash-map :blue 30 :red 100) :green)
nil
user=> (:green (hash-map :blue 30 :red 100))
nil
```

We can also build a new map from an old one using the `assoc` function

```clojure
user=> (assoc (hash-map :blue 30 :red 100) :green 20)
{:blue 30, :green 20, :red 100}
```
If the key already exists in the map, its value will be replaced by the new one:

```clojure
user=> (assoc (hash-map :blue 30 :red 100) :blue 20)
{:blue 20, :red 100}
```

Finally we have a shorthand to build maps, we don't need to use the hash-map function.

```clojure
user=> {"red" 100, "blue" 30}
{"blue" 30, "red" 100}
```

### Exercises

Try to complete the fifth set of Koans `05_maps.clj`.

# Saving values into names

So far we've just been nesting our function calls, what if we wanted to be able to store
the result of a call into a name so we can easily access it? We can use the `def` function
for this.

```clojure
user=> (def PI 3.14)
#'user/PI
user=> (def some-list (list 1 2 3 4))
#'user/some-list
user=> some-list
(1 2 3 4)
user=> PI
3.14
```

# Defining your own functions

So far we've seen how we can use builtin functions, but how do we define our own?

We can use the `fn` function to create new functions, we can use the `def` function from the
previous section to associate the new function with a name. Lets write a simple square function.

`fn` takes two arguments, the first argument is a vector of the arguments and the second is the
operation we want to perform.

```clojure
user=> (def square (fn [number] (* number number)))
#'user/square
user=> (square 5)
25
```

Notice that we didn't need to specify a return statement like we often have to do in other languages.
In Clojure a function call will return the last expression it evaluates.

We can also use a shorthand to combine `def` and `fn` which is `defn`.

```clojure
(defn square [number] (* number number))
```

# Thinking functionally

Functional approach to programming means that a solution is constructed as a composition of functions. Each function returns a new entity that's one step closer to the desired results. This is different from the more common imperative approach that keeps changing data and variables in memory (often using loops) until the result is constructed or determined. 

For example, consider figuring out if a string is a palindrome. In a traditional approach one would have a loop in which an index is changing as the string is being traversed that compares the string characters to each other. In a functional appoach it would be to just compare a string to its reverse and return the result. 

```clojure
user=> (defn is-palindrome? [s] (= s (clojure.string/reverse s)))
#'user/is-palindrome?
user=> (is-palindrome? "anna")
true
user=> (is-palindrome? "ann")
false
```
Here the function `is-palindrome?` uses functions = and 'clojure.string/reverse` as its elements. It does not directly iterate over the string in a loop. 

This is a simple example. More interesting examples involve working with functions at all levels of the language. Functions a what's called first class citizens in functional languages, so you can pass them as parameters to other functions, or even construct them "on the fly" and pass them to other functions. Below is a simple, somewhat artificial example of passing a function to a function: suppose we have a vector of at least two elements, and we want to check that both elements satisfy a given condition, but we don't know ahead of time what the condition is. Here is the function:
```clojure
user=> (defn check-condition [v f] (and (f (first v)) (f (second v))))
#'user/check-condition
user=> (check-condition [3 4] odd?)
false
user=> (check-condition ["eye" "bob"] is-palindrome?)
true
```
We can also put together a function right in the call to `check-condition` using the `fn` syntax that we have covered earlier, and it doesn't even need a name: it's called an anonymous function. In this case we are checking if the first tow elements of a vector are less than 10:
```clojure
user=> (check-condition [4 5] (fn [n] (< n 10)))
true
user=> (check-condition [4 15] (fn [n] (< n 10)))
false
```

## Recursion

TODO: now we just need to extend the last example to all the elements of a vector. 

## Higher-order functions

A higher-order function is one that either takes a function as input or returns a function as
output. In Clojure we can treat functions as values just like any other type. This is one
of the really powerful things of functional programming in general.

To explore this further we'll go over 3 built-in higher order functions that are integral
to a functional programmers toolbox.

### Map

`map` is a function that takes a function as its first argument and a sequence as its second
and applies the function to each element building a new list from that.

```clojure
user=> (map square [1 2 3])
(1 4 9)
user=> (map square '(1 2 3))
(1 4 9)
```

So we passed square as a value, and either a list or a vector and got a new list built.

### Filter

`filter` works like `map` in the sense that it also takes a function as its first argument
and a sequence as its second. The function passed to `filter` must return a boolean.
Like `map` we will walk through each element of the sequence and apply the function to it,
if the function we passed in evaluates to `true` on the element we will keep it, if it's `false`
we will drop the element from the sequence.

Lets use a built-in function `even?` which returns `true` if the number we pass it is even, otherwise
`false`.

```clojure
user=> (even? 3)
false
user=> (even? 2)
true
```

Now lets pass `even?` to `filter`.

```clojure
user=> (filter even? [1 2 3 4 5 6])
(2 4 6)
```

### Reduce

`reduce` also takes two arguments, the first one is a function and the second is a sequence just
like `map` and `filter`. The function we pass in must take two arguments instead of one.

`reduce` applies the function to the first element of the sequence and the second. It then
applies the result of the first call and applies it to the third element of the sequence.
It then walks through the rest of the sequence.

A quick example is we can reduce `+` over a sequence of numbers to add them all together.

```clojure
user=> (reduce + [1 2 3 4])
10
```


